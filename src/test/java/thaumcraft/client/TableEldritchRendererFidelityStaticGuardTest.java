package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TableEldritchRendererFidelityStaticGuardTest {

    @Test
    public void researchAndEldritchRenderersKeepReferenceShapedItemDisplayContracts() throws IOException {
        String research = read("src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java");
        String cap = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchCapRenderer.java");
        String obelisk = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchObeliskRenderer.java");
        String capModel = read("src/main/java/thaumcraft/client/renderers/models/ModelEldritchCap.java");
        String lock = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchLockRenderer.java");
        String eldritchCrystal = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchCrystalRenderer.java");
        String crabSpawner = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchCrabSpawnerRenderer.java");

        assertTrue(research.contains("textures/misc/quill.png"));
        assertTrue(research.contains("TileRenderHelper.drawTexturedQuad(0.5F"));
        assertFalse(research.contains("renderFloatingItem(new ItemStack(Items.FEATHER)"));

        assertTrue(cap.contains("textures/models/obelisk_cap_altar.png"));
        assertTrue(cap.contains("public TileEldritchCapRenderer(ResourceLocation capTexture)"));
        assertTrue(cap.contains("tile.getWorld().provider.getDimension() == Config.dimensionOuterId"));
        assertTrue(cap.contains("new ModelEldritchCap()"));
        assertTrue(cap.contains("MODEL.renderCap();"));
        assertTrue(cap.contains("renderItem(eye.copy(), ItemCameraTransforms.TransformType.GROUND)"));
        assertFalse(cap.contains("TileRenderHelper.renderFloatingItem("));

        assertTrue(obelisk.contains("new ModelEldritchCap()"));
        assertTrue(obelisk.contains("renderObeliskCapPair()"));
        assertTrue(obelisk.contains("CAP_MODEL.renderCap();"));
        assertFalse(obelisk.contains("TileRenderHelper.drawTexturedQuad(0.52F"));

        assertTrue(capModel.contains("Wavefront \"Cap\" group triangles from obelisk_cap.obj"));
        assertTrue(capModel.contains("buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL)"));
        assertTrue(capModel.contains("private static final int[][] TRIANGLES"));

        assertTrue(lock.contains("renderItem(key, ItemCameraTransforms.TransformType.GROUND)"));
        assertTrue(lock.contains("ActiveRenderInfo.getRotationX()"));
        assertTrue(lock.contains("private static final float FIELD_MIN = -2.0F;"));
        assertTrue(lock.contains("private static final float FIELD_MAX = 3.0F;"));
        assertFalse(lock.contains("TileRenderHelper.renderFloatingItem(key"));

        assertTrue(eldritchCrystal.contains("new ModelCrystal()"));
        assertTrue(eldritchCrystal.contains("OpenGlHelper.setLightmapTextureCoords"));
        assertTrue(eldritchCrystal.contains("model.render();"));
        assertFalse(eldritchCrystal.contains("TileRenderHelper.drawTexturedQuad(0.26F"));

        assertTrue(crabSpawner.contains("renderVentGeometry()"));
        assertTrue(crabSpawner.contains("drawTexturedCuboid("));
        assertTrue(crabSpawner.contains("DefaultVertexFormats.POSITION_TEX_NORMAL"));
        assertFalse(crabSpawner.contains("drawCross("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
