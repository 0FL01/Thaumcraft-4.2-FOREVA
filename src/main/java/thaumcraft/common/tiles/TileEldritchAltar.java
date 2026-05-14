package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.TileThaumcraft;

public class TileEldritchAltar extends TileThaumcraft {
    private boolean spawner = false;
    private boolean open = false;
    private boolean spawnedClerics = false;
    private byte spawnType = 0;
    private byte eyes = 0;

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.eyes = compound.getByte("eyes");
        this.open = compound.getBoolean("open");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setByte("eyes", this.eyes);
        compound.setBoolean("open", this.open);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.spawnedClerics = compound.getBoolean("spawnedClerics");
        this.spawner = compound.getBoolean("spawner");
        this.spawnType = compound.getByte("spawntype");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setBoolean("spawnedClerics", this.spawnedClerics);
        ret.setBoolean("spawner", this.spawner);
        ret.setByte("spawntype", this.spawnType);
        return ret;
    }

    public boolean isSpawner() {
        return this.spawner;
    }

    public void setSpawner(boolean spawner) {
        this.spawner = spawner;
        this.markDirty();
    }

    public byte getSpawnType() {
        return this.spawnType;
    }

    public void setSpawnType(byte spawnType) {
        this.spawnType = spawnType;
        this.markDirty();
    }

    public byte getEyes() {
        return this.eyes;
    }

    public void setEyes(byte eyes) {
        this.eyes = eyes;
        this.markDirty();
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        this.markDirty();
    }
}
