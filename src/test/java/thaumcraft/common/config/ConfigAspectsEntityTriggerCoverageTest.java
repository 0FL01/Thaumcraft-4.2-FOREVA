package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigAspectsEntityTriggerCoverageTest {

    private static final Pattern ENTITY_TRIGGER_PATTERN = Pattern.compile("setEntityTriggers\\(([^)]*)\\)");
    private static final Pattern REGISTER_ENTITY_TAG_PATTERN = Pattern.compile("registerEntityTag\\(\\s*\"([^\"]+)\"");
    private static final Pattern REGISTER_TC_ENTITY_TAG_PATTERN = Pattern.compile("registerEntityTag\\(\\s*tcEntity\\(\"([^\"]+)\"\\)");
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile("\"([^\"]+)\"");

    @Test
    public void configResearchEntityTriggersHaveAspectsRegistration() throws IOException {
        String configResearch = readConfigResearchFamily();
        String configAspects = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");

        Set<String> triggerEntityKeys = extractTriggerEntityKeys(configResearch);
        Set<String> registeredEntityKeys = extractRegisteredEntityKeys(configAspects);

        Set<String> missing = new HashSet<>();
        for (String entityKey : triggerEntityKeys) {
            if (!registeredEntityKeys.contains(entityKey)) {
                missing.add(entityKey);
            }
        }

        assertFalse("ConfigResearch setEntityTriggers(...) has no entries", triggerEntityKeys.isEmpty());
        assertTrue("Missing entity tag registrations in ConfigAspects: " + missing, missing.isEmpty());
    }

    @Test
    public void configAspectsKeepsReferenceShapedEntityScanCorpus() throws IOException {
        String configAspects = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");
        Set<String> registeredEntityKeys = extractRegisteredEntityKeys(configAspects);

        assertTrue("ConfigAspects should keep broad entity scan coverage close to reference, actual: "
                        + registeredEntityKeys.size(),
                registeredEntityKeys.size() >= 45);

        for (String key : Arrays.asList(
                "minecraft:zombie",
                "minecraft:wither_skeleton",
                "minecraft:creeper",
                "minecraft:horse",
                "minecraft:xp_orb",
                "minecraft:ender_dragon",
                "minecraft:spawner_minecart",
                "thaumcraft:pech",
                "thaumcraft:thaumslime",
                "thaumcraft:taintswarm",
                "thaumcraft:cultistknight",
                "thaumcraft:wisp",
                "thaumcraft:golem")) {
            assertTrue("Missing reference-shaped entity scan tag: " + key, registeredEntityKeys.contains(key));
        }

        assertTrue("ConfigAspects should keep pech, powered creeper, and wisp type discriminators",
                configAspects.contains("new ThaumcraftApi.EntityTagsNBT(\"PechType\", (byte)0)")
                        && configAspects.contains("new ThaumcraftApi.EntityTagsNBT(\"PechType\", (byte)1)")
                        && configAspects.contains("new ThaumcraftApi.EntityTagsNBT(\"PechType\", (byte)2)")
                        && configAspects.contains("new ThaumcraftApi.EntityTagsNBT(\"powered\", (byte)1)")
                        && configAspects.contains("new ThaumcraftApi.EntityTagsNBT(\"Type\", tag.getTag())"));
    }

    private static Set<String> extractTriggerEntityKeys(String source) {
        Set<String> out = new HashSet<>();
        Matcher triggerCalls = ENTITY_TRIGGER_PATTERN.matcher(source);
        while (triggerCalls.find()) {
            Matcher quoted = QUOTED_STRING_PATTERN.matcher(triggerCalls.group(1));
            while (quoted.find()) {
                String trigger = quoted.group(1);
                String normalized = normalizeTriggerToEntityKey(trigger);
                if (normalized != null) {
                    out.add(normalized);
                }
            }
        }
        return out;
    }

    private static Set<String> extractRegisteredEntityKeys(String source) {
        Set<String> out = new HashSet<>();
        Matcher matcher = REGISTER_ENTITY_TAG_PATTERN.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1).toLowerCase(Locale.ROOT));
        }
        Matcher tcMatcher = REGISTER_TC_ENTITY_TAG_PATTERN.matcher(source);
        while (tcMatcher.find()) {
            out.add(("thaumcraft:" + tcMatcher.group(1)).toLowerCase(Locale.ROOT));
        }
        return out;
    }

    private static String normalizeTriggerToEntityKey(String trigger) {
        if (trigger == null) {
            return null;
        }
        String cleaned = trigger.trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        if (cleaned.indexOf(':') >= 0) {
            return cleaned.toLowerCase(Locale.ROOT);
        }
        if (cleaned.startsWith("Thaumcraft.") && cleaned.length() > "Thaumcraft.".length()) {
            return "thaumcraft:" + cleaned.substring("Thaumcraft.".length()).toLowerCase(Locale.ROOT);
        }
        return "minecraft:" + cleaned.toLowerCase(Locale.ROOT);
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
