package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesInfusionResearchCoverageStaticGuardTest {

    private static final Pattern INFUSION_REGISTER_PATTERN = Pattern.compile("registerInfusionRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_ENCHANT_REGISTER_PATTERN = Pattern.compile("registerInfusionEnchantmentRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_PAGE_PATTERN = Pattern.compile("recipeInfusion\\(\"([^\"]+)\"\\)");
    private static final Pattern INFUSION_ENCHANT_PAGE_PATTERN = Pattern.compile("recipeInfusionEnchantment\\(\"([^\"]+)\"\\)");

    @Test
    public void infusionRegistrationCorpusShouldStayReferenceSizedAndUnique() throws IOException {
        String recipeSource = readMergedRecipeSource();
        Set<String> infusionRegistered = extractUnique(recipeSource, INFUSION_REGISTER_PATTERN);
        Set<String> infusionEnchantRegistered = extractUnique(recipeSource, INFUSION_ENCHANT_REGISTER_PATTERN);

        assertTrue("Infusion registration corpus should stay at reference baseline (63 keys), actual: " + infusionRegistered.size(),
                infusionRegistered.size() == 63);
        assertTrue("Infusion enchantment registration corpus should stay at reference baseline (24 keys), actual: " + infusionEnchantRegistered.size(),
                infusionEnchantRegistered.size() == 24);
    }

    @Test
    public void infusionResearchPagesShouldCoverRegisteredRecipesWithoutDanglingReferences() throws IOException {
        String recipeSource = readMergedRecipeSource();
        String researchSource = readResearchSource();

        Set<String> infusionRegistered = extractUnique(recipeSource, INFUSION_REGISTER_PATTERN);
        Set<String> infusionEnchantRegistered = extractUnique(recipeSource, INFUSION_ENCHANT_REGISTER_PATTERN);
        Set<String> infusionPages = extractUnique(researchSource, INFUSION_PAGE_PATTERN);
        Set<String> infusionEnchantPages = extractUnique(researchSource, INFUSION_ENCHANT_PAGE_PATTERN);

        Set<String> missingInfusionPages = new HashSet<>(infusionRegistered);
        missingInfusionPages.removeAll(infusionPages);
        Set<String> danglingInfusionPages = new HashSet<>(infusionPages);
        danglingInfusionPages.removeAll(infusionRegistered);

        Set<String> missingInfusionEnchantPages = new HashSet<>(infusionEnchantRegistered);
        missingInfusionEnchantPages.removeAll(infusionEnchantPages);

        Set<String> danglingInfusionEnchantPages = new HashSet<>(infusionEnchantPages);
        danglingInfusionEnchantPages.removeAll(infusionEnchantRegistered);

        assertTrue("Every registered infusion recipe key should be wired into research pages, missing: "
                        + sorted(missingInfusionPages),
                missingInfusionPages.isEmpty());
        assertTrue("Every infusion recipe page key should resolve to a registered infusion recipe, dangling: "
                        + sorted(danglingInfusionPages),
                danglingInfusionPages.isEmpty());
        assertTrue("Every registered infusion enchantment key should be wired into research pages, missing: "
                        + sorted(missingInfusionEnchantPages),
                missingInfusionEnchantPages.isEmpty());
        assertTrue("Every infusion enchantment page key should resolve to a registered infusion enchantment recipe, dangling: "
                        + sorted(danglingInfusionEnchantPages),
                danglingInfusionEnchantPages.isEmpty());
    }

    private static String readMergedRecipeSource() throws IOException {
        return ConfigRecipesSourceReader.readMergedSource();
    }

    private static String readResearchSource() throws IOException {
        StringBuilder out = new StringBuilder();
        for (String file : Arrays.asList(
                "src/main/java/thaumcraft/common/config/research/ConfigResearchThaumaturgy.java",
                "src/main/java/thaumcraft/common/config/research/ConfigResearchArtifice.java",
                "src/main/java/thaumcraft/common/config/research/ConfigResearchGolemancy.java",
                "src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java")) {
            out.append(readFile(file)).append('\n');
        }
        return out.toString();
    }

    private static Set<String> extractUnique(String source, Pattern pattern) {
        Set<String> out = new HashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static List<String> sorted(Set<String> input) {
        List<String> out = new ArrayList<>(input);
        java.util.Collections.sort(out);
        return out;
    }
}
