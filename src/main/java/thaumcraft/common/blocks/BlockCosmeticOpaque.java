package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.ConnectedTextureUtils;
import thaumcraft.common.tiles.TileOwned;

import net.minecraft.block.properties.PropertyInteger;

import java.util.Random;
import java.util.function.Predicate;

public class BlockCosmeticOpaque extends Block {

    public static final String[] opaqueTypes = {"arcaneStoneBrick", "arcaneStoneTile", "arcaneStonePaver", "stonePaver", "stonePaverTraveller"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 4);
    public static final IUnlistedProperty<Integer> GLASS_DOWN = new IntUnlistedProperty("glass_down", 0, 46);
    public static final IUnlistedProperty<Integer> GLASS_UP = new IntUnlistedProperty("glass_up", 0, 46);
    public static final IUnlistedProperty<Integer> GLASS_NORTH = new IntUnlistedProperty("glass_north", 0, 46);
    public static final IUnlistedProperty<Integer> GLASS_SOUTH = new IntUnlistedProperty("glass_south", 0, 46);
    public static final IUnlistedProperty<Integer> GLASS_WEST = new IntUnlistedProperty("glass_west", 0, 46);
    public static final IUnlistedProperty<Integer> GLASS_EAST = new IntUnlistedProperty("glass_east", 0, 46);

    public BlockCosmeticOpaque() {
        super(Material.ROCK);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return state.getValue(TYPE) != 2;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(TYPE) != 2;
    }

    @Override
    public int getLightOpacity(IBlockState state) {
        return state.getValue(TYPE) <= 1 ? 3 : 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return state.getValue(TYPE) == 2
                ? layer == BlockRenderLayer.TRANSLUCENT
                : layer == BlockRenderLayer.SOLID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (state.getValue(TYPE) == 2) {
            IBlockState neighbor = world.getBlockState(pos.offset(side));
            if (neighbor.getBlock() == this && neighbor.getValue(TYPE) == 2) {
                return false;
            }
        }
        return super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 3; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(TYPE) == 2;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(TYPE) == 2 ? new TileOwned() : null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileOwned && placer instanceof EntityPlayer) {
            ((TileOwned) tile).owner = placer.getName();
            tile.markDirty();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (this.getMetaFromState(world.getBlockState(target.getBlockPos())) == 2) {
            BlockPos pos = target.getBlockPos();
            Thaumcraft.proxy.blockWard(world, pos.getX(), pos.getY(), pos.getZ(), target.sideHit,
                    (float) (target.hitVec.x - pos.getX()),
                    (float) (target.hitVec.y - pos.getY()),
                    (float) (target.hitVec.z - pos.getZ()));
            return true;
        }
        return false;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            int meta = state.getValue(TYPE);
            if (meta == 3 || meta == 4) {
                int target = meta == 3 ? BlockCosmeticSolid.TYPE_WARDING : BlockCosmeticSolid.TYPE_TRAVEL;
                worldIn.setBlockState(pos, ConfigBlocks.blockCosmeticSolid.getDefaultState()
                        .withProperty(BlockCosmeticSolid.TYPE, target), 3);
                return;
            }
        }
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(TYPE) == 2 && Config.wardedStone ? Items.AIR : super.getItemDropped(state, rand, fortune);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        if (state.getValue(TYPE) == 2) {
            return Config.wardedStone ? -1.0F : 5.0F;
        }
        return super.getBlockHardness(state, world, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        if (world.getBlockState(pos).getValue(TYPE) == 2) {
            return 999.0F;
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return state.getValue(TYPE) != 2 && super.canEntityDestroy(state, world, pos, entity);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        if (world.getBlockState(pos).getValue(TYPE) != 2) {
            super.onBlockExploded(world, pos, explosion);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
                new IProperty[]{TYPE},
                new IUnlistedProperty[]{GLASS_DOWN, GLASS_UP, GLASS_NORTH, GLASS_SOUTH, GLASS_WEST, GLASS_EAST});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(TYPE) != 2) {
            return state;
        }
        Predicate<BlockPos> connected = check -> {
            IBlockState neighbor = world.getBlockState(check);
            return neighbor.getBlock() == this && neighbor.getValue(TYPE) == 2;
        };
        return ((IExtendedBlockState) state)
                .withProperty(GLASS_DOWN, ConnectedTextureUtils.getTextureIndex(pos, EnumFacing.DOWN.getIndex(), connected))
                .withProperty(GLASS_UP, ConnectedTextureUtils.getTextureIndex(pos, EnumFacing.UP.getIndex(), connected))
                .withProperty(GLASS_NORTH, ConnectedTextureUtils.getTextureIndex(pos, EnumFacing.NORTH.getIndex(), connected))
                .withProperty(GLASS_SOUTH, ConnectedTextureUtils.getTextureIndex(pos, EnumFacing.SOUTH.getIndex(), connected))
                .withProperty(GLASS_WEST, ConnectedTextureUtils.getTextureIndex(pos, EnumFacing.WEST.getIndex(), connected))
                .withProperty(GLASS_EAST, ConnectedTextureUtils.getTextureIndex(pos, EnumFacing.EAST.getIndex(), connected));
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

    private static final class IntUnlistedProperty implements IUnlistedProperty<Integer> {
        private final String name;
        private final int min;
        private final int max;

        private IntUnlistedProperty(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isValid(Integer value) {
            return value != null && value >= this.min && value <= this.max;
        }

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public String valueToString(Integer value) {
            return String.valueOf(value);
        }
    }
}
