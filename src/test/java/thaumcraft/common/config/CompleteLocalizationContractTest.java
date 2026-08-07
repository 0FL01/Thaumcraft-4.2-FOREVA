package thaumcraft.common.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompleteLocalizationContractTest {

    private static final Path LANG_DIR = Paths.get("src/main/resources/assets/thaumcraft/lang");
    private static final String[] COMPLETE_LOCALES = {
            "de_de.lang", "fr_fr.lang", "es_es.lang", "es_ar.lang", "es_mx.lang",
            "ru_ru.lang", "zh_cn.lang", "zh_tw.lang"
    };
    private static final Pattern PLACEHOLDER = Pattern.compile("%(?:\\d+\\$)?[a-z]|%[A-Z]+|%%");
    private static final Pattern IMAGE = Pattern.compile("<IMG>(.*?)</IMG>");
    private static final Set<Character> FORMATTING_CODES = new HashSet<Character>(Arrays.asList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r',
            'A', 'B', 'C', 'D', 'E', 'F', 'K', 'L', 'M', 'N', 'O', 'R'));
    private static final Set<String> REVIEWED_IDENTICAL_KEYS = new HashSet<String>(Arrays.asList((
            "entity.thaumcraft.alumentum.name entity.thaumcraft.golem.name entity.thaumcraft.wisp.name "
                    + "focus.upgrade.frugal.name golemblurb.3.text golemblurb.6.text golemblurb.7.text "
                    + "golemthreat.2.text golemthreat.5.text item.ItemGolemDecoration.3.name "
                    + "item.ItemGolemDecoration.5.name item.Wand.name item.Wand.obsidian.rod "
                    + "item.WandCasting.vis item.thaumcraft.essence.name item.thaumcraft.nugget.name "
                    + "item.thaumcraft.resource.0.name item.thaumcraft.resource.1.name "
                    + "item.thaumcraft.resource.14.name item.thaumcraft.resource.alumentum.name "
                    + "item.thaumcraft.resource.dust.name item.thaumcraft.resource.nitor.name "
                    + "item.thaumcraft.thaumometer.name item.thaumcraft.thaumonomicon.0.name "
                    + "item.thaumcraft.thaumonomicon.name itemGroup.thaumcraft nodetype.NORMAL.name "
                    + "potion.infvisexhaust potion.thaumarhia tc.aspect.auram tc.aspect.fames "
                    + "tc.aspect.metallum tc.research_category.ARTIFICE tc.research_category.ELDRITCH "
                    + "tc.research_name.ALUMENTUM tc.research_name.INFUSION tc.research_name.NITOR "
                    + "tc.research_name.TABLE tc.research_name.THAUMOMETER "
                    + "tc.research_name.THAUMONOMICON tc.research_text.FOCUSEXCAVATION "
                    + "tile.thaumcraft.airy.1.name tile.thaumcraft.custom_plant.5.name "
                    + "tile.thaumcraft.metal_device.10.name tile.thaumcraft.metal_device.11.name "
                    + "tile.thaumcraft.table.0.name tile.thaumcraft.table.1.name").split(" ")));

    @Test
    public void completeLocalesShouldMatchCanonicalSchemaAndFormattingContracts() throws Exception {
        LocaleData english = readLocale("en_us.lang");
        assertEquals(1639, english.values.size());

        for (String fileName : COMPLETE_LOCALES) {
            assertEquals(fileName.toLowerCase(Locale.ROOT), fileName);
            LocaleData translated = readLocale(fileName);
            assertEquals("Key order drift in " + fileName, english.keys, translated.keys);

            int untranslated = 0;
            for (String key : english.keys) {
                String expected = english.values.get(key);
                String actual = translated.values.get(key);
                assertEquals("Placeholder mismatch for " + key + " in " + fileName,
                        tokens(PLACEHOLDER, expected), tokens(PLACEHOLDER, actual));
                assertEquals("Research image mismatch for " + key + " in " + fileName,
                        tokens(IMAGE, expected), tokens(IMAGE, actual));
                assertFormattingCodes(fileName, key, actual);
                if (expected.equals(actual) && !REVIEWED_IDENTICAL_KEYS.contains(key)) {
                    untranslated++;
                }
            }
            assertTrue(fileName + " is below the required 99% reviewed translation coverage: " + untranslated,
                    untranslated <= english.values.size() / 100);
        }
    }

    @Test
    public void resourcePackShouldUseNative112LowercaseLocalePaths() throws Exception {
        String metadata = readSource("src/main/resources/pack.mcmeta");
        assertTrue("Minecraft 1.12 resources must use pack format 3",
                Pattern.compile("\\\"pack_format\\\"\\s*:\\s*3").matcher(metadata).find());

        String build = readSource("build.gradle");
        assertFalse("Format-3 packs must not emit the legacy en_US locale alias",
                build.contains("en_US.lang"));

        try (Stream<Path> files = Files.list(LANG_DIR)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".lang")) {
                    assertEquals("Locale resource paths must remain lowercase",
                            fileName.toLowerCase(Locale.ROOT), fileName);
                }
            }
        }
    }

    @Test
    public void runtimeLocalizationKeysAndRawWarpTokenShouldRemainReachable() throws Exception {
        LocaleData english = readLocale("en_us.lang");
        for (String key : Arrays.asList(
                "item.ItemBaubleBlanks.3.name", "tc.discount", "tc.boss.warden", "tc.boss.golem",
                "tc.boss.crimson", "tc.boss.taint", "tc.pressureplate.everything",
                "tc.pressureplate.except_owner", "tc.pressureplate.owner_only", "tc.golem.notowner")) {
            assertTrue("Missing canonical runtime key " + key, english.values.containsKey(key));
        }

        String plate = readSource("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");
        String events = readSource("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");
        String browser = readSource("src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java");
        assertTrue(plate.contains("\"tc.pressureplate.everything\"")
                && plate.contains("\"tc.pressureplate.except_owner\"")
                && plate.contains("\"tc.pressureplate.owner_only\""));
        assertTrue(events.contains("new TextComponentTranslation(\"tc.golem.notowner\")"));
        assertTrue(browser.contains("I18n.translateToLocal(\"tc.forbidden\")"));
        assertFalse(browser.contains("I18n.format(\"tc.forbidden\")"));
    }

    private static LocaleData readLocale(String fileName) throws Exception {
        Path path = LANG_DIR.resolve(fileName);
        byte[] bytes = Files.readAllBytes(path);
        assertFalse(fileName + " must be BOM-free", bytes.length >= 3
                && (bytes[0] & 0xff) == 0xef && (bytes[1] & 0xff) == 0xbb && (bytes[2] & 0xff) == 0xbf);
        List<String> keys = new ArrayList<String>();
        Map<String, String> values = new LinkedHashMap<String, String>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            lineNumber++;
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int equals = line.indexOf('=');
            assertTrue("Malformed record at " + fileName + ':' + lineNumber, equals > 0);
            String key = line.substring(0, equals);
            String value = line.substring(equals + 1);
            assertFalse("Empty value for " + key + " in " + fileName, value.isEmpty());
            assertFalse("Duplicate key " + key + " in " + fileName, values.containsKey(key));
            keys.add(key);
            values.put(key, value);
        }
        return new LocaleData(keys, values);
    }

    private static List<String> tokens(Pattern pattern, String value) {
        List<String> tokens = new ArrayList<String>();
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            tokens.add(matcher.groupCount() == 0 ? matcher.group() : matcher.group(1));
        }
        Collections.sort(tokens);
        return tokens;
    }

    private static void assertFormattingCodes(String fileName, String key, String value) {
        for (int index = value.indexOf('\u00a7'); index >= 0; index = value.indexOf('\u00a7', index + 2)) {
            assertTrue("Malformed formatting code for " + key + " in " + fileName,
                    index + 1 < value.length() && FORMATTING_CODES.contains(value.charAt(index + 1)));
        }
    }

    private static String readSource(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static final class LocaleData {
        private final List<String> keys;
        private final Map<String, String> values;

        private LocaleData(List<String> keys, Map<String, String> values) {
            this.keys = keys;
            this.values = values;
        }
    }
}
