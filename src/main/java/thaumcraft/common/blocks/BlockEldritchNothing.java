package thaumcraft.common.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.client.lib.EldritchDiagnostics;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.TileEldritchNothing;

/**
 * Invisible void block used in the Outer Lands to fill empty space.
 * Original 1.7.10: zero-collision, shrunken selection box (0.125 inset).
 * Port notes: Block.NULL_AABB is null in 1.12.2 and callers like
 * WalkNodeProcessor.getSafePoint do not null-check, so all "no-box"
 * returns must use a real zero-size AxisAlignedBB, not NULL_AABB.
 */
public class BlockEldritchNothing extends Block {
    public static final PropertyBool EXPOSED = PropertyBool.create("exposed");
    /** Non-null zero-size AABB — replaces NULL_AABB which is null in 1.12.2. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    private static final AxisAlignedBB SELECTION_AABB = new AxisAlignedBB(
            0.125D, 0.125D, 0.125D, 0.875D, 0.875D, 0.875D);

    public BlockEldritchNothing() {
        super(Material.ROCK);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.STONE);
        this.setLightLevel(0.2F);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(EXPOSED, false));
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

    /** Original pick/selection inset; collision remains empty. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SELECTION_AABB;
    }

    /** No collision — zero-size AABB. */
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, net.minecraft.util.math.RayTraceResult target,
                                  World world, BlockPos pos, EntityPlayer player) {
        return ItemStack.EMPTY;
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(java.util.Random random) {
        return 0;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) this.updateExposure(world, pos, state);
        super.neighborChanged(state, world, pos, block, fromPos);
    }

    private void updateExposure(World world, BlockPos pos, IBlockState state) {
        boolean exposed = BlockUtils.isBlockExposed(world, pos.getX(), pos.getY(), pos.getZ());
        if (state.getValue(EXPOSED) == exposed) return;
        IBlockState updated = state.withProperty(EXPOSED, exposed);
        world.setBlockState(pos, updated, 3);
        if (exposed) {
            if (!(world.getTileEntity(pos) instanceof TileEldritchNothing)) {
                world.setTileEntity(pos, new TileEldritchNothing());
            }
        } else {
            world.removeTileEntity(pos);
        }
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (entity.ticksExisted > 20
                && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode)) {
            entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, 8.0F);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, EXPOSED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(EXPOSED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(EXPOSED) ? 1 : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return state.getValue(EXPOSED); }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(EXPOSED) ? new TileEldritchNothing() : null;
    }
}
