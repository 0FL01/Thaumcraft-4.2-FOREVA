package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import thaumcraft.common.blocks.ItemBlocks.BlockCustomOreItem;

public final class LifterItemColor implements IItemColor {
    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return BlockCustomOreItem.colors[4];
        }
        if (tintIndex == 1) {
            return BlockCustomOreItem.colors[5];
        }
        return -1;
    }
}
