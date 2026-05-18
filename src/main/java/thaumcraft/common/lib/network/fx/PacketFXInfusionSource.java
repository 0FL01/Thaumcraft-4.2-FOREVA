package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.tiles.TileInfusionMatrix;
import thaumcraft.common.tiles.TilePedestal;

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
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            int sx = this.x - this.dx;
            int sy = this.y - this.dy;
            int sz = this.z - this.dz;
            String fxKey = sx + ":" + sy + ":" + sz + ":" + this.color;

            TileEntity matrixTile = world.getTileEntity(new BlockPos(this.x, this.y, this.z));
            if (!(matrixTile instanceof TileInfusionMatrix)) return;
            TileInfusionMatrix matrix = (TileInfusionMatrix) matrixTile;

            int ticks = 15;
            TileEntity sourceTile = world.getTileEntity(new BlockPos(sx, sy, sz));
            if (sourceTile instanceof TilePedestal) {
                ticks = 60;
            }

            TileInfusionMatrix.SourceFX sourceFx = matrix.sourceFX.get(fxKey);
            if (sourceFx != null) {
                sourceFx.ticks = ticks;
                matrix.sourceFX.put(fxKey, sourceFx);
            } else {
                matrix.sourceFX.put(fxKey, new TileInfusionMatrix.SourceFX(new BlockPos(sx, sy, sz), ticks, this.color));
            }
        });
        return null;
    }
}
