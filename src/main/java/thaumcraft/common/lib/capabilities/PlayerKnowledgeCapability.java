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
    private int warpCounter = 0;

    // Discovered aspects and their research pool amounts. Reference PlayerKnowledge
    // stores both concepts in one AspectList: presence means discovered, amount is pool.
    private final AspectList discoveredAspects = new AspectList();
    private boolean initializedAspects = false;

    // Scanned entities, items, phenomena
    private final Set<String> scannedEntities = new HashSet<>();
    private final Set<String> scannedItems = new HashSet<>();
    private final Set<String> scannedPhenomena = new HashSet<>();

    // Completed research
    private final Set<String> researchComplete = new HashSet<>();

    // Player runic shield charge. Max charge is equipment-derived and resynced live.
    private int runicCharge = 0;

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

    @Override
    public int getWarpCounter() {
        return warpCounter;
    }

    @Override
    public void setWarpCounter(int counter) {
        this.warpCounter = Math.max(0, counter);
    }

    // ---- Aspect Discovery ----

    @Override
    public boolean hasDiscoveredAspect(String tag) {
        Aspect aspect = Aspect.getAspect(tag);
        return aspect != null && discoveredAspects.aspects.containsKey(aspect);
    }

    @Override
    public boolean hasDiscoveredAspect(Aspect aspect) {
        return aspect != null && discoveredAspects.aspects.containsKey(aspect);
    }

    @Override
    public void addDiscoveredAspect(String tag) {
        Aspect aspect = Aspect.getAspect(tag);
        if (aspect != null && !discoveredAspects.aspects.containsKey(aspect)) {
            discoveredAspects.add(aspect, 0);
        }
    }

    @Override
    public AspectList getAspectsDiscovered() {
        addDiscoveredPrimalAspects();
        return discoveredAspects.copy();
    }

    @Override
    public void setAspectsDiscovered(AspectList aspects) {
        discoveredAspects.aspects.clear();
        if (aspects != null) {
            for (Aspect aspect : aspects.getAspects()) {
                if (aspect != null) {
                    discoveredAspects.aspects.put(aspect, Math.max(0, aspects.getAmount(aspect)));
                }
            }
        }
        addDiscoveredPrimalAspects();
    }

    @Override
    public boolean hasDiscoveredParentAspects(Aspect aspect) {
        if (aspect == null) return false;
        Aspect[] components = aspect.getComponents();
        if (components == null) return true;
        for (Aspect component : components) {
            if (!hasDiscoveredAspect(component)) return false;
        }
        return true;
    }

    @Override
    public void addDiscoveredPrimalAspects() {
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (aspect != null && !discoveredAspects.aspects.containsKey(aspect)) {
                discoveredAspects.add(aspect, 0);
            }
        }
    }

    @Override
    public int getAspectPoolFor(Aspect aspect) {
        return aspect == null ? 0 : Math.max(0, discoveredAspects.getAmount(aspect));
    }

    @Override
    public boolean addAspectPool(Aspect aspect, int amount) {
        if (aspect == null || amount == 0) return false;
        if (!discoveredAspects.aspects.containsKey(aspect)) {
            discoveredAspects.add(aspect, 0);
        }
        int current = discoveredAspects.getAmount(aspect);
        int updated = Math.max(0, current + amount);
        discoveredAspects.aspects.put(aspect, updated);
        return updated != current;
    }

    @Override
    public boolean setAspectPool(Aspect aspect, int amount) {
        if (aspect == null) return false;
        discoveredAspects.aspects.put(aspect, Math.max(0, amount));
        return true;
    }

    @Override
    public boolean hasInitializedAspects() {
        return initializedAspects;
    }

    @Override
    public void setInitializedAspects(boolean initialized) {
        this.initializedAspects = initialized;
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
            addScanKey(scannedEntities, entityName);
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
            addScanKey(scannedItems, registryName);
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
            addScanKey(scannedPhenomena, key);
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

    @Override
    public int getRunicCharge() {
        return runicCharge;
    }

    @Override
    public void setRunicCharge(int amount) {
        this.runicCharge = Math.max(0, amount);
    }

    @Override
    public void reset() {
        warpPerm = 0;
        warpSticky = 0;
        warpTemp = 0;
        warpCounter = 0;
        runicCharge = 0;
        initializedAspects = false;
        discoveredAspects.aspects.clear();
        scannedEntities.clear();
        scannedItems.clear();
        scannedPhenomena.clear();
        researchComplete.clear();
    }

    // ---- NBT Serialization ----

    private static final String TAG_WARP_PERM = "warpPerm";
    private static final String TAG_WARP_STICKY = "warpSticky";
    private static final String TAG_WARP_TEMP = "warpTemp";
    private static final String TAG_WARP_COUNTER = "warpCounter";
    private static final String TAG_DISCOVERED_ASPECTS = "discoveredAspects";
    private static final String TAG_INITIALIZED_ASPECTS = "initializedAspects";
    private static final String TAG_SCANNED_ENTITIES = "scannedEntities";
    private static final String TAG_SCANNED_ITEMS = "scannedItems";
    private static final String TAG_SCANNED_PHENOMENA = "scannedPhenomena";
    private static final String TAG_RESEARCH_COMPLETE = "researchComplete";
    private static final String TAG_RUNIC_CHARGE = "runicCharge";

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger(TAG_WARP_PERM, warpPerm);
        nbt.setInteger(TAG_WARP_STICKY, warpSticky);
        nbt.setInteger(TAG_WARP_TEMP, warpTemp);
        nbt.setInteger(TAG_WARP_COUNTER, warpCounter);
        nbt.setInteger(TAG_RUNIC_CHARGE, runicCharge);
        nbt.setBoolean(TAG_INITIALIZED_ASPECTS, initializedAspects);

        writeAspectList(nbt, TAG_DISCOVERED_ASPECTS, discoveredAspects);
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
        warpCounter = nbt.getInteger(TAG_WARP_COUNTER);
        runicCharge = nbt.getInteger(TAG_RUNIC_CHARGE);
        initializedAspects = nbt.getBoolean(TAG_INITIALIZED_ASPECTS);

        readAspectList(nbt, TAG_DISCOVERED_ASPECTS, discoveredAspects);
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

    private void writeAspectList(NBTTagCompound nbt, String tag, AspectList aspects) {
        NBTTagList list = new NBTTagList();
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("key", aspect.getTag());
            entry.setInteger("amount", Math.max(0, aspects.getAmount(aspect)));
            list.appendTag(entry);
        }
        nbt.setTag(tag, list);
    }

    private void readAspectList(NBTTagCompound nbt, String tag, AspectList aspects) {
        aspects.aspects.clear();
        if (!nbt.hasKey(tag, Constants.NBT.TAG_LIST)) return;
        NBTTagList list = nbt.getTagList(tag, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            Aspect aspect = Aspect.getAspect(entry.getString("key"));
            if (aspect != null) {
                aspects.aspects.put(aspect, Math.max(0, entry.getInteger("amount")));
            }
        }
    }

    private void addScanKey(Set<String> set, String key) {
        set.add(key);
        if (key.startsWith("#")) {
            set.remove("@" + key.substring(1));
        }
    }
}
