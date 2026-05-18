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

        assertTrue("TileDeconstructionTableRenderer should render model-based table plus thaumometer/input/aspect overlays",
                deconRenderer.contains("new ModelArcaneWorkbench()")
                        && deconRenderer.contains("renderTableModel")
                        && deconRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && deconRenderer.contains("renderThaumometer")
                        && deconRenderer.contains("renderItemGround")
                        && deconRenderer.contains("tile.aspect.getImage()")
                        && !deconRenderer.contains("renderPlate("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
