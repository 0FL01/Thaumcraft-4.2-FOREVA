package thaumcraft.common.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.client.lib.EldritchDiagnostics;

/**
 * Invisible void block used in the Outer Lands to fill empty space.
 * Original 1.7.10: zero-collision, shrunken selection box (0.125 inset).
 * Port notes: Block.NULL_AABB is null in 1.12.2 and callers like
 * WalkNodeProcessor.getSafePoint do not null-check, so all "no-box"
 * returns must use a real zero-size AxisAlignedBB, not NULL_AABB.
 */
public class BlockEldritchNothing extends Block {
    /** Non-null zero-size AABB — replaces NULL_AABB which is null in 1.12.2. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockEldritchNothing() {
        super(Material.AIR);
        this.setHardness(0.0f);
        this.setDefaultState(this.blockState.getBaseState());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        EldritchDiagnostics.logRenderType("INVISIBLE");
        return EnumBlockRenderType.INVISIBLE;
    }

    /* --- bounding-box overrides (ported from 1.7.10) --- */

    /** No selection / raytrace target — zero-size AABB. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return ZERO_AABB;
    }

    /** No collision — zero-size AABB. */
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    /** Skip raytrace entirely for this invisible block. */
    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }

    /* --- tile entity --- */

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new thaumcraft.common.tiles.TileEldritchNothing();
    }
}
