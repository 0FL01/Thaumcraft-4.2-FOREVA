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

public class PacketSyncScannedEntities extends PacketBase {
    private Set<String> scannedEntities;

    public PacketSyncScannedEntities() {}

    public PacketSyncScannedEntities(Set<String> scannedEntities) {
        this.scannedEntities = scannedEntities == null ? new HashSet<>() : new HashSet<>(scannedEntities);
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

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            if (player != null && scannedEntities != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.getScannedEntities().clear();
                    for (String key : scannedEntities) {
                        knowledge.scanEntity(key);
                    }
                }
            }
        });
        return null;
    }
}
