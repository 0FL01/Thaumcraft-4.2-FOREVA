package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.tiles.TileResearchTable;

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
            if (player.getEntityId() != this.playerid) return;
            if (player.world.provider.getDimension() != this.dim) return;
            if (this.aspect1 == null || this.aspect2 == null) return;
            TileEntity tile = player.world.getTileEntity(new BlockPos(this.x, this.y, this.z));
            if (!(tile instanceof TileResearchTable)) return;
            Aspect result = getCombinationResult(this.aspect1, this.aspect2);
            if (result == null) return;
            IPlayerKnowledge knowledge = thaumcraft.common.CommonProxy.getPlayerKnowledge(player);
            if (knowledge != null && !knowledge.hasDiscoveredAspect(result)) {
                knowledge.addDiscoveredAspect(result.getTag());
                PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(knowledge.getAspectsDiscovered()), player);
            }
        });
        return null;
    }

    private static Aspect getCombinationResult(Aspect a, Aspect b) {
        for (Aspect aspect : Aspect.getCompoundAspects()) {
            Aspect[] components = aspect.getComponents();
            if (components == null || components.length != 2) continue;
            if ((components[0] == a && components[1] == b) || (components[0] == b && components[1] == a)) {
                return aspect;
            }
        }
        return null;
    }
}
