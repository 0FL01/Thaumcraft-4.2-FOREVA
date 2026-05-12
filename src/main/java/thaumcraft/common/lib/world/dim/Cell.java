package thaumcraft.common.lib.world.dim;

public class Cell {
    public boolean north;
    public boolean south;
    public boolean east;
    public boolean west;
    public boolean above;
    public boolean below;
    public byte feature;

    public Cell() {
        this.north = this.south = this.east = this.west = this.above = this.below = false;
        this.feature = 0;
    }

    public Cell(short packed) {
        this.unpack(packed);
    }

    private void unpack(short pack) {
        int data = pack & 0xFFFF;
        this.north = (data & 1) != 0;
        this.south = (data & 2) != 0;
        this.east  = (data & 4) != 0;
        this.west  = (data & 8) != 0;
        this.above = (data & 16) != 0;
        this.below = (data & 32) != 0;
        this.feature = (byte)((data >> 8) & 0xFF);
    }

    public short pack() {
        int data = 0;
        if (this.north) data |= 1;
        if (this.south) data |= 2;
        if (this.east)  data |= 4;
        if (this.west)  data |= 8;
        if (this.above) data |= 16;
        if (this.below) data |= 32;
        data |= (this.feature & 0xFF) << 8;
        return (short) data;
    }
}
