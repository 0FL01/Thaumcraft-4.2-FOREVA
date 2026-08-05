package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuiFocalManipulatorLayoutStaticGuardTest {

    @Test
    public void focalManipulatorShouldKeepOriginalUpgradeControlsAndLayout() throws IOException {
        String source = readSource();

        assertTrue("Start strip must keep the original 48,88 96x8 region",
                source.contains("private static final int START_X = 48;")
                        && source.contains("private static final int START_Y = 88;")
                        && source.contains("private static final int START_WIDTH = 96;")
                        && source.contains("private static final int START_HEIGHT = 8;"));
        assertTrue("Start strip must use the original texture row",
                source.contains("8, 240, START_WIDTH, START_HEIGHT);"));
        assertTrue("Craft progress must reuse the Start strip with aspect tinting",
                source.contains("112 + start, 240, width, START_HEIGHT);"));
        assertTrue("The visible Start strip and click path must share one availability gate and region",
                source.contains("if (canStartUpgrade()) {")
                        && source.contains("if (canStartUpgrade() && isMouseIn(mouseX, mouseY, START_X, START_Y, START_WIDTH, START_HEIGHT))"));
        assertTrue("Start click must keep the vanilla enchant-packet route",
                source.contains("this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, this.selected);"));
        assertTrue("All players must possess the displayed experience level",
                source.contains("this.mc.player.experienceLevel >= required"));
        assertFalse("Creative mode must not bypass the experience prerequisite",
                source.contains("this.mc.player.capabilities.isCreativeMode"));

        assertTrue("Selected upgrades must use the original 200,0 texture highlight",
                source.contains("this.drawTexturedModalRect(x, y, 200, 0, OPTION_SIZE, OPTION_SIZE);"));
        assertTrue("Experience must use the original orb and compact numeric position",
                source.contains("this.guiLeft + 108, this.guiTop + 59, 200, 16, 16, 16")
                        && source.contains("this.guiLeft + 125, this.guiTop + 64"));
        assertTrue("Vis costs must use the original compact half-scale aspect list",
                source.contains("GlStateManager.scale(0.5F, 0.5F, 0.5F);")
                        && source.contains("this.fontRenderer.drawString(aspect.getName(), 0, row * 10, aspect.getColor());"));

        assertFalse("The shifted port-only rank label must stay removed", source.contains("String rankText"));
        assertFalse("Vis costs must not return to large horizontal aspect icons",
                source.contains("drawIcon(aspect.getImage()"));
        assertFalse("The shifted generic progress rectangle must stay removed",
                source.contains("drawRect(this.guiLeft + 60, this.guiTop + 124"));
    }

    @Test
    public void focalManipulatorShouldRenderFocusItemTooltipAfterTheContainer() throws IOException {
        String source = readSource();
        int container = source.indexOf("super.drawScreen(mouseX, mouseY, partialTicks);");
        int itemTooltip = source.indexOf("this.renderHoveredToolTip(mouseX, mouseY);", container);
        int upgradeTooltip = source.indexOf("this.drawUpgradeTooltip(mouseX, mouseY);", itemTooltip);

        assertTrue("Container must render before its item tooltip", container >= 0 && itemTooltip > container);
        assertTrue("Custom upgrade tooltip must remain after the standard item tooltip", upgradeTooltip > itemTooltip);
    }

    private static String readSource() throws IOException {
        return new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java")), StandardCharsets.UTF_8);
    }
}
