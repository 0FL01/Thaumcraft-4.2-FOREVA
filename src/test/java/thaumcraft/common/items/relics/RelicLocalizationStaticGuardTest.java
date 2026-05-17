package thaumcraft.common.items.relics;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class RelicLocalizationStaticGuardTest {

    @Test
    public void handMirrorAndSanityCheckerLocalizationKeysExist() throws IOException {
        String handMirrorSource = readFile("src/main/java/thaumcraft/common/items/relics/ItemHandMirror.java");
        String sanityCheckerSource = readFile("src/main/java/thaumcraft/common/items/relics/ItemSanityChecker.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("Hand mirror must keep linked-destination localization key usage and key presence",
                handMirrorSource.contains("tc.handmirrorlinkedto")
                        && lang.contains("tc.handmirrorlinkedto=Linked to"));
        assertTrue("Sanity checker must keep total-warp localization key usage and key presence",
                sanityCheckerSource.contains("new TextComponentTranslation(\"tc.sanity\", total)")
                        && lang.contains("tc.sanity=Current Warp: %s"));
        assertTrue("Sanity checker must keep detailed-warp localization key usage and key presence",
                sanityCheckerSource.contains("new TextComponentTranslation(\"tc.sanity.detail\", perm, sticky, temp)")
                        && lang.contains("tc.sanity.detail=Permanent: %1$s  Sticky: %2$s  Temporary: %3$s"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
