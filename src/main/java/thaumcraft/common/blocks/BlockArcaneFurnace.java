package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileArcaneFurnace;
import thaumcraft.common.tiles.TileArcaneFurnaceNozzle;

public class BlockArcaneFurnace extends BlockContainer {
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 10);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final AxisAlignedBB CORE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    private static final AxisAlignedBB NOZZLE_WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
    private static final AxisAlignedBB NOZZLE_EAST_AABB = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB NOZZLE_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
    private static final AxisAlignedBB NOZZLE_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);

    public BlockArcaneFurnace() {
        super(Material.ROCK);
        this.setHardness(10.0F);
        this.setResistance(500.0F);
        this.setLightLevel(0.2F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0).withProperty(FACING, EnumFacing.NORTH));
        this.setHarvestLevel("pickaxe", 2);
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
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (this.getMetaFromState(state) != 10) {
            return state.withProperty(FACING, EnumFacing.NORTH);
        }
        return state.withProperty(FACING, this.getNozzleFacing(worldIn, pos));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == 0 || meta == 2 || meta == 4 || meta == 5 || meta == 6 || meta == 8;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) {
            return new TileArcaneFurnace();
        }
        if (meta == 2 || meta == 4 || meta == 5 || meta == 6 || meta == 8) {
            return new TileArcaneFurnaceNozzle();
        }
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int meta = 0; meta <= 10; meta++) {
            list.add(new ItemStack(this, 1, meta));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta == 0 || meta == 10) {
            return 13;
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta == 0) {
            return CORE_AABB;
        }
        if (meta == 10) {
            return this.getNozzleBounds(this.getNozzleFacing(worldIn, pos));
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return this.getBoundingBox(state, worldIn, pos).offset(pos);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing side) {
        return this.getMetaFromState(base_state) != 0;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        int meta = this.getMetaFromState(state);
        if (meta != 0) {
            return;
        }
        if (entityIn.posX < pos.getX() + 0.3D) {
            entityIn.motionX += 1.0E-4D;
        }
        if (entityIn.posX > pos.getX() + 0.7D) {
            entityIn.motionX -= 1.0E-4D;
        }
        if (entityIn.posZ < pos.getZ() + 0.3D) {
            entityIn.motionZ += 1.0E-4D;
        }
        if (entityIn.posZ > pos.getZ() + 0.7D) {
            entityIn.motionZ -= 1.0E-4D;
        }

        if (entityIn instanceof EntityItem) {
            entityIn.motionY = 0.025F;
            if (!worldIn.isRemote) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof TileArcaneFurnace) {
                    ItemStack stack = ((EntityItem) entityIn).getItem();
                    if (!stack.isEmpty() && ((TileArcaneFurnace) tile).addItemsToInventory(stack.copy())) {
                        entityIn.setDead();
                    }
                }
            }
            return;
        }

        if (entityIn instanceof EntityLivingBase && !entityIn.isImmuneToFire()) {
            entityIn.attackEntityFrom(net.minecraft.util.DamageSource.HOT_FLOOR, 3.0F);
            entityIn.setFire(10);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(TYPE, MathHelper.clamp(meta, 0, 10))
                .withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, net.minecraft.util.EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, net.minecraft.util.EnumHand hand) {
        return this.getDefaultState()
                .withProperty(TYPE, MathHelper.clamp(meta, 0, 10))
                .withProperty(FACING, EnumFacing.NORTH);
    }

    private EnumFacing getNozzleFacing(IBlockAccess world, BlockPos pos) {
        if (world.getBlockState(pos.west()).getBlock() == this && this.getMetaFromState(world.getBlockState(pos.west())) == 0) {
            return EnumFacing.WEST;
        }
        if (world.getBlockState(pos.east()).getBlock() == this && this.getMetaFromState(world.getBlockState(pos.east())) == 0) {
            return EnumFacing.EAST;
        }
        if (world.getBlockState(pos.north()).getBlock() == this && this.getMetaFromState(world.getBlockState(pos.north())) == 0) {
            return EnumFacing.NORTH;
        }
        return EnumFacing.SOUTH;
    }

    private AxisAlignedBB getNozzleBounds(EnumFacing facing) {
        switch (facing) {
            case WEST:
                return NOZZLE_WEST_AABB;
            case EAST:
                return NOZZLE_EAST_AABB;
            case NORTH:
                return NOZZLE_NORTH_AABB;
            default:
                return NOZZLE_SOUTH_AABB;
        }
    }
}
