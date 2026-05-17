package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.beams.FXBeam;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXInfusionSource extends PacketBase {
    private int x;
    private int y;
    private int z;
    private byte dx;
    private byte dy;
    private byte dz;
    private int color;

    public PacketFXInfusionSource() {}

    public PacketFXInfusionSource(int x, int y, int z, byte dx, byte dy, byte dz, int color) {
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
            double sx = this.x + mc.world.rand.nextFloat() * 0.5F + 0.25F;
            double sy = this.y + mc.world.rand.nextFloat() * 0.5F + 0.25F;
            double sz = this.z + mc.world.rand.nextFloat() * 0.5F + 0.25F;
            float red = 0.6F;
            float green = 0.8F;
            float blue = 1.0F;

            if (this.dx == 0 && this.dy == 0 && this.dz == 0 && this.color > 0) {
                Entity target = mc.world.getEntityByID(this.color);
                if (target == null) return;
                FXBeam beam = new FXBeam(
                        mc.world,
                        target.posX,
                        target.getEntityBoundingBox().minY + target.height * 0.5,
                        target.posZ,
                        sx,
                        sy,
                        sz,
                        0.8F,
                        0.4F,
                        1.0F,
                        8,
                        true,
                        8);
                beam.setType(1);
                beam.setReverse(true);
                beam.setPulse(true);
                ParticleEngine.addEffect(mc.world, beam);
                return;
            }

            double tx = this.x - this.dx + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            double ty = this.y - this.dy + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            double tz = this.z - this.dz + mc.world.rand.nextFloat() * 0.6F + 0.2F;
            FXBeam beam = new FXBeam(mc.world, tx, ty, tz, sx, sy, sz, red, green, blue, 8, true, 12);
            beam.setType(1);
            beam.setPulse(true);
            ParticleEngine.addEffect(mc.world, beam);
        });
        return null;
    }
}
