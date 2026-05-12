package thaumcraft.common.lib.world.dim;

public class CellLoc implements Comparable<CellLoc> {
    public int x;
    public int z;

    public CellLoc() {}

    public CellLoc(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public CellLoc(CellLoc loc) {
        this.x = loc.x;
        this.z = loc.z;
    }

    public void set(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public float getDistanceSquared(int x, int z) {
        float dx = (float)(this.x - x);
        float dz = (float)(this.z - z);
        return dx * dx + dz * dz;
    }

    public float getDistanceSquaredToChunkCoordinates(CellLoc loc) {
        return this.getDistanceSquared(loc.x, loc.z);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CellLoc)) return false;
        CellLoc loc = (CellLoc) o;
        return this.x == loc.x && this.z == loc.z;
    }

    @Override
    public int hashCode() {
        return (this.x * 397) ^ this.z;
    }

    @Override
    public int compareTo(CellLoc o) {
        int comp = Integer.compare(this.z, o.z);
        if (comp == 0) comp = Integer.compare(this.x, o.x);
        return comp;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.z + ")";
    }
}
