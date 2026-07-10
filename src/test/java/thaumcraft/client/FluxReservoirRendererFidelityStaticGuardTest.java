package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FluxReservoirRendererFidelityStaticGuardTest {

    @Test
    public void fluxScrubberAndReservoirRenderersUseModelDrivenDevicePaths() throws IOException {
        String fluxRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileFluxScrubberRenderer.java");
        String fluxModel = read("src/main/java/thaumcraft/client/renderers/models/ModelFluxScrubber.java");
        String capModel = read("src/main/java/thaumcraft/client/renderers/models/ModelEldritchCap.java");
        String stoneDeviceBlockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockstonedevice.json");
        String fluxBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_14.json");
        String reservoirRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEssentiaReservoirRenderer.java");
        String reservoirItemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemEssentiaReservoirRenderer.java");
        String reservoirTile = read("src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java");
        String reservoirModel = read("src/main/resources/assets/thaumcraft/models/block/blockessentiareservoir.json");
        String reservoirItemModel = read("src/main/resources/assets/thaumcraft/models/item/blockessentiareservoir_tesr.json");

        assertTrue("Flux scrubber renderer should keep the orientation path and render the full reference cap-plus-tip model through TESR",
                fluxRenderer.contains("new ModelFluxScrubber()")
                        && fluxRenderer.contains("translateFromOrientation(")
                        && fluxRenderer.contains("model.renderCap(MODEL_SCALE)")
                        && fluxRenderer.contains("model.renderTip(MODEL_SCALE)")
                        && !fluxRenderer.contains("TileRenderHelper.orientBillboardToPlayer()")
                        && !fluxRenderer.contains("TileRenderHelper.drawTexturedQuad("));

        assertTrue("ModelFluxScrubber should reuse the exact obelisk-cap OBJ groups instead of substitute cuboids",
                fluxModel.contains("new ModelEldritchCap()")
                        && fluxModel.contains("renderCap(float scale)")
                        && fluxModel.contains("renderTip(float scale)")
                        && fluxModel.contains("renderCapGroup()")
                        && fluxModel.contains("renderTipGroup()")
                        && !fluxModel.contains("ModelRenderer")
                        && capModel.contains("public void renderCapGroup()")
                        && capModel.contains("public void renderTipGroup()"));

        assertTrue("Stone-device fallback blockstates should remain metadata-specific",
                stoneDeviceBlockstate.contains("\"type=13\": { \"model\": \"thaumcraft:blockstonedevice_13\" }")
                        && stoneDeviceBlockstate.contains("\"type=14\": { \"model\": \"thaumcraft:blockstonedevice_14\" }"));
        assertTrue("Flux scrubber fallback assets should retain representative source texture and bounds while TESR owns the live shell",
                fluxBlockModel.contains("\"ambientocclusion\": false")
                        && fluxBlockModel.contains("\"surface\": \"thaumcraft:models/fluxscrubber\"")
                        && fluxBlockModel.contains("\"from\": [4, 7, 4]")
                        && fluxBlockModel.contains("\"to\": [12, 9, 12]"));

        assertTrue("Reservoir TESR should restore the source OBJ shell before the separate textured liquid pass",
                reservoirRenderer.contains("renderReservoirShell(tile, x, y, z);")
                        && reservoirRenderer.contains("renderLiquid(tile, x, y, z);")
                        && reservoirRenderer.contains("textures/models/reservoir.obj")
                        && reservoirRenderer.contains("textures/models/reservoir.png")
                        && reservoirRenderer.contains("CCModel.parseObjModels(RESERVOIR_OBJ)")
                        && reservoirRenderer.contains("models.get(\"Cylinder001\")")
                        && reservoirRenderer.contains("CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL)")
                        && reservoirRenderer.contains("model.render(CCRenderState.normalAttrib)")
                        && reservoirRenderer.contains("BufferBuilder")
                        && reservoirRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR")
                        && reservoirRenderer.contains("thaumcraft:blocks/animatedglow")
                        && reservoirRenderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && reservoirRenderer.contains("OpenGlHelper.setLightmapTextureCoords")
                        && reservoirRenderer.contains("float r = tile.colorR;")
                        && reservoirRenderer.contains("float g = tile.colorG;")
                        && reservoirRenderer.contains("float b = tile.colorB;")
                        && reservoirRenderer.contains("float a = 0.9F;")
                        && reservoirRenderer.contains("drawTexturedCuboid(")
                        && reservoirRenderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);")
                        && !reservoirRenderer.contains("tile.getWorld() == null"));
        assertTrue("Reservoir client ticks should retain TC4's rotating aspect and 20-tick color interpolation",
                reservoirTile.contains("this.displayAspect = aspects[this.count / 20 % aspects.length];")
                        && reservoirTile.contains("this.stepR = (this.colorR - this.targetR) / 20.0F;")
                        && reservoirTile.contains("this.colorR -= this.stepR;")
                        && reservoirTile.contains("TCSounds.CREAK"));

        assertTrue("Reservoir shell should retain the exact TC4 six-facing orientation chain and local half-block offset",
                reservoirRenderer.contains("GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);")
                        && reservoirRenderer.contains("GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);")
                        && reservoirRenderer.contains("GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);")
                        && reservoirRenderer.contains("GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);")
                        && reservoirRenderer.contains("GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);")
                        && reservoirRenderer.contains("GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);")
                        && reservoirRenderer.contains("GlStateManager.translate(0.0D, 0.0D, -0.5D);"));

        assertTrue("Reservoir item renderer should compose the baked core with the worldless OBJ/liquid TESR without cancelling Forge's outer transform",
                reservoirItemRenderer.contains("extends TileEntityItemStackRenderer")
                        && reservoirItemRenderer.contains("new TileEssentiaReservoirRenderer()")
                        && reservoirItemRenderer.contains("reservoirRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && reservoirItemRenderer.contains("renderReservoirCore(mc);")
                        && reservoirItemRenderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && reservoirItemRenderer.contains("GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);")
                        && reservoirItemRenderer.contains("mc.getBlockRendererDispatcher().renderBlockBrightness(")
                        && reservoirItemRenderer.contains("ConfigBlocks.blockEssentiaReservoir.getDefaultState()")
                        && reservoirItemRenderer.contains("essentia.readFromNBT(tag);"));
        assertFalse("Reservoir item renderer must not cancel Forge's builtin/entity outer -0.5 translation",
                reservoirItemRenderer.contains("GlStateManager.translate(0.5F, 0.5F, 0.5F)"));

        assertTrue("Reservoir block model should keep only the exact TC4 central core cube",
                reservoirModel.contains("\"ambientocclusion\": false")
                        && reservoirModel.contains("\"reservoir\": \"thaumcraft:blocks/essentiareservoir\"")
                        && reservoirModel.contains("\"from\": [2, 2, 2]")
                        && reservoirModel.contains("\"to\": [14, 14, 14]"));
        assertEquals("Reservoir block model must contain exactly one baked element", 1,
                countOccurrences(reservoirModel, "\"from\""));

        assertTrue("Reservoir builtin item model should provide all block display contexts",
                reservoirItemModel.contains("\"parent\": \"builtin/entity\"")
                        && reservoirItemModel.contains("\"gui\"")
                        && reservoirItemModel.contains("\"ground\"")
                        && reservoirItemModel.contains("\"fixed\"")
                        && reservoirItemModel.contains("\"thirdperson_righthand\"")
                        && reservoirItemModel.contains("\"thirdperson_lefthand\"")
                        && reservoirItemModel.contains("\"firstperson_righthand\"")
                        && reservoirItemModel.contains("\"firstperson_lefthand\"")
                        && reservoirItemModel.contains("\"rotation\": [30, 225, 0]")
                        && reservoirItemModel.contains("\"scale\": [0.625, 0.625, 0.625]"));

        assertArrayEquals("Runtime reservoir.obj must remain identical to the TC4 source asset",
                Files.readAllBytes(Paths.get("thaumcraft_src/assets/thaumcraft/textures/models/reservoir.obj")),
                Files.readAllBytes(Paths.get("src/main/resources/assets/thaumcraft/textures/models/reservoir.obj")));
        assertArrayEquals("Runtime reservoir.png must remain identical to the TC4 source asset",
                Files.readAllBytes(Paths.get("thaumcraft_src/assets/thaumcraft/textures/models/reservoir.png")),
                Files.readAllBytes(Paths.get("src/main/resources/assets/thaumcraft/textures/models/reservoir.png")));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
