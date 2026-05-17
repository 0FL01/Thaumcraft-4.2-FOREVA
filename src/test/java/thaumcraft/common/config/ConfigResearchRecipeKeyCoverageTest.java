package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class ConfigResearchRecipeKeyCoverageTest {

    private static final Pattern RECIPE_GET_PATTERN = Pattern.compile(
            "(?:recipes\\.get|recipeI|recipeArcane|recipeCrucible|recipeInfusion|recipeInfusionEnchantment|recipeList)\\(\"([^\"]+)\"\\)");
    private static final Pattern RECIPE_PUT_PATTERN = Pattern.compile("recipes\\.put\\(\"([^\"]+)\"");
    private static final Pattern SPECIAL_RECIPE_HANDLE_PATTERN = Pattern.compile("specialResearchRecipeHandles\\.put\\(\"([^\"]+)\"");
    private static final Pattern SPECIAL_RECIPE_HANDLE_BRIDGE_PATTERN = Pattern.compile("addSpecialResearchRecipeHandle\\(\"([^\"]+)\"");
    private static final Pattern ARCANE_PATTERN = Pattern.compile("registerArcaneRecipe\\(\"([^\"]+)\"");
    private static final Pattern SHAPELESS_ARCANE_PATTERN = Pattern.compile("registerShapelessArcaneRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_PATTERN = Pattern.compile("registerInfusionRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_ENCHANT_PATTERN = Pattern.compile("registerInfusionEnchantmentRecipe\\(\"([^\"]+)\"");

    @Test
    public void everyDirectRecipeLookupInConfigResearchHasRegistrationKeyInConfigRecipes() throws IOException {
        String researchSource = readConfigResearchFamily();
        String recipesSource = ConfigRecipesSourceReader.readMergedSource();

        Set<String> lookedUpKeys = extract(researchSource, RECIPE_GET_PATTERN);
        Set<String> availableKeys = new TreeSet<>();
        availableKeys.addAll(extract(recipesSource, RECIPE_PUT_PATTERN));
        availableKeys.addAll(extract(recipesSource, SPECIAL_RECIPE_HANDLE_PATTERN));
        availableKeys.addAll(extract(recipesSource, SPECIAL_RECIPE_HANDLE_BRIDGE_PATTERN));
        availableKeys.addAll(extract(recipesSource, ARCANE_PATTERN));
        availableKeys.addAll(extract(recipesSource, SHAPELESS_ARCANE_PATTERN));
        availableKeys.addAll(extract(recipesSource, INFUSION_PATTERN));
        availableKeys.addAll(extract(recipesSource, INFUSION_ENCHANT_PATTERN));

        List<String> missing = new ArrayList<>();
        for (String key : lookedUpKeys) {
            if (!availableKeys.contains(key)) {
                missing.add(key);
            }
        }

        assertTrue("ConfigResearch should keep reference-sized recipe lookup coverage (>=272 unique keys), actual: " + lookedUpKeys.size(),
                lookedUpKeys.size() >= 272);
        assertTrue("Missing ConfigRecipes registrations for ConfigResearch recipes.get keys: " + missing, missing.isEmpty());
    }

    private static Set<String> extract(String source, Pattern pattern) {
        Set<String> out = new TreeSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static String readConfigResearchFamily() throws IOException {
        Path configDir = Paths.get("src/main/java/thaumcraft/common/config/research");
        try (Stream<Path> stream = Files.list(configDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.startsWith("ConfigResearch") && name.endsWith(".java");
                    })
                    .sorted()
                    .map(path -> {
                        try {
                            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.joining("\n"));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }
}
