package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigResearchAspectTriggerCoverageTest {

    private static final Pattern ASPECT_TRIGGER_CALL_PATTERN = Pattern.compile("setAspectTriggers\\(([^)]*)\\)");
    private static final Pattern ASPECT_REF_PATTERN = Pattern.compile("Aspect\\.([A-Z_]+)");
    private static final Pattern ASPECT_DECL_PATTERN = Pattern.compile("public\\s+static\\s+final\\s+Aspect\\s+([A-Z_]+)\\s*=");

    @Test
    public void configResearchAspectTriggersReferenceDeclaredAspects() throws IOException {
        String configResearch = readConfigResearchFamily();
        String aspectApi = readFile("src/main/java/thaumcraft/api/aspects/Aspect.java");

        Set<String> triggers = extractAspectTriggerNames(configResearch);
        Set<String> declared = extractDeclaredAspectNames(aspectApi);

        Set<String> missing = new HashSet<>();
        for (String trigger : triggers) {
            if (!declared.contains(trigger)) {
                missing.add(trigger);
            }
        }

        assertFalse("ConfigResearch has no setAspectTriggers(...) calls", triggers.isEmpty());
        assertTrue("ConfigResearch aspect triggers missing in Aspect API declarations: " + missing, missing.isEmpty());
    }

    private static Set<String> extractAspectTriggerNames(String source) {
        Set<String> out = new HashSet<>();
        Matcher calls = ASPECT_TRIGGER_CALL_PATTERN.matcher(source);
        while (calls.find()) {
            Matcher refs = ASPECT_REF_PATTERN.matcher(calls.group(1));
            while (refs.find()) {
                out.add(refs.group(1));
            }
        }
        return out;
    }

    private static Set<String> extractDeclaredAspectNames(String source) {
        Set<String> out = new HashSet<>();
        Matcher matcher = ASPECT_DECL_PATTERN.matcher(source);
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
