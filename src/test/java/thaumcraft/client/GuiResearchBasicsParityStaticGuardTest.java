package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuiResearchBasicsParityStaticGuardTest {

    @Test
    public void aspectsEntryShouldAppendDiscoveredAspectAndSourceItemPages() throws IOException {
        String recipe = read("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");
        String mapper = read("src/main/java/thaumcraft/client/gui/MappingThread.java");
        String ticks = read("src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java");

        assertTrue(recipe.contains("\"ASPECTS\".equals(research.key)")
                && recipe.contains("knowledge.getAspectsDiscovered()")
                && recipe.contains("visiblePages.add(new ResearchPage(aspectPage.copy()))")
                && recipe.contains("knowledge.getScannedItems()")
                && recipe.contains("this.aspectItems.put(aspect, items)")
                && recipe.contains("this.drawAspectSources(aspect, mouseX, mouseY)"));
        assertTrue(mapper.contains("for (Item item : Item.REGISTRY)")
                && mapper.contains("ScanManager.generateItemHash(item, stack.getMetadata())")
                && ticks.contains("new Thread(new MappingThread()"));
    }

    @Test
    public void nodeJarShouldRenderEveryCompoundAspectCost() throws IOException {
        String recipe = read("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");

        assertTrue(recipe.contains("aspects == null ? 0 : aspects.size(), mouseX, mouseY, 1"));
        assertFalse(recipe.contains("y + 182, 5, mouseX, mouseY, 1"));
    }

    @Test
    public void browserShouldUseGalacticTitlesAndSeparateTooltipContentFromExtras() throws IOException {
        String browser = read("src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java");

        assertTrue(browser.contains("minecraft.standardGalacticFontRenderer"));
        assertTrue(browser.contains("ScaledTextBlock summary = this.layoutTooltipText")
                && browser.contains("cursorY += summary.height;")
                && browser.contains("cursorY += 16;")
                && browser.contains("cursorY + 3"));
    }

    @Test
    public void researchMasteryCombinationShortcutShouldUseShift() throws IOException {
        String table = read("src/main/java/thaumcraft/client/gui/GuiResearchTable.java");

        assertTrue(table.contains("this.isShiftKeyDown() && RESEARCHER_2"));
        assertFalse(table.contains("this.isCtrlKeyDown() && RESEARCHER_2"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
