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

public class ConfigRecipesArcaneRegistrationAuditStaticGuardTest {

    private static final Pattern ARCANE_PATTERN = Pattern.compile("registerArcaneRecipe\\(\"([^\"]+)\"");
    private static final Pattern SHAPELESS_ARCANE_PATTERN = Pattern.compile("registerShapelessArcaneRecipe\\(\"([^\"]+)\"");

    @Test
    public void configRecipesArcaneRegistrationCorpusShouldStayUniqueAndSized() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        List<String> keys = new ArrayList<>();
        keys.addAll(extract(source, ARCANE_PATTERN));
        keys.addAll(extract(source, SHAPELESS_ARCANE_PATTERN));

        Set<String> unique = new HashSet<>(keys);
        List<String> duplicates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String key : keys) {
            if (!seen.add(key)) {
                duplicates.add(key);
            }
        }

        assertTrue("Arcane registration keys should stay unique: " + duplicates, duplicates.isEmpty());
        assertTrue("Arcane registration corpus should stay at reference-sized baseline (89 keys), actual: " + unique.size(),
                unique.size() == 89);
    }

    @Test
    public void configRecipesArcaneRegistrationCorpusShouldKeepRepresentativeKeyFamilies() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("Arcane corpus should retain representative progression keys",
                source.contains("registerArcaneRecipe(\"PrimalCharm\"")
                        && source.contains("registerArcaneRecipe(\"ArcaneStone1\"")
                        && source.contains("registerArcaneRecipe(\"WandCapGold\"")
                        && source.contains("registerArcaneRecipe(\"WandRodGreatwood\"")
                        && source.contains("registerArcaneRecipe(\"FocusFire\"")
                        && source.contains("registerArcaneRecipe(\"Goggles\"")
                        && source.contains("registerArcaneRecipe(\"ArcaneSpa\"")
                        && source.contains("registerArcaneRecipe(\"MnemonicMatrix\""));
        assertTrue("Arcane corpus should retain shapeless arcane keys and family loops",
                source.contains("registerShapelessArcaneRecipe(\"MirrorGlass\"")
                        && source.contains("registerShapelessArcaneRecipe(\"TubeValve\"")
                        && source.contains("registerShapelessArcaneRecipe(\"TubeFilter\"")
                        && source.contains("registerShapelessArcaneRecipe(\"TubeRestrict\"")
                        && source.contains("registerShapelessArcaneRecipe(\"TubeOneway\"")
                        && source.contains("registerArcaneRecipe(\"Banner_\" + color")
                        && source.contains("registerArcaneRecipe(\"PrimalArrow_\" + i"));
    }

    private static List<String> extract(String source, Pattern pattern) {
        List<String> out = new ArrayList<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
