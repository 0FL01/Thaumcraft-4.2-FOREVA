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
        String furnaceBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_0.json");

        assertTrue("ModelAlchemyFurnaceAdvanced should define base/tank/lava panels",
                model.contains("class ModelAlchemyFurnaceAdvanced extends ModelBase")
                        && model.contains("base")
                        && model.contains("tankPanel")
                        && model.contains("lavaPanel")
                        && model.contains("renderBase(float scale)")
                        && model.contains("renderTankPanel(float scale)")
                        && model.contains("renderLavaPanel(float scale)"));

        assertTrue("TileAlchemyFurnaceAdvancedRenderer should keep only dynamic lava-panel and heat-glow paths after the static shell moved into the block model",
                renderer.contains("new ModelAlchemyFurnaceAdvanced()")
                        && renderer.contains("for (int side = 0; side < 4; side++)")
                        && renderer.contains("model.renderLavaPanel(MODEL_SCALE)")
                        && renderer.contains("bindTexture(burning ? TANK_ON : TANK);")
                        && renderer.contains("drawFurnaceGlowQuad(")
                        && !renderer.contains("model.renderBase(MODEL_SCALE)")
                        && !renderer.contains("model.renderTankPanel(MODEL_SCALE)")
                        && !renderer.contains("TileRenderHelper.orientBillboardToPlayer()")
                        && !renderer.contains("TileRenderHelper.drawTexturedQuad("));

        assertTrue("TileAlembicRenderer should use model-driven bore nozzle path instead of ad-hoc cuboid fallback",
                alembic.contains("new ModelBoreBase()")
                        && alembic.contains("modelBore.renderNozzle(MODEL_SCALE)")
                        && alembic.contains("renderOutputNozzles(")
                        && !alembic.contains("drawPrism(")
                        && !alembic.contains("drawTexturedCuboid("));

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
