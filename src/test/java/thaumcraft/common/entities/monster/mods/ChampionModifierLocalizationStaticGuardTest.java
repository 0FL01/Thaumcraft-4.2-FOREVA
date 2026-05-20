package thaumcraft.common.entities.monster.mods;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ChampionModifierLocalizationStaticGuardTest {

    @Test
    public void championModifierDisplayNamesShouldExistInEnglishLang() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/mods/ChampionModifier.java");
        String entityUtils = readFile("src/main/java/thaumcraft/common/lib/utils/EntityUtils.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(source.contains("String key = \"champion.mod.\" + this.name;"));
        assertTrue(source.contains("return key.equals(translated) ? this.displayName : translated;"));
        assertTrue(source.contains("new ChampionModifier(6, \"warp\", \"Warped\", 1"));
        assertTrue(entityUtils.contains("mob.setCustomNameTag(buildChampionDisplayName(mob, type));"));
        assertTrue(entityUtils.contains("current.startsWith(\"champion.mod.\")"));

        assertTrue(lang.contains("champion.mod.bold=Bold"));
        assertTrue(lang.contains("champion.mod.spine=Spined"));
        assertTrue(lang.contains("champion.mod.armor=Armored"));
        assertTrue(lang.contains("champion.mod.mighty=Mighty"));
        assertTrue(lang.contains("champion.mod.grim=Grim"));
        assertTrue(lang.contains("champion.mod.warded=Warded"));
        assertTrue(lang.contains("champion.mod.warp=Warped"));
        assertTrue(lang.contains("champion.mod.undying=Undying"));
        assertTrue(lang.contains("champion.mod.fiery=Fiery"));
        assertTrue(lang.contains("champion.mod.sickly=Sickly"));
        assertTrue(lang.contains("champion.mod.venomous=Venomous"));
        assertTrue(lang.contains("champion.mod.vampiric=Vampiric"));
        assertTrue(lang.contains("champion.mod.infested=Infested"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
