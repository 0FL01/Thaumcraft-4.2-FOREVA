package thaumcraft.common.lib.world;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThaumcraftWorldGeneratorGreatwoodStaticGuardTest {

    @Test
    public void generateGreatwoodShouldKeepSpiderVariantChanceContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java");

        assertTrue(source.contains("public static void generateGreatwood(World world, Random rand, int chunkX, int chunkZ)"));
        assertTrue(source.contains("new WorldGenGreatwoodTrees(false).generate(world, rand, pos.getX(), pos.getY(), pos.getZ(), rand.nextInt(16) == 0);"));
        assertTrue(source.contains("BiomeHandler.getBiomeSupportsGreatwood(biome)"));
    }

    @Test
    public void worldgenHeightCapsShouldUseActualWorldHeightNotSeaLevel() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java");

        assertFalse(source.contains("getSeaLevel()"));
        assertTrue(source.contains("if (pos.getY() > world.getActualHeight())"));
        assertTrue(source.contains("if (by > world.getActualHeight())"));
        assertTrue(source.contains("if (ringY < world.getActualHeight())"));
        assertTrue(source.contains("if (topY > world.getActualHeight())"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
