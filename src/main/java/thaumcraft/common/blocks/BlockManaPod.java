package thaumcraft.common.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemManaBean;
import thaumcraft.common.tiles.TileManaPod;

public class BlockManaPod extends Block {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 7);
    private static final Map<String, Aspect> ASPECT_DROP_CACHE = new HashMap<>();

    public BlockManaPod() {
        super(Material.PLANTS);
        this.setTickRandomly(true);
        this.setHardness(0.5F);
        this.setSoundType(net.minecraft.block.SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int getLightValue(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getMetaFromState(state);
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        float divisor = Math.max(1.0F, 8.0F - this.getMetaFromState(blockState));
        return super.getBlockHardness(blockState, worldIn, pos) / divisor;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileManaPod();
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        if (!canBlockStay(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        } else if (rand.nextInt(30) == 0) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileManaPod) {
                ((TileManaPod) tile).checkGrowth();
            }
            ASPECT_DROP_CACHE.remove(cacheKey(world, pos));
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!canBlockStay(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN && canBlockStay(worldIn, pos);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileManaPod && ((TileManaPod) tile).aspect != null) {
            ASPECT_DROP_CACHE.put(cacheKey(world, pos), ((TileManaPod) tile).aspect);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        int metadata = this.getMetaFromState(state);
        if (metadata < 2 || ConfigItems.itemManaBean == null) {
            return drops;
        }

        Random random = world instanceof World ? ((World) world).rand : new Random();
        int count = metadata == 7 && random.nextFloat() > 0.33F ? 2 : 1;
        Aspect aspect = getDropAspect(world, pos);
        for (int i = 0; i < count; i++) {
            ItemStack bean = new ItemStack(ConfigItems.itemManaBean);
            ((ItemManaBean) bean.getItem()).setAspects(bean, new AspectList().add(aspect, 1));
            drops.add(bean);
        }
        if (world instanceof World) {
            ASPECT_DROP_CACHE.remove(cacheKey((World) world, pos));
        }
        return drops;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return ConfigItems.itemManaBean == null ? ItemStack.EMPTY : new ItemStack(ConfigItems.itemManaBean);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 7));
    }

    private boolean canBlockStay(World world, BlockPos pos) {
        if (!BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.MAGICAL)) return false;
        Block support = world.getBlockState(pos.up()).getBlock();
        return support == Blocks.LOG || support == Blocks.LOG2 || support == ConfigBlocks.blockMagicalLog;
    }

    private Aspect getDropAspect(IBlockAccess world, BlockPos pos) {
        if (world instanceof World) {
            Aspect cached = ASPECT_DROP_CACHE.get(cacheKey((World) world, pos));
            if (cached != null) return cached;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileManaPod && ((TileManaPod) tile).aspect != null) {
            return ((TileManaPod) tile).aspect;
        }
        return Aspect.PLANT;
    }

    private static String cacheKey(World world, BlockPos pos) {
        int dimension = world.provider == null ? 0 : world.provider.getDimension();
        return dimension + ":" + pos.toLong();
    }
}
