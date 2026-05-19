package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class ConfigResearchRecipeLookupTypeAuditTest {

    private static final Pattern RECIPE_I_LOOKUP_PATTERN = Pattern.compile("recipeI\\(\"([^\"]+)\"\\)");
    private static final Pattern ARCANE_LOOKUP_PATTERN = Pattern.compile("recipeArcane\\(\"([^\"]+)\"\\)");
    private static final Pattern CRUCIBLE_LOOKUP_PATTERN = Pattern.compile("recipeCrucible\\(\"([^\"]+)\"\\)");
    private static final Pattern INFUSION_LOOKUP_PATTERN = Pattern.compile("recipeInfusion\\(\"([^\"]+)\"\\)");
    private static final Pattern INFUSION_ENCHANT_LOOKUP_PATTERN = Pattern.compile("recipeInfusionEnchantment\\(\"([^\"]+)\"\\)");
    private static final Pattern LIST_LOOKUP_PATTERN = Pattern.compile("recipeList\\(\"([^\"]+)\"\\)");

    private static final Pattern DIRECT_PUT_PATTERN = Pattern.compile("recipes\\.put\\(\"([^\"]+)\"");
    private static final Pattern SPECIAL_HANDLE_PATTERN = Pattern.compile("specialResearchRecipeHandles\\.put\\(\"([^\"]+)\"");
    private static final Pattern SPECIAL_BRIDGE_PATTERN = Pattern.compile("addSpecialResearchRecipeHandle\\(\"([^\"]+)\"");
    private static final Pattern ARCANE_REGISTER_PATTERN = Pattern.compile("register(?:Shapeless)?ArcaneRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_REGISTER_PATTERN = Pattern.compile("registerInfusionRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_ENCHANT_REGISTER_PATTERN = Pattern.compile("registerInfusionEnchantmentRecipe\\(\"([^\"]+)\"");
    private static final Pattern CRUCIBLE_REGISTER_PATTERN =
            Pattern.compile("recipes\\.put\\(\"([^\"]+)\"\\s*,\\s*ThaumcraftApi\\.addCrucibleRecipe\\(");
    private static final Pattern CRUCIBLE_FIELD_DECL_PATTERN = Pattern.compile("private\\s+static\\s+CrucibleRecipe\\s+(\\w+)\\s*;");
    private static final Pattern DIRECT_VAR_PUT_PATTERN = Pattern.compile("recipes\\.put\\(\"([^\"]+)\"\\s*,\\s*(\\w+)\\s*\\);");
    private static final Pattern LIST_REGISTER_PATTERN =
            Pattern.compile("recipes\\.put\\(\"([^\"]+)\"\\s*,\\s*Arrays\\.asList\\(");

    @Test
    public void typedConfigResearchLookupsShouldResolveAgainstMatchingRecipeHandleFamilies() throws IOException {
        String researchSource = readConfigResearchFamily();
        String recipesSource = ConfigRecipesSourceReader.readMergedSource();

        Map<String, Set<String>> lookupFamilies = new HashMap<>();
        lookupFamilies.put("IRecipe", extract(researchSource, RECIPE_I_LOOKUP_PATTERN));
        lookupFamilies.put("Arcane", extract(researchSource, ARCANE_LOOKUP_PATTERN));
        lookupFamilies.put("Crucible", extract(researchSource, CRUCIBLE_LOOKUP_PATTERN));
        lookupFamilies.put("Infusion", extract(researchSource, INFUSION_LOOKUP_PATTERN));
        lookupFamilies.put("InfusionEnchantment", extract(researchSource, INFUSION_ENCHANT_LOOKUP_PATTERN));
        lookupFamilies.put("List", extract(researchSource, LIST_LOOKUP_PATTERN));

        Set<String> allRegistered = new TreeSet<>();
        allRegistered.addAll(extract(recipesSource, DIRECT_PUT_PATTERN));
        allRegistered.addAll(extract(recipesSource, SPECIAL_HANDLE_PATTERN));
        allRegistered.addAll(extract(recipesSource, SPECIAL_BRIDGE_PATTERN));
        allRegistered.addAll(extract(recipesSource, ARCANE_REGISTER_PATTERN));
        allRegistered.addAll(extract(recipesSource, INFUSION_REGISTER_PATTERN));
        allRegistered.addAll(extract(recipesSource, INFUSION_ENCHANT_REGISTER_PATTERN));

        Set<String> arcaneRegistered = extract(recipesSource, ARCANE_REGISTER_PATTERN);
        Set<String> crucibleRegistered = extract(recipesSource, CRUCIBLE_REGISTER_PATTERN);
        crucibleRegistered.addAll(extractKeysBackedByDeclaredType(
                recipesSource,
                CRUCIBLE_FIELD_DECL_PATTERN,
                DIRECT_VAR_PUT_PATTERN));
        Set<String> infusionRegistered = extract(recipesSource, INFUSION_REGISTER_PATTERN);
        Set<String> infusionEnchantRegistered = extract(recipesSource, INFUSION_ENCHANT_REGISTER_PATTERN);
        Set<String> listRegistered = extract(recipesSource, LIST_REGISTER_PATTERN);

        Set<String> iRecipeRegistered = new TreeSet<>(allRegistered);
        iRecipeRegistered.removeAll(arcaneRegistered);
        iRecipeRegistered.removeAll(crucibleRegistered);
        iRecipeRegistered.removeAll(infusionRegistered);
        iRecipeRegistered.removeAll(infusionEnchantRegistered);
        iRecipeRegistered.removeAll(listRegistered);

        List<String> conflicts = new ArrayList<>();
        Set<String> allLookupKeys = new TreeSet<>();
        for (Map.Entry<String, Set<String>> entry : lookupFamilies.entrySet()) {
            for (String key : entry.getValue()) {
                if (!allLookupKeys.add(key)) {
                    conflicts.add(key);
                }
            }
        }

        assertTrue("ConfigResearch recipe helper families should not overlap on the same key: " + conflicts,
                conflicts.isEmpty());

        assertCoverage("IRecipe", lookupFamilies.get("IRecipe"), iRecipeRegistered);
        assertCoverage("Arcane", lookupFamilies.get("Arcane"), arcaneRegistered);
        assertCoverage("Crucible", lookupFamilies.get("Crucible"), crucibleRegistered);
        assertCoverage("Infusion", lookupFamilies.get("Infusion"), infusionRegistered);
        assertCoverage("InfusionEnchantment", lookupFamilies.get("InfusionEnchantment"), infusionEnchantRegistered);
        assertCoverage("List", lookupFamilies.get("List"), listRegistered);
    }

    private static void assertCoverage(String family, Set<String> lookedUp, Set<String> registered) {
        List<String> missing = new ArrayList<>();
        for (String key : lookedUp) {
            if (!registered.contains(key)) {
                missing.add(key);
            }
        }
        assertTrue("ConfigResearch " + family + " lookups missing matching registration family keys: " + missing,
                missing.isEmpty());
    }

    private static Set<String> extract(String source, Pattern pattern) {
        Set<String> out = new TreeSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static Set<String> extractKeysBackedByDeclaredType(
            String source,
            Pattern declarationPattern,
            Pattern putPattern) {
        Set<String> typedVariables = new TreeSet<>();
        Matcher declarationMatcher = declarationPattern.matcher(source);
        while (declarationMatcher.find()) {
            typedVariables.add(declarationMatcher.group(1));
        }

        Set<String> out = new TreeSet<>();
        Matcher putMatcher = putPattern.matcher(source);
        while (putMatcher.find()) {
            if (typedVariables.contains(putMatcher.group(2))) {
                out.add(putMatcher.group(1));
            }
        }
        return out;
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
