package thaumcraft.common.integration.waila;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WailaOptionalBoundaryGuardTest {
    @Test
    public void optionalReferencesStayInsideCommonSafeIntegrationPackage() throws IOException {
        Path sources = Paths.get("src/main/java");
        try (Stream<Path> paths = Files.walk(sources)) {
            paths.filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                try {
                    String source = read(path);
                    String normalized = path.toString().replace('\\', '/');
                    if (source.contains("mcp.mobius.waila")) {
                        assertTrue("Hwyla reference escaped optional boundary: " + normalized,
                                normalized.contains("/thaumcraft/common/integration/waila/"));
                    }
                    if (normalized.contains("/thaumcraft/common/integration/waila/")) {
                        assertFalse("Hwyla plugin must remain dedicated-server safe: " + normalized,
                                source.contains("net.minecraft.client."));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    public void pluginUsesOnlyAnnotatedBodyRegistration() throws IOException {
        String plugin = read(Paths.get(
                "src/main/java/thaumcraft/common/integration/waila/GolemWailaPlugin.java"));
        String build = read(Paths.get("build.gradle"));
        String metadata = read(Paths.get("src/main/resources/mcmod.info"));

        assertTrue(plugin.contains("@WailaPlugin"));
        assertTrue(plugin.contains("implements IWailaPlugin, IWailaEntityProvider"));
        assertTrue(plugin.contains("registerBodyProvider(this, IGolemInfo.class)"));
        assertFalse(plugin.contains("registerHeadProvider"));
        assertFalse(plugin.contains("registerTailProvider"));
        assertFalse(plugin.contains("registerNBTProvider"));
        assertFalse(plugin.contains("registerOverrideEntityProvider"));
        assertFalse(plugin.contains("FMLInterModComms"));
        assertTrue(build.contains("deobfProvided 'curse.maven:hwyla-253449:2568753'"));
        assertFalse(metadata.toLowerCase().contains("waila"));
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
