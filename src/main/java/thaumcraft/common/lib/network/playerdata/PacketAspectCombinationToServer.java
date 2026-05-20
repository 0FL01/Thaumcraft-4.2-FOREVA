package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.tiles.TileResearchTable;

import java.util.HashMap;
import java.util.Map;

public class PacketAspectCombinationToServer extends PacketBase {
    private int dim;
    private int playerid;
    private int x;
    private int y;
    private int z;
    private Aspect aspect1;
    private Aspect aspect2;
    private boolean ab1;
    private boolean ab2;

    public PacketAspectCombinationToServer() {}

    public PacketAspectCombinationToServer(EntityPlayer player, int x, int y, int z, Aspect aspect1, Aspect aspect2, boolean ab1, boolean ab2, boolean ret) {
        this.dim = player.world.provider.getDimension();
        this.playerid = player.getEntityId();
        this.x = x;
        this.y = y;
        this.z = z;
        this.aspect1 = aspect1;
        this.aspect2 = aspect2;
        this.ab1 = ab1;
        this.ab2 = ab2;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dim = buf.readInt();
        this.playerid = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.aspect1 = Aspect.getAspect(ByteBufUtils.readUTF8String(buf));
        this.aspect2 = Aspect.getAspect(ByteBufUtils.readUTF8String(buf));
        this.ab1 = buf.readBoolean();
        this.ab2 = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dim);
        buf.writeInt(this.playerid);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        ByteBufUtils.writeUTF8String(buf, this.aspect1 == null ? "" : this.aspect1.getTag());
        ByteBufUtils.writeUTF8String(buf, this.aspect2 == null ? "" : this.aspect2.getTag());
        buf.writeBoolean(this.ab1);
        buf.writeBoolean(this.ab2);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            dispatch(player, this.playerid, this.dim, this.x, this.y, this.z, this.aspect1, this.aspect2, this.ab1, this.ab2);
        });
        return null;
    }

    static boolean dispatch(EntityPlayer player,
                            int playerid,
                            int dim,
                            int x,
                            int y,
                            int z,
                            Aspect aspect1,
                            Aspect aspect2,
                            boolean useBonus1,
                            boolean useBonus2) {
        if (player == null || player.getEntityId() != playerid) return false;
        if (player.world.provider.getDimension() != dim) return false;
        if (aspect1 == null || aspect2 == null) return false;
        TileResearchTable researchTable = PacketAspectPlaceToServer.resolveResearchTable(player, new BlockPos(x, y, z));
        if (researchTable == null) return false;
        IPlayerKnowledge knowledge = thaumcraft.common.CommonProxy.getPlayerKnowledge(player);
        if (knowledge == null) return false;
        Aspect combo = consumeCombinationInputs(player, researchTable, knowledge, aspect1, aspect2, useBonus1, useBonus2);
        if (combo == null) return false;
        ScanManager.checkAndSyncAspectKnowledge(player, combo, 1);
        ResearchManager.updateCache(player.getName(), knowledge);
        return true;
    }

    static Aspect consumeCombinationInputs(EntityPlayer player,
                                           TileResearchTable researchTable,
                                           IPlayerKnowledge knowledge,
                                           Aspect aspect1,
                                           Aspect aspect2,
                                           boolean useBonus1,
                                           boolean useBonus2) {
        if (player == null || researchTable == null || knowledge == null || aspect1 == null || aspect2 == null) {
            return null;
        }
        if (!knowledge.hasDiscoveredAspect(aspect1) || !knowledge.hasDiscoveredAspect(aspect2)) {
            return null;
        }
        Aspect combo = ResearchManager.getCombinationResult(aspect1, aspect2);
        if (combo == null) {
            return null;
        }

        Map<Aspect, Integer> poolCosts = new HashMap<>();
        Map<Aspect, Integer> bonusCosts = new HashMap<>();
        addCost(useBonus1 ? bonusCosts : poolCosts, aspect1);
        addCost(useBonus2 ? bonusCosts : poolCosts, aspect2);
        if (!hasAvailableCosts(knowledge, poolCosts, researchTable, bonusCosts)) {
            return null;
        }

        for (Map.Entry<Aspect, Integer> entry : poolCosts.entrySet()) {
            Aspect aspect = entry.getKey();
            int amount = entry.getValue();
            if (!knowledge.addAspectPool(aspect, -amount)) {
                return null;
            }
            if (player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(
                        new PacketAspectPool(aspect.getTag(), (short) (-amount), knowledge.getAspectPoolFor(aspect)),
                        (EntityPlayerMP) player);
            }
        }

        boolean changedBonus = false;
        for (Map.Entry<Aspect, Integer> entry : bonusCosts.entrySet()) {
            researchTable.bonusAspects.remove(entry.getKey(), entry.getValue());
            changedBonus = true;
        }
        if (changedBonus && researchTable.getWorld() != null) {
            researchTable.getWorld().notifyBlockUpdate(
                    researchTable.getPos(),
                    researchTable.getWorld().getBlockState(researchTable.getPos()),
                    researchTable.getWorld().getBlockState(researchTable.getPos()),
                    3);
            researchTable.markDirty();
        }
        return combo;
    }

    private static void addCost(Map<Aspect, Integer> costs, Aspect aspect) {
        costs.put(aspect, costs.getOrDefault(aspect, 0) + 1);
    }

    private static boolean hasAvailableCosts(IPlayerKnowledge knowledge,
                                             Map<Aspect, Integer> poolCosts,
                                             TileResearchTable researchTable,
                                             Map<Aspect, Integer> bonusCosts) {
        for (Map.Entry<Aspect, Integer> entry : poolCosts.entrySet()) {
            if (knowledge.getAspectPoolFor(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Aspect, Integer> entry : bonusCosts.entrySet()) {
            if (researchTable.bonusAspects.getAmount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
}
