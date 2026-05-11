package thaumcraft.common.lib.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.HashSet;
import java.util.Set;

public class PlayerKnowledgeCapability implements IPlayerKnowledge {

    private EntityPlayer player;

    // Warp
    private int warpPerm = 0;
    private int warpSticky = 0;
    private int warpTemp = 0;

    // Aspects discovered
    private final Set<String> discoveredAspects = new HashSet<>();

    // Scanned entities, items, phenomena
    private final Set<String> scannedEntities = new HashSet<>();
    private final Set<String> scannedItems = new HashSet<>();
    private final Set<String> scannedPhenomena = new HashSet<>();

    // Completed research
    private final Set<String> researchComplete = new HashSet<>();

    public PlayerKnowledgeCapability() {
    }

    public PlayerKnowledgeCapability(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    // ---- Warp ----

    @Override
    public int getWarpPerm() {
        return warpPerm;
    }

    @Override
    public void setWarpPerm(int amount) {
        this.warpPerm = Math.max(0, amount);
    }

    @Override
    public void addWarpPerm(int amount) {
        this.warpPerm = Math.max(0, this.warpPerm + amount);
    }

    @Override
    public int getWarpSticky() {
        return warpSticky;
    }

    @Override
    public void setWarpSticky(int amount) {
        this.warpSticky = Math.max(0, amount);
    }

    @Override
    public void addWarpSticky(int amount) {
        this.warpSticky = Math.max(0, this.warpSticky + amount);
    }

    @Override
    public int getWarpTemp() {
        return warpTemp;
    }

    @Override
    public void setWarpTemp(int amount) {
        this.warpTemp = Math.max(0, amount);
    }

    @Override
    public void addWarpTemp(int amount) {
        this.warpTemp = Math.max(0, this.warpTemp + amount);
    }

    @Override
    public int getTotalWarp() {
        return warpPerm + warpSticky + warpTemp;
    }

    // ---- Aspect Discovery ----

    @Override
    public boolean hasDiscoveredAspect(String tag) {
        return discoveredAspects.contains(tag);
    }

    @Override
    public boolean hasDiscoveredAspect(Aspect aspect) {
        return aspect != null && discoveredAspects.contains(aspect.getTag());
    }

    @Override
    public void addDiscoveredAspect(String tag) {
        if (tag != null) {
            discoveredAspects.add(tag);
        }
    }

    @Override
    public AspectList getAspectsDiscovered() {
        AspectList list = new AspectList();
        for (String tag : discoveredAspects) {
            Aspect aspect = Aspect.aspects.get(tag);
            if (aspect != null) {
                list.add(aspect, 1);
            }
        }
        return list;
    }

    // ---- Scanned Entities ----

    @Override
    public Set<String> getScannedEntities() {
        return scannedEntities;
    }

    @Override
    public boolean hasScannedEntity(String entityName) {
        return scannedEntities.contains(entityName);
    }

    @Override
    public void scanEntity(String entityName) {
        if (entityName != null) {
            scannedEntities.add(entityName);
        }
    }

    // ---- Scanned Items ----

    @Override
    public Set<String> getScannedItems() {
        return scannedItems;
    }

    @Override
    public boolean hasScannedItem(String registryName) {
        return scannedItems.contains(registryName);
    }

    @Override
    public void scanItem(String registryName) {
        if (registryName != null) {
            scannedItems.add(registryName);
        }
    }

    // ---- Scanned Phenomena ----

    @Override
    public Set<String> getScannedPhenomena() {
        return scannedPhenomena;
    }

    @Override
    public boolean hasScannedPhenomena(String key) {
        return scannedPhenomena.contains(key);
    }

    @Override
    public void scanPhenomena(String key) {
        if (key != null) {
            scannedPhenomena.add(key);
        }
    }

    // ---- Research ----

    @Override
    public Set<String> getResearchComplete() {
        return researchComplete;
    }

    @Override
    public boolean isResearchComplete(String key) {
        return researchComplete.contains(key);
    }

    @Override
    public void addResearch(String key) {
        if (key != null) {
            researchComplete.add(key);
        }
    }

    @Override
    public void removeResearch(String key) {
        if (key != null) {
            researchComplete.remove(key);
        }
    }

    // ---- NBT Serialization ----

    private static final String TAG_WARP_PERM = "warpPerm";
    private static final String TAG_WARP_STICKY = "warpSticky";
    private static final String TAG_WARP_TEMP = "warpTemp";
    private static final String TAG_DISCOVERED_ASPECTS = "discoveredAspects";
    private static final String TAG_SCANNED_ENTITIES = "scannedEntities";
    private static final String TAG_SCANNED_ITEMS = "scannedItems";
    private static final String TAG_SCANNED_PHENOMENA = "scannedPhenomena";
    private static final String TAG_RESEARCH_COMPLETE = "researchComplete";

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger(TAG_WARP_PERM, warpPerm);
        nbt.setInteger(TAG_WARP_STICKY, warpSticky);
        nbt.setInteger(TAG_WARP_TEMP, warpTemp);

        writeStringSet(nbt, TAG_DISCOVERED_ASPECTS, discoveredAspects);
        writeStringSet(nbt, TAG_SCANNED_ENTITIES, scannedEntities);
        writeStringSet(nbt, TAG_SCANNED_ITEMS, scannedItems);
        writeStringSet(nbt, TAG_SCANNED_PHENOMENA, scannedPhenomena);
        writeStringSet(nbt, TAG_RESEARCH_COMPLETE, researchComplete);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt == null) return;

        warpPerm = nbt.getInteger(TAG_WARP_PERM);
        warpSticky = nbt.getInteger(TAG_WARP_STICKY);
        warpTemp = nbt.getInteger(TAG_WARP_TEMP);

        readStringSet(nbt, TAG_DISCOVERED_ASPECTS, discoveredAspects);
        readStringSet(nbt, TAG_SCANNED_ENTITIES, scannedEntities);
        readStringSet(nbt, TAG_SCANNED_ITEMS, scannedItems);
        readStringSet(nbt, TAG_SCANNED_PHENOMENA, scannedPhenomena);
        readStringSet(nbt, TAG_RESEARCH_COMPLETE, researchComplete);
    }

    private void writeStringSet(NBTTagCompound nbt, String tag, Set<String> set) {
        NBTTagList list = new NBTTagList();
        for (String s : set) {
            list.appendTag(new NBTTagString(s));
        }
        nbt.setTag(tag, list);
    }

    private void readStringSet(NBTTagCompound nbt, String tag, Set<String> set) {
        set.clear();
        if (nbt.hasKey(tag, Constants.NBT.TAG_LIST)) {
            NBTTagList list = nbt.getTagList(tag, Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.tagCount(); i++) {
                set.add(list.getStringTagAt(i));
            }
        }
    }
}
