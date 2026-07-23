package thaumcraft.common.clientguard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SyntheticItemTooltipStaticGuardTest {

    private static final Path GUI_DIRECTORY = Paths.get("src/main/java/thaumcraft/client/gui");

    @Test
    public void arcaneWorkbenchShouldRenderOnlyItsEmptySlotPreviewTooltip() throws IOException {
        String source = read("GuiArcaneWorkbench.java");
        assertDrawOrder(source, "this.renderArcanePreviewTooltip(mouseX, mouseY);");
        assertTrue(source.contains("!this.playerInventory.getItemStack().isEmpty()"));
        assertTrue(source.contains("this.inventorySlots.getSlot(0).getHasStack()"));
        assertTrue(source.contains("this.isPointInRegion(160, 64, 16, 16, mouseX, mouseY)"));
        assertTrue(source.contains("this.renderToolTip(preview, mouseX, mouseY);"));
        assertEquals(1, countOccurrences(source, "this.renderToolTip(preview, mouseX, mouseY);"));
    }

    @Test
    public void thaumatoriumShouldRenderItsRecipeOutputTooltip() throws IOException {
        String source = read("GuiThaumatorium.java");
        assertDrawOrder(source, "this.renderRecipeOutputTooltip(mouseX, mouseY);");
        assertTrue(source.contains("!this.mc.player.inventory.getItemStack().isEmpty()"));
        assertTrue(source.contains("isMouseIn(mouseX, mouseY, 112, 16, 16, 16)"));
        assertTrue(source.contains("this.renderToolTip(output, mouseX, mouseY);"));
        assertEquals(1, countOccurrences(source, "this.renderToolTip(output, mouseX, mouseY);"));
    }

    private static void assertDrawOrder(String source, String syntheticCall) {
        int drawScreen = source.indexOf("public void drawScreen(int mouseX, int mouseY, float partialTicks)");
        int drawContainer = source.indexOf("super.drawScreen(mouseX, mouseY, partialTicks);", drawScreen);
        int drawSlotTooltip = source.indexOf("this.renderHoveredToolTip(mouseX, mouseY);", drawContainer);
        int drawSyntheticTooltip = source.indexOf(syntheticCall, drawSlotTooltip);

        assertTrue(drawScreen >= 0);
        assertTrue(drawContainer > drawScreen);
        assertTrue(drawSlotTooltip > drawContainer);
        assertTrue(drawSyntheticTooltip > drawSlotTooltip);
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            ++count;
            index += needle.length();
        }
        return count;
    }

    private static String read(String fileName) throws IOException {
        return new String(Files.readAllBytes(GUI_DIRECTORY.resolve(fileName)), StandardCharsets.UTF_8);
    }
}
