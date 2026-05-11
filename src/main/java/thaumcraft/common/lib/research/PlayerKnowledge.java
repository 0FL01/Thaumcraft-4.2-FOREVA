package thaumcraft.common.lib.research;

import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

public class PlayerKnowledge {

    /**
     * Check if a player has discovered a specific aspect.
     */
    public boolean hasDiscoveredAspect(String username, Aspect aspect) {
        // For online players, check capability directly
        // For offline, we'd need a cache (offline support deferred)
        if (aspect == null) return false;
        // The capability-based approach is used at runtime
        // This method exists for API compatibility
        return false;
    }

    /**
     * Check if a player has discovered a specific aspect (entity player variant).
     */
    public boolean hasDiscoveredAspect(EntityPlayer player, Aspect aspect) {
        if (player == null || aspect == null) return false;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        return knowledge != null && knowledge.hasDiscoveredAspect(aspect);
    }

    /**
     * Get the aspects a player has discovered.
     */
    public AspectList getAspectsDiscovered(String username) {
        return new AspectList();
    }

    /**
     * Get the aspects a player has discovered (entity player variant).
     */
    public AspectList getAspectsDiscovered(EntityPlayer player) {
        if (player == null) return new AspectList();
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            return knowledge.getAspectsDiscovered();
        }
        return new AspectList();
    }

    /**
     * Mark an aspect as discovered by a player.
     */
    public void addDiscoveredAspect(EntityPlayer player, Aspect aspect) {
        if (player == null || aspect == null) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.addDiscoveredAspect(aspect.getTag());
        }
    }
}
