package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileEssentiaReservoir;

public class BlockEssentiaReservoir extends BlockContainer {
    public BlockEssentiaReservoir() {
        super(Material.IRON);
        this.setHardness(2.0F);
        this.setResistance(17.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.TRANSLUCENT; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileEssentiaReservoir(); }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) { return true; }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEssentiaReservoir) {
            TileEssentiaReservoir reservoir = (TileEssentiaReservoir) tile;
            return MathHelper.floor((float) reservoir.essentia.visSize() / (float) reservoir.maxAmount * 14.0F)
                    + (reservoir.essentia.visSize() > 0 ? 1 : 0);
        }
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEssentiaReservoir) {
            int spills = ((TileEssentiaReservoir) tile).essentia.visSize() / 16;
            if (spills > 0) {
                worldIn.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1.0F, false);
                int placed = 0;
                for (int attempt = 0; attempt < 50; ++attempt) {
                    BlockPos target = pos.add(worldIn.rand.nextInt(5) - worldIn.rand.nextInt(5),
                            worldIn.rand.nextInt(5) - worldIn.rand.nextInt(5),
                            worldIn.rand.nextInt(5) - worldIn.rand.nextInt(5));
                    if (!worldIn.isAirBlock(target)) continue;
                    worldIn.setBlockState(target, this.getFluxState(target.getY() < pos.getY()), 3);
                    if (placed++ >= spills) break;
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    protected IBlockState getFluxState(boolean goo) {
        return (goo ? ConfigBlocks.blockFluxGoo : ConfigBlocks.blockFluxGas).getDefaultState();
    }
}
