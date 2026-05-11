package thaumcraft.codechicken.lib.vec;

import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;

public class CuboidCoord
implements Iterable<BlockCoord>,
Copyable<CuboidCoord> {
    public BlockCoord min;
    public BlockCoord max;

    public CuboidCoord() {
        this.min = new BlockCoord();
        this.max = new BlockCoord();
    }

    public CuboidCoord(BlockCoord min, BlockCoord max) {
        this.min = min;
        this.max = max;
    }

    public CuboidCoord(BlockCoord coord) {
        this(coord, coord.copy());
    }

    public CuboidCoord(int[] ia) {
        this(ia[0], ia[1], ia[2], ia[3], ia[4], ia[5]);
    }

    public CuboidCoord(int x1, int y1, int z1, int x2, int y2, int z2) {
        this(new BlockCoord(x1, y1, z1), new BlockCoord(x2, y2, z2));
    }

    public CuboidCoord expand(int amount) {
        return this.expand(amount, amount, amount);
    }

    public CuboidCoord expand(int x, int y, int z) {
        this.max.add(x, y, z);
        this.min.sub(x, y, z);
        return this;
    }

    public CuboidCoord expand(int side, int amount) {
        if (side % 2 == 0) {
            this.min = this.min.offset(side, amount);
        } else {
            this.max = this.max.offset(side, amount);
        }
        return this;
    }

    public int size(int s) {
        switch (s) {
            case 0: 
            case 1: {
                return this.max.y - this.min.y + 1;
            }
            case 2: 
            case 3: {
                return this.max.z - this.min.z + 1;
            }
            case 4: 
            case 5: {
                return this.max.x - this.min.x + 1;
            }
        }
        return 0;
    }

    public int getSide(int s) {
        switch (s) {
            case 0: {
                return this.min.y;
            }
            case 1: {
                return this.max.y;
            }
            case 2: {
                return this.min.z;
            }
            case 3: {
                return this.max.z;
            }
            case 4: {
                return this.min.x;
            }
            case 5: {
                return this.max.x;
            }
        }
        throw new IndexOutOfBoundsException("Switch Falloff");
    }

    public CuboidCoord setSide(int s, int v) {
        switch (s) {
            case 0: {
                this.min.y = v;
                break;
            }
            case 1: {
                this.max.y = v;
                break;
            }
            case 2: {
                this.min.z = v;
                break;
            }
            case 3: {
                this.max.z = v;
                break;
            }
            case 4: {
                this.min.x = v;
                break;
            }
            case 5: {
                this.max.x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
        return this;
    }

    public int getVolume() {
        return (this.max.x - this.min.x + 1) * (this.max.y - this.min.y + 1) * (this.max.z - this.min.z + 1);
    }

    public Vector3 getCenterVec() {
        return new Vector3((double)this.min.x + (double)(this.max.x - this.min.x + 1) / 2.0, (double)this.min.y + (double)(this.max.y - this.min.y + 1) / 2.0, (double)this.min.z + (double)(this.max.z - this.min.z + 1) / 2.0);
    }

    public BlockCoord getCenter(BlockCoord store) {
        store.set(this.min.x + (this.max.x - this.min.x) / 2, this.min.y + (this.max.y - this.min.y) / 2, this.min.z + (this.max.z - this.min.z) / 2);
        return store;
    }

    public boolean contains(BlockCoord coord) {
        return this.contains(coord.x, coord.y, coord.z);
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.min.x && x <= this.max.x && y >= this.min.y && y <= this.max.y && z >= this.min.z && z <= this.max.z;
    }

    public int[] intArray() {
        return new int[]{this.min.x, this.min.y, this.min.z, this.max.x, this.max.y, this.max.z};
    }

    @Override
    public CuboidCoord copy() {
        return new CuboidCoord(this.min.copy(), this.max.copy());
    }

    public Cuboid6 bounds() {
        return new Cuboid6(this.min.x, this.min.y, this.min.z, this.max.x + 1, this.max.y + 1, this.max.z + 1);
    }

    public AxisAlignedBB toAABB() {
        return this.bounds().toAABB();
    }

    public void set(BlockCoord min, BlockCoord max) {
        this.min.set(min);
        this.max.set(max);
    }

    public CuboidCoord set(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.min.set(x1, y1, z1);
        this.max.set(x2, y2, z2);
        return this;
    }

    public CuboidCoord set(BlockCoord coord) {
        this.min.set(coord);
        this.max.set(coord);
        return this;
    }

    public CuboidCoord set(int[] ia) {
        return this.set(ia[0], ia[1], ia[2], ia[3], ia[4], ia[5]);
    }

    public CuboidCoord include(BlockCoord coord) {
        return this.include(coord.x, coord.y, coord.z);
    }

    public CuboidCoord include(int x, int y, int z) {
        if (x < this.min.x) {
            this.min.x = x;
        } else if (x > this.max.x) {
            this.max.x = x;
        }
        if (y < this.min.y) {
            this.min.y = y;
        } else if (y > this.max.y) {
            this.max.y = y;
        }
        if (z < this.min.z) {
            this.min.z = z;
        } else if (z > this.max.z) {
            this.max.z = z;
        }
        return this;
    }

    @Override
    public Iterator<BlockCoord> iterator() {
        return new Iterator<BlockCoord>(){
            BlockCoord b = null;

            @Override
            public boolean hasNext() {
                return this.b == null || !this.b.equals(CuboidCoord.this.max);
            }

            @Override
            public BlockCoord next() {
                if (this.b == null) {
                    this.b = CuboidCoord.this.min.copy();
                } else if (this.b.z != CuboidCoord.this.max.z) {
                    ++this.b.z;
                } else {
                    this.b.z = CuboidCoord.this.min.z;
                    if (this.b.y != CuboidCoord.this.max.y) {
                        ++this.b.y;
                    } else {
                        this.b.y = CuboidCoord.this.min.y;
                        ++this.b.x;
                    }
                }
                return this.b.copy();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

