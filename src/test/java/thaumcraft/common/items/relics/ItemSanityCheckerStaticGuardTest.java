package thaumcraft.common.items.relics;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemSanityCheckerStaticGuardTest {

    @Test
    public void sanityCheckerKeepsWarpBreakdownCapabilityContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/relics/ItemSanityChecker.java");

        assertTrue("Sanity checker must read full warp breakdown from PLAYER_KNOWLEDGE capability",
                source.contains("knowledge.getWarpPerm()")
                        && source.contains("knowledge.getWarpSticky()")
                        && source.contains("knowledge.getWarpTemp()")
                        && source.contains("int total = perm + sticky + temp;"));
        assertTrue("Sanity checker must keep both total and detailed warp status messages",
                source.contains("new TextComponentTranslation(\"tc.sanity\", total)")
                        && source.contains("new TextComponentTranslation(\"tc.sanity.detail\", perm, sticky, temp)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
