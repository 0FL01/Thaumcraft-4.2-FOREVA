package thaumcraft.common.config;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EldritchLocalizationParityTest {
    @Test
    public void auditedEnglishDisplayValuesMatchTc4235() throws Exception {
        Map<String, String> lang = readLang();
        assertEquals("Void", lang.get("tc.aspect.vacuos"));
        assertEquals("Darkness", lang.get("tc.aspect.tenebrae"));
        assertEquals("Alien, Strange, The Eldritch", lang.get("tc.aspect.alienis"));
        assertEquals("Advanced Alchemical Furnace", lang.get("tile.thaumcraft.alchemy_furnace_advanced.name"));
        assertEquals("Wand Focus: Primal", lang.get("item.thaumcraft.focus_primal.name"));
        assertEquals("Void Sword", lang.get("item.thaumcraft.sword_void.name"));
        assertEquals("Void Pickaxe", lang.get("item.thaumcraft.pick_void.name"));
        assertEquals("Void Axe", lang.get("item.thaumcraft.axe_void.name"));
        assertEquals("Void Shovel", lang.get("item.thaumcraft.shovel_void.name"));
        assertEquals("Void Hoe", lang.get("item.thaumcraft.hoe_void.name"));
        assertEquals("Void Helm", lang.get("item.thaumcraft.helm_void.name"));
        assertEquals("Void Chestplate", lang.get("item.thaumcraft.chest_void.name"));
        assertEquals("Void Leggings", lang.get("item.thaumcraft.legs_void.name"));
        assertEquals("Void Boots", lang.get("item.thaumcraft.boots_void.name"));
        assertEquals("Void Thaumaturge Hood", lang.get("item.thaumcraft.helm_void_robe.name"));
        assertEquals("Void Thaumaturge Robe", lang.get("item.thaumcraft.chest_void_robe.name"));
        assertEquals("Void Thaumaturge Leggings", lang.get("item.thaumcraft.legs_void_robe.name"));
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
}
