package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileMirrorEssentiaStaticGuardTest {

    @Test
    public void mirrorEssentiaShouldKeepReferenceLinkInvalidationAndDrainContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileMirrorEssentia.java");

        assertTrue(source.contains("target.linked = false;"));
        assertTrue(source.contains("target.linkedFacing = null;"));
        assertTrue(source.contains("this.markDirtyAndSync();"));
        assertTrue(source.contains("target.markDirtyAndSync();"));
        assertTrue(source.contains("public int addToContainer(Aspect tag, int amount) { return 0; }"));
        assertTrue(source.contains("if (!this.isLinkValid() || amount > 1) return false;"));
        assertTrue(source.contains("EssentiaHandler.drainEssentia(target, tag, this.linkedFacing, 8, true)"));
    }

    @Test
    public void mirrorEssentiaShouldKeepServerRelinkCadenceContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileMirrorEssentia.java");

        assertTrue(source.contains("this.count++ % this.inc == 0"));
        assertTrue(source.contains("if (this.inc < 600) this.inc += 20;"));
        assertTrue(source.contains("this.restoreLink();"));
        assertTrue(source.contains("this.inc = 40;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
