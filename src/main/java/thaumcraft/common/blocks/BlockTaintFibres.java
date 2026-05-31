package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.util.Random;

public class BlockTaintFibres extends Block {

    public static final String[] fibreTypes = {"taintFibres", "taintGrassShort", "taintGrassTall", "taintSporeStalk", "taintSporeStalkMature"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 4);

    public BlockTaintFibres() {
        super(Material.VINE);
        this.setHardness(0.5f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;
        if (!isTaintBiome(world, pos)) {
            world.setBlockToAir(pos);
        }
    }

    private static boolean isTaintBiome(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        return Biome.getIdForBiome(biome) == Config.biomeTaintID
                || biome == ThaumcraftWorldGenerator.biomeTaint
                || biome != null && ThaumcraftWorldGenerator.biomeTaint != null
                && Biome.getIdForBiome(biome) == Biome.getIdForBiome(ThaumcraftWorldGenerator.biomeTaint);
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
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
}
