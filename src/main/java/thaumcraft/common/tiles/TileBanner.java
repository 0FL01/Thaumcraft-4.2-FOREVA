package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.TileThaumcraft;

public class TileBanner extends TileThaumcraft {
    private byte facing = 0;

    public byte getFacing() {
        return this.facing;
    }

    public void setFacing(byte facing) {
        this.facing = facing;
        this.markDirty();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.facing = compound.getByte("facing");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setByte("facing", this.facing);
    }
}
