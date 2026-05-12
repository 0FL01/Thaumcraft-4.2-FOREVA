package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;

public class TileEldritchPortal extends TileEntity {
    /** Cooldown counter for teleportation (world time). */
    public long lastTeleport = 0;

    @Override
    public void readFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.lastTeleport = compound.getLong("lastTeleport");
    }

    @Override
    public net.minecraft.nbt.NBTTagCompound writeToNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setLong("lastTeleport", this.lastTeleport);
        return compound;
    }
}
