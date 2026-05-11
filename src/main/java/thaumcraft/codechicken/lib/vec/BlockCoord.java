package thaumcraft.codechicken.lib.vec;

import net.minecraft.tileentity.TileEntity;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.Vector3;

public class BlockCoord
implements Comparable<BlockCoord>,
Copyable<BlockCoord> {
    public int x;
    public int y;
    public int z;
    public static final BlockCoord[] sideOffsets = new BlockCoord[]{new BlockCoord(0, -1, 0), new BlockCoord(0, 1, 0), new BlockCoord(0, 0, -1), new BlockCoord(0, 0, 1), new BlockCoord(-1, 0, 0), new BlockCoord(1, 0, 0)};

    public BlockCoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockCoord(Vector3 v) {
        this(MathHelper.floor_double(v.x), MathHelper.floor_double(v.y), MathHelper.floor_double(v.z));
    }

    public BlockCoord(TileEntity tile) {
        this(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
    }

    public BlockCoord(int[] ia) {
        this(ia[0], ia[1], ia[2]);
    }

    public BlockCoord() {
    }

    public static BlockCoord fromAxes(int[] ia) {
        return new BlockCoord(ia[2], ia[0], ia[1]);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BlockCoord)) {
            return false;
        }
        BlockCoord o2 = (BlockCoord)obj;
        return this.x == o2.x && this.y == o2.y && this.z == o2.z;
    }

    public int hashCode() {
        return (this.x ^ this.z) * 31 + this.y;
    }

    @Override
    public int compareTo(BlockCoord o) {
        if (this.x != o.x) {
            return this.x < o.x ? 1 : -1;
        }
        if (this.y != o.y) {
            return this.y < o.y ? 1 : -1;
        }
        if (this.z != o.z) {
            return this.z < o.z ? 1 : -1;
        }
        return 0;
    }

    public Vector3 toVector3Centered() {
        return new Vector3((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5);
    }

    public BlockCoord multiply(int i) {
        this.x *= i;
        this.y *= i;
        this.z *= i;
        return this;
    }

    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public int mag2() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public boolean isZero() {
        return this.x == 0 && this.y == 0 && this.z == 0;
    }

    public boolean isAxial() {
        return this.x == 0 ? this.y == 0 || this.z == 0 : this.y == 0 && this.z == 0;
    }

    public BlockCoord add(BlockCoord coord2) {
        this.x += coord2.x;
        this.y += coord2.y;
        this.z += coord2.z;
        return this;
    }

    public BlockCoord add(int i, int j, int k) {
        this.x += i;
        this.y += j;
        this.z += k;
        return this;
    }

    public BlockCoord sub(BlockCoord coord2) {
        this.x -= coord2.x;
        this.y -= coord2.y;
        this.z -= coord2.z;
        return this;
    }

    public BlockCoord sub(int i, int j, int k) {
        this.x -= i;
        this.y -= j;
        this.z -= k;
        return this;
    }

    public BlockCoord offset(int side) {
        return this.offset(side, 1);
    }

    public BlockCoord offset(int side, int amount) {
        BlockCoord offset = sideOffsets[side];
        this.x += offset.x * amount;
        this.y += offset.y * amount;
        this.z += offset.z * amount;
        return this;
    }

    public BlockCoord inset(int side) {
        return this.inset(side, 1);
    }

    public BlockCoord inset(int side, int amount) {
        return this.offset(side, -amount);
    }

    public int getSide(int side) {
        switch (side) {
            case 0: 
            case 1: {
                return this.y;
            }
            case 2: 
            case 3: {
                return this.z;
            }
            case 4: 
            case 5: {
                return this.x;
            }
        }
        throw new IndexOutOfBoundsException("Switch Falloff");
    }

    public BlockCoord setSide(int s, int v) {
        switch (s) {
            case 0: 
            case 1: {
                this.y = v;
                break;
            }
            case 2: 
            case 3: {
                this.z = v;
                break;
            }
            case 4: 
            case 5: {
                this.x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
        return this;
    }

    public int[] intArray() {
        return new int[]{this.x, this.y, this.z};
    }

    @Override
    public BlockCoord copy() {
        return new BlockCoord(this.x, this.y, this.z);
    }

    public BlockCoord set(int i, int j, int k) {
        this.x = i;
        this.y = j;
        this.z = k;
        return this;
    }

    public BlockCoord set(BlockCoord coord) {
        return this.set(coord.x, coord.y, coord.z);
    }

    public BlockCoord set(int[] ia) {
        return this.set(ia[0], ia[1], ia[2]);
    }

    public int toSide() {
        if (!this.isAxial()) {
            return -1;
        }
        if (this.y < 0) {
            return 0;
        }
        if (this.y > 0) {
            return 1;
        }
        if (this.z < 0) {
            return 2;
        }
        if (this.z > 0) {
            return 3;
        }
        if (this.x < 0) {
            return 4;
        }
        if (this.x > 0) {
            return 5;
        }
        return -1;
    }

    public int absSum() {
        return (this.x < 0 ? -this.x : this.x) + (this.y < 0 ? -this.y : this.y) + (this.z < 0 ? -this.z : this.z);
    }
}

