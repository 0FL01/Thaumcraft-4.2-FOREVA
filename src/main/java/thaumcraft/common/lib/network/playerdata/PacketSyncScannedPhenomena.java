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

public class PacketSyncScannedPhenomena extends PacketBase {
    private Set<String> scannedPhenomena;

    public PacketSyncScannedPhenomena() {}

    public PacketSyncScannedPhenomena(Set<String> scannedPhenomena) {
        this.scannedPhenomena = scannedPhenomena == null ? new HashSet<>() : new HashSet<>(scannedPhenomena);
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

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            if (player != null && scannedPhenomena != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.getScannedPhenomena().clear();
                    for (String key : scannedPhenomena) {
                        knowledge.scanPhenomena(key);
                    }
                }
            }
        });
        return null;
    }
}
