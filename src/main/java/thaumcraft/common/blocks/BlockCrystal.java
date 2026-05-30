package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.tiles.TileCrystal;
import thaumcraft.common.tiles.TileEldritchCrystal;

import java.util.Random;

public class BlockCrystal
extends BlockContainer
implements IInfusionStabiliser {

    private Random random = new Random();
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 7);

    public BlockCrystal() {
        super(Material.GLASS);
        this.setHardness(0.7f);
        this.setResistance(1.0f);
        this.setLightLevel(0.5f);
        this.setSoundType(new CustomStepSound("crystal", 1.0f, 1.0f));
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i <= 6; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta <= 6) {
            return new TileCrystal();
        }
        if (meta == 7) {
            return new TileEldritchCrystal();
        }
        return new TileCrystal();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta <= 6) {
            return new TileCrystal();
        }
        if (meta == 7) {
            return new TileEldritchCrystal();
        }
        return new TileCrystal();
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        int md = this.getMetaFromState(state);
        if (md < 6) {
            return 8;
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        int md = this.getMetaFromState(state);
        if (md < 6) {
            return 15728880; // Fullbright
        }
        return super.getPackedLightmapCoords(state, source, pos);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (worldIn.isRemote) {
            int md = this.getMetaFromState(stateIn);
            if (md <= 6 && rand.nextInt(17) == 0) {
                double x = pos.getX() + 0.3 + rand.nextFloat() * 0.4;
                double y = pos.getY() + 0.3 + rand.nextFloat() * 0.4;
                double z = pos.getZ() + 0.3 + rand.nextFloat() * 0.4;
                int colorIndex = md == 6 ? rand.nextInt(6) : md;
                int color = ItemShard.colors[colorIndex];
                float r = (color >> 16 & 255) / 255.0f;
                float g = (color >> 8 & 255) / 255.0f;
                float b = (color & 255) / 255.0f;
                Thaumcraft.proxy.drawGenericParticles(worldIn,
                        x, y, z,
                        0.0D, 0.004D, 0.0D,
                        r, g, b, 0.75F,
                        false, 128, 8, -1, 8, 0, 0.45F, 1);
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.checkAndDropBlock(worldIn, pos, state);
    }

    private void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isSideSolid(pos.east(), net.minecraft.util.EnumFacing.WEST)) return true;
        if (worldIn.isSideSolid(pos.west(), net.minecraft.util.EnumFacing.EAST)) return true;
        if (worldIn.isSideSolid(pos.north(), net.minecraft.util.EnumFacing.SOUTH)) return true;
        if (worldIn.isSideSolid(pos.south(), net.minecraft.util.EnumFacing.NORTH)) return true;
        if (worldIn.isSideSolid(pos.down(), net.minecraft.util.EnumFacing.UP)) return true;
        if (worldIn.isSideSolid(pos.up(), net.minecraft.util.EnumFacing.DOWN)) return true;
        return false;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int md = this.getMetaFromState(state);
        if (md < 6) {
            for (int t = 0; t < 6; t++) {
                drops.add(new ItemStack(ConfigItems.itemShard, 1, md));
            }
            return;
        }
        if (md == 6) {
            for (int t = 0; t < 6; t++) {
                drops.add(new ItemStack(ConfigItems.itemShard, 1, t));
            }
            return;
        }
        if (md == 7) {
            drops.add(new ItemStack(ConfigItems.itemShard, 1, 6));
            return;
        }
        super.getDrops(drops, world, pos, state, fortune);
    }

    @Override
    public boolean canStabaliseInfusion(World world, int x, int y, int z) {
        return true;
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
}
