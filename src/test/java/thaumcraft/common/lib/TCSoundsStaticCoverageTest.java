package thaumcraft.common.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class TCSoundsStaticCoverageTest {

    private static final Pattern SOUND_CALL_PATTERN = Pattern.compile("sound\\(\"([^\"]+)\"\\)");
    private static final Pattern SOUND_JSON_KEY_PATTERN = Pattern.compile("(?m)^\\s*\"([^\"]+)\"\\s*:\\s*\\{");

    @Test
    public void tcSoundsDeclarationsMatchSoundsJsonAndAssets() throws IOException {
        String tcSoundsSource = readFile("src/main/java/thaumcraft/common/lib/TCSounds.java");
        String soundsJson = readFile("src/main/resources/assets/thaumcraft/sounds.json");
        JsonObject soundsRoot = new JsonParser().parse(soundsJson).getAsJsonObject();

        Set<String> declaredKeys = extract(tcSoundsSource, SOUND_CALL_PATTERN);
        Set<String> jsonKeys = extract(soundsJson, SOUND_JSON_KEY_PATTERN);
        Set<String> soundFileBases = listSoundFileBases("src/main/resources/assets/thaumcraft/sounds");

        Set<String> missingInJson = new HashSet<>(declaredKeys);
        missingInJson.removeAll(jsonKeys);
        assertTrue("TCSounds keys missing in sounds.json: " + missingInJson, missingInJson.isEmpty());

        Set<String> undeclaredInCode = new HashSet<>(jsonKeys);
        undeclaredInCode.removeAll(declaredKeys);
        assertTrue("sounds.json keys missing in TCSounds declarations: " + undeclaredInCode, undeclaredInCode.isEmpty());

        Set<String> unnamespacedEntries = new HashSet<>();
        Set<String> missingAssets = new HashSet<>();
        for (String key : jsonKeys) {
            JsonObject event = soundsRoot.getAsJsonObject(key);
            JsonArray sounds = event.getAsJsonArray("sounds");
            for (JsonElement sound : sounds) {
                String entryName = sound.isJsonObject()
                        ? sound.getAsJsonObject().get("name").getAsString()
                        : sound.getAsString();
                if (!entryName.startsWith("thaumcraft:")) {
                    unnamespacedEntries.add(key + " -> " + entryName);
                }
                String baseName = entryName.contains(":") ? entryName.substring(entryName.indexOf(':') + 1) : entryName;
                boolean hasBaseOrVariant = soundFileBases.contains(baseName) || hasVariant(soundFileBases, baseName);
                if (!hasBaseOrVariant) {
                    missingAssets.add(key + " -> " + entryName);
                }
            }
        }
        assertTrue("sounds.json entries without explicit thaumcraft namespace: " + unnamespacedEntries, unnamespacedEntries.isEmpty());
        assertTrue("sounds.json keys without matching .ogg assets: " + missingAssets, missingAssets.isEmpty());
    }

    private static Set<String> extract(String source, Pattern pattern) {
        Set<String> out = new HashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
        return out;
    }

    private static Set<String> listSoundFileBases(String root) throws IOException {
        Path base = Paths.get(root);
        try (java.util.stream.Stream<Path> stream = Files.walk(base)) {
            List<String> names = stream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".ogg"))
                    .map(name -> name.substring(0, name.length() - 4))
                    .collect(Collectors.toList());
            return new HashSet<>(names);
        }
    }

    private static boolean hasVariant(Set<String> fileBases, String key) {
        for (String base : fileBases) {
            if (base.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
