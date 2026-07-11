package thaumcraft.common.lib.world.dim;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class OuterLandsArrivalAndSpawnStaticGuardTest {

    @Test
    public void teleporterPreparesChunksAndUsesAnInteriorPortalApproach() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java");

        assertTrue(source.contains("this.prepareDestination(entity, 1);")
                && source.contains("this.prepareDestination(entity, 2);")
                && source.contains("int[][] offsets = {{-5, 0}, {5, 0}, {0, -5}, {0, 5}};")
                && source.contains("this.isSafeArrival(candidate)")
                && source.contains("hasCeiling(this.world, candidate, 16)"));
        assertTrue("Outer Lands fallback must try an enclosed interior before the surface fallback",
                source.contains("this.world.provider.getDimension() == Config.dimensionOuterId")
                        && source.indexOf("this.findSafeInterior(entity)")
                        < source.indexOf("this.world.getTopSolidOrLiquidBlock"));
    }

    @Test
    public void naturalOuterLandsSpawnsRequireAnEnclosedMazeCell() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event)")
                && source.contains("event.isSpawner()")
                && source.contains("MazeHandler.getFromHashMap")
                && source.contains("!TeleporterThaumcraft.hasCeiling(world, pos, 16)")
                && source.contains("event.setResult(Event.Result.DENY)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
