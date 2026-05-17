package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigResearchStrictRecipeLookupStaticGuardTest {

    @Test
    public void configResearchInitShouldEnableStrictRecipeHandleLookups() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigResearch.java");

        assertTrue("ConfigResearch should keep strict recipe lookup gate state",
                source.contains("private static boolean strictRecipeLookups = false;"));
        assertTrue("ConfigResearch should use strict lookup map for recipe handles",
                source.contains("public static final Map<String, Object> recipes = new ResearchRecipeHandleMap();"));
        assertTrue("ConfigResearch.init should bracket registration with strict recipe lookup mode",
                source.contains("strictRecipeLookups = true;")
                        && source.contains("} finally {")
                        && source.contains("strictRecipeLookups = false;"));
        assertTrue("Strict recipe lookup map should fail fast when a handle key is missing during init",
                source.contains("if (strictRecipeLookups && key instanceof String)")
                        && source.contains("if (!containsKey(key))")
                        && source.contains("throw new IllegalStateException(\"Missing ConfigResearch recipe handle: \" + key);"));
        assertTrue("Strict recipe lookup map should fail fast when a handle key resolves to null during init",
                source.contains("if (value == null)")
                        && source.contains("throw new IllegalStateException(\"Null ConfigResearch recipe handle: \" + key);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
