package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuiResearchRecipeStaticGuardTest {

    @Test
    public void thaumonomiconRecipeGuiShouldNotRenderRawPageEnums() throws IOException {
        String source = read("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");

        assertTrue("GuiResearchRecipe must dispatch by ResearchPage.PageType",
                source.contains("switch (page.type)")
                        && source.contains("drawArcaneCraftingPage")
                        && source.contains("drawCruciblePage")
                        && source.contains("drawInfusionPage"));
        assertTrue("Research text pages must handle TC4 markup tokens",
                source.contains("<BR>") && source.contains("<LINE>") && source.contains("<IMG>"));
        assertFalse("Raw enum names must never be drawn into the Thaumonomicon",
                source.contains("page.type.name()"));
    }

    @Test
    public void thaumonomiconRecipeLangKeysShouldExist() throws IOException {
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(lang.contains("recipe.type.workbench=Workbench"));
        assertTrue(lang.contains("recipe.type.arcane=Arcane Workbench"));
        assertTrue(lang.contains("recipe.type.crucible=Crucible"));
        assertTrue(lang.contains("recipe.type.construct=Mystical Construct"));
        assertTrue(lang.contains("tc.inst.5=§4Dangerous§0"));
    }

    @Test
    public void thaumonomiconTextPagesShouldUseDedicatedUnicodeFontMetrics() throws IOException {
        String source = read("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");

        assertTrue("Research text must use the dedicated forced-Unicode renderer expected by TC4 page layouts",
                source.contains("private final FontRenderer researchFontRenderer;")
                        && source.contains("new FontRenderer(")
                        && source.contains("minecraft.getTextureManager(), true)")
                        && source.contains("this.researchFontRenderer.listFormattedStringToWidth")
                        && source.contains("this.researchFontRenderer.drawString"));
        assertTrue("TC4 markup must be laid out with the same renderer in a single wrapped text pass",
                source.contains("prepareMarkupText(text, inserts)")
                        && source.contains("this.researchFontRenderer.FONT_HEIGHT"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
