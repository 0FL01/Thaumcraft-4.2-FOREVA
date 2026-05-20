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
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/LayeredFieldPlaneHelper.java");

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

        assertTrue("TileHoleRenderer should keep layered tunnel routing through the shared helper",
                hole.contains("LayeredFieldPlaneHelper.renderLayeredFace(")
                        && hole.contains("shouldRenderFace(tile.getPos(), face)"));

        assertTrue("TileEldritchNothingRenderer should keep layered tunnel field routing through the shared helper",
                nothing.contains("LayeredFieldPlaneHelper.renderLayeredFace(")
                        && nothing.contains("return !adjacent.isOpaqueCube();"));

        assertTrue("LayeredFieldPlaneHelper should keep shared tunnel-field texgen and camera-parallax contracts",
                helper.contains("textures/misc/tunnel.png")
                        && helper.contains("textures/misc/particlefield.png")
                        && helper.contains("textures/misc/particlefield32.png")
                        && helper.contains("for (int i = 0; i < 16; i++)")
                        && helper.contains("FIELD_COLOR_SEED = 31100L")
                        && helper.contains("ActiveRenderInfo.getRotationX()")
                        && helper.contains("GL11.glTexGen(")
                        && helper.contains("GlStateManager.matrixMode(5890)"));

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
