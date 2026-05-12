package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.tiles.TileResearchTable;

public class PacketAspectPlaceToServer extends PacketBase {
    private int dim;
    private int playerid;
    private int x;
    private int y;
    private int z;
    private Aspect aspect;
    private byte q;
    private byte r;

    public PacketAspectPlaceToServer() {}

    public PacketAspectPlaceToServer(EntityPlayer player, byte q, byte r, int x, int y, int z, Aspect aspect) {
        this.dim = player.world.provider.getDimension();
        this.playerid = player.getEntityId();
        this.x = x;
        this.y = y;
        this.z = z;
        this.aspect = aspect;
        this.q = q;
        this.r = r;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dim = buf.readInt();
        this.playerid = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        String tag = ByteBufUtils.readUTF8String(buf);
        this.aspect = "null".equals(tag) || tag.isEmpty() ? null : Aspect.getAspect(tag);
        this.q = buf.readByte();
        this.r = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dim);
        buf.writeInt(this.playerid);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        ByteBufUtils.writeUTF8String(buf, this.aspect == null ? "null" : this.aspect.getTag());
        buf.writeByte(this.q);
        buf.writeByte(this.r);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (player.getEntityId() != this.playerid) return;
            if (player.world.provider.getDimension() != this.dim) return;
            TileEntity tile = player.world.getTileEntity(new BlockPos(this.x, this.y, this.z));
            if (tile instanceof TileResearchTable) {
                tile.markDirty();
            }
        });
        return null;
    }
}
