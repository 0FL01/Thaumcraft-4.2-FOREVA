package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
    public IMessage onMessage(MessageContext ctx) {
        // Client-side FX handled in Phase 8 (ParticleEngine)
        return null;
    }
}
