package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEldritchNothing extends TileEntity {

    @Override
    public double getMaxRenderDistanceSquared() {
        return 256.0; // 16 blocks — reduces TESR dispatch to ~110 per frame
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1);
    }
}
