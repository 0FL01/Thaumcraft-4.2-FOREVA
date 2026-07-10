package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Eldritch stone stairs used in Eldritch dimension room generation.
 * Based on original BlockCosmeticStairs.
 */
public class BlockStairsEldritch extends BlockThaumcraftStairs {

    public BlockStairsEldritch() {
        super(ConfigBlocks.blockCosmeticSolid.getStateFromMeta(11));
        this.setLightOpacity(0);
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
        this.useNeighborBrightness = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
