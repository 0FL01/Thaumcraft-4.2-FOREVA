package thaumcraft.common.entities.monster.mods;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChampionModFxStaticGuardTest {

    @Test
    public void championModifierFxHooksStayWired() throws IOException {
        String[] files = new String[]{
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModArmored.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModBold.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModFire.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModGrim.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModInfested.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModMighty.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModPoison.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModSickly.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModSpined.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModUndying.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModVampire.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModWarded.java",
                "src/main/java/thaumcraft/common/entities/monster/mods/ChampionModWarp.java"
        };

        for (String file : files) {
            String source = readFile(file);
            assertTrue("Champion mod FX class must keep showFX override: " + file,
                    source.contains("public void showFX(EntityLivingBase boss)"));
            assertTrue("Champion mod FX class must route showFX through proxy helper: " + file,
                    source.contains("Thaumcraft.proxy."));
            assertFalse("Champion mod FX class must not keep TODO placeholder: " + file,
                    source.contains("TODO: client FX") || source.contains("Phase 8"));
        }
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
