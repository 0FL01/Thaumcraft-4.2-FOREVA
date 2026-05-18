package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThaumatoriumInventoryNameParityStaticGuardTest {

    @Test
    public void thaumatoriumInventoryNameUsesReferenceAlchemyFurnaceKey() throws IOException {
        String bottom = read("src/main/java/thaumcraft/common/tiles/TileThaumatorium.java");
        String top = read("src/main/java/thaumcraft/common/tiles/TileThaumatoriumTop.java");

        assertTrue("TileThaumatorium must keep the reference container name key",
                bottom.contains("return \"container.alchemyfurnace\";"));
        assertTrue("TileThaumatoriumTop fallback must keep the reference container name key",
                top.contains("return this.resolveBottom() == null ? \"container.alchemyfurnace\" : this.resolveBottom().getName();"));

        assertFalse("Thaumatorium classes should not drift back to non-reference container key",
                bottom.contains("container.thaumatorium") || top.contains("container.thaumatorium"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
