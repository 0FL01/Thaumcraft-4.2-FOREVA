package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketBase;

public class PacketWarpMessage extends PacketBase {
    protected int data = 0;
    protected byte type = 0;

    public PacketWarpMessage() {}

    public PacketWarpMessage(EntityPlayer player, byte type, int change) {
        this.data = change;
        this.type = type;
    }

    public PacketWarpMessage(byte type, int change) {
        this.data = change;
        this.type = type;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.data);
        buf.writeByte(this.type);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.data = buf.readInt();
        this.type = buf.readByte();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            World world = Thaumcraft.proxy.getClientWorld();
            if (player == null || world == null) return;
            if (this.data == 0) return;

            if (this.type == 0 && this.data > 0) {
                PlayerNotifications.addNotification(I18n.format("tc.addwarp"));
                playWhisper(world, player);
            } else if (this.type == 1) {
                PlayerNotifications.addNotification(I18n.format(
                        this.data < 0 ? "tc.removewarpsticky" : "tc.addwarpsticky"));
                if (this.data > 0) {
                    playWhisper(world, player);
                }
            } else if (this.data > 0) {
                PlayerNotifications.addNotification(I18n.format("tc.addwarptemp"));
            }
        });
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static void playWhisper(World world, EntityPlayer player) {
        world.playSound(
                player,
                player.posX,
                player.posY,
                player.posZ,
                TCSounds.WHISPERS,
                SoundCategory.PLAYERS,
                0.5f,
                1.0f);
    }
}
