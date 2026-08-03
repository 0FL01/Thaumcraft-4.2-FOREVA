package thaumcraft.common.entities.monster.boss;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EldritchBossNameStaticGuardTest {

    @Test
    public void baseNamesShouldNotExposeChampionFormatTokens() throws Exception {
        Map<String, String> lang = readLang();

        assertEquals("Eldritch Construct", lang.get("entity.thaumcraft.eldritchgolem.name"));
        assertEquals("Eldritch Warden", lang.get("entity.thaumcraft.eldritchwarden.name"));
        assertEquals("Warded Eldritch Construct", String.format(
                lang.get("entity.thaumcraft.eldritchgolem.champion.name"), "Warded"));
        assertEquals("Aphoom-Zhah the Mighty", String.format(
                lang.get("entity.thaumcraft.eldritchwarden.champion.name"), "Aphoom-Zhah", "Mighty"));
    }

    @Test
    public void championNamesShouldUseDedicatedLocalizedTemplates() throws Exception {
        String golem = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java");
        String warden = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java");

        assertTrue(golem.contains("public void generateName()"));
        assertTrue(golem.contains("EntityUtils.getChampionModifierType(this)"));
        assertTrue(golem.contains("entity.thaumcraft.eldritchgolem.champion.name"));
        assertTrue(warden.contains("public void generateName()"));
        assertTrue(warden.contains("EntityUtils.getChampionModifierType(this)"));
        assertTrue(warden.contains("entity.thaumcraft.eldritchwarden.champion.name"));
        assertTrue(warden.contains("this.getTitle()"));
    }

    private static Map<String, String> readLang() throws Exception {
        List<String> lines = Files.readAllLines(
                Paths.get("src/main/resources/assets/thaumcraft/lang/en_us.lang"), StandardCharsets.UTF_8);
        Map<String, String> values = new LinkedHashMap<>();
        for (String line : lines) {
            int equals = line.indexOf('=');
            if (equals > 0 && !line.startsWith("#")) {
                values.put(line.substring(0, equals), line.substring(equals + 1));
            }
        }
        return values;
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
