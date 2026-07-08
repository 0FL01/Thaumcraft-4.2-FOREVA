package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class InfusionRendererFidelityStaticGuardTest {

    @Test
    public void infusionRendererFamilyKeepsReferenceMatrixAndPillarModelContracts() throws IOException {
        String matrixRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileRunicMatrixRenderer.java");
        String pillarRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileInfusionPillarRenderer.java");
        String pillarModel = read("src/main/java/thaumcraft/client/renderers/models/ModelInfusionPillar.java");

        assertTrue("TileRunicMatrixRenderer should keep the animated cube cluster and halo paths while leaving the idle shell available for TESR-only world/item rendering",
                matrixRenderer.contains("new ModelCube(0)")
                        && matrixRenderer.contains("new ModelCube(32)")
                        && matrixRenderer.contains("renderCubeCluster(")
                        && matrixRenderer.contains("renderCubeOverlay(")
                        && matrixRenderer.contains("drawHalo(")
                        && matrixRenderer.contains("if (tile.getWorld() != null) {"));

        assertTrue("TileRunicMatrixRenderer should harden the solid shell pass and restore GL state so active overlay cannot make matrix textures transparent",
                matrixRenderer.contains("prepareSolidMatrixPass();\n        renderCubeCluster(tile, ticks, instability, startUp);")
                        && matrixRenderer.contains("renderCubeOverlay(tile, ticks, instability, startUp);\n        }\n        restoreDefaultMatrixPass();")
                        && matrixRenderer.contains("private static void prepareSolidMatrixPass()")
                        && matrixRenderer.contains("private static void restoreDefaultMatrixPass()")
                        && matrixRenderer.contains("GlStateManager.disableBlend();")
                        && matrixRenderer.contains("GlStateManager.depthMask(false);")
                        && matrixRenderer.contains("GlStateManager.depthMask(true);")
                        && matrixRenderer.contains("GlStateManager.blendFunc(770, 771);")
                        && matrixRenderer.contains("GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);"));

        assertTrue("TileInfusionPillarRenderer should use the dedicated model-driven reference pillar path instead of the old prism fallback",
                pillarRenderer.contains("new ModelInfusionPillar()")
                        && pillarRenderer.contains("bindTexture(TEXTURE);")
                        && pillarRenderer.contains("MODEL.render();")
                        && pillarRenderer.contains("if (orientation == 3)")
                        && pillarRenderer.contains("orientation == 4")
                        && pillarRenderer.contains("orientation == 5")
                        && !pillarRenderer.contains("drawPillarPrism("));

        assertTrue("ModelInfusionPillar should preserve the original pillar wavefront geometry surface",
                pillarModel.contains("Wavefront pillar.obj triangles")
                        && pillarModel.contains("private static final float[][] VERTICES")
                        && pillarModel.contains("private static final float[][] UVS")
                        && pillarModel.contains("private static final float[][] NORMALS")
                        && pillarModel.contains("private static final int[][] TRIANGLES")
                        && pillarModel.contains("DefaultVertexFormats.POSITION_TEX_NORMAL")
                        && pillarModel.contains("GL11.GL_TRIANGLES")
                        && pillarModel.contains(".tex(uv[0], 1.0F - uv[1])"));

        assertTrue("Infusion pillar texture asset must exist for the dedicated renderer path",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/models/pillar.png")));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
