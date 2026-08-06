package thaumcraft.common.items;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import static org.junit.Assert.assertEquals;

public class ItemNuggetEdibleRegistryRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void legacySubtypeCarrierIsHiddenButSeparateItemIsExposed() {
        NonNullList<ItemStack> legacy = NonNullList.create();
        new ItemNuggetEdible().getSubItems(CreativeTabThaumcraft.tabThaumcraft, legacy);
        assertEquals(0, legacy.size());

        NonNullList<ItemStack> current = NonNullList.create();
        ItemNuggetEdible item = new ItemNuggetEdible(false);
        item.getSubItems(CreativeTabThaumcraft.tabThaumcraft, current);
        assertEquals(1, current.size());
        assertEquals(0, current.get(0).getMetadata());
        assertEquals(10, item.getMaxItemUseDuration(current.get(0)));
    }
}
