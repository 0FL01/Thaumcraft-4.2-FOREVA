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

        assertTrue(source.contains("textures/models/wandtable.png"));
        assertTrue(source.contains("new ModelArcaneWorkbench()"));
        assertTrue(source.contains("focus.getItem() instanceof ItemFocusBasic"));
        assertTrue(source.contains("tableModel.renderAll(MODEL_SCALE);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
