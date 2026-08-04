package thaumcraft.common.clientguard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ResearchRecipeItemTooltipStaticGuardTest {

    private static final String SOURCE_PATH = "src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java";

    @Test
    public void itemEntriesShouldUseTheStandardStackAwareTooltipPath() throws IOException {
        String source = readSource();
        assertTrue(source.contains("private ItemStack tooltipStack = ItemStack.EMPTY;"));
        assertTrue(source.contains("this.setItemTooltip(stack, mouseX, mouseY);"));
        assertTrue(source.contains("this.drawLinkedItemTooltip(this.tooltipStack, this.tooltipX, this.tooltipY);"));
        assertTrue(source.contains("this.renderToolTip(stack, x, y);"));

        int drawScreen = source.indexOf("public void drawScreen(int mouseX, int mouseY, float partialTicks)");
        int drawPages = source.indexOf("this.drawPage(", drawScreen);
        int drawBase = source.indexOf("super.drawScreen(mouseX, mouseY, partialTicks);", drawPages);
        int drawItemTooltip = source.indexOf("this.drawLinkedItemTooltip(this.tooltipStack, this.tooltipX, this.tooltipY);", drawBase);
        assertTrue(drawPages > drawScreen);
        assertTrue(drawBase > drawPages);
        assertTrue(drawItemTooltip > drawBase);
    }

    @Test
    public void itemAndAspectTooltipsShouldRemainMutuallyExclusive() throws IOException {
        String source = readSource();
        int textSetter = source.indexOf("private void setTooltip(List<String> lines, int x, int y)");
        int itemSetter = source.indexOf("private void setItemTooltip(ItemStack stack, int x, int y)");
        assertTrue(source.indexOf("this.tooltipStack = ItemStack.EMPTY;", textSetter) > textSetter);
        assertTrue(source.indexOf("this.tooltip = null;", itemSetter) > itemSetter);
        assertTrue(source.indexOf("this.tooltipStack = stack;", itemSetter) > itemSetter);
    }

    private static String readSource() throws IOException {
        return new String(Files.readAllBytes(Paths.get(SOURCE_PATH)), StandardCharsets.UTF_8);
    }
}
