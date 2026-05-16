package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXBlockZap extends PacketBase {
    private float x;
    private float y;
    private float z;
    private float dx;
    private float dy;
    private float dz;

    public PacketFXBlockZap() {}

    public PacketFXBlockZap(float x, float y, float z, float dx, float dy, float dz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.dx);
        buf.writeFloat(this.dy);
        buf.writeFloat(this.dz);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.dx = buf.readFloat();
        this.dy = buf.readFloat();
        this.dz = buf.readFloat();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            Thaumcraft.proxy.bolt(
                    Minecraft.getMinecraft().world,
                    this.x,
                    this.y,
                    this.z,
                    this.dx,
                    this.dy,
                    this.dz,
                    0x66CCFF,
                    4);
            Minecraft.getMinecraft().world.playSound(
                    Minecraft.getMinecraft().player,
                    this.x,
                    this.y,
                    this.z,
                    TCSounds.ZAP,
                    SoundCategory.BLOCKS,
                    0.1F,
                    1.0F + Minecraft.getMinecraft().world.rand.nextFloat() * 0.2F);
        });
        return null;
    }
}
