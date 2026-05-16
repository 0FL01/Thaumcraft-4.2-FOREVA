package thaumcraft.common.entities.monster;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class MonsterSoundNullOverrideAllowlistTest {

    private static final Pattern NULL_SOUND_OVERRIDE_PATTERN = Pattern.compile(
            "(getAmbientSound|getHurtSound|getDeathSound)\\s*\\([^)]*\\)\\s*\\{\\s*return\\s+null\\s*;\\s*\\}");

    private static final Set<String> ALLOWLIST;

    static {
        Set<String> allow = new HashSet<>();
        // 1.7.10 reference swarm living sound is an empty string; 1.12 equivalent uses null ambient override.
        allow.add("EntityTaintSwarm#getAmbientSound");
        ALLOWLIST = Collections.unmodifiableSet(allow);
    }

    @Test
    public void onlyAllowlistedMonsterSoundMethodsMayReturnNull() throws IOException {
        List<Path> files = listMonsterSourceFiles();
        List<String> violations = new ArrayList<>();

        for (Path file : files) {
            String className = stripJavaSuffix(file.getFileName().toString());
            String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            Matcher matcher = NULL_SOUND_OVERRIDE_PATTERN.matcher(source);
            while (matcher.find()) {
                String methodName = matcher.group(1);
                String signature = className + "#" + methodName;
                if (!ALLOWLIST.contains(signature)) {
                    violations.add(signature + " in " + file.toString());
                }
            }
        }

        assertTrue("Unexpected null sound overrides (missing allowlist entry or regression): " + violations, violations.isEmpty());
    }

    private static List<Path> listMonsterSourceFiles() throws IOException {
        Path monsterRoot = Paths.get("src/main/java/thaumcraft/common/entities/monster");
        try (java.util.stream.Stream<Path> stream = Files.walk(monsterRoot)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
        }
    }

    private static String stripJavaSuffix(String name) {
        return name.endsWith(".java") ? name.substring(0, name.length() - 5) : name;
    }
}
