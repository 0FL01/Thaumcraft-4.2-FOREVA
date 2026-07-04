package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class HellbatFocusVisualParityStaticGuardTest {

    @Test
    public void hellbatFocusShouldKeepOriginalTwoLayerIconAndWandOrnament() throws IOException {
        String focusHellbat = read("src/main/java/thaumcraft/common/items/wands/foci/FocusHellbat.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/focushellbat.json");
        String modelWand = read("src/main/java/thaumcraft/client/renderers/models/gear/ModelWand.java");

        assertTrue("FocusHellbat must expose the original ornament sprite for equipped wand/staff rendering",
                focusHellbat.contains("getOrnament")
                        && focusHellbat.contains("thaumcraft:items/focus_hellbat_orn")
                        && focusHellbat.contains("getTextureMapBlocks().getAtlasSprite(ORNAMENT_SPRITE)"));

        assertTrue("The 2D Hellbat focus item must keep the TC4 two-pass texture: ornament first, red focus core second",
                itemModel.contains("\"layer0\": \"thaumcraft:items/focus_hellbat_orn\"")
                        && itemModel.contains("\"layer1\": \"thaumcraft:items/focus_hellbat\""));

        assertTrue("ModelWand must keep the ornament render path used by Hellbat and other TC4 ornamented foci",
                modelWand.contains("focusItem.getOrnament(focusStack)")
                        && modelWand.contains("renderOrnament(ornament)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
