package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEldritchNothing extends TileEntity {

    @Override
    public double getMaxRenderDistanceSquared() {
        // Reduced from 9216 (96 blocks) to 2304 (48 blocks) to limit TESR dispatch.
        // Beyond 48 blocks the field-effect quads are invisible in the void anyway.
        return 2304.0;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1);
    }
}
