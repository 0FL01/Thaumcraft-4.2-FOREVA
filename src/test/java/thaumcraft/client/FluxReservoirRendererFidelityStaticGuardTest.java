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
        String reservoirModel = read("src/main/java/thaumcraft/client/renderers/models/ModelEssentiaReservoir.java");

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

        assertTrue("Reservoir renderer should use model shell plus explicit 3D liquid volume rendering",
                reservoirRenderer.contains("new ModelEssentiaReservoir()")
                        && reservoirRenderer.contains("model.renderAll(MODEL_SCALE)")
                        && reservoirRenderer.contains("renderLiquid(tile, x, y, z)")
                        && reservoirRenderer.contains("BufferBuilder")
                        && reservoirRenderer.contains("DefaultVertexFormats.POSITION_COLOR")
                        && reservoirRenderer.contains("computeLiquidAlpha(")
                        && !reservoirRenderer.contains("TileRenderHelper.drawTexturedQuad("));

        assertTrue("ModelEssentiaReservoir should define base/walls/top ring geometry",
                reservoirModel.contains("class ModelEssentiaReservoir extends ModelBase")
                        && reservoirModel.contains("base")
                        && reservoirModel.contains("wallNorth")
                        && reservoirModel.contains("wallSouth")
                        && reservoirModel.contains("wallWest")
                        && reservoirModel.contains("wallEast")
                        && reservoirModel.contains("ringTop")
                        && reservoirModel.contains("renderAll(float scale)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
