package thaumcraft.common.lib.world;

import net.minecraft.util.math.ChunkPos;

public class ChunkLoc {
    public int x;
    public int z;

    public ChunkLoc(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkLoc(ChunkPos pos) {
        this.x = pos.x;
        this.z = pos.z;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChunkLoc)) return false;
        ChunkLoc loc = (ChunkLoc) o;
        return this.x == loc.x && this.z == loc.z;
    }

    @Override
    public int hashCode() {
        return this.x * 397 ^ this.z;
    }

    @Override
    public String toString() {
        return "ChunkLoc[" + this.x + ", " + this.z + "]";
    }
}
