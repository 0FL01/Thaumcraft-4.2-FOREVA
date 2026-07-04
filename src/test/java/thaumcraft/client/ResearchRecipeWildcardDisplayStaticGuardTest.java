package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ResearchRecipeWildcardDisplayStaticGuardTest {

    @Test
    public void wildcardRecipeInputsShouldRenderConcreteDisplayStacks() throws IOException {
        String inventoryUtils = read("src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java");
        String shapedArcaneRecipe = read("src/main/java/thaumcraft/api/crafting/ShapedArcaneRecipe.java");
        String guiResearchRecipe = read("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");

        assertTrue("Shaped arcane recipes keep TC4 wildcard Block inputs for crafting compatibility",
                shapedArcaneRecipe.contains("new ItemStack((Block)in, 1, Short.MAX_VALUE)"));

        assertTrue("Research recipe GUI must pass all raw recipe inputs through InventoryUtils.cycleItemStack before rendering",
                guiResearchRecipe.contains("ItemStack stack = InventoryUtils.cycleItemStack(input);")
                        && guiResearchRecipe.contains("return this.safeCopy(stack);"));

        assertTrue("Non-subtype wildcard recipe ingredients such as Blocks.DISPENSER must be rendered as metadata 0 instead of the missing-model wildcard stub",
                inventoryUtils.contains("} else if (it.getMetadata() == Short.MAX_VALUE) {")
                        && inventoryUtils.contains("new ItemStack(it.getItem(), 1, 0)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
