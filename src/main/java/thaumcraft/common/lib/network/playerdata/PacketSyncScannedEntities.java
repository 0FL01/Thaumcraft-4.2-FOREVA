package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.common.lib.network.PacketBase;

import java.util.HashSet;
import java.util.Set;

public class PacketSyncScannedEntities extends PacketBase {
    private Set<String> scannedEntities;

    public PacketSyncScannedEntities() {}

    public PacketSyncScannedEntities(Set<String> scannedEntities) {
        this.scannedEntities = scannedEntities;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        scannedEntities = new HashSet<>();
        for (int i = 0; i < count; i++) {
            scannedEntities.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (scannedEntities == null) {
            buf.writeInt(0);
            return;
        }
        buf.writeInt(scannedEntities.size());
        for (String s : scannedEntities) {
            ByteBufUtils.writeUTF8String(buf, s);
        }
    }

    public Set<String> getScannedEntities() {
        return scannedEntities;
    }
}
