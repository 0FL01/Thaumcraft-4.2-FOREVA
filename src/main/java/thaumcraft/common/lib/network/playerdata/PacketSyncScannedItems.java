package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.common.lib.network.PacketBase;

import java.util.HashSet;
import java.util.Set;

public class PacketSyncScannedItems extends PacketBase {
    private Set<String> scannedItems;

    public PacketSyncScannedItems() {}

    public PacketSyncScannedItems(Set<String> scannedItems) {
        this.scannedItems = scannedItems;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        scannedItems = new HashSet<>();
        for (int i = 0; i < count; i++) {
            scannedItems.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (scannedItems == null) {
            buf.writeInt(0);
            return;
        }
        buf.writeInt(scannedItems.size());
        for (String s : scannedItems) {
            ByteBufUtils.writeUTF8String(buf, s);
        }
    }

    public Set<String> getScannedItems() {
        return scannedItems;
    }
}
