package thaumcraft.common.entities.monster;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class MonsterSoundConstantCoverageTest {

    private static final Pattern TC_SOUND_REF_PATTERN = Pattern.compile("TCSounds\\.([A-Z0-9_]+)");
    private static final Pattern TC_SOUND_DECL_PATTERN = Pattern.compile("public\\s+static\\s+final\\s+SoundEvent\\s+([A-Z0-9_]+)\\s*=");

    @Test
    public void monsterAndBossSoundReferencesUseDeclaredTcSoundsConstants() throws IOException {
        Set<String> declaredConstants = extractDeclaredTcSoundsConstants();
        Set<String> referencedConstants = extractReferencedMonsterSoundConstants();

        Set<String> missing = new HashSet<>(referencedConstants);
        missing.removeAll(declaredConstants);

        assertTrue("Monster/boss sound references missing in TCSounds declarations: " + missing, missing.isEmpty());
    }

    private static Set<String> extractDeclaredTcSoundsConstants() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/TCSounds.java");
        Set<String> out = new HashSet<>();
        Matcher matcher = TC_SOUND_DECL_PATTERN.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static Set<String> extractReferencedMonsterSoundConstants() throws IOException {
        Path root = Paths.get("src/main/java/thaumcraft/common/entities/monster");
        Set<Path> files;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toSet());
        }

        Set<String> out = new HashSet<>();
        for (Path file : files) {
            String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            Matcher matcher = TC_SOUND_REF_PATTERN.matcher(source);
            while (matcher.find()) {
                out.add(matcher.group(1));
            }
        }
        return out;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
