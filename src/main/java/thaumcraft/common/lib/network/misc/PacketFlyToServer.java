package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.items.armor.Hover;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFlyToServer extends PacketBase {
    private int playerid;
    private boolean hover;

    public PacketFlyToServer() {}

    public PacketFlyToServer(EntityPlayer player, boolean hover) {
        this.playerid = player.getEntityId();
        this.hover = hover;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerid = buf.readInt();
        this.hover = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.playerid);
        buf.writeBoolean(this.hover);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (player.getEntityId() == this.playerid) {
                Hover.setHover(this.playerid, this.hover);
            }
        });
        return null;
    }
}
