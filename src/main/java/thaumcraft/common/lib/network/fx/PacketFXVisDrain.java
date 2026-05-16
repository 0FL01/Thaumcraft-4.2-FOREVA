package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
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
            if (Minecraft.getMinecraft().world == null) return;
            double sx = this.to.getX() + 0.4 + Minecraft.getMinecraft().world.rand.nextFloat() * 0.2f;
            double sy = this.to.getY() + 0.4 + Minecraft.getMinecraft().world.rand.nextFloat() * 0.2f;
            double sz = this.to.getZ() + 0.4 + Minecraft.getMinecraft().world.rand.nextFloat() * 0.2f;
            double tx = this.from.getX() + Minecraft.getMinecraft().world.rand.nextFloat();
            double ty = this.from.getY() + Minecraft.getMinecraft().world.rand.nextFloat();
            double tz = this.from.getZ() + Minecraft.getMinecraft().world.rand.nextFloat();
            Thaumcraft.proxy.beam(Minecraft.getMinecraft().world, sx, sy, sz, tx, ty, tz, this.color, false, 12);
        });
        return null;
    }
}
