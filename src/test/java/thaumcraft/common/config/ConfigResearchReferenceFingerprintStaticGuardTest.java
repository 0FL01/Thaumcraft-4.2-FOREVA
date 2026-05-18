package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ConfigResearchReferenceFingerprintStaticGuardTest {
    private static final Pattern KEY_CATEGORY_PATTERN = Pattern.compile(
            "new\\s+ResearchItem\\(\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"", Pattern.MULTILINE);
    private static final Pattern PAGE_TEXT_PATTERN = Pattern.compile(
            "new\\s+ResearchPage\\(\\s*\"(tc\\.research_page\\.[^\"]+)\"", Pattern.MULTILINE);

    // Captured from decompiled 1.7.10 reference ConfigResearch.class.
    private static final int REFERENCE_RESEARCH_KEY_COUNT = 201;
    private static final int REFERENCE_PAGE_KEY_COUNT = 294;
    private static final String REFERENCE_RESEARCH_KEY_SHA256 =
            "397d620877d429c49d4db6ae65552a681ac5b26be5d60cd1e95e5e28cae9f40e";
    private static final String REFERENCE_PAGE_KEY_SHA256 =
            "66a10cd39a47e78472134a35d3b7b4e1b78618996ac6c87907aeb63303d4bf38";

    @Test
    public void configResearchCorpusShouldMatchReferenceFingerprint() throws IOException {
        String source = readConfigResearchFamily();
        Set<String> keyCategoryPairs = extractKeyCategoryPairs(source);
        Set<String> pageKeys = extractPageKeys(source);

        assertEquals("ConfigResearch key/category corpus size drifted from reference",
                REFERENCE_RESEARCH_KEY_COUNT, keyCategoryPairs.size());
        assertEquals("ConfigResearch tc.research_page.* corpus size drifted from reference",
                REFERENCE_PAGE_KEY_COUNT, pageKeys.size());

        assertEquals("ConfigResearch key/category corpus fingerprint drifted from reference",
                REFERENCE_RESEARCH_KEY_SHA256, sha256Hex(joinLines(keyCategoryPairs)));
        assertEquals("ConfigResearch tc.research_page.* corpus fingerprint drifted from reference",
                REFERENCE_PAGE_KEY_SHA256, sha256Hex(joinLines(pageKeys)));
    }

    private static Set<String> extractKeyCategoryPairs(String source) {
        Set<String> out = new TreeSet<>();
        Matcher matcher = KEY_CATEGORY_PATTERN.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1) + "|" + matcher.group(2));
        }
        return out;
    }

    private static Set<String> extractPageKeys(String source) {
        Set<String> out = new TreeSet<>();
        Matcher matcher = PAGE_TEXT_PATTERN.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static String joinLines(Set<String> lines) {
        return String.join("\n", lines) + "\n";
    }

    private static String sha256Hex(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing SHA-256 digest support", e);
        }
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
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
