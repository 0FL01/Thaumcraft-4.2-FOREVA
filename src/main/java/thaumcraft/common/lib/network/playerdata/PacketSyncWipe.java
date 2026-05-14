package thaumcraft.common.lib.network.playerdata;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketBase;

public class PacketSyncWipe extends PacketBase {
    public PacketSyncWipe() {}

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.reset();
                }
            }
        });
        return null;
    }
}
