package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class AlchemyFurnaceAdvancedRendererFidelityStaticGuardTest {

    @Test
    public void alchemyFurnaceAdvancedRendererUsesTc4ObjAndOverlayPaths() throws IOException {
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileAlchemyFurnaceAdvancedRenderer.java");
        String alembic = read("src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java");
        String alembicModel = read("src/main/java/thaumcraft/client/renderers/models/ModelAlembic.java");
        String furnaceBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_0.json");

        assertTrue("TileAlchemyFurnaceAdvancedRenderer should preserve the TC4 OBJ groups and dynamic overlays",
                renderer.contains("TileEntitySpecialRenderer<TileAlchemyFurnaceAdvanced>")
                        && renderer.contains("CCModel.parseObjModels(FURNACE_MODEL)")
                        && renderer.contains("models.get(\"Base\")")
                        && renderer.contains("models.get(\"Tank\")")
                        && renderer.contains("renderModel(this.base)")
                        && renderer.contains("renderModel(this.tank)")
                        && renderer.contains("atlas(\"thaumcraft:blocks/fluxgoo\")")
                        && renderer.contains("atlas(\"thaumcraft:blocks/metalbase\")")
                        && renderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && renderer.contains("Blocks.FIRE.getDefaultState()")
                        && renderer.contains("renderVis(")
                        && renderer.contains("renderHeat(")
                        && renderer.contains("renderQuadCenteredFromIcon(")
                        && renderer.contains("bindTexture(tile.heat > 100 ? FURNACE_ON : FURNACE);")
                        && renderer.contains("bindTexture(tile.vis > 0 ? TANK_ON : TANK);")
                        && renderer.contains("for (int side = 0; side < 4; ++side)")
                        && renderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && !renderer.contains("ModelAlchemyFurnaceAdvanced")
                        && !renderer.contains("Blocks.LAVA")
                        && !renderer.contains("TileRenderHelper.orientBillboardToPlayer()")
                        && !renderer.contains("drawFurnaceGlowQuad("));

        assertTrue("TileAlembicRenderer should use model-driven bore nozzle path instead of ad-hoc cuboid fallback",
                alembic.contains("new ModelBoreBase()")
                        && alembic.contains("new ModelAlembic()")
                        && alembic.contains("modelBore.renderNozzle(MODEL_SCALE)")
                        && alembic.contains("renderOutputNozzles(")
                        && alembic.contains("if (tile.getWorld() != null)")
                        && alembic.contains("GlStateManager.translate(0.0F, 0.0F, -0.4F);")
                        && !alembic.contains("drawPrism(")
                        && !alembic.contains("drawTexturedCuboid("));

        assertTrue("ModelAlembic should preserve the original alembic.obj grouped geometry surface instead of the old ModelRenderer box fallback",
                alembicModel.contains("Wavefront alembic.obj groups")
                        && alembicModel.contains("private static final float[][] VERTICES")
                        && alembicModel.contains("private static final float[][] UVS")
                        && alembicModel.contains("private static final float[][] NORMALS")
                        && alembicModel.contains("private static final int[][] POT_TRIANGLES")
                        && alembicModel.contains("private static final int[][] LEGS_TRIANGLES")
                        && alembicModel.contains("private static final int[][] TUBE_MAIN_TRIANGLES")
                        && alembicModel.contains("private static final int[][] TUBE_SMALL_TRIANGLES")
                        && alembicModel.contains("private static final int[][] PANEL_TRIANGLES")
                        && alembicModel.contains(".tex(uv[0], 1.0F - uv[1])")
                        && alembicModel.contains("DefaultVertexFormats.POSITION_TEX_NORMAL")
                        && !alembicModel.contains("extends ModelBase")
                        && !alembicModel.contains("new ModelRenderer("));

        assertTrue("Basic alchemy furnace block model should be the TC4 full-cube item fallback rather than the advanced furnace tank-panel shell",
                furnaceBlockModel.contains("\"parent\": \"block/cube\"")
                        && furnaceBlockModel.contains("\"down\": \"thaumcraft:blocks/al_furnace_top\"")
                        && furnaceBlockModel.contains("\"up\": \"thaumcraft:blocks/al_furnace_top\"")
                        && furnaceBlockModel.contains("\"north\": \"thaumcraft:blocks/al_furnace_front_off\"")
                        && !furnaceBlockModel.contains("thaumcraft:models/alch_furnace_tank")
                        && !furnaceBlockModel.contains("\"elements\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
