package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketBase;

import java.util.HashSet;
import java.util.Set;

public class PacketSyncScannedItems extends PacketBase {
    private Set<String> scannedItems;

    public PacketSyncScannedItems() {}

    public PacketSyncScannedItems(Set<String> scannedItems) {
        this.scannedItems = scannedItems == null ? new HashSet<>() : new HashSet<>(scannedItems);
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

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            if (player != null && scannedItems != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.getScannedItems().clear();
                    for (String key : scannedItems) {
                        knowledge.scanItem(key);
                    }
                }
            }
        });
        return null;
    }
}
