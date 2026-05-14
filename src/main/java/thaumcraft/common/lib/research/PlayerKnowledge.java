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
        if (aspect == null) return false;
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        return knowledge != null && knowledge.hasDiscoveredAspect(aspect);
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
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        return knowledge != null ? knowledge.getAspectsDiscovered() : new AspectList();
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

    public boolean hasDiscoveredParentAspects(String username, Aspect aspect) {
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        return knowledge != null && knowledge.hasDiscoveredParentAspects(aspect);
    }

    public boolean hasDiscoveredParentAspects(EntityPlayer player, Aspect aspect) {
        if (player == null) return false;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        return knowledge != null && knowledge.hasDiscoveredParentAspects(aspect);
    }

    public void addDiscoveredPrimalAspects(String username) {
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        if (knowledge != null) knowledge.addDiscoveredPrimalAspects();
    }

    public short getAspectPoolFor(String username, Aspect aspect) {
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        return knowledge != null ? (short)knowledge.getAspectPoolFor(aspect) : 0;
    }

    public boolean addAspectPool(String username, Aspect aspect, short amount) {
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        if (knowledge == null) return false;
        boolean changed = knowledge.addAspectPool(aspect, amount);
        ResearchManager.updateCache(username, knowledge);
        return changed;
    }

    public boolean setAspectPool(String username, Aspect aspect, short amount) {
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        if (knowledge == null) return false;
        boolean changed = knowledge.setAspectPool(aspect, amount);
        ResearchManager.updateCache(username, knowledge);
        return changed;
    }

    public void wipePlayerKnowledge(String username) {
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(username);
        if (knowledge != null) {
            knowledge.reset();
            ResearchManager.updateCache(username, knowledge);
        }
    }
}
