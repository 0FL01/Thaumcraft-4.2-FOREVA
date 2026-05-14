package thaumcraft.common.lib.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.Set;

public interface IPlayerKnowledge {

    /**
     * Returns the player this capability is attached to, if available.
     */
    EntityPlayer getPlayer();

    void setPlayer(EntityPlayer player);

    // ---- Warp ----

    int getWarpPerm();

    void setWarpPerm(int amount);

    void addWarpPerm(int amount);

    int getWarpSticky();

    void setWarpSticky(int amount);

    void addWarpSticky(int amount);

    int getWarpTemp();

    void setWarpTemp(int amount);

    void addWarpTemp(int amount);

    int getTotalWarp();

    // ---- Warp Counter (used by WarpEvents random trigger) ----

    int getWarpCounter();

    void setWarpCounter(int counter);

    // ---- Aspect Discovery ----

    boolean hasDiscoveredAspect(String tag);

    boolean hasDiscoveredAspect(Aspect aspect);

    void addDiscoveredAspect(String tag);

    AspectList getAspectsDiscovered();

    void setAspectsDiscovered(AspectList aspects);

    boolean hasDiscoveredParentAspects(Aspect aspect);

    void addDiscoveredPrimalAspects();

    int getAspectPoolFor(Aspect aspect);

    boolean addAspectPool(Aspect aspect, int amount);

    boolean setAspectPool(Aspect aspect, int amount);

    boolean hasInitializedAspects();

    void setInitializedAspects(boolean initialized);

    // ---- Scanned Entities ----

    Set<String> getScannedEntities();

    boolean hasScannedEntity(String entityName);

    void scanEntity(String entityName);

    // ---- Scanned Items ----

    Set<String> getScannedItems();

    boolean hasScannedItem(String registryName);

    void scanItem(String registryName);

    // ---- Scanned Phenomena ----

    Set<String> getScannedPhenomena();

    boolean hasScannedPhenomena(String key);

    void scanPhenomena(String key);

    // ---- Research ----

    Set<String> getResearchComplete();

    boolean isResearchComplete(String key);

    void addResearch(String key);

    void removeResearch(String key);

    // ---- Runic shielding ----

    int getRunicCharge();

    void setRunicCharge(int amount);

    // ---- Full-state replacement ----

    void reset();

    // ---- NBT ----

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound nbt);
}
