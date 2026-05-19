package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumatoriumTopFallbackParityStaticGuardTest {

    @Test
    public void tileThaumatoriumTopMustFallbackToMetaNineWhenBottomIsMissing() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileThaumatoriumTop.java");

        assertTrue("TileThaumatoriumTop must retain blockstate fallback for orphaned top blocks",
                source.contains("state.getValue(BlockMetalDevice.TYPE) == 11")
                        && source.contains("state.withProperty(BlockMetalDevice.TYPE, 9)"));
        assertTrue("TileThaumatoriumTop should keep server-side-only fallback mutation",
                source.contains("if (!this.world.isRemote)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
