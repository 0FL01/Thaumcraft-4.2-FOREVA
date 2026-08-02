package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GolemUseCoreParityStaticGuardTest {

    @Test
    public void emptyHandUseCoreKeepsReferenceFilterAndToggleContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java");

        assertTrue("Empty filter slots must expose their marker colors for empty-hand Use targets",
                source.contains("boolean allEmpty = true;")
                        && source.contains("if (allEmpty) {")
                        && source.contains("result.add(this.colors != null && slot < this.colors.length"));
        assertTrue("Configured filters must match the carried item before exposing a marker color",
                source.contains("InventoryUtils.areItemStacksEqual(filter, match,")
                        && source.contains("this.checkOreDict(), this.ignoreDamage(), this.ignoreNBT()"));
        assertFalse("Empty-hand matching must not return before inspecting an empty filter inventory",
                source.contains("if (match == null || match.isEmpty()) return result;"));
        assertTrue("Entropy comparison settings must remain backed by toggle bits 5 through 7",
                source.contains("public boolean checkOreDict() { return this.getToggles()[5]; }")
                        && source.contains("public boolean ignoreDamage() { return this.getToggles()[6]; }")
                        && source.contains("public boolean ignoreNBT() { return this.getToggles()[7]; }"));
    }

    @Test
    public void useCoreKeepsBoundFakePlayerInteractionContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/ai/interact/AIUseItem.java");

        assertTrue("Use AI must invoke actions through the FakePlayer's bound interaction manager",
                source.contains("this.im = this.player.interactionManager;"));
        assertTrue("Use AI must apply and clear the configured sneaking state",
                source.contains("this.player.setSneaking(this.theGolem.getToggles()[2]);")
                        && source.contains("finally {")
                        && source.contains("this.player.setSneaking(false);"));
        assertTrue("Right-click Use must pass the face stored by the golem marker",
                source.contains("EnumHand.MAIN_HAND, targetPos, clickSide,")
                        && !source.contains("targetPos, clickSide.getOpposite(),"));
    }

    @Test
    public void golemGuiKeepsReferenceUseControlsAndPresentation() throws IOException {
        String gui = read("src/main/java/thaumcraft/client/gui/GuiGolem.java");
        String utils = read("src/main/java/thaumcraft/common/lib/utils/Utils.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("Use GUI must label all three mode toggles",
                gui.contains("\"Empty space\" : \"Block\"")
                        && gui.contains("\"Left click\" : \"Right click\"")
                        && gui.contains("\"Sneaking\" : \"Not sneaking\""));
        assertTrue("Golem GUI must restore the reference entity preview and localized blurb",
                gui.contains("GuiInventory.drawEntityOnScreen")
                        && gui.contains("private static final int GOLEM_PREVIEW_X = 18;")
                        && gui.contains("private static final int GOLEM_PREVIEW_Y = 45;")
                        && gui.contains("this.guiLeft + GOLEM_PREVIEW_X")
                        && gui.contains("this.guiTop + GOLEM_PREVIEW_Y")
                        && gui.contains("I18n.format(blurbKey)")
                        && lang.contains("golemblurb.8.text=Do you wish me to use this item on a block or only on an empty location?"));
        assertTrue("Order upgrade color controls must use their visible reference hit boxes",
                gui.contains("this.golem.getUpgradeAmount(4) <= 0")
                        && gui.contains("96 + a / 2 * 28, 4 + a % 2 * 31, 8, 12")
                        && gui.contains("112 + a / 2 * 28, 4 + a % 2 * 31, 8, 12"));
        assertTrue("Golem filter colors and names must keep the original TC4 palette",
                utils.contains("public static final String[] colorNames")
                        && utils.contains("public static final int[] colors")
                        && gui.contains("Utils.colors[color]")
                        && gui.contains("Utils.colorNames[color]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
