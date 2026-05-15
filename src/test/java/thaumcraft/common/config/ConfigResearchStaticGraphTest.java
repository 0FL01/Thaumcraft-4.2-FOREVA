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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigResearchStaticGraphTest {

    private static final Pattern KEY_CATEGORY_PATTERN = Pattern.compile(
            "new\\s+ResearchItem\\(\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"");
    private static final Pattern PARENTS_PATTERN = Pattern.compile("setParents\\(([^)]*)\\)");
    private static final Pattern PARENTS_HIDDEN_PATTERN = Pattern.compile("setParentsHidden\\(([^)]*)\\)");
    private static final Pattern PAGE_TEXT_PATTERN = Pattern.compile("new\\s+ResearchPage\\(\\s*\"([^\"]+)\"");
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile("\"([^\"]+)\"");

    @Test
    public void configResearchRegisteredGraphHasValidStaticReferences() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigResearch.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        Set<String> allowedCategories = new HashSet<>(Arrays.asList(
                "BASICS", "THAUMATURGY", "ALCHEMY", "ARTIFICE", "GOLEMANCY", "ELDRITCH"));
        Set<String> langKeys = extractLangKeys(lang);

        List<ResearchStatement> statements = parseStatements(source);
        Set<String> keys = new HashSet<>();
        List<String> duplicates = new ArrayList<>();

        for (ResearchStatement statement : statements) {
            if (!keys.add(statement.key)) {
                duplicates.add(statement.key);
            }
            assertTrue("Unexpected category for key " + statement.key + ": " + statement.category,
                    allowedCategories.contains(statement.category));
        }

        assertTrue("Duplicate research keys found: " + duplicates, duplicates.isEmpty());

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

            if (!statement.virtual) {
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
            statement.virtual = chunk.contains("new ResearchItem(\"" + statement.key + "\", \"" + statement.category + "\")");
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

    private static void assertLangKey(Set<String> langKeys, String key, List<String> missing) {
        if (!langKeys.contains(key)) {
            missing.add(key);
        }
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
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
