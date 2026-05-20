package thaumcraft.common;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class PortableHoleWardingRuntimeContractTest {

    @Test
    public void portableHoleAndWardingFamilyShouldKeepRuntimeContracts() throws IOException {
        String tileHole = read("src/main/java/thaumcraft/common/tiles/TileHole.java");
        String tileWardingStone = read("src/main/java/thaumcraft/common/tiles/TileWardingStone.java");
        String tileWardingStoneFence = read("src/main/java/thaumcraft/common/tiles/TileWardingStoneFence.java");
        String focusPortableHole = read("src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java");
        String focusWarding = read("src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java");

        assertTrue("TileHole should keep client sparkle loop, chained opening growth, and restore sparkle cue",
                tileHole.contains("this.surroundwithsparkles();")
                        && tileHole.contains("Thaumcraft.proxy.blockSparkle(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 0x400040, 1);")
                        && tileHole.contains("private void surroundwithsparkles()")
                        && tileHole.contains("ConfigBlocks.blockHole")
                        && tileHole.contains("Thaumcraft.proxy.sparkle(x, y, z, 2);")
                        && tileHole.contains("FocusPortableHole.createHole("));

        assertTrue("TileWardingStone should keep power-gated entity repulsion and airy-fence upkeep",
                tileWardingStone.contains("implements ITickable")
                        && tileWardingStone.contains("this.world.isBlockPowered(this.pos)")
                        && tileWardingStone.contains("getEntitiesWithinAABB(")
                        && tileWardingStone.contains("entity.addVelocity(")
                        && tileWardingStone.contains("ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 4)"));

        assertTrue("TileWardingStoneFence should keep support validation against warding-stone bases",
                tileWardingStoneFence.contains("implements ITickable")
                        && tileWardingStoneFence.contains("ConfigBlocks.blockCosmeticSolid")
                        && tileWardingStoneFence.contains("BlockCosmeticSolid.TYPE")
                        && tileWardingStoneFence.contains("this.world.setBlockToAir(this.pos);"));

        assertTrue("Portable Hole and Warding foci should keep their world-side sparkle feedback send-sites",
                focusPortableHole.contains("new PacketFXBlockSparkle(x, y, z, 0x400040)")
                        && focusWarding.contains("new PacketFXBlockSparkle(c.x, c.y, c.z, 0xFC9A00)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
