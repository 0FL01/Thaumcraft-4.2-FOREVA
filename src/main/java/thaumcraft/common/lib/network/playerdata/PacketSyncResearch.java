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

public class PacketSyncResearch extends PacketBase {
    private Set<String> research;

    public PacketSyncResearch() {}

    public PacketSyncResearch(Set<String> research) {
        this.research = research == null ? new HashSet<>() : new HashSet<>(research);
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

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            if (player != null && research != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.getResearchComplete().clear();
                    for (String key : research) {
                        knowledge.addResearch(key);
                    }
                }
            }
        });
        return null;
    }
}
