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
        String eldritchCrystalModel = read("src/main/java/thaumcraft/client/renderers/models/ModelEldritchCrystal.java");
        String crabSpawner = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchCrabSpawnerRenderer.java");
        String crabVentModel = read("src/main/java/thaumcraft/client/renderers/models/ModelCrabVent.java");

        assertTrue(research.contains("textures/misc/quill.png"));
        assertTrue(research.contains("TileRenderHelper.drawTexturedQuad(0.5F"));
        assertFalse(research.contains("renderFloatingItem(new ItemStack(Items.FEATHER)"));

        assertTrue(cap.contains("textures/models/obelisk_cap_altar.png"));
        assertTrue(cap.contains("public TileEldritchCapRenderer(ResourceLocation capTexture)"));
        assertTrue(cap.contains("tile.getWorld().provider.getDimension() == Config.dimensionOuterId"));
        assertTrue(cap.contains("new ModelEldritchCap()"));
        assertTrue(cap.contains("MODEL.renderCap();"));
        assertTrue(cap.contains("OpenGlHelper.setLightmapTextureCoords"));
        assertTrue(cap.contains("TileRenderHelper.renderEntityItem(altar, eye, 0.0F);"));
        assertFalse(cap.contains("TileRenderHelper.renderFloatingItem("));
        assertFalse(cap.contains("GlStateManager.disableLighting();"));

        assertTrue(obelisk.contains("new ModelEldritchCap()"));
        assertTrue(obelisk.contains("renderObeliskCapPair()"));
        assertTrue(obelisk.contains("CAP_MODEL.renderCap();"));
        assertTrue(obelisk.contains("OpenGlHelper.setLightmapTextureCoords"));
        assertTrue(obelisk.contains("GlStateManager.enableRescaleNormal()"));
        assertTrue(obelisk.contains("renderSides(0.5F, 3.0F);"));
        assertTrue(obelisk.contains(".tex(0.0D, 1.0D)") && obelisk.contains(".tex(1.0D, 0.0D)"));
        assertTrue(obelisk.indexOf("renderSideFields(") < obelisk.indexOf("bindTexture(sideTexture);"));
        assertFalse(obelisk.contains("TileRenderHelper.drawTexturedQuad(0.52F"));
        assertFalse(obelisk.contains("GlStateManager.depthMask(false)"));

        assertTrue(capModel.contains("Wavefront \"Cap\" group triangles from obelisk_cap.obj"));
        assertTrue(capModel.contains("buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL)"));
        assertTrue(capModel.contains(".tex(uv[0], 1.0F - uv[1])"));
        assertTrue(capModel.contains("private static final int[][] TRIANGLES"));
        assertTrue(capModel.contains("Wavefront \"Tip\" group triangles from obelisk_cap.obj"));
        assertTrue(capModel.contains("private static final int[][] TIP_TRIANGLES"));

        assertTrue(lock.contains("TileRenderHelper.renderEntityItem(tile, key, 0.0F);"));
        assertTrue(lock.contains("ActiveRenderInfo.getRotationX()"));
        assertTrue(lock.contains("private static final float FIELD_MIN = -2.0F;"));
        assertTrue(lock.contains("private static final float FIELD_MAX = 3.0F;"));
        assertFalse(lock.contains("TileRenderHelper.renderFloatingItem(key"));

        assertTrue(eldritchCrystal.contains("new ModelEldritchCrystal()"));
        assertTrue(eldritchCrystal.contains("OpenGlHelper.setLightmapTextureCoords"));
        assertTrue(eldritchCrystal.contains("model.renderBase();"));
        assertTrue(eldritchCrystal.contains("model.renderCrystal();"));
        assertTrue(eldritchCrystal.contains("tile.getWorld() == null ? 0 : Math.floorMod(tile.hashCode(), 4)"));
        assertFalse(eldritchCrystal.contains("new ModelCrystal()"));
        assertFalse(eldritchCrystal.contains("TileRenderHelper.drawTexturedQuad("));
        assertFalse(eldritchCrystal.contains("EnumFacing.byIndex"));

        assertTrue(eldritchCrystalModel.contains("Wavefront vcrystal.obj groups from the original 1.7.10 renderer asset."));
        assertTrue(eldritchCrystalModel.contains("CRYSTAL_TRIANGLES"));
        assertTrue(eldritchCrystalModel.contains("BASE_TRIANGLES"));
        assertTrue(eldritchCrystalModel.contains("renderCrystal()"));
        assertTrue(eldritchCrystalModel.contains("renderBase()"));
        assertTrue(eldritchCrystalModel.contains("DefaultVertexFormats.POSITION_TEX_NORMAL"));

        assertTrue(crabSpawner.contains("new ModelCrabVent()"));
        assertTrue(crabSpawner.contains("model.renderAll();"));
        assertFalse(crabSpawner.contains("renderVentGeometry()"));
        assertFalse(crabSpawner.contains("drawTexturedCuboid("));
        assertFalse(crabSpawner.contains("drawCross("));

        assertTrue(crabVentModel.contains("Wavefront crabvent.obj groups from the original 1.7.10 renderer asset."));
        assertTrue(crabVentModel.contains("private static final int[][] VENT_TRIANGLES"));
        assertTrue(crabVentModel.contains("buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
