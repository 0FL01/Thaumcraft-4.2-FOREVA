package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXBlockArc extends PacketBase {

    private int x;
    private int y;
    private int z;
    private int entityId;

    public PacketFXBlockArc() {}

    public PacketFXBlockArc(int x, int y, int z, int entityId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.entityId = buf.readInt();
    }
}
