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

        assertTrue("TileTubeValve should keep client rotation state and squeek feedback toggle path",
                valveTile.contains("public float rotation = 0.0F;")
                        && valveTile.contains("TCSounds.SQUEEK")
                        && valveTile.contains("this.world != null && this.world.isRemote")
                        && valveTile.contains("this.rotation += 20.0F")
                        && valveTile.contains("this.rotation -= 20.0F"));

        assertTrue("TileTubeValveRenderer should consume tile.rotation and orient by facing-axis rotation chain",
                valveRenderer.contains("tile.rotation")
                        && valveRenderer.contains("face.getYOffset() == 0")
                        && valveRenderer.contains("GlStateManager.rotate(90.0F, face.getXOffset(), face.getYOffset(), face.getZOffset())"));

        assertTrue("TileTubeBufferRenderer should render center-anchored chokes using opposite-facing orientation",
                bufferRenderer.contains("renderValve(x, y, z, face.getOpposite()")
                        && bufferRenderer.contains("GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D)")
                        && !bufferRenderer.contains("* 0.42D"));

        assertTrue("TileTubeOnewayRenderer should keep directional orientation transform chain",
                onewayRenderer.contains("face.getYOffset() == 0")
                        && onewayRenderer.contains("GlStateManager.rotate(90.0F, face.getXOffset(), face.getYOffset(), face.getZOffset())"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
