package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ArcaneBoreRendererFidelityStaticGuardTest {

    @Test
    public void boreTileAndRendererShouldKeepReferenceAimingState() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileArcaneBoreRenderer.java");

        assertTrue("TileArcaneBore should keep renderer-facing bore aim/easing state and orientation target mapping",
                tile.contains("public float vRadX = 0.0F;")
                        && tile.contains("public float vRadZ = 0.0F;")
                        && tile.contains("public float tRadX = 0.0F;")
                        && tile.contains("public float tRadZ = 0.0F;")
                        && tile.contains("public float mRadX = 0.0F;")
                        && tile.contains("public float mRadZ = 0.0F;")
                        && tile.contains("public int rotX = 0;")
                        && tile.contains("public int rotZ = 0;")
                        && tile.contains("public int tarX = 0;")
                        && tile.contains("public int tarZ = 0;")
                        && tile.contains("public int speedX = 0;")
                        && tile.contains("public int speedZ = 0;")
                        && tile.contains("this.tarZ = 90;")
                        && tile.contains("this.tarX = 270;")
                        && tile.contains("this.tarX = 180;")
                        && tile.contains("this.rotX = this.tarX;")
                        && tile.contains("this.rotZ = this.tarZ;")
                        && tile.contains("this.tRadX = MathHelper.wrapDegrees((float) this.rotX) + rx;")
                        && tile.contains("this.mRadX = Math.abs((this.vRadX - this.tRadX) / 6.0F);")
                        && tile.contains("this.vRadX *= 0.9F;")
                        && tile.contains("this.vRadZ *= 0.9F;"));

        assertTrue("TileArcaneBoreRenderer should consume the tile aim/easing state instead of static facing-only rotations",
                renderer.contains("GlStateManager.rotate((float) tile.rotX - tile.vRadX + partialTicks * (float) tile.speedX, 0.0F, 1.0F, 0.0F);")
                         && renderer.contains("GlStateManager.rotate((float) tile.rotZ - tile.vRadZ + partialTicks * (float) tile.speedZ, 0.0F, 0.0F, 1.0F);")
                         && renderer.contains("if (tile.baseOrientation == EnumFacing.DOWN) {")
                         && renderer.indexOf("GlStateManager.pushMatrix();\n        bindTexture(BORE_TEXTURE);")
                         < renderer.indexOf("if (tile.baseOrientation == EnumFacing.DOWN) {")
                         && renderer.contains("GlStateManager.rotate(tile.topRotation, 0.0F, 1.0F, 0.0F);")
                         && renderer.contains("emitModel.render(MODEL_SCALE, tile.hasFocus);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
