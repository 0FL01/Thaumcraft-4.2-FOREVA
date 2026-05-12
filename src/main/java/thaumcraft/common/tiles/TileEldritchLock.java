package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;

public class TileEldritchLock extends TileEntity {
    private byte facing = 0;

    public void setFacing(byte facing) {
        this.facing = facing;
        this.markDirty();
    }

    public byte getFacing() {
        return this.facing;
    }

    @Override
    public void readFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.facing = compound.getByte("facing");
    }

    @Override
    public net.minecraft.nbt.NBTTagCompound writeToNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setByte("facing", this.facing);
        return compound;
    }
}
