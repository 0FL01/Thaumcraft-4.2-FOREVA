package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
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
        Thaumcraft.proxy.scheduleClientTask(() -> {
            Thaumcraft.proxy.visDrainFx(Thaumcraft.proxy.getClientWorld(), this.from, this.to, this.color);
        });
        return null;
    }
}
