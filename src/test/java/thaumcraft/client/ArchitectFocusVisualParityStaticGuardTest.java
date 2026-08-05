package thaumcraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArchitectFocusVisualParityStaticGuardTest {

    @Test
    public void wardingAndTradeExposeAndStitchTheirMountedSprites() throws IOException {
        String warding = read("src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java");
        String trade = read("src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java");
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String modelWand = read("src/main/java/thaumcraft/client/renderers/models/gear/ModelWand.java");

        assertTrue(warding.contains("getFocusDepthLayerIcon")
                && warding.contains("thaumcraft:items/focus_warding_depth")
                && warding.contains("getOrnament")
                && warding.contains("thaumcraft:items/focus_warding_orn")
                && warding.contains("getTextureMapBlocks().getAtlasSprite"));
        assertTrue(trade.contains("getOrnament")
                && trade.contains("thaumcraft:items/focus_trade_orn")
                && trade.contains("getTextureMapBlocks().getAtlasSprite"));
        assertTrue(registry.contains("registerSprite(FOCUS_WARDING_DEPTH_SPRITE)")
                && registry.contains("registerSprite(FOCUS_WARDING_ORNAMENT_SPRITE)")
                && registry.contains("registerSprite(FOCUS_TRADE_ORNAMENT_SPRITE)"));
        assertTrue(modelWand.contains("focusItem.getOrnament(focusStack)")
                && modelWand.contains("focusItem.getFocusDepthLayerIcon(focusStack)"));
    }

    @Test
    public void itemModelsKeepOriginalOrnamentThenCoreLayerOrder() throws IOException {
        assertLayers("focuswarding", "focus_warding_orn", "focus_warding");
        assertLayers("focustrade", "focus_trade_orn", "focus_trade");
    }

    @Test
    public void mountedAssetsKeepOriginalBytesAndResolution() throws IOException {
        assertOriginalAsset("focus_warding_depth.png", 8, 72);
        assertOriginalAsset("focus_warding_orn.png", 16, 16);
        assertOriginalAsset("focus_warding.png", 16, 128);
        assertOriginalAsset("focus_trade_orn.png", 16, 16);
        assertOriginalAsset("focus_trade.png", 16, 16);
    }

    @Test
    public void mountedFocusColorsMatchTheFiveConfirmedTc4Values() throws IOException {
        assertTrue(readFocus("Fire").contains("return 0xE55104;"));
        assertTrue(readFocus("Shock").contains("return 0x9FB3BF;"));
        assertTrue(readFocus("Hellbat").contains("return 0xDC3602;"));
        assertTrue(readFocus("Trade").contains("return 0x857B93;"));
        assertTrue(readFocus("Warding").contains("return 0xFFE9CF;"));
    }

    @Test
    public void pechDepthAndHellbatOrnamentRemainPositiveControls() throws IOException {
        String pech = readFocus("Pech");
        String hellbat = readFocus("Hellbat");
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String hellbatModel = read("src/main/resources/assets/thaumcraft/models/item/focushellbat.json");

        assertTrue(pech.contains("getFocusDepthLayerIcon")
                && pech.contains("thaumcraft:items/focus_pech_depth")
                && registry.contains("registerSprite(FOCUS_PECH_DEPTH_SPRITE)"));
        assertTrue(hellbat.contains("getOrnament")
                && hellbat.contains("thaumcraft:items/focus_hellbat_orn")
                && hellbatModel.contains("\"layer0\": \"thaumcraft:items/focus_hellbat_orn\"")
                && hellbatModel.contains("\"layer1\": \"thaumcraft:items/focus_hellbat\""));
    }

    private static void assertLayers(String model, String ornament, String core) throws IOException {
        JsonObject textures = new JsonParser().parse(read(
                "src/main/resources/assets/thaumcraft/models/item/" + model + ".json"))
                .getAsJsonObject().getAsJsonObject("textures");
        assertEquals("thaumcraft:items/" + ornament, textures.get("layer0").getAsString());
        assertEquals("thaumcraft:items/" + core, textures.get("layer1").getAsString());
    }

    private static void assertOriginalAsset(String name, int width, int height) throws IOException {
        Path original = Paths.get("thaumcraft_src/assets/thaumcraft/textures/items", name);
        Path runtime = Paths.get("src/main/resources/assets/thaumcraft/textures/items", name);
        assertArrayEquals(Files.readAllBytes(original), Files.readAllBytes(runtime));
        BufferedImage image = ImageIO.read(runtime.toFile());
        assertNotNull(image);
        assertEquals(width, image.getWidth());
        assertEquals(height, image.getHeight());
    }

    private static String readFocus(String name) throws IOException {
        return read("src/main/java/thaumcraft/common/items/wands/foci/Focus" + name + ".java");
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
