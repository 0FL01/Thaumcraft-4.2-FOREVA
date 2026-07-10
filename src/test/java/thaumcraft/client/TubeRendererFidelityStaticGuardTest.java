package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TubeRendererFidelityStaticGuardTest {

    @Test
    public void tubeValveAndDirectionalTubeRenderersKeepReferenceDrivenTransforms() throws IOException {
        String valveTile = read("src/main/java/thaumcraft/common/tiles/TileTubeValve.java");
        String valveRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeValveRenderer.java");
        String bufferRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeBufferRenderer.java");
        String onewayRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeOnewayRenderer.java");
        String conduitHelper = read("src/main/java/thaumcraft/client/renderers/tile/TubeConduitRenderHelper.java");

        assertTrue("TileTubeValve should keep client rotation state and squeek feedback toggle path",
                valveTile.contains("public float rotation = 0.0F;")
                        && valveTile.contains("TCSounds.SQUEEK")
                        && valveTile.contains("this.world != null && this.world.isRemote")
                        && valveTile.contains("this.rotation += 20.0F")
                        && valveTile.contains("this.rotation -= 20.0F"));

        assertTrue("TileTubeValveRenderer should keep TC4 rotation, orientation, and 0.1-thick sprite extrusion",
                valveRenderer.contains("tile.rotation")
                        && valveRenderer.contains("face.getYOffset() == 0")
                        && valveRenderer.contains("thaumcraft:blocks/pipe_valve")
                        && valveRenderer.contains("VALVE_THICKNESS = 0.1F")
                        && valveRenderer.contains("renderExtrudedSprite(sprite, VALVE_THICKNESS)")
                        && valveRenderer.contains("POSITION_TEX_NORMAL")
                        && valveRenderer.contains("renderValveOverlay()")
                        && !valveRenderer.contains("tile.allowFlow")
                        && valveRenderer.contains("try {")
                        && valveRenderer.contains("} finally {")
                        && valveRenderer.contains("GlStateManager.rotate(90.0F, face.getXOffset(), face.getYOffset(), face.getZOffset())"));

        assertTrue("World shells must keep all TC4 central bounds, textures, filter semantics, and balanced GL state",
                conduitHelper.contains("FALLBACK_MIN = 6.0F / 16.0F")
                        && conduitHelper.contains("EXTENSION = 6.0F / 16.0F")
                        && conduitHelper.contains("JOINT_MIN = 6.5F / 16.0F")
                        && conduitHelper.contains("FILTER_MIN = 5.5F / 16.0F")
                        && conduitHelper.contains("BUFFER_MIN = 4.0F / 16.0F")
                        && conduitHelper.contains("pipe_2")
                        && conduitHelper.contains("pipe_3")
                        && conduitHelper.contains("pipe_restrict")
                        && conduitHelper.contains("filterAspect == null")
                        && conduitHelper.contains("uNorthAtMinX")
                        && conduitHelper.contains("uEastAtMinZ")
                        && conduitHelper.contains("vDownAtMaxZ")
                        && conduitHelper.contains("previousLightX")
                        && conduitHelper.contains("boolean blendEnabled")
                        && conduitHelper.contains("boolean cullEnabled")
                        && conduitHelper.contains("} finally {"));

        assertTrue("TileTubeBufferRenderer should render center-anchored chokes using opposite-facing orientation",
                bufferRenderer.contains("renderValve(x, y, z, face.getOpposite()")
                        && bufferRenderer.contains("GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D)")
                        && bufferRenderer.contains("} finally {")
                        && !bufferRenderer.contains("* 0.42D"));

        assertTrue("TileTubeOnewayRenderer should keep directional orientation transform chain",
                onewayRenderer.contains("face.getYOffset() == 0")
                        && onewayRenderer.contains("} finally {")
                        && onewayRenderer.contains("GlStateManager.rotate(90.0F, face.getXOffset(), face.getYOffset(), face.getZOffset())"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
