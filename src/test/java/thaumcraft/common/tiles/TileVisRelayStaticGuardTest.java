package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileVisRelayStaticGuardTest {

    @Test
    public void visRelayShouldKeepReferenceWandCycleContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileVisRelay.java");

        assertTrue(source.contains("public byte orientation = 1;"));
        assertTrue(source.contains("this.color++;"));
        assertTrue(source.contains("if (this.color > 5) this.color = -1;"));
        assertTrue(source.contains("this.removeThisNode();"));
        assertTrue(source.contains("this.world.playSound(null, this.pos, TCSounds.CRYSTAL, SoundCategory.BLOCKS, 0.2F, 1.0F);"));
        assertTrue(source.contains("public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player)"));
        assertTrue(source.contains("return null;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
