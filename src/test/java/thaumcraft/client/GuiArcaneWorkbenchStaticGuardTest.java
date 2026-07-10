package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuiArcaneWorkbenchStaticGuardTest {

    @Test
    public void arcaneWorkbenchGuiShouldRenderArcaneCostAndInsufficientVisOverlay() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/gui/GuiArcaneWorkbench.java")), StandardCharsets.UTF_8);

        assertTrue("GuiArcaneWorkbench must consume the container's unified player-local preview",
                source.contains("this.workbenchContainer.getArcanePreviewResult()")
                        && source.contains("this.workbenchContainer.getArcanePreviewCost()")
                        && !source.contains("ThaumcraftCraftingManager.findMatchingArcaneRecipe("));
        assertTrue("GuiArcaneWorkbench must render primal aspect-based vis costs",
                source.contains("Aspect.getPrimalAspects()")
                        && source.contains("drawArcaneCostTags(")
                        && source.contains("wand.getConsumptionModifier(")
                        && source.contains("UtilsFX.drawTag(x, y, primal, drawAmount, 0, this.zLevel, 771, alpha, false);"));
        assertTrue("GuiArcaneWorkbench must tint baked ghost vertices and handle builtin results without relying on RenderItem's reset GL color",
                source.contains("LightUtil.renderQuadColor(buffer, quad, ghostColor(color));")
                        && source.contains("GHOST_ALPHA = 168")
                        && source.contains("GL11.GL_TEXTURE_ENV_COLOR")
                        && source.contains("this.itemRender.renderItemIntoGUI(stack, x, y);"));
        assertFalse("The insufficient-vis ghost must not rely on a GL color that RenderItem resets to white",
                source.contains("GlStateManager.color(0.33F, 0.33F, 0.33F, 0.66F)"));
        assertTrue("GuiArcaneWorkbench must keep the insufficient vis warning surface",
                source.contains("Insufficient vis")
                        && source.contains("drawInsufficientVisResult("));
    }
}
