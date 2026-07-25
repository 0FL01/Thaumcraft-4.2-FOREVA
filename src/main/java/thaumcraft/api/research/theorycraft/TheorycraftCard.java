package thaumcraft.api.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TheorycraftCard {
    private long seed;

    public long getSeed() {
        return seed;
    }

    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        return true;
    }

    public boolean isAidOnly() {
        return false;
    }

    public abstract int getInspirationCost();

    public String getResearchCategory() {
        return null;
    }

    public abstract String getLocalizedName();
    public abstract String getLocalizedText();

    public ItemStack[] getRequiredItems() {
        return null;
    }

    public boolean[] getRequiredItemsConsumed() {
        return null;
    }

    public abstract boolean activate(EntityPlayer player, ResearchTableData data);

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("seed", seed);
        return nbt;
    }

    public void deserialize(NBTTagCompound nbt) {
        seed = nbt.getLong("seed");
    }
}
