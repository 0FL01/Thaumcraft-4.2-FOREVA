package thaumcraft.common.lib.world;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumcraftWorldGeneratorCascadingGuardTest {

    private static String readGeneratorSource() throws IOException {
        return new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java")),
                StandardCharsets.UTF_8);
    }

    @Test
    public void structureNodesShouldOnlyGenerateInTheirOwningChunk() throws IOException {
        String source = readGeneratorSource();
        int start = source.indexOf("private boolean generateStructureNode(World world, Random rand, int chunkX, int chunkZ)");
        int end = source.indexOf("private boolean generateWildNodes", start);
        assertTrue(start >= 0 && end > start);

        String method = source.substring(start, end);
        int xGate = method.indexOf("(nearest.getX() >> 4) != chunkX");
        int zGate = method.indexOf("(nearest.getZ() >> 4) != chunkZ");
        int duplicateCheck = method.indexOf("structureNode.containsKey");
        int duplicateMark = method.indexOf("structureNode.put");
        int heightLookup = method.indexOf("world.getHeight(nearest)");
        int nodePlacement = method.indexOf("createRandomNodeAt(world, nodePos");

        assertTrue(method.contains("new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8)"));
        assertTrue(xGate >= 0 && zGate >= 0);
        assertTrue(xGate < duplicateCheck && zGate < duplicateCheck);
        assertTrue(duplicateCheck < duplicateMark);
        assertTrue(duplicateMark < heightLookup);
        assertTrue(heightLookup < nodePlacement);
    }

    @Test
    public void nodeAspectScanShouldSkipUnloadedChunks() throws IOException {
        String source = readGeneratorSource();
        int start = source.indexOf("// Scan 11x11x11 surroundings");
        int end = source.indexOf("if (water > 100)", start);
        assertTrue(start >= 0 && end > start);

        String scan = source.substring(start, end);
        int loadedCheck = scan.indexOf("if (!world.isBlockLoaded(bp, false)) continue;");
        int stateRead = scan.indexOf("world.getBlockState(bp)");
        assertTrue(loadedCheck >= 0 && stateRead > loadedCheck);
    }

    @Test
    public void pointOresShouldUsePopulationSafeOffsets() throws IOException {
        String source = readGeneratorSource();
        int start = source.indexOf("private void generateOres");
        int end = source.indexOf("private boolean placeOreBlockIfStone", start);
        assertTrue(start >= 0 && end > start);

        String method = source.substring(start, end);
        assertTrue(method.contains("new BlockPos(x + 8 + rand.nextInt(16), rand.nextInt(Math.max(1, world.getActualHeight() / 5)), z + 8 + rand.nextInt(16))"));
        assertTrue(method.contains("int bx = x + 8 + rand.nextInt(16)"));
        assertTrue(method.contains("int bz = z + 8 + rand.nextInt(16)"));
    }
}
