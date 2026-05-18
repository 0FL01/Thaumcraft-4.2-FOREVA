package thaumcraft.common.lib.crafting;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumcraftCraftingManagerInfusionMatcherStaticGuardTest {

    @Test
    public void infusionMatcherMethodsRemainPresentAndNullSafe() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java");

        assertTrue("findMatchingInfusionRecipe should stay present with null-safe early return",
                source.contains("public static InfusionRecipe findMatchingInfusionRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player)")
                        && source.contains("if (items == null || input == null || input.isEmpty() || player == null) return null;"));
        assertTrue("findMatchingInfusionEnchantmentRecipe should stay present with null-safe early return",
                source.contains("public static InfusionEnchantmentRecipe findMatchingInfusionEnchantmentRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player)")
                        && source.contains("if (items == null || input == null || input.isEmpty() || player == null) return null;"));
    }

    @Test
    public void infusionMatcherMethodsShouldUseThaumcraftApiRecipeListAndRecipeFilters() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java");

        assertTrue("Infusion matcher should iterate ThaumcraftApi crafting recipes and filter InfusionRecipe",
                source.contains("for (Object recipe : ThaumcraftApi.getCraftingRecipes())")
                        && source.contains("if (!(recipe instanceof InfusionRecipe)) continue;")
                        && source.contains("if (infusionRecipe.matches(items, input, player.world, player)) return infusionRecipe;"));
        assertTrue("Infusion enchantment matcher should iterate ThaumcraftApi crafting recipes and filter InfusionEnchantmentRecipe",
                source.contains("if (!(recipe instanceof InfusionEnchantmentRecipe)) continue;")
                        && source.contains("if (infusionRecipe.matches(items, input, player.world, player)) return infusionRecipe;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
