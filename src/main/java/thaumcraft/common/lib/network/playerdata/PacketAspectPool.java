package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.common.lib.network.PacketBase;

public class PacketAspectPool extends PacketBase {
    private String aspectTag;
    private short amount;
    private int total;

    public PacketAspectPool() {}

    public PacketAspectPool(String aspectTag, short amount, int total) {
        this.aspectTag = aspectTag;
        this.amount = amount;
        this.total = total;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.aspectTag = ByteBufUtils.readUTF8String(buf);
        this.amount = buf.readShort();
        this.total = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.aspectTag);
        buf.writeShort(this.amount);
        buf.writeInt(this.total);
    }

    public String getAspectTag() { return aspectTag; }
    public short getAmount() { return amount; }
    public int getTotal() { return total; }
}
