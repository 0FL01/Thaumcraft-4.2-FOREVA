package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class DynamicDeviceRendererContractTest {

    @Test
    public void stage8cDynamicDeviceBurstKeepsRendererAndModelContracts() throws IOException {
        String brainboxRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileBrainboxRenderer.java");
        String sensorRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileSensorRenderer.java");
        String lifterRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileLifterRenderer.java");
        String renderHelper = read("src/main/java/thaumcraft/client/renderers/tile/TileRenderHelper.java");
        String sensorModel = read("src/main/resources/assets/thaumcraft/models/block/blockwoodendevice_1.json");
        String sensorItemModel = read("src/main/resources/assets/thaumcraft/models/item/blockwoodendevice_1_inventory.json");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("TileRenderHelper must expose reusable textured cuboid helpers for Stage 8-c dynamic device overlays",
                renderHelper.contains("static void drawTexturedCuboid(BufferBuilder buf,")
                        && renderHelper.contains("static void addTexturedFace(BufferBuilder buf,"));
        assertTrue("Brainbox renderer must drive the nozzle geometry from TileBrainbox facing and brainbox atlas texture",
                brainboxRenderer.contains("tile.getPos().offset(tile.facing)")
                        && brainboxRenderer.contains("getAtlasSprite(\"thaumcraft:blocks/brainbox\")")
                        && brainboxRenderer.contains("drawNozzle(buf, tile.facing, brainbox)"));
        assertTrue("Sensor renderer must only overlay the lit state and use the active arcane ear textures",
                sensorRenderer.contains("tile.redstoneSignal <= 0")
                        && sensorRenderer.contains("getAtlasSprite(\"thaumcraft:blocks/arcaneeartopon\")")
                        && sensorRenderer.contains("getAtlasSprite(\"thaumcraft:blocks/arcaneearsideon\")"));
        assertTrue("Lifter renderer must gate glow on active lift state and render with the animated glow atlas",
                lifterRenderer.contains("tile.gettingPower() || tile.rangeAbove <= 0")
                        && lifterRenderer.contains("getAtlasSprite(\"thaumcraft:blocks/animatedglow\")")
                        && lifterRenderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 180.0F, 180.0F)"));
        assertTrue("Sensor block model must carry the multi-part bell geometry instead of the old cube_bottom_top placeholder",
                sensorModel.contains("\"ambientocclusion\": false")
                        && sensorModel.contains("\"belltop\": \"thaumcraft:blocks/arcaneearbelltop\"")
                        && sensorModel.contains("\"from\": [4, 8, 1]")
                        && sensorModel.contains("\"from\": [13, 8, 4]"));
        assertTrue("Arcane Ear inventory model must keep the original lit top and side textures",
                sensorItemModel.contains("\"parent\": \"thaumcraft:block/blockwoodendevice_1\"")
                        && sensorItemModel.contains("\"top\": \"thaumcraft:blocks/arcaneeartopon\"")
                        && sensorItemModel.contains("\"side\": \"thaumcraft:blocks/arcaneearsideon\"")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 1, \"blockwoodendevice_1_inventory\");"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
