package thaumcraft.api;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumcraftSixCompatibilityLayerStaticGuardTest {

    @Test
    public void centralCompatibilityLayerMustOwnShimLifecycleHooks() throws IOException {
        String compat = readFile("src/main/java/thaumcraft/common/compat/ThaumcraftSixCompatibility.java");
        String configBlocks = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String configItems = readFile("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String thaumcraft = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");

        assertTrue("TC6 shim lifecycle should be routed through the central compatibility layer",
                compat.contains("public static void initBlockAliases()")
                        && compat.contains("BlocksTC.init();")
                        && configBlocks.contains("ThaumcraftSixCompatibility.initBlockAliases();")
                        && compat.contains("public static void initItemAliases()")
                        && compat.contains("ItemsTC.init();")
                        && configItems.contains("ThaumcraftSixCompatibility.initItemAliases();")
                        && compat.contains("public static void postAspectRegistryEvent()")
                        && thaumcraft.contains("ThaumcraftSixCompatibility.postAspectRegistryEvent();"));
    }

    @Test
    public void tc6ShimCoverageDocumentMustTrackKnownAddonRegressionMatrix() throws IOException {
        String doc = readFile("docs/compatibility/tc6-shim-coverage.md");

        assertTrue("TC6 shim coverage docs should track current addon smoke matrix and Astral recon state",
                doc.contains("EnderIO")
                        && doc.contains("Fossils and Archeology")
                        && doc.contains("Magic Bees")
                        && doc.contains("scripts/smoke-modsets/enderio.txt")
                        && doc.contains("scripts/smoke-modsets/fossils.txt")
                        && doc.contains("scripts/smoke-modsets/magicbees.txt")
                        && doc.contains("Astral Sorcery")
                        && doc.contains("pending RECON"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
