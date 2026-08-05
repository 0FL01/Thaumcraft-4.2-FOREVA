package thaumcraft.common.config.recipes;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigRecipesWandBasicOutputTest {
    private ItemWandCasting oldWand;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void installWandFixture() {
        this.oldWand = ConfigItems.itemWandCasting;
        ConfigItems.itemWandCasting = new ItemWandCasting();
    }

    @After
    public void restoreWandFixture() {
        ConfigItems.itemWandCasting = this.oldWand;
    }

    @Test
    public void canonicalWandBasicOutputCarriesExplicitIronAndWoodTags() {
        ItemStack output = ConfigRecipesSpecialSlice.createBasicWandRecipeOutput();

        assertTrue(output.hasTagCompound());
        assertEquals("iron", output.getTagCompound().getString(ItemWandCasting.TAG_CAP));
        assertEquals("wood", output.getTagCompound().getString(ItemWandCasting.TAG_ROD));
        assertEquals(2, output.getTagCompound().getKeySet().size());
    }
}
