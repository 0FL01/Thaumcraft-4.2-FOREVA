package thaumcraft.common.blocks;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintSpore;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class BlockTaintFibres extends Block {

    public static final String[] fibreTypes = {"taintFibres", "taintGrassShort", "taintGrassTall", "taintSporeStalk", "taintSporeStalkMature"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 4);
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool EAST = PropertyBool.create("east");

    private static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);
    private static final AxisAlignedBB STALK_AABB = new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.8D, 0.8D);

    public BlockTaintFibres() {
        super(Config.taintMaterial);
        this.setHardness(0.5f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(TYPE, 0)
                .withProperty(DOWN, false)
                .withProperty(UP, false)
                .withProperty(NORTH, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(EAST, false));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;
        int meta = this.getMetaFromState(state);
        if (!isTaintBiome(world, pos)
                || !BlockUtils.isAdjacentToSolidBlock(world, pos)
                || meta == 0 && isOnlyAdjacentToTaint(world, pos)) {
            world.setBlockToAir(pos);
            return;
        }

        taintBiomeSpread(world, pos, rand, this);
        BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
        if (isTaintBiome(world, target)) {
            if (!spreadFibres(world, target)) {
                convertAdjacentBlockToTaint(world, target);
            }
            updateSporeStalk(world, pos, meta, rand);
        }
    }

    private void updateSporeStalk(World world, BlockPos pos, int meta, Random rand) {
        if (meta == 3 && Config.spawnTaintSpore && rand.nextInt(10) == 0 && world.isAirBlock(pos.up())) {
            world.setBlockState(pos, ConfigBlocks.blockTaintFibres.getStateFromMeta(4), 3);
            EntityTaintSpore spore = new EntityTaintSpore(world);
            spore.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntity(spore);
        } else if (meta == 4 && world.getEntitiesWithinAABB(EntityTaintSpore.class, new AxisAlignedBB(pos.up())).isEmpty()) {
            world.setBlockState(pos, ConfigBlocks.blockTaintFibres.getStateFromMeta(3), 3);
        }
    }

    private static void convertAdjacentBlockToTaint(World world, BlockPos target) {
        int adjacent = getAdjacentTaint(world, target);
        IBlockState targetState = world.getBlockState(target);
        Block targetBlock = targetState.getBlock();
        Material material = targetState.getMaterial();
        if (adjacent >= 2 && (Utils.isWoodLog(world, target) || targetBlock.isLeaves(targetState, world, target))) {
            world.setBlockState(target, ConfigBlocks.blockTaint.getStateFromMeta(0), 3);
            world.addBlockEvent(target, ConfigBlocks.blockTaint, 1, 0);
        } else if (adjacent >= 3 && isTaintSoilTarget(material)) {
            world.setBlockState(target, ConfigBlocks.blockTaint.getStateFromMeta(1), 3);
            world.addBlockEvent(target, ConfigBlocks.blockTaint, 1, 0);
        }
    }

    private static boolean isTaintSoilTarget(Material material) {
        return material == Material.GRASS
                || material == Material.GROUND
                || material == Material.ROCK
                || material == Material.SAND
                || material == Material.CLAY;
    }

    public static boolean isTaintBiome(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        return Biome.getIdForBiome(biome) == Config.biomeTaintID
                || biome == ThaumcraftWorldGenerator.biomeTaint
                || biome != null && ThaumcraftWorldGenerator.biomeTaint != null
                && Biome.getIdForBiome(biome) == Biome.getIdForBiome(ThaumcraftWorldGenerator.biomeTaint);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(TYPE) != 0) {
            return state.withProperty(DOWN, false)
                    .withProperty(UP, false)
                    .withProperty(NORTH, false)
                    .withProperty(SOUTH, false)
                    .withProperty(WEST, false)
                    .withProperty(EAST, false);
        }
        return state.withProperty(DOWN, shouldRenderSurface(world, pos, EnumFacing.DOWN))
                .withProperty(UP, shouldRenderSurface(world, pos, EnumFacing.UP))
                .withProperty(NORTH, shouldRenderSurface(world, pos, EnumFacing.NORTH))
                .withProperty(SOUTH, shouldRenderSurface(world, pos, EnumFacing.SOUTH))
                .withProperty(WEST, shouldRenderSurface(world, pos, EnumFacing.WEST))
                .withProperty(EAST, shouldRenderSurface(world, pos, EnumFacing.EAST));
    }

    private static boolean shouldRenderSurface(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos sidePos = pos.offset(facing);
        IBlockState sideState = world.getBlockState(sidePos);
        if (sideState.getBlock() == ConfigBlocks.blockTaint) {
            return false;
        }
        return sideState.isSideSolid(world, sidePos, facing.getOpposite());
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!world.isRemote && (!isTaintBiome(world, pos) || !BlockUtils.isAdjacentToSolidBlock(world, pos))) {
            world.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return isTaintBiome(world, pos) && BlockUtils.isAdjacentToSolidBlock(world, pos);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 5; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 2) return 8;
        if (meta == 4) return 10;
        return 0;
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
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta == 3 || meta == 4) return STALK_AABB;
        if (meta == 1 || meta == 2) return PLANT_AABB;
        return FULL_BLOCK_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote
                || Config.potionFluxTaint == null
                || !(entity instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase living = (EntityLivingBase) entity;
        if (living.isEntityUndead()) {
            return;
        }

        int chance = living instanceof EntityPlayer ? 1000 : 500;
        if (world.rand.nextInt(chance) == 0) {
            int duration = living instanceof EntityPlayer ? 80 : 160;
            living.addPotionEffect(new PotionEffect(Config.potionFluxTaint, duration, 0, false, true));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, DOWN, UP, NORTH, SOUTH, WEST, EAST);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 4));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 4));
    }

    public static boolean spreadFibres(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();
        if (!BlockUtils.isAdjacentToSolidBlock(world, pos)
                || isOnlyAdjacentToTaint(world, pos)
                || material.isLiquid()) {
            return false;
        }
        if (!world.isAirBlock(pos)
                && !block.isReplaceable(world, pos)
                && !(block instanceof BlockFlower)
                && !block.isLeaves(state, world, pos)) {
            return false;
        }

        int meta = 0;
        if (world.rand.nextInt(10) == 0
                && world.isAirBlock(pos.up())
                && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP)) {
            if (world.rand.nextInt(10) < 9) {
                meta = 1;
            } else if (world.rand.nextInt(12) < 10) {
                meta = 2;
            } else {
                meta = 3;
            }
        }
        world.setBlockState(pos, ConfigBlocks.blockTaintFibres.getStateFromMeta(meta), 3);
        world.addBlockEvent(pos, ConfigBlocks.blockTaintFibres, 1, 0);
        return true;
    }

    public static void taintBiomeSpread(World world, BlockPos pos, Random rand, Block source) {
        if (Config.taintSpreadRate <= 0) return;
        int dx = rand.nextInt(3) - 1;
        int dz = rand.nextInt(3) - 1;
        BlockPos target = pos.add(dx, 0, dz);
        if (isTaintBiome(world, target)
                || rand.nextInt(Config.taintSpreadRate * 5) != 0
                || getAdjacentTaint(world, pos) < 2) {
            return;
        }
        Utils.setBiomeAt(world, target.getX(), target.getZ(), ThaumcraftWorldGenerator.biomeTaint);
        world.addBlockEvent(pos, source, 1, 0);
    }

    public static int getAdjacentTaint(IBlockAccess world, BlockPos pos) {
        int count = 0;
        for (EnumFacing facing : EnumFacing.VALUES) {
            Block block = world.getBlockState(pos.offset(facing)).getBlock();
            if (block == ConfigBlocks.blockTaint || block == ConfigBlocks.blockTaintFibres) {
                count++;
            }
        }
        return count;
    }

    public static boolean isOnlyAdjacentToTaint(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos sidePos = pos.offset(facing);
            IBlockState sideState = world.getBlockState(sidePos);
            if (!world.isAirBlock(sidePos) && sideState.getMaterial() != Config.taintMaterial) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        if (id == 1) {
            if (world.isRemote) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), TCSounds.ROOTS,
                        SoundCategory.BLOCKS, 0.1F, 0.9F + world.rand.nextFloat() * 0.2F, false);
            }
            return true;
        }
        return super.eventReceived(state, world, pos, id, param);
    }

    @Override
    public boolean isSideSolid(IBlockState baseState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
}
