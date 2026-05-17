package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;
import thaumcraft.common.config.ConfigBlocks;

public class BlockCosmeticWoodSlabItem extends ItemSlab {

    public BlockCosmeticWoodSlabItem(Block block) {
        super(block, ConfigBlocks.blockSlabWood, ConfigBlocks.blockDoubleSlabWood);
    }
}
