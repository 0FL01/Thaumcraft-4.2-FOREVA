package thaumcraft.common.tiles;

import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;

public class TileNodeStabilizer extends TileThaumcraft implements ITickable {
    public int count = 0;
    public int lock = 0;

    public TileNodeStabilizer() {
    }

    @Override
    public void update() {
        if (this.world == null || !this.world.isRemote) {
            return;
        }
        if (this.lock == 0) {
            this.lock = getStabilizerLockFromState();
        }
        if (this.pos.getY() >= this.world.getHeight() - 1) {
            return;
        }
        BlockPos above = this.pos.up();
        if (this.world.getBlockState(above).getBlock() == ConfigBlocks.blockAiry
                && (this.world.getBlockState(above).getValue(BlockAiry.TYPE) == 0
                || this.world.getBlockState(above).getValue(BlockAiry.TYPE) == 5)
                && !this.world.isAirBlock(this.pos)) {
            if (this.count < 37) {
                ++this.count;
            }
        } else if (this.count > 0) {
            --this.count;
        }
    }

    private int getStabilizerLockFromState() {
        int meta = this.world.getBlockState(this.pos).getBlock().getMetaFromState(this.world.getBlockState(this.pos));
        return meta == 9 ? 1 : 2;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 2, this.pos.getZ() + 1);
    }
}
