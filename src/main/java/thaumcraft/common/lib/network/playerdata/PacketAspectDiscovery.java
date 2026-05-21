package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketBase;

public class PacketAspectDiscovery extends PacketBase {
    private String aspectTag;

    public PacketAspectDiscovery() {}

    public PacketAspectDiscovery(String aspectTag) {
        this.aspectTag = aspectTag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        aspectTag = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, aspectTag == null ? "" : aspectTag);
    }

    public String getAspectTag() {
        return aspectTag;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (player != null && aspect != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.addDiscoveredAspect(aspect.getTag());
                }
                Thaumcraft.proxy.notifyThaumometerAspectDiscovery(aspect);
                if (player.world != null) {
                    player.world.playSound(
                            player,
                            player.posX, player.posY, player.posZ,
                            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                            SoundCategory.PLAYERS,
                            0.2F,
                            0.5F + player.world.rand.nextFloat() * 0.2F);
                }
            }
        });
        return null;
    }
}
