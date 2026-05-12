package thaumcraft.common.lib.world.dim;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class MapBossData extends WorldSavedData {
    public int bossCount;

    public MapBossData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.bossCount = nbt.getInteger("bossCount");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("bossCount", this.bossCount);
        return nbt;
    }
}
