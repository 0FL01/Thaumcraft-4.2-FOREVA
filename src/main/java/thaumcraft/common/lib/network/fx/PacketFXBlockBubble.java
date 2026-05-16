package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
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
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            Color c = new Color(this.color);
            double red = c.getRed() / 255.0;
            double green = c.getGreen() / 255.0;
            double blue = c.getBlue() / 255.0;
            int amount = Thaumcraft.proxy.particleCount(2);
            for (int i = 0; i < amount; i++) {
                double py = this.y + Minecraft.getMinecraft().world.rand.nextFloat();
                Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.WATER_BUBBLE,
                        this.x + Minecraft.getMinecraft().world.rand.nextFloat(), py, this.z,
                        0.0, 0.01, 0.0);
                Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.WATER_BUBBLE,
                        this.x + 1.0, py, this.z + Minecraft.getMinecraft().world.rand.nextFloat(),
                        0.0, 0.01, 0.0);
                Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.REDSTONE,
                        this.x + Minecraft.getMinecraft().world.rand.nextFloat(),
                        py,
                        this.z + Minecraft.getMinecraft().world.rand.nextFloat(),
                        red, green, blue);
            }
        });
        return null;
    }
}
