package thaumcraft.common.lib.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResearchManager {

    // Cache of player research data (server-side)
    private static final Map<String, IPlayerKnowledge> playerDataCache = new HashMap<>();

    /**
     * Check if a player has completed a specific research.
     */
    public static boolean isResearchComplete(String username, String researchkey) {
        IPlayerKnowledge knowledge = getResearchData(username);
        return knowledge != null && knowledge.isResearchComplete(researchkey);
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
            updateCache(player.getName(), knowledge);
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
     * Get online player capability data by username, falling back to the local server cache.
     */
    public static IPlayerKnowledge getResearchData(String username) {
        String key = normalizeUsername(username);
        if (key == null) return null;

        EntityPlayer player = findPlayer(username);
        if (player != null) {
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null) {
                updateCache(username, knowledge);
                return knowledge;
            }
        }

        return playerDataCache.get(key);
    }

    /**
     * Refresh the cache for a player.
     */
    public static void updateCache(String username, IPlayerKnowledge data) {
        String key = normalizeUsername(username);
        if (key != null && data != null) {
            playerDataCache.put(key, data);
        }
    }

    /**
     * Find an online player by username.
     */
    static EntityPlayer findPlayer(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || server.getPlayerList() == null) return null;

        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(username);
        if (player != null) return player;

        for (EntityPlayerMP candidate : server.getPlayerList().getPlayers()) {
            if (candidate != null && candidate.getName().equalsIgnoreCase(username)) {
                return candidate;
            }
        }
        return null;
    }

    private static String normalizeUsername(String username) {
        if (username == null) return null;
        String trimmed = username.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase(Locale.ROOT);
    }
}
