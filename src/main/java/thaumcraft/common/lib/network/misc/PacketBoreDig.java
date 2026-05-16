package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.tiles.TileArcaneBore;

public class PacketBoreDig extends PacketBase {
    private int x;
    private int y;
    private int z;
    private int digloc;

    public PacketBoreDig() {}

    public PacketBoreDig(int x, int y, int z, int digloc) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.digloc = digloc;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.digloc);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.digloc = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(new net.minecraft.util.math.BlockPos(this.x, this.y, this.z));
            if (tile instanceof TileArcaneBore) {
                ((TileArcaneBore) tile).getDigEvent(this.digloc);
            }
        });
        return null;
    }
}
