package thaumcraft.common.blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvanced;
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvancedNozzle;

public class BlockAlchemyFurnace extends BlockContainer {
    public static final int CENTER = 0;
    public static final int LOWER_NOZZLE = 1;
    public static final int UPPER_CORNER = 2;
    public static final int UPPER_CARDINAL = 3;
    public static final int LOWER_CORNER = 4;
    public static final PropertyInteger TYPE = PropertyInteger.create("type", CENTER, LOWER_CORNER);

    private static final AxisAlignedBB ITEM_COLLISION_AABB =
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.7D, 1.0D);
    private final Set<RestoreTarget> restoring = new HashSet<>();

    public BlockAlchemyFurnace() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(17.0F);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CENTER));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == CENTER || meta == LOWER_NOZZLE;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == CENTER) {
            return new TileAlchemyFurnaceAdvanced();
        }
        if (meta == LOWER_NOZZLE) {
            return new TileAlchemyFurnaceAdvancedNozzle();
        }
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return this.createNewTileEntity(world, this.getMetaFromState(state));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
                                      AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
                                      @Nullable Entity entityIn, boolean isActualState) {
        AxisAlignedBB bounds = this.getMetaFromState(state) == CENTER && !(entityIn instanceof EntityLivingBase)
                ? ITEM_COLLISION_AABB
                : FULL_BLOCK_AABB;
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, bounds);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (this.getMetaFromState(state) == CENTER) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileAlchemyFurnaceAdvanced) {
                TileAlchemyFurnaceAdvanced furnace = (TileAlchemyFurnaceAdvanced) tile;
                return getHeatLight(furnace.heat, furnace.maxPower);
            }
        }
        return super.getLightValue(state, world, pos);
    }

    public static int getHeatLight(int heat, int maxPower) {
        if (heat <= 100 || maxPower <= 0) {
            return 0;
        }
        return MathHelper.clamp((int) ((float) heat / maxPower * 12.0F), 0, 15);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote || this.getMetaFromState(state) != CENTER || !(entityIn instanceof EntityItem)) {
            return;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileAlchemyFurnaceAdvanced)) {
            return;
        }
        EntityItem entityItem = (EntityItem) entityIn;
        ItemStack stack = entityItem.getItem();
        if (stack.isEmpty() || !((TileAlchemyFurnaceAdvanced) tile).process(stack)) {
            return;
        }
        stack.shrink(1);
        worldIn.playSound(null, pos, TCSounds.BUBBLE, SoundCategory.BLOCKS,
                0.2F, 1.0F + worldIn.rand.nextFloat() * 0.4F);
        if (stack.isEmpty()) {
            entityItem.setDead();
        } else {
            entityItem.setItem(stack);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileAlchemyFurnaceAdvancedNozzle) {
            TileAlchemyFurnaceAdvanced furnace = ((TileAlchemyFurnaceAdvancedNozzle) tile).getFurnace();
            return furnace != null && furnace.vis > 0 ? 1 : 0;
        }
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this.getRestoredState(this.getMetaFromState(state)).getBlock());
    }

    @Override
    public int damageDropped(IBlockState state) {
        switch (this.getMetaFromState(state)) {
            case LOWER_NOZZLE:
            case LOWER_CORNER:
                return 3;
            case UPPER_CARDINAL:
                return 9;
            case UPPER_CORNER:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world,
                                  BlockPos pos, EntityPlayer player) {
        return ItemStack.EMPTY;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote && !this.restoring.contains(new RestoreTarget(worldIn, pos))) {
            if (this.getMetaFromState(state) == CENTER) {
                this.restoreStructure(worldIn, pos, false);
            } else {
                BlockPos center = this.findCenter(worldIn, pos);
                if (center != null) {
                    TileEntity tile = worldIn.getTileEntity(center);
                    if (tile instanceof TileAlchemyFurnaceAdvanced) {
                        ((TileAlchemyFurnaceAdvanced) tile).destroy = true;
                        tile.markDirty();
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    public void restoreStructure(World world, BlockPos center, boolean includeCenter) {
        RestoreTarget centerTarget = new RestoreTarget(world, center);
        if (this.restoring.contains(centerTarget)) {
            return;
        }
        Set<RestoreTarget> targets = new HashSet<>();
        for (int y = 0; y <= 1; ++y) {
            for (int x = -1; x <= 1; ++x) {
                for (int z = -1; z <= 1; ++z) {
                    if (includeCenter || x != 0 || y != 0 || z != 0) {
                        targets.add(new RestoreTarget(world, center.add(x, y, z)));
                    }
                }
            }
        }
        this.restoring.addAll(targets);
        try {
            for (int y = 0; y <= 1; ++y) {
                for (int x = -1; x <= 1; ++x) {
                    for (int z = -1; z <= 1; ++z) {
                        if (!includeCenter && x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        BlockPos target = center.add(x, y, z);
                        IBlockState targetState = world.getBlockState(target);
                        if (targetState.getBlock() != this) {
                            continue;
                        }
                        IBlockState restored = this.getRestoredState(this.getMetaFromState(targetState));
                        if (restored.getBlock() != Blocks.AIR) {
                            world.setBlockState(target, restored, 3);
                        }
                    }
                }
            }
        } finally {
            this.restoring.removeAll(targets);
        }
    }

    private static final class RestoreTarget {
        private final World world;
        private final BlockPos pos;

        private RestoreTarget(World world, BlockPos pos) {
            this.world = world;
            this.pos = pos.toImmutable();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof RestoreTarget)) {
                return false;
            }
            RestoreTarget target = (RestoreTarget) other;
            return this.world == target.world && this.pos.equals(target.pos);
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(this.world) + this.pos.hashCode();
        }
    }

    private BlockPos findCenter(IBlockAccess world, BlockPos pos) {
        for (int y = -1; y <= 1; ++y) {
            for (int x = -1; x <= 1; ++x) {
                for (int z = -1; z <= 1; ++z) {
                    BlockPos target = pos.add(x, y, z);
                    IBlockState targetState = world.getBlockState(target);
                    if (targetState.getBlock() == this && this.getMetaFromState(targetState) == CENTER) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    private IBlockState getRestoredState(int meta) {
        if (meta == CENTER && ConfigBlocks.blockStoneDevice != null) {
            return ConfigBlocks.blockStoneDevice.getStateFromMeta(0);
        }
        if (ConfigBlocks.blockMetalDevice == null) {
            return Blocks.AIR.getDefaultState();
        }
        switch (meta) {
            case LOWER_NOZZLE:
            case LOWER_CORNER:
                return ConfigBlocks.blockMetalDevice.getStateFromMeta(3);
            case UPPER_CARDINAL:
                return ConfigBlocks.blockMetalDevice.getStateFromMeta(9);
            case UPPER_CORNER:
                return ConfigBlocks.blockMetalDevice.getStateFromMeta(1);
            default:
                return Blocks.AIR.getDefaultState();
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, CENTER, LOWER_CORNER));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }
}
