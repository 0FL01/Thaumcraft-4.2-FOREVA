package thaumcraft.client.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.research.ScanManager;

@SideOnly(Side.CLIENT)
public class MappingThread implements Runnable {

    @Override
    public void run() {
        for (Item item : Item.REGISTRY) {
            if (item == null) {
                continue;
            }
            try {
                NonNullList<ItemStack> stacks = NonNullList.create();
                CreativeTabs tab = item.getCreativeTab();
                item.getSubItems(tab, stacks);
                for (ItemStack stack : stacks) {
                    if (stack == null || stack.isEmpty()) {
                        continue;
                    }
                    GuiResearchRecipe.putToCache(
                            ScanManager.generateItemHash(item, stack.getMetadata()), stack);
                }
            } catch (RuntimeException ignored) {
            }
        }
    }
}
