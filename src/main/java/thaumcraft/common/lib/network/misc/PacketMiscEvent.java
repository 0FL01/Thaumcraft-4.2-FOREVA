package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.ClientTickEventsFML;
import thaumcraft.client.lib.RenderEventHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketBase;

public class PacketMiscEvent extends PacketBase {
    public static final short WARP_EVENT = 0;
    public static final short MIST_EVENT = 1;
    public static final short MIST_EVENT_SHORT = 2;

    private short type;

    public PacketMiscEvent() {}

    public PacketMiscEvent(short type) {
        this.type = type;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(this.type);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = buf.readShort();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            World world = Thaumcraft.proxy.getClientWorld();
            if (player == null || world == null) return;
            switch (this.type) {
                case WARP_EVENT:
                    ClientTickEventsFML.warpVignette = 100;
                    world.playSound(
                            player,
                            player.posX,
                            player.posY,
                            player.posZ,
                            TCSounds.HEARTBEAT,
                            SoundCategory.PLAYERS,
                            1.0f,
                            1.0f);
                    break;
                case MIST_EVENT:
                    RenderEventHandler.fogFiddled = true;
                    RenderEventHandler.fogDuration = 2400;
                    break;
                case MIST_EVENT_SHORT:
                    RenderEventHandler.fogFiddled = true;
                    if (RenderEventHandler.fogDuration < 200) {
                        RenderEventHandler.fogDuration = 200;
                    }
                    break;
                default:
                    break;
            }
        });
        return null;
    }
}
