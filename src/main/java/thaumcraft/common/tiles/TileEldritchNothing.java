package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEldritchNothing extends TileEntity {

    @Override
    public double getMaxRenderDistanceSquared() {
        return 576.0; // 24 blocks — cuts TESR dispatch from ~3000 to ~370 per frame
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1);
    }
}
