package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.research.ResearchManager;

public class PacketPlayerCompleteToServer extends PacketBase {
    private String key;
    private int dim;
    private String username;
    private byte type;

    public PacketPlayerCompleteToServer() {}

    public PacketPlayerCompleteToServer(String key, String username, int dim, byte type) {
        this.key = key;
        this.username = username;
        this.dim = dim;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.key = ByteBufUtils.readUTF8String(buf);
        this.dim = buf.readInt();
        this.username = ByteBufUtils.readUTF8String(buf);
        this.type = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.key == null ? "" : this.key);
        buf.writeInt(this.dim);
        ByteBufUtils.writeUTF8String(buf, this.username == null ? "" : this.username);
        buf.writeByte(this.type);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (processRequest(player, this.key, this.dim, this.username, this.type)) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.LEARN, SoundCategory.PLAYERS, 0.75F, 1.0F);
            }
        });
        return null;
    }

    static boolean processRequest(EntityPlayer player, String key, int dim, String username, byte type) {
        if (player == null || key == null || key.isEmpty()) return false;
        if (player.world.provider.getDimension() != dim) return false;
        if (username != null && !username.isEmpty() && !player.getName().equals(username)) return false;
        if (ResearchManager.isResearchComplete(player, key)) return false;
        ResearchItem research = ResearchCategories.getResearch(key);
        if (research == null) return false;
        if (!ResearchManager.doesPlayerHaveRequisites(player, key)) {
            player.sendMessage(new TextComponentTranslation("tc.researcherror"));
            return false;
        }
        if (type == 0 && research.isSecondary()) {
            return consumeResearchCost(player, research) && completeResearch(player, research);
        }
        if (type == 1 && !research.isSecondary()) {
            return !ResearchManager.createResearchNoteForPlayer(player.world, player, key).isEmpty();
        }
        return false;
    }

    private static boolean completeResearch(EntityPlayer player, ResearchItem research) {
        if (player == null || research == null) {
            return false;
        }
        ResearchManager.addResearch(player, research.key);
        if (research.siblings != null) {
            for (String sibling : research.siblings) {
                if (ResearchCategories.getResearch(sibling) != null
                        && !ResearchManager.isResearchComplete(player, sibling)
                        && ResearchManager.doesPlayerHaveRequisites(player, sibling)) {
                    ResearchManager.addResearch(player, sibling);
                }
            }
        }
        return true;
    }

    private static boolean consumeResearchCost(EntityPlayer player, ResearchItem research) {
        if (player == null || research == null || research.tags == null || research.tags.size() <= 0) {
            return true;
        }
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) {
            return false;
        }
        for (Aspect aspect : research.tags.getAspects()) {
            if (aspect == null) continue;
            int amount = research.tags.getAmount(aspect);
            if (amount > 0 && knowledge.getAspectPoolFor(aspect) < amount) {
                return false;
            }
        }
        for (Aspect aspect : research.tags.getAspects()) {
            if (aspect == null) continue;
            int amount = research.tags.getAmount(aspect);
            if (amount <= 0) continue;
            knowledge.addAspectPool(aspect, -amount);
            if (player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(
                        new PacketAspectPool(aspect.getTag(), (short) -amount, knowledge.getAspectPoolFor(aspect)),
                        (EntityPlayerMP) player
                );
            }
        }
        return true;
    }
}
