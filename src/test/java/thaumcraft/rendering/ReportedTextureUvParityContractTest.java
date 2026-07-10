package thaumcraft.rendering;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** CI-visible guards for UV and composite-render defects confirmed by live testing. */
public class ReportedTextureUvParityContractTest {

    @Test
    public void reservoirAndCrystalizerShouldUseTheirTc4CoordinateDomains() throws IOException {
        String reservoir = read("src/main/java/thaumcraft/client/renderers/item/ItemEssentiaReservoirRenderer.java");
        int correction = reservoir.indexOf("GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);");
        int bakedCore = reservoir.indexOf("renderBlockBrightness(");
        assertTrue("The baked reservoir core must cancel BlockModelRenderer's hidden +90 Y rotation",
                correction >= 0 && bakedCore >= 0 && correction < bakedCore);
        assertFalse("The reservoir core and OBJ must retain Forge's single shared item offset",
                reservoir.contains("GlStateManager.translate(0.5F, 0.5F, 0.5F);")
                        || reservoir.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);"));

        String crystalizer = read("src/main/java/thaumcraft/client/renderers/models/ModelCrystalizer.java");
        assertTrue("Wavefront V must be converted from OBJ to Minecraft texture origin",
                crystalizer.contains(".tex(uv[0], 1.0F - uv[1])"));
    }

    @Test
    public void dynamicallyRenderedValveSpriteShouldBeExplicitlyStitched() throws IOException {
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeValveRenderer.java");
        assertTrue(renderer.contains("thaumcraft:blocks/pipe_valve"));
        assertTrue(registry.contains("new ResourceLocation(\"thaumcraft\", \"blocks/pipe_valve\")"));
        assertTrue(registry.contains("registerSprite(PIPE_VALVE_SPRITE)"));
    }

    @Test
    public void lampItemsShouldUseDedicatedAnimatedInventoryShells() throws IOException {
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String stitcher = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        int[] metas = {7, 8, 13};
        String[] top = {"lamp_top", "lamp_grow_top", "lamp_fert_top"};
        String[] side = {"lamp_side", "lamp_grow_side", "lamp_fert_side"};
        for (int i = 0; i < metas.length; ++i) {
            String modelName = "blockmetaldevice_" + metas[i] + "_inventory";
            assertTrue(proxy.contains("registerBuiltinItemModel(metalDeviceItem, " + metas[i]
                    + ", \"" + modelName + "\");"));
            String model = read("src/main/resources/assets/thaumcraft/models/item/" + modelName + ".json");
            assertTrue(model.contains("\"parent\": \"block/block\"")
                    && model.contains("\"from\": [4, 2, 4]")
                    && model.contains("\"to\": [12, 14, 12]")
                    && model.contains("thaumcraft:blocks/" + top[i])
                    && model.contains("thaumcraft:blocks/" + side[i])
                    && model.contains("\"uv\": [4, 12, 12, 4]")
                    && model.contains("\"uv\": [4, 4, 12, 12]")
                    && model.contains("\"uv\": [4, 2, 12, 14]"));
            assertFalse("Inventory lamps always use TC4's animated on sprites", model.contains("_off"));
            assertVerticalFaceUvs(model, new String[]{"[4,12,12,4]"}, new String[]{"[4,4,12,12]"});
        }
        assertTrue("Dynamically selected world off-sprites must be stitched explicitly",
                stitcher.contains("blocks/lamp_grow_top_off")
                        && stitcher.contains("blocks/lamp_grow_side_off")
                        && stitcher.contains("blocks/lamp_fert_top_off")
                        && stitcher.contains("blocks/lamp_fert_side_off"));
    }

