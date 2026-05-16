package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;

public class BlockMagicalLeaves extends Block implements net.minecraftforge.common.IShearable {

    public static final String[] leafType = {"greatwood", "silverwood"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 1);

    public BlockMagicalLeaves() {
        super(Material.LEAVES);
        this.setHardness(0.2f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state) & 1;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
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
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public java.util.List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return java.util.Collections.singletonList(new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)) & 3));
    }

    @Override
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return 30;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 1));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 1));
    }
}
