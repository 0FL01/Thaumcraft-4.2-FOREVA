package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class BlockArcaneFurnaceItem extends BlockMetadataItem {
    public BlockArcaneFurnaceItem(Block block) {
        super(block);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.block.getTranslationKey();
    }
}
