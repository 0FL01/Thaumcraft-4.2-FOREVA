package thaumcraft.common.lib.research;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.IScribeTools;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ResearchManager {

    // Cache of player research data (server-side)
    private static final Map<String, IPlayerKnowledge> playerDataCache = new HashMap<>();
    private static List<ResearchItem> allHiddenResearch;
    private static List<ResearchItem> allValidResearch;

    public static class HexEntry {
        public Aspect aspect;
        public int type;

        public HexEntry(Aspect aspect, int type) {
            this.aspect = aspect;
            this.type = type;
        }
    }

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

            // Sync to client.
            // Guard against null connection: during PlayerEvent.LoadFromFile the
            // network handler is not yet set up, so sendTo would NPE.
            if (!player.getEntityWorld().isRemote && player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP) player;
                if (mp.connection != null) {
                    PacketHandler.INSTANCE.sendTo(
                            new PacketResearchComplete(researchkey),
                            mp
                    );
                }
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
        return doesPlayerHaveRequisites(completed, item);
    }

    public static boolean doesPlayerHaveRequisites(EntityPlayer player, String key) {
        if (player == null) return false;
        IPlayerKnowledge knowledge = getResearchData(player);
        if (knowledge == null) return false;
        ResearchItem item = ResearchCategories.getResearch(key);
        if (item == null) return false;
        Set<String> completed = new HashSet<>(knowledge.getResearchComplete());
        return doesPlayerHaveRequisites(completed, item);
    }

    private static boolean doesPlayerHaveRequisites(Set<String> completed, ResearchItem item) {
        if (completed == null || item == null) return false;
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

    public static ItemStack createResearchNoteForPlayer(World world, EntityPlayer player, String key) {
        if (world == null || player == null || key == null || key.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int existing = getResearchSlot(player, key);
        if (existing >= 0) {
            return player.inventory.getStackInSlot(existing);
        }
        if (!consumeInkFromPlayer(player, false)) {
            return ItemStack.EMPTY;
        }
        if (!player.inventory.hasItemStack(new ItemStack(Items.PAPER))) {
            return ItemStack.EMPTY;
        }

        ItemStack note = createNote(new ItemStack(ConfigItems.itemResearchNotes), key, world);
        if (note.isEmpty()) {
            return ItemStack.EMPTY;
        }

        consumeInkFromPlayer(player, true);
        player.inventory.clearMatchingItems(Items.PAPER, -1, 1, null);

        if (!player.inventory.addItemStackToInventory(note.copy())) {
            player.dropItem(note.copy(), false);
        }
        player.inventoryContainer.detectAndSendChanges();
        return note;
    }

    public static int getResearchSlot(EntityPlayer player, String key) {
        if (player == null || key == null || key.isEmpty()) {
            return -1;
        }
        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (stack.isEmpty() || stack.getItem() != ConfigItems.itemResearchNotes) {
                continue;
            }
            ResearchNoteData data = getData(stack);
            if (data != null && key.equals(data.key)) {
                return slot;
            }
        }
        return -1;
    }

    public static boolean consumeInkFromPlayer(EntityPlayer player, boolean doit) {
        if (player == null) {
            return false;
        }
        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof IScribeTools)) {
                continue;
            }
            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                continue;
            }
            if (doit) {
                stack.damageItem(1, player);
                if (stack.getCount() <= 0) {
                    player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
                }
            }
            return true;
        }
        return false;
    }

    public static boolean consumeInkFromTable(ItemStack stack, boolean doit) {
        if (stack != null
                && !stack.isEmpty()
                && stack.getItem() instanceof IScribeTools
                && stack.getItemDamage() < stack.getMaxDamage()) {
            if (doit) {
                stack.setItemDamage(stack.getItemDamage() + 1);
            }
            return true;
        }
        return false;
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

    public static String findHiddenResearch(EntityPlayer player) {
        if (player == null || player.world == null) return "FAIL";
        if (allHiddenResearch == null) {
            allHiddenResearch = new ArrayList<>();
            for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {
                for (ResearchItem research : category.research.values()) {
                    if (research != null && research.isHidden() && hasUsableResearchTags(research)) {
                        allHiddenResearch.add(research);
                    }
                }
            }
        }

        ArrayList<String> candidates = new ArrayList<>();
        for (ResearchItem research : allHiddenResearch) {
            if (research == null) continue;
            if (isResearchComplete(player, research.key)) continue;
            if (!doesPlayerHaveRequisites(player, research.key)) continue;
            if (research.getItemTriggers() == null && research.getEntityTriggers() == null && research.getAspectTriggers() == null) {
                continue;
            }
            candidates.add(research.key);
        }
        if (candidates.isEmpty()) return "FAIL";
        int pick = new java.util.Random(player.world.getTotalWorldTime() / 50L).nextInt(candidates.size());
        return candidates.get(pick);
    }

    public static String findMatchingResearch(EntityPlayer player, Aspect aspect) {
        if (player == null || player.world == null || aspect == null) return null;
        if (allValidResearch == null) {
            allValidResearch = new ArrayList<>();
            for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {
                for (ResearchItem research : category.research.values()) {
                    boolean secondary = research.isSecondary() && (Config.researchDifficulty == 0 || Config.researchDifficulty == -1);
                    if (secondary || research.isHidden() || research.isLost() || research.isAutoUnlock()
                            || research.isVirtual() || research.isStub()) {
                        continue;
                    }
                    allValidResearch.add(research);
                }
            }
        }
        ArrayList<String> keys = new ArrayList<>();
        for (ResearchItem research : allValidResearch) {
            if (isResearchComplete(player, research.key)) continue;
            if (!doesPlayerHaveRequisites(player, research.key)) continue;
            if (research.tags.getAmount(aspect) <= 0) continue;
            keys.add(research.key);
        }
        if (keys.isEmpty()) return null;
        return keys.get(player.world.rand.nextInt(keys.size()));
    }

    /**
     * Grant a hidden/lost research clue entry (@KEY) from a scan clue object + awarded aspects.
     */
    public static boolean createClue(World world, EntityPlayer player, Object clue, AspectList aspects) {
        if (world == null || player == null) return false;
        ArrayList<String> keys = new ArrayList<>();
        for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {
            clueCheck:
            for (ResearchItem research : category.research.values()) {
                if (research == null || research.tags == null || research.tags.size() <= 0) continue;
                if (!research.isHidden() && !research.isLost()) continue;
                if (isResearchComplete(player, research.key)) continue;
                if (isResearchComplete(player, "@" + research.key)) continue;

                if (clue instanceof ItemStack) {
                    ItemStack[] triggers = research.getItemTriggers();
                    if (triggers != null && triggers.length > 0) {
                        for (ItemStack trigger : triggers) {
                            if (!InventoryUtils.areItemStacksEqual(trigger, (ItemStack) clue, true, true, false)) continue;
                            keys.add(research.key);
                            continue clueCheck;
                        }
                    }
                } else if (clue instanceof String) {
                    String[] triggers = research.getEntityTriggers();
                    if (triggers != null && triggers.length > 0) {
                        for (String trigger : triggers) {
                            if (!entityTriggerMatches(trigger, (String) clue)) continue;
                            keys.add(research.key);
                            continue clueCheck;
                        }
                    }
                }

                Aspect[] aspectTriggers = research.getAspectTriggers();
                if (aspects == null || aspects.size() <= 0 || aspectTriggers == null || aspectTriggers.length <= 0) continue;
                for (Aspect aspect : aspectTriggers) {
                    if (aspect == null || aspects.getAmount(aspect) <= 0) continue;
                    keys.add(research.key);
                    continue clueCheck;
                }
            }
        }

        if (keys.isEmpty()) return false;
        String key = keys.get(world.rand.nextInt(keys.size()));
        addResearch(player, "@" + key);
        return true;
    }

    public static ItemStack createNote(ItemStack stack, String key, World world) {
        if (stack == null || stack.isEmpty() || key == null || key.isEmpty()) return ItemStack.EMPTY;
        ResearchItem research = ResearchCategories.getResearch(key);
        Aspect primary = getResearchPrimaryTag(research);
        if (research == null || research.tags == null || research.tags.size() <= 0 || primary == null || world == null) return ItemStack.EMPTY;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = stack.getTagCompound();
        tag.setString("key", key);
        tag.setInteger("color", primary.getColor());
        tag.setBoolean("complete", false);
        tag.setInteger("copies", 0);

        int radius = 1 + Math.min(3, research.getComplexity());
        HashMap<String, HexUtils.Hex> hexLocs = HexUtils.generateHexes(radius);
        ArrayList<HexUtils.Hex> outerRing = HexUtils.distributeRingRandomly(radius, research.tags.size(), world.rand);
        HashMap<String, HexEntry> hexEntries = new HashMap<>();
        HashMap<String, HexUtils.Hex> hexes = new HashMap<>();
        for (HexUtils.Hex hex : hexLocs.values()) {
            hexes.put(hex.toString(), hex);
            hexEntries.put(hex.toString(), new HexEntry(null, 0));
        }
        int count = 0;
        Aspect[] researchAspects = research.tags.getAspects();
        for (HexUtils.Hex hex : outerRing) {
            hexes.put(hex.toString(), hex);
            hexEntries.put(hex.toString(), new HexEntry(researchAspects[count], 1));
            count++;
        }
        if (research.getComplexity() > 1) {
            int blanks = research.getComplexity() * 2;
            HexUtils.Hex[] temp = hexes.values().toArray(new HexUtils.Hex[0]);
            while (blanks > 0) {
                int index = world.rand.nextInt(temp.length);
                HexUtils.Hex candidate = temp[index];
                HexEntry candidateEntry = hexEntries.get(candidate.toString());
                if (candidateEntry == null || candidateEntry.type != 0) {
                    continue;
                }
                boolean valid = true;
                for (int n = 0; n < 6; n++) {
                    HexUtils.Hex neighbour = candidate.getNeighbour(n);
                    HexEntry neighbourEntry = hexEntries.get(neighbour.toString());
                    if (!hexes.containsKey(neighbour.toString()) || neighbourEntry == null || neighbourEntry.type != 1) {
                        continue;
                    }
                    int neighbourCount = 0;
                    for (int q = 0; q < 6; q++) {
                        if (hexes.containsKey(hexes.get(neighbour.toString()).getNeighbour(q).toString())) {
                            neighbourCount++;
                        }
                        if (neighbourCount >= 2) break;
                    }
                    if (neighbourCount < 2) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) {
                    continue;
                }
                hexes.remove(candidate.toString());
                hexEntries.remove(candidate.toString());
                temp = hexes.values().toArray(new HexUtils.Hex[0]);
                blanks--;
            }
        }

        NBTTagList hexGrid = new NBTTagList();
        for (HexUtils.Hex hex : hexes.values()) {
            HexEntry entry = hexEntries.get(hex.toString());
            if (entry == null) {
                continue;
            }
            NBTTagCompound hexTag = new NBTTagCompound();
            hexTag.setByte("hexq", (byte) hex.q);
            hexTag.setByte("hexr", (byte) hex.r);
            hexTag.setByte("type", (byte) entry.type);
            if (entry.aspect != null) {
                hexTag.setString("aspect", entry.aspect.getTag());
            }
            hexGrid.appendTag(hexTag);
        }
        tag.setTag("hexgrid", hexGrid);
        return stack;
    }

    public static ResearchNoteData getData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) return null;
        return readNoteData(stack.getTagCompound());
    }

    static ResearchNoteData readNoteData(NBTTagCompound tag) {
        if (tag == null) return null;
        ResearchNoteData data = new ResearchNoteData();
        data.key = tag.getString("key");
        data.color = tag.getInteger("color");
        data.complete = tag.getBoolean("complete");
        data.copies = tag.getInteger("copies");

        NBTTagList hexGrid = tag.getTagList("hexgrid", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < hexGrid.tagCount(); i++) {
            NBTTagCompound hexTag = hexGrid.getCompoundTagAt(i);
            int q = hexTag.getByte("hexq");
            int r = hexTag.getByte("hexr");
            int type = hexTag.getByte("type");
            Aspect aspect = Aspect.getAspect(hexTag.getString("aspect"));
            HexUtils.Hex hex = new HexUtils.Hex(q, r);
            String hexKey = hex.toString();
            data.hexes.put(hexKey, hex);
            data.hexEntries.put(hexKey, new HexEntry(aspect, type));
        }
        return data;
    }

    public static void updateData(ItemStack stack, ResearchNoteData data) {
        if (stack == null || stack.isEmpty() || data == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        writeNoteData(stack.getTagCompound(), data);
    }

    static void writeNoteData(NBTTagCompound tag, ResearchNoteData data) {
        if (tag == null || data == null) return;
        tag.setString("key", data.key == null ? "" : data.key);
        tag.setInteger("color", data.color);
        tag.setBoolean("complete", data.complete);
        tag.setInteger("copies", data.copies);

        NBTTagList hexGrid = new NBTTagList();
        for (HexUtils.Hex hex : data.hexes.values()) {
            String hexKey = hex.toString();
            HexEntry entry = data.hexEntries.get(hexKey);
            if (entry == null) continue;
            NBTTagCompound hexTag = new NBTTagCompound();
            hexTag.setByte("hexq", (byte)hex.q);
            hexTag.setByte("hexr", (byte)hex.r);
            hexTag.setByte("type", (byte)entry.type);
            if (entry.aspect != null) {
                hexTag.setString("aspect", entry.aspect.getTag());
            }
            hexGrid.appendTag(hexTag);
        }
        tag.setTag("hexgrid", hexGrid);
    }

    public static boolean checkResearchCompletion(ItemStack contents, ResearchNoteData note, String username) {
        if (contents == null || contents.isEmpty() || note == null || note.hexes == null || note.hexEntries == null) {
            return false;
        }
        ArrayList<String> checked = new ArrayList<>();
        ArrayList<String> main = new ArrayList<>();
        ArrayList<String> remains = new ArrayList<>();
        for (HexUtils.Hex hex : note.hexes.values()) {
            HexEntry entry = note.hexEntries.get(hex.toString());
            if (entry != null && entry.type == 1) {
                main.add(hex.toString());
            }
        }
        for (HexUtils.Hex hex : note.hexes.values()) {
            HexEntry entry = note.hexEntries.get(hex.toString());
            if (entry == null || entry.type != 1) continue;
            main.remove(hex.toString());
            checkConnections(note, hex, checked, main, remains, username);
            break;
        }
        if (main.size() == 0) {
            ArrayList<String> remove = new ArrayList<>();
            for (HexUtils.Hex hex : note.hexes.values()) {
                HexEntry entry = note.hexEntries.get(hex.toString());
                if (entry == null || entry.type == 1 || remains.contains(hex.toString())) continue;
                remove.add(hex.toString());
            }
            for (String s : remove) {
                note.hexEntries.remove(s);
                note.hexes.remove(s);
            }
            note.complete = true;
            updateData(contents, note);
            return true;
        }
        return false;
    }

    private static void checkConnections(ResearchNoteData note, HexUtils.Hex hex, ArrayList<String> checked, ArrayList<String> main, ArrayList<String> remains, String username) {
        checked.add(hex.toString());
        for (int a = 0; a < 6; ++a) {
            HexUtils.Hex target = hex.getNeighbour(a);
            String targetKey = target.toString();
            if (checked.contains(targetKey) || !note.hexEntries.containsKey(targetKey)) continue;
            HexEntry sourceEntry = note.hexEntries.get(hex.toString());
            HexEntry targetEntry = note.hexEntries.get(targetKey);
            if (sourceEntry == null || targetEntry == null || targetEntry.type < 1) continue;
            Aspect aspect1 = sourceEntry.aspect;
            Aspect aspect2 = targetEntry.aspect;
            if (aspect1 == null || aspect2 == null) continue;
            if (!playerHasDiscoveredAspect(username, aspect1) || !playerHasDiscoveredAspect(username, aspect2)) continue;
            boolean linkedFrom1 = !aspect1.isPrimal() && aspect1.getComponents() != null
                    && (aspect1.getComponents()[0] == aspect2 || aspect1.getComponents()[1] == aspect2);
            boolean linkedFrom2 = !aspect2.isPrimal() && aspect2.getComponents() != null
                    && (aspect2.getComponents()[0] == aspect1 || aspect2.getComponents()[1] == aspect1);
            if (!linkedFrom1 && !linkedFrom2) continue;
            remains.add(targetKey);
            if (targetEntry.type == 1) {
                main.remove(targetKey);
            }
            checkConnections(note, target, checked, main, remains, username);
        }
    }

    private static boolean playerHasDiscoveredAspect(String username, Aspect aspect) {
        if (username == null || aspect == null) return false;
        IPlayerKnowledge knowledge = getResearchData(username);
        return knowledge != null && knowledge.hasDiscoveredAspect(aspect);
    }

    static boolean entityTriggerMatches(String trigger, String clueEntityKey) {
        if (trigger == null || trigger.trim().isEmpty() || clueEntityKey == null || clueEntityKey.trim().isEmpty()) {
            return false;
        }
        Set<String> triggerForms = expandEntityTriggerForms(trigger);
        Set<String> clueForms = expandEntityTriggerForms(clueEntityKey);
        for (String form : triggerForms) {
            if (clueForms.contains(form)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> expandEntityTriggerForms(String input) {
        Set<String> forms = new HashSet<>();
        String base = input.trim().toLowerCase(Locale.ROOT);
        forms.add(base);

        int colon = base.indexOf(':');
        int dot = base.indexOf('.');
        if (colon >= 0 && colon < base.length() - 1) {
            forms.add(base.substring(colon + 1));
        }
        if (dot >= 0 && dot < base.length() - 1) {
            String namespaced = base.substring(0, dot) + ":" + base.substring(dot + 1);
            forms.add(namespaced);
            forms.add(base.substring(dot + 1));
        }
        if (colon < 0 && dot < 0) {
            forms.add("minecraft:" + base);
        }
        return forms;
    }

    private static boolean hasUsableResearchTags(ResearchItem research) {
        return research.tags != null && getResearchPrimaryTag(research) != null;
    }

    private static Aspect getResearchPrimaryTag(ResearchItem research) {
        if (research == null || research.tags == null) return null;
        for (Aspect aspect : research.tags.getAspects()) {
            if (aspect != null) return aspect;
        }
        return null;
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
