package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChampionModRunicStaticGuardTest {

    @Test
    public void championAttributeAndRunicHooksShouldMatchReferenceBaseline() throws IOException {
        String entityUtils = readFile("src/main/java/thaumcraft/common/lib/utils/EntityUtils.java");
        String runic = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java");

        assertTrue(entityUtils.contains("public static final IAttribute CHAMPION_MOD"));
        assertTrue(entityUtils.contains("new RangedAttribute(null, \"tc.mobmod\", -2.0D, -2.0D, 100.0D)"));
        assertTrue(entityUtils.contains("modai.applyModifier(ChampionModifier.mods[type].attributeMod);"));
        assertTrue(entityUtils.contains("mob.enablePersistence();"));

        assertTrue(runic.contains("mob.getEntityAttribute(EntityUtils.CHAMPION_MOD)"));
        assertTrue(runic.contains("ChampionModifier.mods[t].effect.performEffect("));
        assertFalse(runic.contains("skip for now (no CHAMPION_MOD attribute)"));
        assertFalse(runic.contains("The port does not have EntityUtils.CHAMPION_MOD attribute"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
