package thaumcraft.client.integration.jei;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

final class ResearchVisibilityTicker {
    private final ResearchVisibility<ThaumcraftRecipeWrapper> visibility;

    ResearchVisibilityTicker(ResearchVisibility<ThaumcraftRecipeWrapper> visibility) {
        this.visibility = visibility;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        Set<String> completed = null;
        if (minecraft.player != null && PlayerKnowledgeProvider.PLAYER_KNOWLEDGE != null) {
            IPlayerKnowledge knowledge = minecraft.player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null) {
                completed = new HashSet<String>(knowledge.getResearchComplete());
            }
        }
        this.visibility.update(minecraft.player, minecraft.world, completed);
    }
}
