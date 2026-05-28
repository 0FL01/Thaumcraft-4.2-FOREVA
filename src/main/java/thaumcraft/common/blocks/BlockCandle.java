package thaumcraft.common.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.common.Thaumcraft;

public class BlockCandle extends Block implements IInfusionStabiliser {

    /** Non-null zero-size AABB — replaces NULL_AABB which is null in 1.12.2. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 15);
    public static final int[] COLORS = new int[]{
            0xF0F0F0, 0xEB8844, 0xC353CD, 0x6689F3,
            0xDEC850, 0x41CC54, 0xD884B8, 0x434343,
            0xA0A0A0, 0x287697, 0x7B337E, 0x253193,
            0x51301A, 0x3B513A, 0xB3342C, 0x1E1B1B
    };
    private static final AxisAlignedBB CANDLE_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5D, 0.625D);

    public BlockCandle() {
        super(Material.CIRCUITS);
        this.setHardness(0.1F);
        this.setSoundType(SoundType.CLOTH);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setLightLevel(0.95F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i < 16; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return;
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CANDLE_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return ZERO_AABB;
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
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.7D;
        double z = pos.getZ() + 0.5D;
        Thaumcraft.proxy.drawGenericParticles(worldIn,
                x, y, z,
                0.0D, 0.0D, 0.0D,
                0.25F, 0.25F, 0.25F, 0.7F,
                false, 0, 8, -1, 8, 0, 0.45F, 1);
        Thaumcraft.proxy.drawGenericParticles(worldIn,
                x, y, z,
                0.0D, 0.0D, 0.0D,
                1.0F, 1.0F, 1.0F, 0.9F,
                false, 48, 1, 1, 12, 0, 0.3F, 1);
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
        return this.getDefaultState().withProperty(TYPE, Math.max(0, Math.min(15, meta)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    public static int getCandleColor(int meta) {
        int idx = Math.max(0, Math.min(15, meta));
        return COLORS[idx];
    }
}
