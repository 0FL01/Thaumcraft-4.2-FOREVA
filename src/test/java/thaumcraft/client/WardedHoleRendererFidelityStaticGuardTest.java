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
        String nothing = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchNothingRenderer.java");
        String obelisk = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchObeliskRenderer.java");

        assertTrue("TileWardedRenderer should keep warded connected-texture matrix routing",
                warded.contains("CONNECTED_TEXTURE_REF_BY_ID")
                        && warded.contains("ICON_CACHE")
                        && warded.contains("(worldTime + side) % 10L != 0L")
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
                        && hole.contains("ActiveRenderInfo")
                        && hole.contains("parallaxOffsets(")
                        && hole.contains("shouldRenderFace(tile.getPos(), face)"));

        assertTrue("TileEldritchNothingRenderer should keep layered tunnel field + camera-parallax contracts",
                nothing.contains("textures/misc/tunnel.png")
                        && nothing.contains("textures/misc/particlefield.png")
                        && nothing.contains("textures/misc/particlefield32.png")
                        && nothing.contains("for (int i = 0; i < 16; i++)")
                        && nothing.contains("ActiveRenderInfo")
                        && nothing.contains("parallaxOffsets(")
                        && nothing.contains("return !adjacent.isOpaqueCube();"));

        assertTrue("TileEldritchObeliskRenderer should keep layered side fields with camera-parallax contracts",
                obelisk.contains("textures/misc/tunnel.png")
                        && obelisk.contains("textures/misc/particlefield.png")
                        && obelisk.contains("textures/misc/particlefield32.png")
                        && obelisk.contains("for (int i = 0; i < 16; i++)")
                        && obelisk.contains("ActiveRenderInfo")
                        && obelisk.contains("parallaxOffsets(")
                        && obelisk.contains("for (EnumFacing facing : EnumFacing.HORIZONTALS)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
