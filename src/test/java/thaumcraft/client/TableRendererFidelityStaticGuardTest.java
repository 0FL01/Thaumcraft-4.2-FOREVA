package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TableRendererFidelityStaticGuardTest {

    @Test
    public void tableFamilyRenderersUseModelDrivenPaths() throws IOException {
        String modelTable = read("src/main/java/thaumcraft/client/renderers/models/ModelTable.java");
        String tableRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTableRenderer.java");
        String deconRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileDeconstructionTableRenderer.java");
        String arcaneWorkbenchRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileArcaneWorkbenchRenderer.java");
        String researchRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java");
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/TileRenderHelper.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blocktable.json");
        String plainTableModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_0.json");
        String researchMasterModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_2.json");
        String researchPartnerModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_6.json");
        String deconModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_14.json");
        String arcaneWorkbenchModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_15.json");

        assertTrue("ModelTable should define the classic top/legs/crossbar geometry",
                modelTable.contains("class ModelTable extends ModelBase")
                        && modelTable.contains("top")
                        && modelTable.contains("leg1")
                        && modelTable.contains("leg2")
                        && modelTable.contains("crossbar")
                        && modelTable.contains("renderAll(float scale)"));

        assertTrue("TileTableRenderer should no longer duplicate the static table shell once the shell moved into block models",
                !tableRenderer.contains("new ModelTable()")
                        && !tableRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && !tableRenderer.contains("TileRenderHelper.orientBillboardToPlayer();"));

        assertTrue("TileDeconstructionTableRenderer should keep thaumometer/input/aspect overlays after the static shell moved into the block model",
                deconRenderer.contains("renderThaumometer")
                        && deconRenderer.contains("renderItemGround")
                        && deconRenderer.contains("tile.aspect.getImage()")
                        && deconRenderer.contains("TileRenderHelper.renderEntityItem(tile, thaumometer, 0.0F);")
                        && deconRenderer.contains("TileRenderHelper.renderEntityItem(tile, stack, 0.0F);")
                        && !deconRenderer.contains("renderTableModel")
                        && !deconRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && !deconRenderer.contains("renderPlate("));

        assertTrue("TileArcaneWorkbenchRenderer should keep only the wand overlay after the static shell moved into the block model",
                arcaneWorkbenchRenderer.contains("wand.getItem() instanceof ItemWandCasting")
                        && arcaneWorkbenchRenderer.contains("TileRenderHelper.renderEntityItem(tile, wand, 0.0F);")
                        && !arcaneWorkbenchRenderer.contains("renderTableModel")
                        && !arcaneWorkbenchRenderer.contains("tableModel.renderAll(MODEL_SCALE);"));

        assertTrue("TileRenderHelper should keep the shared TESR entity-item path worldless-safe for display-item renderers",
                helper.contains("static void renderEntityItem(TileEntity tile, ItemStack stack, float hoverStart)")
                        && helper.contains("renderEntityItem(tile == null ? null : tile.getWorld(), stack, hoverStart);")
                        && helper.contains("return world != null ? world : Minecraft.getMinecraft().world;")
                        && helper.contains("renderStack.setCount(1);"));

        assertTrue("TileResearchTableRenderer should keep only inkwell/quill/parchment/notes overlays after the static shell moved into block models",
                researchRenderer.contains("tableModel.renderInkwell(MODEL_SCALE);")
                        && researchRenderer.contains("tableModel.renderScroll(MODEL_SCALE, color);")
                        && !researchRenderer.contains("tableModel.renderAll(MODEL_SCALE);"));

        assertTrue("Table blockstate should route plain and research table halves to shaped block models instead of the old shared cube placeholder",
                blockstate.contains("\"type=1\": { \"model\": \"thaumcraft:blocktable_0\", \"y\": 90 }")
                        && blockstate.contains("\"type=2\": { \"model\": \"thaumcraft:blocktable_2\", \"y\": 270 }")
                        && blockstate.contains("\"type=5\": { \"model\": \"thaumcraft:blocktable_2\" }")
                        && blockstate.contains("\"type=6\": { \"model\": \"thaumcraft:blocktable_6\", \"y\": 90 }")
                        && blockstate.contains("\"type=8\": { \"model\": \"thaumcraft:blocktable_6\" }"));

        assertTrue("Plain and research table block models should now carry the shell geometry that used to live in TESR-only paths",
                plainTableModel.contains("\"surface\": \"thaumcraft:models/table\"")
                        && plainTableModel.contains("\"from\": [0, 12, 0]")
                        && plainTableModel.contains("\"from\": [0, 0, 4]")
                        && researchMasterModel.contains("\"surface\": \"thaumcraft:models/restable\"")
                        && researchMasterModel.contains("\"from\": [4, 2, 6]")
                        && researchPartnerModel.contains("\"surface\": \"thaumcraft:models/restable\"")
                        && researchPartnerModel.contains("\"from\": [10, 0, 10]"));

        assertTrue("Deconstruction and arcane workbench block models should now carry the table shell geometry instead of full-cube placeholders",
                deconModel.contains("\"ambientocclusion\": false")
                        && deconModel.contains("\"surface\": \"thaumcraft:models/decontable\"")
                        && deconModel.contains("\"from\": [0, 8, 0]")
                        && deconModel.contains("\"from\": [9, 4, 11]")
                        && arcaneWorkbenchModel.contains("\"surface\": \"thaumcraft:models/worktable\"")
                        && arcaneWorkbenchModel.contains("\"from\": [0, 0, 0]")
                        && arcaneWorkbenchModel.contains("\"from\": [3, 4, 11]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
