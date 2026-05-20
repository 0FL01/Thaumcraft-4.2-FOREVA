package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class MirrorRendererFidelityStaticGuardTest {

    @Test
    public void tileMirrorRendererKeepsLayeredPortalPaneAndFrameContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java");
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/LayeredFieldPlaneHelper.java");

        assertTrue("TileMirrorRenderer should keep mirror portal textures and pane overlays",
                source.contains("textures/blocks/mirrorpane.png")
                        && source.contains("textures/blocks/mirrorpanetrans.png"));

        assertTrue("TileMirrorRenderer should keep linked-vs-unlinked pane flow",
                source.contains("if (linked && isVisible(tile))")
                        && source.contains("renderPortalLayers(")
                        && source.contains("renderPane(facing, x, y, z, MIRROR_PANE_TRANS")
                        && source.contains("renderPane(facing, x, y, z, MIRROR_PANE"));

        assertTrue("TileMirrorRenderer should keep mirror frame atlas pass and orientation transform",
                source.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && source.contains("blocks/mirrorframe")
                        && source.contains("blocks/mirrorframe2")
                        && source.contains("transformFromOrientation("));

        assertTrue("TileMirrorRenderer should route linked portal fields through the shared layered-field helper with inset bounds",
                source.contains("LayeredFieldPlaneHelper.renderLayeredFaceRect(")
                        && source.contains("INSET, 1.0F - INSET, INSET, 1.0F - INSET")
                        && source.contains("view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks"));

        assertTrue("LayeredFieldPlaneHelper should keep the tunnel/particle texgen and matrix flow used by mirror portals",
                helper.contains("textures/misc/tunnel.png")
                        && helper.contains("textures/misc/particlefield.png")
                        && helper.contains("FIELD_COLOR_SEED = 31100L")
                        && helper.contains("GL11.glTexGeni")
                        && helper.contains("GlStateManager.matrixMode(5890)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
