package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileFocalManipulatorRendererStaticGuardTest {

    @Test
    public void focalManipulatorRendererKeepsReferenceModelAndFocusContract() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileFocalManipulatorRenderer.java");
        String thaumatorium = read("src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java");
        String arcaneWorkbench = read("src/main/java/thaumcraft/client/renderers/tile/TileArcaneWorkbenchRenderer.java");

        assertTrue(source.contains("textures/models/wandtable.png"));
        assertTrue(source.contains("new ModelArcaneWorkbench()"));
        assertTrue(source.contains("focus.getItem() instanceof ItemFocusBasic"));
        assertTrue(source.contains("tableModel.renderAll(MODEL_SCALE);"));
        assertTrue(source.contains("MathHelper.sin(ticks / 14.0F) * 0.2F + 0.2F"));
        assertTrue(source.contains("renderItem(focus.copy(), ItemCameraTransforms.TransformType.GROUND)"));

        assertTrue(thaumatorium.contains("textures/models/thaumatorium.png"));
        assertTrue(thaumatorium.contains("model.renderAll();"));
        assertTrue(thaumatorium.contains("renderItem(output.copy(), ItemCameraTransforms.TransformType.GROUND)"));
        assertTrue(thaumatorium.contains("GlStateManager.scale(0.75F, 0.75F, 0.75F);"));

        assertTrue(arcaneWorkbench.contains("textures/models/worktable.png"));
        assertTrue(arcaneWorkbench.contains("wand.getItem() instanceof ItemWandCasting"));
        assertTrue(arcaneWorkbench.contains("renderItem(wand.copy(), ItemCameraTransforms.TransformType.GROUND)"));
        assertTrue(arcaneWorkbench.contains("GlStateManager.scale(0.60F, 0.60F, 0.60F);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
