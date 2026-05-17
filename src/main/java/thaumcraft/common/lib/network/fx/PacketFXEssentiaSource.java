package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.beams.FXBeam;
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
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world == null) return;
            float red = ((this.color >> 16) & 0xFF) / 255.0F;
            float green = ((this.color >> 8) & 0xFF) / 255.0F;
            float blue = (this.color & 0xFF) / 255.0F;
            double tx = this.x - this.dx + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            double ty = this.y - this.dy + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            double tz = this.z - this.dz + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            double sx = this.x + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            double sy = this.y + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            double sz = this.z + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            FXBeam beam = new FXBeam(mc.world, sx, sy, sz, tx, ty, tz, red, green, blue, 6, true, 10);
            beam.setType(1);
            beam.setPulse(true);
            ParticleEngine.addEffect(mc.world, beam);
        });
        return null;
    }
}
