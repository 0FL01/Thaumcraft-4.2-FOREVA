package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

public class BlockFluxGas extends BlockFluidFinite {

    /** Non-null zero-size AABB — replaces NULL_AABB which is null in 1.12.2. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockFluxGas() {
        super(ConfigBlocks.FLUXGAS, Config.fluxGoomaterial);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setHardness(0.0F);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(MathHelper.clamp(meta, 0, this.getMaxRenderHeightMeta()));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return MathHelper.clamp(state.getValue(BlockFluidBase.LEVEL), 0, this.getMaxRenderHeightMeta());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    @Override
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
        return originalColor;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return this.getMetaFromState(world.getBlockState(pos)) < 2;
    }
}
