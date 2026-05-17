package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GuiArcaneWorkbenchStaticGuardTest {

    @Test
    public void arcaneWorkbenchGuiShouldRenderArcaneCostAndInsufficientVisOverlay() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/gui/GuiArcaneWorkbench.java")), StandardCharsets.UTF_8);

        assertTrue("GuiArcaneWorkbench must query arcane matcher output and cost to mirror reference vis gating",
                source.contains("ThaumcraftCraftingManager.findMatchingArcaneRecipe(")
                        && source.contains("ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects("));
        assertTrue("GuiArcaneWorkbench must render primal aspect-based vis costs",
                source.contains("Aspect.getPrimalAspects()")
                        && source.contains("drawArcaneCostTags(")
                        && source.contains("wand.getConsumptionModifier("));
        assertTrue("GuiArcaneWorkbench must keep the insufficient vis warning surface",
                source.contains("Insufficient vis")
                        && source.contains("drawInsufficientVisResult("));
    }
}
