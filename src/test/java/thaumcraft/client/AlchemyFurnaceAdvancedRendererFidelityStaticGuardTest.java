package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class AlchemyFurnaceAdvancedRendererFidelityStaticGuardTest {

    @Test
    public void alchemyFurnaceAdvancedRendererUsesModelDrivenBaseTankAndGlowPaths() throws IOException {
        String model = read("src/main/java/thaumcraft/client/renderers/models/ModelAlchemyFurnaceAdvanced.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileAlchemyFurnaceAdvancedRenderer.java");
        String alembic = read("src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java");
        String alembicModel = read("src/main/java/thaumcraft/client/renderers/models/ModelAlembic.java");
        String furnaceBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_0.json");

        assertTrue("ModelAlchemyFurnaceAdvanced should define base/tank/lava panels",
                model.contains("class ModelAlchemyFurnaceAdvanced extends ModelBase")
                        && model.contains("base")
                        && model.contains("tankPanel")
                        && model.contains("lavaPanel")
                        && model.contains("renderBase(float scale)")
                        && model.contains("renderTankPanel(float scale)")
                        && model.contains("renderLavaPanel(float scale)"));

        assertTrue("TileAlchemyFurnaceAdvancedRenderer should keep the dedicated tank-frame path plus reference-shaped vis/lava atlas overlays after the static shell moved into the block model",
                renderer.contains("new ModelAlchemyFurnaceAdvanced()")
                        && renderer.contains("model.renderTankPanel(MODEL_SCALE)")
                        && renderer.contains("new ResourceLocation(\"thaumcraft\", \"blocks/al_furnace_top_filled\")")
                        && renderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && renderer.contains("Blocks.LAVA.getDefaultState()")
                        && renderer.contains("renderVisPanels(")
                        && renderer.contains("renderHeatPanels(")
                        && renderer.contains("drawAtlasQuad(")
                        && renderer.contains("bindTexture(content > 0.0F ? TANK_ON : TANK);")
                        && renderer.contains("for (int side = 0; side < 4; side++)")
                        && renderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && !renderer.contains("model.renderBase(MODEL_SCALE)")
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
                        && alembicModel.contains("DefaultVertexFormats.POSITION_TEX_NORMAL")
                        && !alembicModel.contains("extends ModelBase")
                        && !alembicModel.contains("new ModelRenderer("));

        assertTrue("Alchemy furnace block model should now carry the static base and tank-panel shell instead of the old full-cube placeholder",
                furnaceBlockModel.contains("\"ambientocclusion\": false")
                        && furnaceBlockModel.contains("\"front\": \"thaumcraft:blocks/al_furnace_front_off\"")
                        && furnaceBlockModel.contains("\"tank\": \"thaumcraft:models/alch_furnace_tank\"")
                        && furnaceBlockModel.contains("\"from\": [0, 0, 0]")
                        && furnaceBlockModel.contains("\"to\": [16, 14, 16]")
                        && furnaceBlockModel.contains("\"from\": [4.5, 5, 0]")
                        && furnaceBlockModel.contains("\"from\": [15, 5, 4.5]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
