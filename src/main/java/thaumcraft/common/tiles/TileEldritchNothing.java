package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockEldritchNothing;

public class TileEldritchNothing extends TileEntity {

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock()
                || oldState.getBlock() instanceof BlockEldritchNothing
                && oldState.getValue(BlockEldritchNothing.EXPOSED)
                != newState.getValue(BlockEldritchNothing.EXPOSED);
    }

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