    @Test
    public void tubeItemsAndWorldShellsShouldUseBoundedUvsAndNormals() throws IOException {
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemTubeRenderer.java");
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/TubeConduitRenderHelper.java");

        assertTrue(proxy.contains("registerBuiltinItemModel(tubeItem, 1, \"blocktube_tesr\");")
                && proxy.contains("registerBuiltinItemModel(tubeItem, 7, \"blocktube_tesr\");"));
        assertFalse("Static filter/restricted/directional shells must use baked models",
                proxy.contains("registerBuiltinItemModel(tubeItem, 3, \"blocktube_tesr\");")
                        || proxy.contains("registerBuiltinItemModel(tubeItem, 5, \"blocktube_tesr\");")
                        || proxy.contains("registerBuiltinItemModel(tubeItem, 6, \"blocktube_tesr\");"));
        assertTrue("Valve remains a baked-shell plus dynamic-overlay hybrid",
                itemRenderer.contains("else if (meta == 1)")
                        && itemRenderer.contains("renderBakedShell(meta);")
                        && itemRenderer.contains("valve.facing = EnumFacing.EAST;"));
        assertFalse(itemRenderer.contains("renderInventoryShell"));

        assertTrue(helper.contains("DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL")
                && helper.contains("EXTENSION = 6.0F / 16.0F")
                && helper.contains("getInterpolatedU")
                && helper.contains("getInterpolatedV")
                && helper.contains("16.0F - minX * 16.0F")
                && helper.contains("16.0F - minZ * 16.0F")
                && helper.contains("maxZ * 16.0F")
                && helper.contains(".normal(normalX, normalY, normalZ)")
                && helper.contains("FILTER(\"thaumcraft:blocks/pipe_1\")")
                && helper.contains("BUFFER(\"thaumcraft:blocks/pipe_buffer\")")
                && helper.contains("RESTRICTED(\"thaumcraft:blocks/pipe_restrict\")")
                && helper.contains("DIRECTIONAL(\"thaumcraft:blocks/pipe_1\")"));
        assertFalse("Sparse tube sprites must not be stretched across complete cuboid faces",
                helper.contains("TileRenderHelper.drawTexturedCuboid")
                        || helper.contains("sprite.getMinU()")
                        || helper.contains("sprite.getMaxU()"));
        assertTrue("DOWN, NORTH, WEST and EAST vertices must retain TC4's distinct UV directions",
                helper.contains("minX, minY, maxZ, uMinX, vDownAtMaxZ")
                        && helper.contains("minX, minY, minZ, uMinX, vDownAtMinZ")
                        && helper.contains("minX, maxY, minZ, uNorthAtMinX, vSideMin")
                        && helper.contains("maxX, maxY, minZ, uNorthAtMaxX, vSideMin")
                        && helper.contains("minX, maxY, maxZ, uMaxZ, vSideMin")
                        && helper.contains("minX, maxY, minZ, uMinZ, vSideMin")
                        && helper.contains("maxX, maxY, minZ, uEastAtMinZ, vSideMin")
                        && helper.contains("maxX, maxY, maxZ, uEastAtMaxZ, vSideMin"));

        assertModelContains("blocktube_1.json", "[7, 0, 7]", "[6, 6, 6]", "pipe_2");
        assertModelContains("blocktube_3.json", "[7, 0, 7]", "[5.5, 5.5, 5.5]", "pipe_filter_core");
        assertModelContains("blocktube_5.json", "[7, 0, 7]", "[6.5, 6.5, 6.5]", "pipe_restrict");
        assertModelContains("blocktube_6.json", "[7, 0, 7]", "[6.984, 1.6, 6.984]", "pipe_3");
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blocktube_1.json")
                .contains("\"down\": { \"uv\": [7, 9, 9, 7]"));
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blocktube_3.json")
                .contains("\"down\": { \"uv\": [5.5, 10.5, 10.5, 5.5]"));
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blocktube_5.json")
                .contains("\"down\": { \"uv\": [6.5, 9.5, 9.5, 6.5]"));
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blocktube_6.json")
                .contains("\"down\": { \"uv\": [6.984, 9.016, 9.016, 6.984]"));
        assertVerticalFaceUvs(read("src/main/resources/assets/thaumcraft/models/block/blocktube_1.json"),
                new String[]{"[7,9,9,7]", "[6,10,10,6]"},
                new String[]{"[7,7,9,9]", "[6,6,10,10]"});
        assertVerticalFaceUvs(read("src/main/resources/assets/thaumcraft/models/block/blocktube_3.json"),
                new String[]{"[7,9,9,7]", "[5.5,10.5,10.5,5.5]", "[5.5,10.5,10.5,5.5]"},
                new String[]{"[7,7,9,9]", "[5.5,5.5,10.5,10.5]", "[5.5,5.5,10.5,10.5]"});
        assertVerticalFaceUvs(read("src/main/resources/assets/thaumcraft/models/block/blocktube_5.json"),
                new String[]{"[7,9,9,7]", "[6.5,9.5,9.5,6.5]"},
                new String[]{"[7,7,9,9]", "[6.5,6.5,9.5,9.5]"});
        assertVerticalFaceUvs(read("src/main/resources/assets/thaumcraft/models/block/blocktube_6.json"),
                new String[]{"[7,9,9,7]", "[6.984,9.016,9.016,6.984]", "[6.5,9.5,9.5,6.5]"},
                new String[]{"[7,7,9,9]", "[6.984,6.984,9.016,9.016]", "[6.5,6.5,9.5,9.5]"});
    }

    private static void assertModelContains(String name, String firstBounds, String secondBounds,
                                            String texture) throws IOException {
        String model = read("src/main/resources/assets/thaumcraft/models/block/" + name);
        assertTrue(model.contains("\"parent\": \"block/block\"")
                && model.contains(firstBounds)
                && model.contains(secondBounds)
                && model.contains(texture));
    }

    private static void assertVerticalFaceUvs(String model, String[] expectedDown, String[] expectedUp) {
        JsonArray elements = new JsonParser().parse(model).getAsJsonObject().getAsJsonArray("elements");
        assertEquals(expectedDown.length, elements.size());
        assertEquals(expectedDown.length, expectedUp.length);
        for (int i = 0; i < elements.size(); ++i) {
            JsonObject faces = elements.get(i).getAsJsonObject().getAsJsonObject("faces");
            assertEquals("DOWN UV mismatch for element " + i, expectedDown[i],
                    faces.getAsJsonObject("down").getAsJsonArray("uv").toString());
            assertEquals("UP UV mismatch for element " + i, expectedUp[i],
                    faces.getAsJsonObject("up").getAsJsonArray("uv").toString());
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
