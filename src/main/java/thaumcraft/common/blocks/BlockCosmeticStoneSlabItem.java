package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;
import thaumcraft.common.config.ConfigBlocks;

public class BlockCosmeticStoneSlabItem extends ItemSlab {

    public BlockCosmeticStoneSlabItem(Block block) {
        super(block, ConfigBlocks.blockSlabStone, ConfigBlocks.blockDoubleSlabStone);
    }
}
