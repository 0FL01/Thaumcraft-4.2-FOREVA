package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockCustomOreItem extends ItemBlock {
    public static final int[] colors = {0xFFFFFF, 0xFFFF7E, 16727041, 37119, 40960, 0xEECCFF, 0x555577, 0xFFFFFF};

    public BlockCustomOreItem(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }
}
