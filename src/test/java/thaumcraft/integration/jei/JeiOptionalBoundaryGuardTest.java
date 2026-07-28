package thaumcraft.integration.jei;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JeiOptionalBoundaryGuardTest {
    @Test
    public void jeiReferencesStayInsideDormantClientIntegrationPackage() throws IOException {
        Path sources = Paths.get("src/main/java");
        try (Stream<Path> paths = Files.walk(sources)) {
            paths.filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                try {
                    String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                    if (source.contains("mezz.jei")) {
                        String normalized = path.toString().replace('\\', '/');
                        assertTrue("JEI reference escaped optional client boundary: " + normalized,
                                normalized.contains("/thaumcraft/client/integration/jei/"));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        String plugin = read("src/main/java/thaumcraft/client/integration/jei/ThaumcraftJeiPlugin.java");
        assertFalse(plugin.contains("EventBusSubscriber"));
    }

    @Test
    public void apiJarIncludesOnlyPublicApiPackage() throws IOException {
        String build = read("build.gradle");
        assertTrue(build.contains("include 'thaumcraft/api/**'"));
        assertFalse(build.contains("include 'thaumcraft/client/integration/jei/**'"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
