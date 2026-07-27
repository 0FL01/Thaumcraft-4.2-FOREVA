package thaumcraft.common.lib.world;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class MagicalForestTreeCascadingGuardTest {

    @Test
    public void bigMagicTreeShouldPreflightItsWorldgenFootprint() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/WorldGenBigMagicTree.java");
        int height = source.indexOf("this.heightLimit = 11 + this.rand.nextInt(this.heightLimitLimit)");
        int areaCheck = source.indexOf("this.worldgen && !world.isAreaLoaded(");
        int locationCheck = source.indexOf("this.validTreeLocation()", areaCheck);

        assertTrue(source.contains("this.worldgen = !notify"));
        assertTrue(source.contains("private static final int WORLDGEN_RADIUS = 10"));
        assertTrue(source.contains("pos.add(-WORLDGEN_RADIUS, -1, -WORLDGEN_RADIUS)"));
        assertTrue(source.contains("pos.add(WORLDGEN_RADIUS, this.heightLimit, WORLDGEN_RADIUS)"));
        assertTrue(height >= 0 && height < areaCheck);
        assertTrue(areaCheck < locationCheck);
    }

    @Test
    public void greatwoodShouldPreflightItsWorldgenFootprint() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/WorldGenGreatwoodTrees.java");
        int height = source.indexOf("this.heightLimit = this.heightLimitLimit + this.rand.nextInt(this.heightLimitLimit)");
        int areaCheck = source.indexOf("this.worldgen && !worldIn.isAreaLoaded(");
        int locationCheck = source.indexOf("this.validTreeLocation(tx + a, tz + b)", areaCheck);

        assertTrue(source.contains("this.worldgen = !notify"));
        assertTrue(source.contains("private static final int WORLDGEN_RADIUS = 16"));
        assertTrue(source.contains("new BlockPos(x - WORLDGEN_RADIUS, y - 2, z - WORLDGEN_RADIUS)"));
        assertTrue(source.contains("new BlockPos(x + WORLDGEN_RADIUS, y + this.heightLimit * 2, z + WORLDGEN_RADIUS)"));
        assertTrue(height >= 0 && height < areaCheck);
        assertTrue(areaCheck < locationCheck);
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
