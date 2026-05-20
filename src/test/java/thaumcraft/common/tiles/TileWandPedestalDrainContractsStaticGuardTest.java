package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileWandPedestalDrainContractsStaticGuardTest {

    @Test
    public void wandPedestalTileAndTesrKeepDrainContracts() throws IOException {
        String tileSource = read("src/main/java/thaumcraft/common/tiles/TileWandPedestal.java");
        String rendererSource = read("src/main/java/thaumcraft/client/renderers/tile/TileWandPedestalRenderer.java");

        assertTrue(tileSource.contains("implements ITickable"));
        assertTrue(tileSource.contains("public boolean draining = false;"));
        assertTrue(tileSource.contains("public int drainX = 0;"));
        assertTrue(tileSource.contains("public int drainColor = 0;"));
        assertTrue(tileSource.contains("private List<BlockPos> nodes = null;"));
        assertTrue(tileSource.contains("if (this.counter % 5 == 0"));
        assertTrue(tileSource.contains("instanceof ItemWandCasting"));
        assertTrue(tileSource.contains("instanceof ItemAmuletVis"));
        assertTrue(tileSource.contains("this.drainColor = color;"));
        assertTrue(tileSource.contains("private void findNodes()"));

        assertTrue(rendererSource.contains("if (!stack.isEmpty() && tile.draining)"));
        assertTrue(rendererSource.contains("TileRenderHelper.drawWispyLine("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
