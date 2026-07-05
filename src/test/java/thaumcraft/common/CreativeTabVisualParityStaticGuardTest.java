package thaumcraft.common;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CreativeTabVisualParityStaticGuardTest {

    @Test
    public void thaumcraftUsesSingleTc4CreativeTab() throws Exception {
        String thaumcraft = read("src/main/java/thaumcraft/common/Thaumcraft.java");
        String creativeTab = read("src/main/java/thaumcraft/common/lib/CreativeTabThaumcraft.java");

        assertTrue("Thaumcraft.tabTC should alias the single TC4 creative tab instead of creating a duplicate tab",
                thaumcraft.contains("public static final CreativeTabs tabTC = CreativeTabThaumcraft.tabThaumcraft;"));
        assertFalse("Thaumcraft should not create a second thaumcraft creative tab with the same label",
                thaumcraft.contains("new CreativeTabs(\"thaumcraft\")"));
        assertFalse("The TC tab icon should not fall back to the temporary Ender Eye placeholder",
                thaumcraft.contains("Items.ENDER_EYE"));

        assertTrue("CreativeTabThaumcraft should keep the original TC4 wand icon source",
                creativeTab.contains("new ItemStack(ConfigItems.itemWandCasting)"));
    }

    @Test
    public void thaumcraftCreativeTabHasLocalizedTitle() throws Exception {
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("Creative inventory should show Thaumcraft instead of raw itemGroup.thaumcraft",
                lang.contains("\nitemGroup.thaumcraft=Thaumcraft\n")
                        || lang.startsWith("itemGroup.thaumcraft=Thaumcraft\n"));
    }

    @Test
    public void wandCreativeTabIconKeepsGuiRenderCalibrationPath() throws Exception {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String clientModels = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String calibration = read("src/main/resources/assets/thaumcraft/render_calibration/wand_casting.json");

        assertTrue("Casting wand items should still route to the TEISR baked-model shell",
                clientProxy.contains("new ResourceLocation(\"thaumcraft\", \"wandcasting_tesr\")"));
        assertTrue("Casting wand baked model should still capture GUI transform type before TEISR renders",
                clientModels.contains("new WandPerspectiveModel(model)"));
        assertTrue("Wand calibration should keep a dedicated GUI context for creative-tab tuning",
                calibration.contains("\"GUI\": {") && calibration.contains("\"translate\": [0.5, 0.5, 0.0]"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
