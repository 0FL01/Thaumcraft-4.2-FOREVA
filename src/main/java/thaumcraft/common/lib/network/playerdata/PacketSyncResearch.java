package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.common.lib.network.PacketBase;

import java.util.HashSet;
import java.util.Set;

public class PacketSyncResearch extends PacketBase {
    private Set<String> research;

    public PacketSyncResearch() {}

    public PacketSyncResearch(Set<String> research) {
        this.research = research;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        research = new HashSet<>();
        for (int i = 0; i < count; i++) {
            research.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (research == null) {
            buf.writeInt(0);
            return;
        }
        buf.writeInt(research.size());
        for (String s : research) {
            ByteBufUtils.writeUTF8String(buf, s);
        }
    }

    public Set<String> getResearch() {
        return research;
    }
}
