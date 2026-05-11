package thaumcraft.common.blocks;

import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileNode;

public class BlockMagicalLog extends BlockLog {

    public static final String[] woodType = new String[]{"greatwood", "silverwood"};

    public BlockMagicalLog() {
        this.setHardness(2.5f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // greatwood
        list.add(new ItemStack(this, 1, 1)); // silverwood
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if ((meta & 3) == 2) return 1; // silverwood knot -> silverwood
        return meta & 3;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return (this.getMetaFromState(state) & 3) == 2; // silverwood knot has node
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if ((this.getMetaFromState(state) & 3) == 2) {
            return new TileNode();
        }
        return null;
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
    public int getFlammability(IBlockAccess world, net.minecraft.util.math.BlockPos pos, net.minecraft.util.EnumFacing face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, net.minecraft.util.math.BlockPos pos, net.minecraft.util.EnumFacing face) {
        return 5;
    }
}
