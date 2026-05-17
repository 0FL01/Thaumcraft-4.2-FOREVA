package thaumcraft.common.lib.crafting;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumcraftCraftingManagerArcaneMatcherStaticGuardTest {

    @Test
    public void arcaneMatcherMethodsRemainPresentAndNullSafe() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java");

        assertTrue("findMatchingArcaneRecipe should stay present with null-safe early return",
                source.contains("public static ItemStack findMatchingArcaneRecipe(IInventory awb, EntityPlayer player)")
                        && source.contains("if (awb == null || player == null) return ItemStack.EMPTY;"));
        assertTrue("findMatchingArcaneRecipeAspects should stay present with null-safe early return",
                source.contains("public static AspectList findMatchingArcaneRecipeAspects(IInventory awb, EntityPlayer player)")
                        && source.contains("if (awb == null || player == null) return new AspectList();"));
    }

    @Test
    public void arcaneMatcherMethodsShouldUseThaumcraftApiRecipeListAndIAcraneRecipeFilters() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java");

        assertTrue("Arcane matcher should iterate ThaumcraftApi crafting recipes and filter IArcaneRecipe",
                source.contains("for (Object recipe : ThaumcraftApi.getCraftingRecipes())")
                        && source.contains("if (!(recipe instanceof IArcaneRecipe)) continue;"));
        assertTrue("Arcane matcher should call IArcaneRecipe.matches against player world and player",
                source.contains("arcaneRecipe.matches(awb, player.world, player)")
                        && source.contains("if (!arcaneRecipe.matches(awb, player.world, player)) continue;"));
        assertTrue("Arcane matcher should return recipe output and aspect cost from matched arcane recipe",
                source.contains("arcaneRecipe.getCraftingResult(awb)")
                        && source.contains("return arcaneRecipe.getAspects() != null ? arcaneRecipe.getAspects() : arcaneRecipe.getAspects(awb);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
