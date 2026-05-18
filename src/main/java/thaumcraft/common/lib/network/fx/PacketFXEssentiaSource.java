package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.events.EssentiaHandler;
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
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            int tx = this.x - this.dx;
            int ty = this.y - this.dy;
            int tz = this.z - this.dz;
            String key = this.x + ":" + this.y + ":" + this.z + ":" + tx + ":" + ty + ":" + tz + ":" + this.color;
            EssentiaHandler.EssentiaSourceFX sourceFx = EssentiaHandler.sourceFX.get(key);
            if (sourceFx != null) {
                sourceFx.ticks = 15;
                EssentiaHandler.sourceFX.put(key, sourceFx);
            } else {
                EssentiaHandler.sourceFX.put(
                        key,
                        new EssentiaHandler.EssentiaSourceFX(
                                new BlockPos(this.x, this.y, this.z),
                                new BlockPos(tx, ty, tz),
                                15,
                                this.color));
            }
        });
        return null;
    }
}
