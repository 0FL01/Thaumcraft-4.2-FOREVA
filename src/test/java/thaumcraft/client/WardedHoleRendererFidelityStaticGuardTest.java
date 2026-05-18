package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WardedHoleRendererFidelityStaticGuardTest {

    @Test
    public void wardedAndHoleRenderersKeepConnectedAndLayeredContracts() throws IOException {
        String warded = read("src/main/java/thaumcraft/client/renderers/tile/TileWardedRenderer.java");
        String hole = read("src/main/java/thaumcraft/client/renderers/tile/TileHoleRenderer.java");

        assertTrue("TileWardedRenderer should keep warded connected-texture matrix routing",
                warded.contains("CONNECTED_TEXTURE_REF_BY_ID")
                        && warded.contains("warded_glass_")
                        && warded.contains("face.getOpposite().getIndex()")
                        && warded.contains("isConnectedBlock(")
                        && warded.contains("ConfigBlocks.blockWarded"));

        assertTrue("TileWardedRenderer should keep warding-focus visibility gating",
                warded.contains("focus instanceof FocusWarding")
                        && warded.contains("isWardingWandHeld()"));

        assertTrue("TileHoleRenderer should keep layered tunnel and particle passes",
                hole.contains("textures/misc/tunnel.png")
                        && hole.contains("textures/misc/particlefield.png")
                        && hole.contains("textures/misc/particlefield32.png")
                        && hole.contains("for (int i = 0; i < 16; i++)")
                        && hole.contains("shouldRenderFace(tile.getPos(), face)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
