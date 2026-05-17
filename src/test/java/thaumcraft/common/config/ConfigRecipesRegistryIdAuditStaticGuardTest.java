package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesRegistryIdAuditStaticGuardTest {

    private static final Pattern REGISTRY_NAME_PATTERN =
            Pattern.compile("setRegistryName\\(\"([a-z0-9_]+)\",\\s*\"([a-z0-9_]+)\"\\)");

    @Test
    public void configRecipesRegistryNamesStayUniqueAndThaumcraftScoped() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        Matcher matcher = REGISTRY_NAME_PATTERN.matcher(source);
        Set<String> unique = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        List<String> wrongNamespace = new ArrayList<>();

        while (matcher.find()) {
            String namespace = matcher.group(1);
            String path = matcher.group(2);
            String id = namespace + ":" + path;
            if (!unique.add(id)) {
                duplicates.add(id);
            }
            if (!"thaumcraft".equals(namespace)) {
                wrongNamespace.add(id);
            }
        }

        assertTrue("ConfigRecipes must keep all setRegistryName ids in thaumcraft namespace: " + wrongNamespace,
                wrongNamespace.isEmpty());
        assertTrue("ConfigRecipes must not contain duplicate recipe registry ids: " + duplicates,
                duplicates.isEmpty());
        assertTrue("ConfigRecipes should keep expected special recipe ids for robe/void-robe dyes",
                source.contains("setRegistryName(\"thaumcraft\", \"robearmordye\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidrobearmordye\")"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
