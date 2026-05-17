package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigResearchStaticGraphTest {

    private static final Pattern KEY_CATEGORY_PATTERN = Pattern.compile(
            "new\\s+ResearchItem\\(\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"");
    private static final Pattern PARENTS_PATTERN = Pattern.compile("setParents\\(([^)]*)\\)");
    private static final Pattern PARENTS_HIDDEN_PATTERN = Pattern.compile("setParentsHidden\\(([^)]*)\\)");
    private static final Pattern PAGE_TEXT_PATTERN = Pattern.compile("new\\s+ResearchPage\\(\\s*\"([^\"]+)\"");
    private static final Pattern RESOURCE_LOCATION_PATTERN = Pattern.compile("new\\s+ResourceLocation\\(\\s*\"thaumcraft\"\\s*,\\s*\"([^\"]+)\"\\s*\\)");
    private static final Pattern LANG_IMG_PATTERN = Pattern.compile("<IMG>thaumcraft:([^:<]+)(?::[^<]*)?</IMG>");
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile("\"([^\"]+)\"");
    private static final String ASSET_ROOT = "src/main/resources/assets/thaumcraft";

    @Test
    public void configResearchRegisteredGraphHasValidStaticReferences() throws IOException {
        String source = readConfigResearchFamily();
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        Set<String> allowedCategories = new HashSet<>(Arrays.asList(
                "BASICS", "THAUMATURGY", "ALCHEMY", "ARTIFICE", "GOLEMANCY", "ELDRITCH"));
        Set<String> langKeys = extractLangKeys(lang);

        List<ResearchStatement> statements = parseStatements(source);
        Set<String> keys = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        Map<String, Integer> categoryCounts = new HashMap<>();

        for (ResearchStatement statement : statements) {
            if (!keys.add(statement.key)) {
                duplicates.add(statement.key);
            }
            assertTrue("Unexpected category for key " + statement.key + ": " + statement.category,
                    allowedCategories.contains(statement.category));
            categoryCounts.put(statement.category, categoryCounts.getOrDefault(statement.category, 0) + 1);
        }

        assertTrue("Duplicate research keys found: " + duplicates, duplicates.isEmpty());
        assertTrue("ConfigResearch should keep reference-sized 201 research entries, got " + statements.size(),
                statements.size() == 201);
        for (String category : allowedCategories) {
            assertTrue("Research category has no entries in ConfigResearch: " + category,
                    categoryCounts.getOrDefault(category, 0) > 0);
        }

        List<String> missingParents = new ArrayList<>();
        List<String> missingLang = new ArrayList<>();

        for (ResearchStatement statement : statements) {
            for (String parent : statement.parents) {
                if (!keys.contains(parent)) {
                    missingParents.add(statement.key + " -> " + parent);
                }
            }
            for (String parent : statement.parentsHidden) {
                if (!keys.contains(parent)) {
                    missingParents.add(statement.key + " (hidden) -> " + parent);
                }
            }

            if (!statement.virtual && !statement.pageTextKeys.isEmpty()) {
                assertLangKey(langKeys, "tc.research_name." + statement.key, missingLang);
                assertLangKey(langKeys, "tc.research_text." + statement.key, missingLang);
            }
            for (String pageKey : statement.pageTextKeys) {
                if (pageKey.startsWith("tc.research_page.")) {
                    assertLangKey(langKeys, pageKey, missingLang);
                }
            }
        }

        assertTrue("Missing parent references in ConfigResearch: " + missingParents, missingParents.isEmpty());
        assertTrue("Missing research localization keys in en_us.lang: " + missingLang, missingLang.isEmpty());
        assertTrue("Parent cycle(s) found in ConfigResearch: " + findParentCycles(statements), findParentCycles(statements).isEmpty());
    }

    @Test
    public void researchResourceLocationsAndLangImagesResolveToAssets() throws IOException {
        String source = readConfigResearchFamily();
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        Set<String> configResourcePaths = extractMatches(source, RESOURCE_LOCATION_PATTERN);
        Set<String> imgResourcePaths = extractMatches(lang, LANG_IMG_PATTERN);

        List<String> missingConfigResources = new ArrayList<>();
        for (String path : configResourcePaths) {
            assertAssetExists(path, missingConfigResources);
        }

        List<String> missingLangImages = new ArrayList<>();
        for (String path : imgResourcePaths) {
            assertAssetExists(path, missingLangImages);
        }

        assertTrue("Missing ConfigResearch ResourceLocation assets: " + missingConfigResources, missingConfigResources.isEmpty());
        assertTrue("Missing <IMG> assets referenced by en_us.lang: " + missingLangImages, missingLangImages.isEmpty());
    }

    private static List<ResearchStatement> parseStatements(String source) {
        List<ResearchStatement> out = new ArrayList<>();
        String[] chunks = source.split("\\.registerResearchItem\\(\\);");
        for (String chunk : chunks) {
            Matcher rootMatcher = KEY_CATEGORY_PATTERN.matcher(chunk);
            if (!rootMatcher.find()) {
                continue;
            }
            ResearchStatement statement = new ResearchStatement();
            statement.key = rootMatcher.group(1);
            statement.category = rootMatcher.group(2);
            statement.virtual = chunk.contains(".setVirtual()");
            statement.parents.addAll(extractQuotedArgs(chunk, PARENTS_PATTERN));
            statement.parentsHidden.addAll(extractQuotedArgs(chunk, PARENTS_HIDDEN_PATTERN));

            Matcher pageMatcher = PAGE_TEXT_PATTERN.matcher(chunk);
            while (pageMatcher.find()) {
                statement.pageTextKeys.add(pageMatcher.group(1));
            }
            out.add(statement);
        }
        return out;
    }

    private static List<String> extractQuotedArgs(String chunk, Pattern callPattern) {
        List<String> out = new ArrayList<>();
        Matcher callMatcher = callPattern.matcher(chunk);
        while (callMatcher.find()) {
            String body = callMatcher.group(1);
            Matcher quoted = QUOTED_STRING_PATTERN.matcher(body);
            while (quoted.find()) {
                out.add(quoted.group(1));
            }
        }
        return out;
    }

    private static Set<String> extractLangKeys(String lang) {
        Set<String> keys = new HashSet<>();
        for (String line : lang.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            int eq = trimmed.indexOf('=');
            if (eq <= 0) continue;
            keys.add(trimmed.substring(0, eq));
        }
        return keys;
    }

    private static Set<String> extractMatches(String source, Pattern pattern) {
        Set<String> out = new HashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static void assertLangKey(Set<String> langKeys, String key, List<String> missing) {
        if (!langKeys.contains(key)) {
            missing.add(key);
        }
    }

    private static void assertAssetExists(String assetPath, List<String> missing) {
        Path resolved = Paths.get(ASSET_ROOT).resolve(assetPath);
        if (!Files.exists(resolved)) {
            missing.add(assetPath);
        }
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

    private static List<String> findParentCycles(List<ResearchStatement> statements) {
        Map<String, Set<String>> graph = new HashMap<>();
        for (ResearchStatement statement : statements) {
            Set<String> edges = graph.computeIfAbsent(statement.key, k -> new HashSet<>());
            edges.addAll(statement.parents);
            edges.addAll(statement.parentsHidden);
        }

        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();
        LinkedHashSet<String> trace = new LinkedHashSet<>();
        List<String> cycles = new ArrayList<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfsCycles(node, graph, visited, stack, trace, cycles);
            }
        }
        return cycles;
    }

    private static void dfsCycles(
            String node,
            Map<String, Set<String>> graph,
            Set<String> visited,
            Set<String> stack,
            LinkedHashSet<String> trace,
            List<String> cycles) {
        visited.add(node);
        stack.add(node);
        trace.add(node);

        for (String next : graph.getOrDefault(node, java.util.Collections.emptySet())) {
            if (!graph.containsKey(next)) {
                continue;
            }
            if (!visited.contains(next)) {
                dfsCycles(next, graph, visited, stack, trace, cycles);
            } else if (stack.contains(next)) {
                StringBuilder sb = new StringBuilder();
                boolean inCycle = false;
                for (String n : trace) {
                    if (n.equals(next)) {
                        inCycle = true;
                    }
                    if (inCycle) {
                        if (sb.length() > 0) sb.append(" -> ");
                        sb.append(n);
                    }
                }
                if (sb.length() > 0) {
                    sb.append(" -> ").append(next);
                    cycles.add(sb.toString());
                }
            }
        }

        stack.remove(node);
        trace.remove(node);
    }

    private static class ResearchStatement {
        String key;
        String category;
        boolean virtual;
        List<String> parents = new ArrayList<>();
        List<String> parentsHidden = new ArrayList<>();
        List<String> pageTextKeys = new ArrayList<>();
    }
}
