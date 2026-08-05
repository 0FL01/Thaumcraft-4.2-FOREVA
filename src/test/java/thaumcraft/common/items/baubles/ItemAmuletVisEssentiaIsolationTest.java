package thaumcraft.common.items.baubles;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ItemAmuletVisEssentiaIsolationTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void storedVisRemainsNativeAndIsNotExposedAsEssentia() {
        ItemAmuletVis item = new ItemAmuletVis();
        ItemStack amulet = new ItemStack(item, 1, 1);
        item.storeVis(amulet, Aspect.AIR, 12500);
        item.storeVis(amulet, Aspect.FIRE, 7500);

        assertFalse(item instanceof IEssentiaContainerItem);
        AspectList bonus = ThaumcraftCraftingManager.getBonusTags(amulet, new AspectList());
        assertEquals(0, bonus.getAmount(Aspect.AIR));
        assertEquals(0, bonus.getAmount(Aspect.FIRE));
        assertEquals(12500, item.getVis(amulet, Aspect.AIR));
        assertEquals(7500, item.getAllVis(amulet).getAmount(Aspect.FIRE));
    }
}
