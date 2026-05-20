package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumometerItemRendererContractTest {

    @Test
    public void thaumometerShouldUseBuiltinEntityScannerRenderer() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/itemthaumometer_tesr.json");

        assertTrue("ClientProxy should route itemThaumometer onto a builtin/entity model and install the dedicated scanner renderer",
                clientProxy.contains("if (item == ConfigItems.itemThaumometer) {")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"itemthaumometer_tesr\");")
                        && clientProxy.contains("ConfigItems.itemThaumometer.setTileEntityItemStackRenderer(new ItemThaumometerRenderer());"));

        assertTrue("ItemThaumometerRenderer should restore the scanner OBJ render path plus live first-person scan readout surface",
                renderer.contains("extends TileEntityItemStackRenderer")
                        && renderer.contains("textures/models/scanner.obj")
                        && renderer.contains("textures/models/scanner.png")
                        && renderer.contains("textures/models/scanscreen.png")
                        && renderer.contains("CCModel.parseObjModels")
                        && renderer.contains("SCANNER_VERTICAL_CENTER = -0.1F")
                        && renderer.contains("GlStateManager.translate(0.0F, SCANNER_VERTICAL_CENTER, 0.0F);")
                        && renderer.contains("player.isHandActive()")
                        && renderer.contains("ScanManager.hasBeenScanned")
                        && renderer.contains("EntityUtils.getPointedEntity")
                        && renderer.contains("player.rayTrace(10.0D, 1.0F)")
                        && renderer.contains("ThaumcraftApi.scanEventhandlers"));

        assertTrue("The thaumometer item-model stub must stay builtin/entity and keep the first-person/ground transforms that approximate the original handheld presentation",
                itemModel.contains("\"parent\": \"builtin/entity\"")
                        && itemModel.contains("\"firstperson_righthand\"")
                        && itemModel.contains("\"rotation\": [0, 0, 90]")
                        && itemModel.contains("\"translation\": [1.0, 3.5, 1.0]")
                        && itemModel.contains("\"ground\"")
                        && itemModel.contains("\"scale\": [0.6, 0.6, 0.6]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
