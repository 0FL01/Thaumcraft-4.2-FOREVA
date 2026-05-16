package thaumcraft.common.entities.monster;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;

public class EntityCultistSoundContractTest {

    private static final Pattern NULL_AMBIENT = Pattern.compile("getAmbientSound\\s*\\([^)]*\\)\\s*\\{\\s*return\\s+null\\s*;\\s*\\}");
    private static final Pattern NULL_HURT = Pattern.compile("getHurtSound\\s*\\([^)]*\\)\\s*\\{\\s*return\\s+null\\s*;\\s*\\}");
    private static final Pattern NULL_DEATH = Pattern.compile("getDeathSound\\s*\\([^)]*\\)\\s*\\{\\s*return\\s+null\\s*;\\s*\\}");

    @Test
    public void cultistDoesNotOverrideHostileSoundsWithNull() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityCultist.java");

        assertFalse("EntityCultist getAmbientSound() must not return null explicitly", NULL_AMBIENT.matcher(source).find());
        assertFalse("EntityCultist getHurtSound(...) must not return null explicitly", NULL_HURT.matcher(source).find());
        assertFalse("EntityCultist getDeathSound() must not return null explicitly", NULL_DEATH.matcher(source).find());
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
