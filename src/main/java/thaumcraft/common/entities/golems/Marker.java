package thaumcraft.common.entities.golems;

public class Marker extends java.lang.Object {
    public int x, y, z;
    public byte dim, side, color;

    public Marker() {}

    public Marker(int x, int y, int z, byte dim, byte side, byte color) {
        this.x = x; this.y = y; this.z = z;
        this.dim = dim; this.side = side; this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Marker)) return false;
        Marker m = (Marker) o;
        return this.x == m.x && this.y == m.y && this.z == m.z && this.dim == m.dim
            && this.side == m.side && this.color == m.color;
    }

    @Override
    public int hashCode() {
        int result = (this.x * 31 + this.y) * 31 + this.z;
        result = result * 31 + this.dim;
        result = result * 31 + this.side;
        result = result * 31 + this.color;
        return result;
    }

    public boolean equalsFuzzy(Marker m) {
        return this.x == m.x && this.y == m.y && this.z == m.z
            && this.dim == m.dim && this.side == m.side
            && (this.color == m.color || this.color == -1);
    }
}
