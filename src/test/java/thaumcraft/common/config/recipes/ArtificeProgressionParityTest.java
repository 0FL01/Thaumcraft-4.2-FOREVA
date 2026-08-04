package thaumcraft.common.config.recipes;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.init.Bootstrap;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.crafting.InfusionRecipe;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArtificeProgressionParityTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void legacyPotionIngredientsUseCraftable112PotionStacks() {
        assertPotion(ConfigRecipesInfusionEquipmentSlice.drinkablePotion(PotionTypes.STRONG_STRENGTH),
                Items.POTIONITEM, PotionTypes.STRONG_STRENGTH);
        assertPotion(ConfigRecipesInfusionEquipmentSlice.drinkablePotion(PotionTypes.STRONG_SWIFTNESS),
                Items.POTIONITEM, PotionTypes.STRONG_SWIFTNESS);
        assertPotion(ConfigRecipesInfusionEquipmentSlice.drinkablePotion(PotionTypes.LONG_REGENERATION),
                Items.POTIONITEM, PotionTypes.LONG_REGENERATION);
        assertPotion(ConfigRecipesInfusionEquipmentSlice.splashPotion(PotionTypes.STRONG_HARMING),
                Items.SPLASH_POTION, PotionTypes.STRONG_HARMING);

        ItemStack expected = ConfigRecipesInfusionEquipmentSlice.splashPotion(PotionTypes.STRONG_HARMING);
        ItemStack wrongForm = ConfigRecipesInfusionEquipmentSlice.drinkablePotion(PotionTypes.STRONG_HARMING);
        assertTrue(InfusionRecipe.areItemStacksEqual(expected.copy(), expected, true));
        assertFalse(InfusionRecipe.areItemStacksEqual(wrongForm, expected, true));
    }

    @Test
    public void artificeResearchKeepsOriginalKineticPageAndSinisterItemWarp() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/config/research/ConfigResearchArtifice.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("new ResearchPage(ConfigResearch.recipeInfusion(\"RunicGirdleKinetic\"))"));
        assertFalse(source.contains("new ResearchPage(ConfigResearch.recipeInfusion(\"RunicGirdleKinetic_2\"))"));
        assertTrue(source.contains("ThaumcraftApi.addWarpToResearch(\"SINSTONE\", 2);"));
        assertTrue(source.contains("ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.itemCompassStone), 1);"));
    }

    private static void assertPotion(ItemStack stack, Item item, PotionType type) {
        assertEquals(item, stack.getItem());
        assertEquals(0, stack.getMetadata());
        assertEquals(type, PotionUtils.getPotionFromItem(stack));
    }
}
