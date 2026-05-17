package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.beams.FXBeam;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXVisDrain extends PacketBase {

    private BlockPos from;
    private BlockPos to;
    private int color;

    public PacketFXVisDrain() {}

    public PacketFXVisDrain(BlockPos from, BlockPos to, int color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.from.toLong());
        buf.writeLong(this.to.toLong());
        buf.writeInt(this.color);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.from = BlockPos.fromLong(buf.readLong());
        this.to = BlockPos.fromLong(buf.readLong());
        this.color = buf.readInt();
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
            double sx = this.to.getX() + 0.4D + mc.world.rand.nextFloat() * 0.2F;
            double sy = this.to.getY() + 0.4D + mc.world.rand.nextFloat() * 0.2F;
            double sz = this.to.getZ() + 0.4D + mc.world.rand.nextFloat() * 0.2F;
            double tx = this.from.getX() + mc.world.rand.nextFloat();
            double ty = this.from.getY() + mc.world.rand.nextFloat();
            double tz = this.from.getZ() + mc.world.rand.nextFloat();
            FXBeam beam = new FXBeam(mc.world, sx, sy, sz, tx, ty, tz, red, green, blue, 12, false, 14);
            beam.setType(2);
            beam.setReverse(true);
            beam.setPulse(true);
            ParticleEngine.addEffect(mc.world, beam);
        });
        return null;
    }
}
