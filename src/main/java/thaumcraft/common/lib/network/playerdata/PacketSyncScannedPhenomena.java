package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.common.lib.network.PacketBase;

import java.util.HashSet;
import java.util.Set;

public class PacketSyncScannedPhenomena extends PacketBase {
    private Set<String> scannedPhenomena;

    public PacketSyncScannedPhenomena() {}

    public PacketSyncScannedPhenomena(Set<String> scannedPhenomena) {
        this.scannedPhenomena = scannedPhenomena;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        scannedPhenomena = new HashSet<>();
        for (int i = 0; i < count; i++) {
            scannedPhenomena.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (scannedPhenomena == null) {
            buf.writeInt(0);
            return;
        }
        buf.writeInt(scannedPhenomena.size());
        for (String s : scannedPhenomena) {
            ByteBufUtils.writeUTF8String(buf, s);
        }
    }

    public Set<String> getScannedPhenomena() {
        return scannedPhenomena;
    }
}
