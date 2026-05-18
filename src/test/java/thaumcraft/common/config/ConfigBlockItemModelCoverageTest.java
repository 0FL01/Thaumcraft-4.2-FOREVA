package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ConfigBlockItemModelCoverageTest {

    private static final Pattern LEGACY_BLOCK_PATH = Pattern.compile("legacyPath\\(\"([^\"]+)\"\\)");

    @Test
    public void everyConfigBlockRegistryPathHasItemModelJson() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        Set<String> blockPaths = extractBlockPaths(source);

        Path modelDir = Paths.get("src/main/resources/assets/thaumcraft/models/item");
        List<String> missing = new ArrayList<>();
        for (String path : blockPaths) {
            Path model = modelDir.resolve(path + ".json");
            if (!Files.exists(model)) {
                missing.add(path);
            }
        }

        assertTrue("Missing thaumcraft block item models: " + missing, missing.isEmpty());
    }

    private static Set<String> extractBlockPaths(String source) {
        Set<String> out = new HashSet<>();
        Matcher matcher = LEGACY_BLOCK_PATH.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1).toLowerCase());
        }
        return out;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
