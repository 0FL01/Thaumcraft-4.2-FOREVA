package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class VisEnergyRendererFidelityStaticGuardTest {

    @Test
    public void energizedNodeAndWorkbenchChargerRenderersUseReferenceShapedPaths() throws IOException {
        String nodeRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeEnergizedRenderer.java");
        String chargerRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileMagicWorkbenchChargerRenderer.java");
        String chargerModel = read("src/main/java/thaumcraft/client/renderers/models/ModelMagicWorkbenchCharger.java");

        assertTrue("TileNodeEnergizedRenderer should keep node-core rendering and animated lightning-ring overlay",
                nodeRenderer.contains("TileNodeRenderer.renderNodeAt(")
                        && nodeRenderer.contains("tile.getNodeType()")
                        && nodeRenderer.contains("tile.getNodeModifier()")
                        && nodeRenderer.contains("textures/items/lightningringv.png")
                        && nodeRenderer.contains("RING_FRAMES = 16")
                        && nodeRenderer.contains("drawTexturedQuad(0.33F, u0, u1, v0, v1)")
                        && !nodeRenderer.contains("textures/misc/node_bubble.png"));

        assertTrue("TileMagicWorkbenchChargerRenderer should keep model-driven ring/support/crystal render path",
                chargerRenderer.contains("new ModelMagicWorkbenchCharger()")
                        && chargerRenderer.contains("model.renderRingFloat(MODEL_SCALE)")
                        && chargerRenderer.contains("model.renderSupport(MODEL_SCALE)")
                        && chargerRenderer.contains("model.renderCrystal(MODEL_SCALE)")
                        && !chargerRenderer.contains("TileRenderHelper.drawTexturedQuad("));

        assertTrue("ModelMagicWorkbenchCharger should expose ring/support/crystal model parts",
                chargerModel.contains("class ModelMagicWorkbenchCharger extends ModelBase")
                        && chargerModel.contains("ringFloat")
                        && chargerModel.contains("support")
                        && chargerModel.contains("crystal")
                        && chargerModel.contains("renderRingFloat(float scale)")
                        && chargerModel.contains("renderSupport(float scale)")
                        && chargerModel.contains("renderCrystal(float scale)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
