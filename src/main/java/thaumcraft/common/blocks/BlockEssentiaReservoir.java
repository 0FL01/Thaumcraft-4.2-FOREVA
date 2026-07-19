package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        ItemStack held = playerIn.getHeldItem(hand);
        if (tile instanceof TileEssentiaReservoir && !held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
            return ((TileEssentiaReservoir) tile).onWandRightClick(worldIn, held, playerIn,
                    pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), 0) >= 0;
        }
        return false;
    }

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
        if (tile instanceof TileEssentiaReservoir && ((TileEssentiaReservoir) tile).essentia.visSize() > 0) {
            int spills = Math.max(1, ((TileEssentiaReservoir) tile).essentia.visSize() / 16);
            for (int i = 0; i < Math.min(50, spills); ++i) {
                BlockPos target = pos.add(worldIn.rand.nextInt(5) - worldIn.rand.nextInt(5),
                        worldIn.rand.nextInt(5) - worldIn.rand.nextInt(5),
                        worldIn.rand.nextInt(5) - worldIn.rand.nextInt(5));
                if (!worldIn.isAirBlock(target)) continue;
                worldIn.setBlockState(target, (target.getY() < pos.getY()
                        ? ConfigBlocks.blockFluxGoo.getDefaultState()
                        : ConfigBlocks.blockFluxGas.getDefaultState()), 3);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
