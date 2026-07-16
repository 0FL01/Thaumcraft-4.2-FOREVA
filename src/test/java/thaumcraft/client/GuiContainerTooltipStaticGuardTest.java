package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GuiContainerTooltipStaticGuardTest {

    @Test
    public void arcaneWorkbenchAndHoverHarnessShouldRenderHoveredItemTooltips() throws IOException {
        assertRendersHoveredTooltip("src/main/java/thaumcraft/client/gui/GuiArcaneWorkbench.java");
        assertRendersHoveredTooltip("src/main/java/thaumcraft/client/gui/GuiHoverHarness.java");
    }

    private static void assertRendersHoveredTooltip(String path) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        int drawScreen = source.indexOf("public void drawScreen(int mouseX, int mouseY, float partialTicks)");
        int drawContainer = source.indexOf("super.drawScreen(mouseX, mouseY, partialTicks);", drawScreen);
        int drawTooltip = source.indexOf("this.renderHoveredToolTip(mouseX, mouseY);", drawContainer);

        assertTrue(path + " must override drawScreen", drawScreen >= 0);
        assertTrue(path + " must render the container before its tooltip", drawContainer > drawScreen);
        assertTrue(path + " must render the hovered item tooltip last", drawTooltip > drawContainer);
    }
}
