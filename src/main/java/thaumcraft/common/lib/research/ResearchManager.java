package thaumcraft.common.lib.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;

import java.util.HashMap;
import java.util.Map;

public class ResearchManager {

    // Cache of player research data (server-side)
    private static final Map<String, IPlayerKnowledge> playerDataCache = new HashMap<>();

    /**
     * Check if a player has completed a specific research.
     */
    public static boolean isResearchComplete(String username, String researchkey) {
        // Look up via capabilities if player is online
        EntityPlayer player = findPlayer(username);
        if (player != null) {
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null) {
                return knowledge.isResearchComplete(researchkey);
            }
        }
        // Fall back to cache
        IPlayerKnowledge cached = playerDataCache.get(username);
        return cached != null && cached.isResearchComplete(researchkey);
    }

    /**
     * Check if a player has completed a specific research (EntityPlayer variant).
     */
    public static boolean isResearchComplete(EntityPlayer player, String researchkey) {
        if (player == null) return false;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            return knowledge.isResearchComplete(researchkey);
        }
        return false;
    }

    /**
     * Mark a research as complete for a player.
     */
    public static void addResearch(EntityPlayer player, String researchkey) {
        if (player == null || researchkey == null) return;

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null && !knowledge.isResearchComplete(researchkey)) {
            knowledge.addResearch(researchkey);

            // Sync to client
            if (!player.getEntityWorld().isRemote && player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(
                        new PacketResearchComplete(researchkey),
                        (EntityPlayerMP) player
                );
            }

            // Trigger research completion callbacks
            triggerResearchComplete(player, researchkey);
        }
    }

    /**
     * Trigger callbacks when research is completed (unlock recipes, etc.)
     */
    private static void triggerResearchComplete(EntityPlayer player, String researchkey) {
        // Look up research item and fire its onResearchComplete
        for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {
            ResearchItem item = category.research.get(researchkey);
            if (item != null) {
                if (item.onResearchComplete != null) {
                    item.onResearchComplete.accept(player, researchkey);
                }
                break;
            }
        }
    }

    /**
     * Get the player's research data.
     */
    public static IPlayerKnowledge getResearchData(EntityPlayer player) {
        if (player == null) return null;
        return player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
    }

    /**
     * Refresh the cache for a player.
     */
    public static void updateCache(String username, IPlayerKnowledge data) {
        if (username != null && data != null) {
            playerDataCache.put(username, data);
        }
    }

    /**
     * Find an online player by username.
     */
    private static EntityPlayer findPlayer(String username) {
        // This will be checked via the capability system at runtime
        // For offline players, we rely on the cache
        return null;
    }
}
