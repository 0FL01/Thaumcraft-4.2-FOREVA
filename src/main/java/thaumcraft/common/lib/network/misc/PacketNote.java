package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.tiles.TileSensor;

public class PacketNote extends PacketBase {
    private int x;
    private int y;
    private int z;
    private int dim;
    private byte note;

    public PacketNote() {
    }

    public PacketNote(int x, int y, int z, int dim) {
        this(x, y, z, dim, (byte) -1);
    }

    public PacketNote(int x, int y, int z, int dim, byte note) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.note = note;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeInt(this.dim);
        buffer.writeByte(this.note);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.dim = buffer.readInt();
        this.note = buffer.readByte();
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            this.handleClient();
            return null;
        }
        if (this.note == -1) {
            this.scheduleServer(ctx, player -> {
                WorldServer world = DimensionManager.getWorld(this.dim);
                if (world == null) {
                    return;
                }
                TileEntity tile = world.getTileEntity(new BlockPos(this.x, this.y, this.z));
                byte syncedNote = -1;
                if (tile instanceof TileEntityNote) {
                    syncedNote = ((TileEntityNote) tile).note;
                } else if (tile instanceof TileSensor) {
                    syncedNote = ((TileSensor) tile).note;
                }
                if (syncedNote >= 0) {
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketNote(this.x, this.y, this.z, this.dim, syncedNote),
                            new NetworkRegistry.TargetPoint(this.dim, this.x, this.y, this.z, 8.0));
                }
            });
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handleClient() {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (this.note < 0 || world == null) {
                return;
            }
            TileEntity tile = world.getTileEntity(new BlockPos(this.x, this.y, this.z));
            if (tile instanceof TileEntityNote) {
                ((TileEntityNote) tile).note = this.note;
            } else if (tile instanceof TileSensor) {
                ((TileSensor) tile).note = this.note;
            }
        });
    }
}
