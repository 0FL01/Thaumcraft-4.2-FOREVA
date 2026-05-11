package thaumcraft.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class WorldCoordinates
implements Comparable {
    public int x;
    public int y;
    public int z;
    public int dim;

    public WorldCoordinates() {
    }

    public WorldCoordinates(int par1, int par2, int par3, int d) {
        this.x = par1;
        this.y = par2;
        this.z = par3;
        this.dim = d;
    }

    public WorldCoordinates(TileEntity tile) {
        this.x = tile.getPos().getX();
        this.y = tile.getPos().getY();
        this.z = tile.getPos().getZ();
        this.dim = tile.getWorld().provider.getDimension();
    }

    public WorldCoordinates(WorldCoordinates par1BlockPos) {
        this.x = par1BlockPos.x;
        this.y = par1BlockPos.y;
        this.z = par1BlockPos.z;
        this.dim = par1BlockPos.dim;
    }

    public boolean equals(Object par1Obj) {
        if (!(par1Obj instanceof WorldCoordinates)) {
            return false;
        }
        WorldCoordinates coordinates = (WorldCoordinates)par1Obj;
        return this.x == coordinates.x && this.y == coordinates.y && this.z == coordinates.z && this.dim == coordinates.dim;
    }

    public int hashCode() {
        return this.x + this.y << 8 + this.z << 16 + this.dim << 24;
    }

    public int compareWorldCoordinate(WorldCoordinates par1) {
        return this.dim == par1.dim ? (this.y == par1.y ? (this.z == par1.z ? this.x - par1.x : this.z - par1.z) : this.y - par1.y) : -1;
    }

    public void set(int par1, int par2, int par3, int d) {
        this.x = par1;
        this.y = par2;
        this.z = par3;
        this.dim = d;
    }

    public float getDistanceSquared(int par1, int par2, int par3) {
        float f = this.x - par1;
        float f1 = this.y - par2;
        float f2 = this.z - par3;
        return f * f + f1 * f1 + f2 * f2;
    }

    public float getDistanceSquaredToWorldCoordinates(WorldCoordinates par1BlockPos) {
        return this.getDistanceSquared(par1BlockPos.x, par1BlockPos.y, par1BlockPos.z);
    }

    public int compareTo(Object par1Obj) {
        return this.compareWorldCoordinate((WorldCoordinates)par1Obj);
    }

    public void readNBT(NBTTagCompound nbt) {
        this.x = nbt.getInteger("w_x");
        this.y = nbt.getInteger("w_y");
        this.z = nbt.getInteger("w_z");
        this.dim = nbt.getInteger("w_d");
    }

    public void writeNBT(NBTTagCompound nbt) {
        nbt.setInteger("w_x", this.x);
        nbt.setInteger("w_y", this.y);
        nbt.setInteger("w_z", this.z);
        nbt.setInteger("w_d", this.dim);
    }
}
