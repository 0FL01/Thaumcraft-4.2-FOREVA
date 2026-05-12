package thaumcraft.common.lib.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Base class for all network packets.
 * Each subclass overrides fromBytes/toBytes for serialization,
 * and optionally onMessage for handling on the receiving side.
 */
public class PacketBase implements IMessage {

    public PacketBase() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    /**
     * Override in subclasses to handle the message.
     * Called on the network thread — use Minecraft.addScheduledTask
     * or Server.addScheduledTask for main-thread work.
     * @return null in most cases
     */
    public IMessage onMessage(MessageContext ctx) {
        return null;
    }

    protected EntityPlayerMP getServerPlayer(MessageContext ctx) {
        return ctx != null && ctx.getServerHandler() != null ? ctx.getServerHandler().player : null;
    }

    protected void scheduleServer(MessageContext ctx, Consumer<EntityPlayerMP> task) {
        EntityPlayerMP player = this.getServerPlayer(ctx);
        if (player != null && player.getServerWorld() != null && task != null) {
            player.getServerWorld().addScheduledTask(() -> task.accept(player));
        }
    }
}
