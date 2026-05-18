package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EtherealBloomParityStaticGuardTest {

    @Test
    public void tileAndRendererKeepEtherealBloomGrowthAndCleanseContracts() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileEtherealBloom.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEtherealBloomRenderer.java");

        assertTrue("TileEtherealBloom should keep growth counters and tick update surface",
                tile.contains("public int counter = 0;")
                        && tile.contains("public int growthCounter = 0;")
                        && tile.contains("public void update()"));
        assertTrue("TileEtherealBloom should keep periodic biome cleanse behavior",
                tile.contains("this.counter % 20 == 0")
                        && tile.contains("Utils.setBiomeAt(this.world, tx, tz, biome);"));
        assertTrue("TileEtherealBloom should keep roots sound bootstrap on client",
                tile.contains("this.world.isRemote && this.growthCounter == 0")
                        && tile.contains("TCSounds.ROOTS"));

        assertTrue("TileEtherealBloomRenderer should keep layered bloom rendering assets",
                renderer.contains("textures/misc/nodes.png")
                        && renderer.contains("textures/models/crystalcapacitor.png")
                        && renderer.contains("blocks/shimmerleaf")
                        && renderer.contains("blocks/purifier_stalk"));
        assertTrue("TileEtherealBloomRenderer should keep node pulse + core + leaf/stalk layer flow",
                renderer.contains("renderNodePulse(")
                        && renderer.contains("renderCrystalCore(")
                        && renderer.contains("renderLeafLayers(")
                        && renderer.contains("renderStalkLayers("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
