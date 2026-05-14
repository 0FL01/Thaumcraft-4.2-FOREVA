package thaumcraft.common.lib.research;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

            int warp = ThaumcraftApi.getWarp(researchkey);
            if (warp > 0 && !Config.wuss && !player.getEntityWorld().isRemote) {
                if (warp > 1) {
                    int sticky = warp / 2;
                    int permanent = warp - sticky;
                    if (permanent > 0) Thaumcraft.addWarpToPlayer(player, permanent, false);
                    if (sticky > 0) Thaumcraft.addStickyWarpToPlayer(player, sticky);
                } else {
                    Thaumcraft.addWarpToPlayer(player, warp, false);
                }
            }

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

        IPlayerKnowledge cached = playerDataCache.get(key);
        if (cached != null) return cached;

        cached = loadFreshCapabilityFromPlayerData(username);
        if (cached != null) {
            playerDataCache.put(key, cached);
        }
        return cached;
    }

    /**
     * Refresh the cache for a player.
     */
    public static void updateCache(String username, IPlayerKnowledge data) {
        String key = normalizeUsername(username);
        if (key != null && data != null) {
            playerDataCache.put(key, copyKnowledge(data));
        }
    }

    public static void updateCache(EntityPlayer player) {
        if (player == null) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        updateCache(player.getName(), knowledge);
    }

    public static void initializeFreshPlayerData(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null || knowledge.hasInitializedAspects()) return;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (aspect != null) {
                knowledge.setAspectPool(aspect, 15 + player.world.rand.nextInt(5));
            }
        }
        knowledge.setInitializedAspects(true);
        updateCache(player.getName(), knowledge);
    }

    public static boolean completeResearchUnsaved(String username, String key) {
        IPlayerKnowledge knowledge = getResearchData(username);
        if (knowledge == null || key == null || knowledge.isResearchComplete(key)) return false;
        knowledge.addResearch(key);
        updateCache(username, knowledge);
        return true;
    }

    public static boolean completeAspectUnsaved(String username, Aspect aspect, short amount) {
        IPlayerKnowledge knowledge = getResearchData(username);
        if (knowledge == null || aspect == null) return false;
        boolean changed = knowledge.setAspectPool(aspect, amount);
        updateCache(username, knowledge);
        return changed;
    }

    public void completeAspect(EntityPlayer player, Aspect aspect, short amount) {
        if (player == null || aspect == null) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null && knowledge.setAspectPool(aspect, amount)) {
            updateCache(player.getName(), knowledge);
        }
    }

    public static boolean completeScannedObjectUnsaved(String username, String object) {
        IPlayerKnowledge knowledge = getResearchData(username);
        if (knowledge == null || object == null) return false;
        boolean changed = !knowledge.hasScannedItem(object);
        knowledge.scanItem(object);
        updateCache(username, knowledge);
        return changed;
    }

    public static boolean completeScannedEntityUnsaved(String username, String key) {
        IPlayerKnowledge knowledge = getResearchData(username);
        if (knowledge == null || key == null) return false;
        boolean changed = !knowledge.hasScannedEntity(key);
        knowledge.scanEntity(key);
        updateCache(username, knowledge);
        return changed;
    }

    public static boolean completeScannedPhenomenaUnsaved(String username, String key) {
        IPlayerKnowledge knowledge = getResearchData(username);
        if (knowledge == null || key == null) return false;
        boolean changed = !knowledge.hasScannedPhenomena(key);
        knowledge.scanPhenomena(key);
        updateCache(username, knowledge);
        return changed;
    }

    public void completeScannedObject(EntityPlayer player, String object) {
        if (player == null || object == null) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.scanItem(object);
            updateCache(player.getName(), knowledge);
        }
    }

    public void completeScannedEntity(EntityPlayer player, String key) {
        if (player == null || key == null) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.scanEntity(key);
            updateCache(player.getName(), knowledge);
        }
    }

    public void completeScannedPhenomena(EntityPlayer player, String key) {
        if (player == null || key == null) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.scanPhenomena(key);
            updateCache(player.getName(), knowledge);
        }
    }

    public static ArrayList<String> getResearchForPlayer(String username) {
        IPlayerKnowledge knowledge = getResearchData(username);
        return knowledge == null ? new ArrayList<>() : new ArrayList<>(knowledge.getResearchComplete());
    }

    public static ArrayList<String> getResearchForPlayerSafe(String username) {
        String key = normalizeUsername(username);
        IPlayerKnowledge knowledge = key == null ? null : playerDataCache.get(key);
        return knowledge == null ? new ArrayList<>() : new ArrayList<>(knowledge.getResearchComplete());
    }

    public static boolean doesPlayerHaveRequisites(String username, String key) {
        ResearchItem item = ResearchCategories.getResearch(key);
        if (item == null) return false;
        Set<String> completed = new HashSet<>(getResearchForPlayer(username));
        if (item.parents != null) {
            for (String parent : item.parents) {
                if (!completed.contains(parent)) return false;
            }
        }
        if (item.parentsHidden != null) {
            for (String parent : item.parentsHidden) {
                if (!completed.contains(parent)) return false;
            }
        }
        return true;
    }

    public static Aspect getCombinationResult(Aspect aspect1, Aspect aspect2) {
        for (Aspect aspect : Aspect.aspects.values()) {
            Aspect[] components = aspect.getComponents();
            if (components != null && components.length == 2
                    && ((components[0] == aspect1 && components[1] == aspect2)
                    || (components[0] == aspect2 && components[1] == aspect1))) {
                return aspect;
            }
        }
        return null;
    }

    public static AspectList reduceToPrimals(AspectList al) {
        return reduceToPrimals(al, false);
    }

    public static AspectList reduceToPrimals(AspectList al, boolean merge) {
        AspectList out = new AspectList();
        if (al == null) return out;
        for (Aspect aspect : al.getAspects()) {
            if (aspect == null) continue;
            if (aspect.isPrimal()) {
                if (merge) out.merge(aspect, al.getAmount(aspect));
                else out.add(aspect, al.getAmount(aspect));
            } else {
                AspectList parents = new AspectList();
                parents.add(aspect.getComponents()[0], al.getAmount(aspect));
                parents.add(aspect.getComponents()[1], al.getAmount(aspect));
                out.add(reduceToPrimals(parents, merge));
            }
        }
        return out;
    }

    public static void syncWarp(EntityPlayer player) {
        if (player == null || player.world.isRemote || !(player instanceof EntityPlayerMP)) return;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return;
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(knowledge.getWarpPerm(), knowledge.getWarpSticky(), knowledge.getWarpTemp(), knowledge.getWarpCounter()), (EntityPlayerMP)player);
        updateCache(player.getName(), knowledge);
    }

    private static IPlayerKnowledge copyKnowledge(IPlayerKnowledge source) {
        PlayerKnowledgeCapability copy = new PlayerKnowledgeCapability();
        copy.deserializeNBT(source.serializeNBT());
        return copy;
    }

    private static IPlayerKnowledge loadFreshCapabilityFromPlayerData(String username) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || username == null || server.getPlayerProfileCache() == null) return null;

        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
        if (profile == null || profile.getId() == null) return null;

        File playerData = getCurrentServerPlayerDataFile(server, profile);
        if (playerData == null || !playerData.isFile()) return null;

        try (FileInputStream in = new FileInputStream(playerData)) {
            NBTTagCompound root = CompressedStreamTools.readCompressed(in);
            if (root == null || !root.hasKey("ForgeCaps")) return null;
            NBTTagCompound caps = root.getCompoundTag("ForgeCaps");
            String capKey = EventHandlerEntity.PLAYER_KNOWLEDGE_KEY.toString();
            if (!caps.hasKey(capKey)) return null;
            PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
            knowledge.deserializeNBT(caps.getCompoundTag(capKey));
            return knowledge;
        } catch (IOException ignored) {
            return null;
        }
    }

    private static File getCurrentServerPlayerDataFile(MinecraftServer server, GameProfile profile) {
        ISaveHandler saveHandler = server.getEntityWorld() == null ? null : server.getEntityWorld().getSaveHandler();
        if (!(saveHandler instanceof SaveHandler)) return null;
        File worldDir = ((SaveHandler)saveHandler).getWorldDirectory();
        return new File(new File(worldDir, "playerdata"), profile.getId().toString() + ".dat");
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
