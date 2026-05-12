package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
            if (this.key == null || this.key.isEmpty()) return;
            if (player.world.provider.getDimension() != this.dim) return;
            if (this.username != null && !this.username.isEmpty() && !player.getName().equals(this.username)) return;
            if (ResearchManager.isResearchComplete(player, this.key)) return;
            ResearchItem research = ResearchCategories.getResearch(this.key);
            if (research == null) return;
            if (this.type == 0) {
                completeResearch(player, research);
            }
        });
        return null;
    }

    private static void completeResearch(EntityPlayer player, ResearchItem research) {
        ResearchManager.addResearch(player, research.key);
        if (research.siblings != null) {
            for (String sibling : research.siblings) {
                if (ResearchCategories.getResearch(sibling) != null && !ResearchManager.isResearchComplete(player, sibling)) {
                    ResearchManager.addResearch(player, sibling);
                }
            }
        }
    }
}
