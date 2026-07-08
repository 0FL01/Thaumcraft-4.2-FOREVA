package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileLifter;

import java.util.Random;

public class BlockLifter extends BlockContainer {

    public BlockLifter() {
        super(Material.WOOD);
        this.setHardness(2.5F);
        this.setResistance(15.0F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.UP && side != EnumFacing.DOWN;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @javax.annotation.Nullable EnumFacing side) {
        return side != null && side.getHorizontalIndex() >= 0;
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileLifter) {
            TileLifter lifter = (TileLifter) te;
            if (!lifter.gettingPower() && lifter.rangeAbove > 0) {
                Thaumcraft.proxy.sparkle(pos.getX() + 0.2F + rand.nextFloat() * 0.6F,
                        pos.getY() + 1.0F,
                        pos.getZ() + 0.2F + rand.nextFloat() * 0.6F,
                        1.0F, 3, -0.3F);
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        updateLifterStack(worldIn, pos);
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        updateLifterStack(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileLifter) {
            TileLifter lifter = (TileLifter) te;
            if (lifter.gettingPower() != lifter.lastPowerState) {
                updateLifterStack(worldIn, pos);
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    private void updateLifterStack(World world, BlockPos pos) {
        int count = 1;
        BlockPos below = pos.down(count);
        while (world.getBlockState(below).getBlock() == this) {
            TileEntity te = world.getTileEntity(below);
            if (te instanceof TileLifter) {
                ((TileLifter) te).requiresUpdate = true;
            }
            count++;
            below = pos.down(count);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileLifter();
    }
}
