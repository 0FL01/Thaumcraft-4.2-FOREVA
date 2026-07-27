package thaumcraft.common.lib.world;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaintAndEldritchWorldgenCascadingGuardTest {

    @Test
    public void taintDecorationShouldNotNotifyObserversOrScanUnloadedNeighbors() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java");
        int start = source.indexOf("private void decorateSpecial");
        int end = source.indexOf("public TempCategory getTempCategory", start);
        assertTrue(start >= 0 && end > start);

        String method = source.substring(start, end);
        int areaCheck = method.indexOf("world.isAreaLoaded(tpos.add(-1, -1, -1), tpos.add(1, 1, 1), false)");
        int adjacencyCheck = method.indexOf("BlockUtils.isAdjacentToSolidBlock(world, tpos)");

        assertTrue(source.contains("Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_OBSERVERS"));
        assertEquals(3, countOccurrences(method, "WORLDGEN_FLAGS"));
        assertTrue(areaCheck >= 0 && areaCheck < adjacencyCheck);
    }

    @Test
    public void eldritchRingShouldPreflightItsCompleteFootprint() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/WorldGenEldritchRing.java");
        int start = source.indexOf("public boolean generate(World world, Random rand, BlockPos pos)");
        int end = source.indexOf("private boolean isValidRingSpawn", start);
        assertTrue(start >= 0 && end > start);

        String method = source.substring(start, end);
        int areaCheck = method.indexOf("world.isAreaLoaded(pos.add(-4, -4, -4), pos.add(4, 8, 4), false)");
        int locationCheck = method.indexOf("isValidRingSpawn(world");
        int placement = method.indexOf("world.setBlockState");

        assertTrue(areaCheck >= 0 && areaCheck < locationCheck);
        assertTrue(areaCheck < placement);
    }

    private static int countOccurrences(String source, String token) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(token, index)) >= 0) {
            ++count;
            index += token.length();
        }
        return count;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
