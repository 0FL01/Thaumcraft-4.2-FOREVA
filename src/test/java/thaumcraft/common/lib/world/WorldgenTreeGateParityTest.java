package thaumcraft.common.lib.world;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorldgenTreeGateParityTest {

    @Test
    public void silverwoodEligibilityMatchesTc4TruthTable() {
        assertFalse(ThaumcraftWorldGenerator.isSilverwoodBiomeEligible(true, false, true, false, false));
        assertFalse(ThaumcraftWorldGenerator.isSilverwoodBiomeEligible(false, true, true, false, false));
        assertFalse(ThaumcraftWorldGenerator.isSilverwoodBiomeEligible(false, false, false, false, false));
        assertTrue(ThaumcraftWorldGenerator.isSilverwoodBiomeEligible(false, false, true, false, false));
        assertTrue(ThaumcraftWorldGenerator.isSilverwoodBiomeEligible(false, false, false, true, false));
        assertTrue(ThaumcraftWorldGenerator.isSilverwoodBiomeEligible(false, false, false, false, true));
    }

    @Test
    public void vegetationRatesAndMushroomDrawOrderMatchTc4() throws IOException {
        String generator = read("src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java");
        assertTrue(generator.contains("if (rand.nextInt(60) == 3)"));
        assertTrue(generator.contains("if (rand.nextInt(25) == 7)"));
        assertTrue(generator.indexOf("rand.nextInt(60) == 3") < generator.indexOf("rand.nextInt(25) == 7"));

        String biome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java");
        int loop = biome.indexOf("for (int mx = 0; mx < 4; ++mx)");
        int xDraw = biome.indexOf("rand.nextInt(3)", loop);
        int zDraw = biome.indexOf("rand.nextInt(3)", xDraw + 1);
        int height = biome.indexOf("world.getHeight", zDraw);
        int gate = biome.indexOf("rand.nextInt(40)", height);
        assertTrue(loop >= 0 && loop < xDraw && xDraw < zDraw && zDraw < height && height < gate);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
