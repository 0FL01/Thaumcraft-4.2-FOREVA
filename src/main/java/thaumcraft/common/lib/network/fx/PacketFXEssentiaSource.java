package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXEssentiaSource extends PacketBase {
    private int x;
    private int y;
    private int z;
    private byte dx;
    private byte dy;
    private byte dz;
    private int color;

    public PacketFXEssentiaSource() {}

    public PacketFXEssentiaSource(int x, int y, int z, byte dx, byte dy, byte dz, int color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.color = color;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.color);
        buf.writeByte(this.dx);
        buf.writeByte(this.dy);
        buf.writeByte(this.dz);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.color = buf.readInt();
        this.dx = buf.readByte();
        this.dy = buf.readByte();
        this.dz = buf.readByte();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            double tx = this.x - this.dx + Minecraft.getMinecraft().world.rand.nextFloat() * 0.6F + 0.2F;
            double ty = this.y - this.dy + Minecraft.getMinecraft().world.rand.nextFloat() * 0.6F + 0.2F;
            double tz = this.z - this.dz + Minecraft.getMinecraft().world.rand.nextFloat() * 0.6F + 0.2F;
            double sx = this.x + Minecraft.getMinecraft().world.rand.nextFloat() * 0.6F + 0.2F;
            double sy = this.y + Minecraft.getMinecraft().world.rand.nextFloat() * 0.6F + 0.2F;
            double sz = this.z + Minecraft.getMinecraft().world.rand.nextFloat() * 0.6F + 0.2F;
            Thaumcraft.proxy.beam(Minecraft.getMinecraft().world, sx, sy, sz, tx, ty, tz, this.color, true, 6);
        });
        return null;
    }
}
