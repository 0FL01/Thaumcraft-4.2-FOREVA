package thaumcraft.client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GuiThaumatoriumVisualParityStaticGuardTest {

    @Test
    public void thaumatoriumShouldRestoreTc4AspectAndSelectionRendering() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/gui/GuiThaumatorium.java")), StandardCharsets.UTF_8);
        int aspectsStart = source.indexOf("private void drawAspects(");
        int outputStart = source.indexOf("private void drawOutput(", aspectsStart);
        String aspects = source.substring(aspectsStart, outputStart);

        assertTrue("Required aspect amounts must use the readable tinted tag renderer",
                aspects.contains("UtilsFX.drawTag(x, y, aspect, required, 0, this.zLevel, 771, 1.0F, false, true);"));
        assertTrue("Essentia gauges must only render for the selected recipe",
                aspects.indexOf("if (isSelected(recipe)) {") < aspects.indexOf("176, 8, 14, 6"));
        assertTrue("Gauge fills must remain clamped to the original twelve-pixel span",
                aspects.contains("MathHelper.clamp(") && aspects.contains("* 12.0F), 0, 12)"));
    }

    @Test
    public void outputPulseAndRecipeCounterShouldKeepTc4Semantics() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/gui/GuiThaumatorium.java")), StandardCharsets.UTF_8);

        assertTrue("Output dull/pulse must follow the same availability gate as recipe toggling",
                source.contains("this.inventory.recipeHash.size() < this.inventory.maxRecipes || isSelected(recipe)"));
        assertTrue("Compact recipe counts must remain crisp under Unicode locales",
                source.contains("UtilsFX.drawCompactCenteredString(this.fontRenderer, text, 0.0F, 0.0F, 0xFFFFFF);"));
    }
}
