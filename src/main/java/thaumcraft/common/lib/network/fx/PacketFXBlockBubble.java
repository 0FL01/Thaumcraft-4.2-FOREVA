package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import java.awt.Color;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXBlockBubble extends PacketBase {
    private int x;
    private int y;
    private int z;
    private int color;

    public PacketFXBlockBubble() {}

    public PacketFXBlockBubble(int x, int y, int z, int color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.color);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.color = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            Color c = new Color(this.color);
            float red = c.getRed() / 255.0F;
            float green = c.getGreen() / 255.0F;
            float blue = c.getBlue() / 255.0F;
            int amount = Thaumcraft.proxy.particleCount(1);
            for (int i = 0; i < amount; i++) {
                double py = this.y + world.rand.nextFloat();
                Thaumcraft.proxy.crucibleBubble(
                        world,
                        this.x,
                        (float) py,
                        this.z + world.rand.nextFloat(),
                        red, green, blue);
                Thaumcraft.proxy.crucibleBubble(
                        world,
                        this.x + 1,
                        (float) py,
                        this.z + world.rand.nextFloat(),
                        red, green, blue);
                Thaumcraft.proxy.crucibleBubble(
                        world,
                        this.x + world.rand.nextFloat(),
                        (float) py,
                        this.z,
                        red, green, blue);
                Thaumcraft.proxy.crucibleBubble(
                        world,
                        this.x + world.rand.nextFloat(),
                        (float) py,
                        this.z + 1,
                        red, green, blue);
            }
        });
        return null;
    }
}
