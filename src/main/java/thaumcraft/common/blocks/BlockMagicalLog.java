package thaumcraft.common.blocks;

import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileNode;

public class BlockMagicalLog extends BlockLog {

    public static final String[] woodType = new String[]{"greatwood", "silverwood"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 3);

    public BlockMagicalLog() {
        this.setHardness(2.5f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
        this.setHarvestLevel("axe", 0);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // greatwood
        list.add(new ItemStack(this, 1, 1)); // silverwood
    }

    @Override
    public int damageDropped(IBlockState state) {
        int type = state.getValue(TYPE);
        if (type == 2 || type == 3) return 1; // silverwood knot/legacy alias -> silverwood
        return type;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(TYPE) == 2; // silverwood knot has node
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(TYPE) == 2) {
            return new TileNode();
        }
        return null;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        int type = state.getValue(TYPE);
        return (type == 1 || type == 2) ? 7 : super.getLightValue(state, world, pos);
    }

    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, net.minecraft.util.math.BlockPos pos) {
        return true;
    }

    @Override
    public boolean isWood(IBlockAccess world, net.minecraft.util.math.BlockPos pos) {
        return true;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, LOG_AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int type = meta & 3;
        int axisBits = (meta >> 2) & 3;
        IBlockState state = this.getDefaultState().withProperty(TYPE, type);
        switch (axisBits) {
            case 0: state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y); break;
            case 1: state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X); break;
            case 2: state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z); break;
            case 3: state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE); break;
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(TYPE) & 3;
        switch (state.getValue(LOG_AXIS)) {
            case X: meta |= 1 << 2; break;
            case Z: meta |= 2 << 2; break;
            case NONE: meta |= 3 << 2; break;
            default: break;
        }
        return meta;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 3));
        EnumFacing.Axis axis = placer.getHorizontalFacing().getAxis();
        switch (axis) {
            case X: state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X); break;
            case Z: state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z); break;
            default: state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y); break;
        }
        return state;
    }
}
