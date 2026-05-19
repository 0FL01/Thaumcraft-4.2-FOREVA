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
        String deconModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_14.json");
        String arcaneWorkbenchModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_15.json");

        assertTrue("ModelTable should define the classic top/legs/crossbar geometry",
                modelTable.contains("class ModelTable extends ModelBase")
                        && modelTable.contains("top")
                        && modelTable.contains("leg1")
                        && modelTable.contains("leg2")
                        && modelTable.contains("crossbar")
                        && modelTable.contains("renderAll(float scale)"));

        assertTrue("TileTableRenderer should render model-based table texture with metadata orientation",
                tableRenderer.contains("new ModelTable()")
                        && tableRenderer.contains("int md = tile.getBlockMetadata();")
                        && tableRenderer.contains("if (md >= 6)")
                        && tableRenderer.contains("if (md == 1)")
                        && tableRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && !tableRenderer.contains("TileRenderHelper.orientBillboardToPlayer();"));

        assertTrue("TileDeconstructionTableRenderer should keep thaumometer/input/aspect overlays after the static shell moved into the block model",
                deconRenderer.contains("new ModelArcaneWorkbench()")
                        && deconRenderer.contains("renderThaumometer")
                        && deconRenderer.contains("renderItemGround")
                        && deconRenderer.contains("tile.aspect.getImage()")
                        && !deconRenderer.contains("renderTableModel")
                        && !deconRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && !deconRenderer.contains("renderPlate("));

        assertTrue("TileArcaneWorkbenchRenderer should keep only the wand overlay after the static shell moved into the block model",
                arcaneWorkbenchRenderer.contains("wand.getItem() instanceof ItemWandCasting")
                        && arcaneWorkbenchRenderer.contains("renderItem(wand.copy(), ItemCameraTransforms.TransformType.GROUND)")
                        && !arcaneWorkbenchRenderer.contains("renderTableModel")
                        && !arcaneWorkbenchRenderer.contains("tableModel.renderAll(MODEL_SCALE);"));

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
