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

public class PacketResearchComplete extends PacketBase {
    private String researchKey;

    public PacketResearchComplete() {}

    public PacketResearchComplete(String researchKey) {
        this.researchKey = researchKey;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        researchKey = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, researchKey);
    }

    public String getResearchKey() {
        return researchKey;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            if (player != null && researchKey != null && !researchKey.isEmpty()) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.addResearch(researchKey);
                }
            }
        });
        return null;
    }
}
