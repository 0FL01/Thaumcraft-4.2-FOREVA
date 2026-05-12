package thaumcraft.common.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.world.WorldGenGreatwoodTrees;
import thaumcraft.common.lib.world.WorldGenSilverwoodTrees;
import thaumcraft.common.tiles.TileEtherealBloom;

import java.util.Random;

public class BlockCustomPlant extends BlockBush {

    public static final String[] plantTypes = {"greatwoodSapling", "silverwoodSapling", "shimmerleaf", "cinderpearl", "etherealBloom", "manashroom"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 5);

    public BlockCustomPlant() {
        super(Material.PLANTS);
        this.setHardness(0.0f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, Math.min(Math.max(meta, 0), 5));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 6; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = state.getValue(TYPE);
        if (meta == 1 || meta == 2 || meta == 3) return 8;
        if (meta == 4) return 15;
        if (meta == 5) return 8;
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(TYPE) == 4;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(TYPE) == 4) {
            return new TileEtherealBloom();
        }
        return null;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        int type = world.getBlockState(pos).getValue(TYPE);
        if (type == 2) return EnumPlantType.Plains;
        if (type == 3) return EnumPlantType.Desert;
        if (type >= 4) return EnumPlantType.Cave;
        return EnumPlantType.Plains;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;
        super.updateTick(world, pos, state, rand);
        int type = state.getValue(TYPE);
        if (type == 0 && world.getLight(pos.up()) >= 9 && rand.nextInt(25) == 0) {
            this.growGreatTree(world, pos, rand);
        } else if (type == 1 && world.getLight(pos.up()) >= 9 && rand.nextInt(50) == 0) {
            this.growSilverTree(world, pos, rand);
        }
    }

    private void growGreatTree(World world, BlockPos pos, Random rand) {
        if (world == null || world.provider == null) return;
        if (world.isRemote) return;
        world.setBlockToAir(pos);
        WorldGenGreatwoodTrees obj = new WorldGenGreatwoodTrees(true);
        if (!obj.generate(world, rand, pos)) {
            world.setBlockState(pos, this.getStateFromMeta(0));
        }
    }

    private void growSilverTree(World world, BlockPos pos, Random rand) {
        if (world == null || world.provider == null) return;
        if (world.isRemote) return;
        world.setBlockToAir(pos);
        WorldGenSilverwoodTrees obj = new WorldGenSilverwoodTrees(true, 7, 5);
        if (!obj.generate(world, rand, pos)) {
            world.setBlockState(pos, this.getStateFromMeta(1));
        }
    }
}
