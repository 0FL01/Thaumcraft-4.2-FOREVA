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
        String reservoirRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEssentiaReservoirRenderer.java");
        String reservoirModel = read("src/main/resources/assets/thaumcraft/models/block/blockessentiareservoir.json");

        assertTrue("Flux scrubber renderer should use model cap/tip orientation path instead of billboard quads",
                fluxRenderer.contains("new ModelFluxScrubber()")
                        && fluxRenderer.contains("translateFromOrientation(")
                        && fluxRenderer.contains("model.renderCap(MODEL_SCALE)")
                        && fluxRenderer.contains("model.renderTip(MODEL_SCALE)")
                        && !fluxRenderer.contains("TileRenderHelper.orientBillboardToPlayer()")
                        && !fluxRenderer.contains("TileRenderHelper.drawTexturedQuad("));

        assertTrue("ModelFluxScrubber should expose cap and tip parts",
                fluxModel.contains("class ModelFluxScrubber extends ModelBase")
                        && fluxModel.contains("cap")
                        && fluxModel.contains("tip")
                        && fluxModel.contains("renderCap(float scale)")
                        && fluxModel.contains("renderTip(float scale)"));

        assertTrue("Reservoir renderer should keep only the textured 3D liquid volume while the static shell lives in the block model",
                reservoirRenderer.contains("renderLiquid(tile, x, y, z)")
                        && reservoirRenderer.contains("BufferBuilder")
                        && reservoirRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR")
                        && reservoirRenderer.contains("thaumcraft:blocks/animatedglow")
                        && reservoirRenderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && reservoirRenderer.contains("OpenGlHelper.setLightmapTextureCoords")
                        && reservoirRenderer.contains("computeLiquidAlpha(")
                        && reservoirRenderer.contains("drawTexturedCuboid(")
                        && !reservoirRenderer.contains("new ModelEssentiaReservoir()")
                        && !reservoirRenderer.contains("model.renderAll(MODEL_SCALE)"));

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
