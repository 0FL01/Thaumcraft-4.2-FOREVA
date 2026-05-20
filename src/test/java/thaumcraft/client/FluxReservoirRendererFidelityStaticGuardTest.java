package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class FluxReservoirRendererFidelityStaticGuardTest {

    @Test
    public void fluxScrubberAndReservoirRenderersUseModelDrivenDevicePaths() throws IOException {
        String fluxRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileFluxScrubberRenderer.java");
        String fluxModel = read("src/main/java/thaumcraft/client/renderers/models/ModelFluxScrubber.java");
        String stoneDeviceBlockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockstonedevice.json");
        String fluxBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_14.json");
        String reservoirRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEssentiaReservoirRenderer.java");
        String reservoirItemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemEssentiaReservoirRenderer.java");
        String reservoirModel = read("src/main/resources/assets/thaumcraft/models/block/blockessentiareservoir.json");

        assertTrue("Flux scrubber renderer should keep the orientation path and animated tip while the static cap lives in the block model",
                fluxRenderer.contains("new ModelFluxScrubber()")
                        && fluxRenderer.contains("translateFromOrientation(")
                        && fluxRenderer.contains("model.renderTip(MODEL_SCALE)")
                        && !fluxRenderer.contains("model.renderCap(MODEL_SCALE)")
                        && !fluxRenderer.contains("TileRenderHelper.orientBillboardToPlayer()")
                        && !fluxRenderer.contains("TileRenderHelper.drawTexturedQuad("));

        assertTrue("ModelFluxScrubber should expose cap and tip parts",
                fluxModel.contains("class ModelFluxScrubber extends ModelBase")
                        && fluxModel.contains("cap")
                        && fluxModel.contains("tip")
                        && fluxModel.contains("renderCap(float scale)")
                        && fluxModel.contains("renderTip(float scale)"));

        assertTrue("Stone-device blockstate should route focal manipulator and flux scrubber away from the old arcane-stone placeholder",
                stoneDeviceBlockstate.contains("\"type=13\": { \"model\": \"thaumcraft:blockstonedevice_13\" }")
                        && stoneDeviceBlockstate.contains("\"type=14\": { \"model\": \"thaumcraft:blockstonedevice_14\" }"));
        assertTrue("Flux scrubber block model should now carry the static cap shell instead of the old full-cube placeholder",
                fluxBlockModel.contains("\"ambientocclusion\": false")
                        && fluxBlockModel.contains("\"surface\": \"thaumcraft:models/fluxscrubber\"")
                        && fluxBlockModel.contains("\"from\": [4, 7, 4]")
                        && fluxBlockModel.contains("\"to\": [12, 9, 12]"));

        assertTrue("Reservoir renderer should keep only the textured 3D liquid volume while the static shell lives in the block model",
                reservoirRenderer.contains("renderLiquid(tile, x, y, z)")
                        && reservoirRenderer.contains("BufferBuilder")
                        && reservoirRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR")
                        && reservoirRenderer.contains("thaumcraft:blocks/animatedglow")
                        && reservoirRenderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && reservoirRenderer.contains("OpenGlHelper.setLightmapTextureCoords")
                        && reservoirRenderer.contains("computeLiquidAlpha(")
                        && reservoirRenderer.contains("drawTexturedCuboid(")
                        && !reservoirRenderer.contains("tile.getWorld() == null")
                        && !reservoirRenderer.contains("new ModelEssentiaReservoir()")
                        && !reservoirRenderer.contains("model.renderAll(MODEL_SCALE)"));

        assertTrue("Reservoir item renderer should reuse the block-model shell plus the worldless TESR liquid path instead of falling back to a static normal item model",
                reservoirItemRenderer.contains("extends TileEntityItemStackRenderer")
                        && reservoirItemRenderer.contains("new ModelResourceLocation(\"thaumcraft:blockessentiareservoir\", \"inventory\")")
                        && reservoirItemRenderer.contains("new TileEssentiaReservoirRenderer()")
                        && reservoirItemRenderer.contains("mc.getRenderItem().renderItem(stack, model);")
                        && reservoirItemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);")
                        && reservoirItemRenderer.contains("essentia.readFromNBT(tag);"));

        assertTrue("Reservoir block model should define the basin shell geometry after the shell moved out of TESR",
                reservoirModel.contains("\"ambientocclusion\": false")
                        && reservoirModel.contains("\"from\": [2, 7, 2]")
                        && reservoirModel.contains("\"from\": [2, 2, 13]")
                        && reservoirModel.contains("\"from\": [13, 2, 3]")
                        && reservoirModel.contains("\"from\": [2, 1, 2]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
