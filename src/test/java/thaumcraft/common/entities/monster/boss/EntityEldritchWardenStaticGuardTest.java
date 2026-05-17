package thaumcraft.common.entities.monster.boss;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntityEldritchWardenStaticGuardTest {

    @Test
    public void eldritchWardenShouldKeepReferenceSwimAndSpawnStateContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java");

        assertTrue(source.contains("this.getNavigator() instanceof PathNavigateGround"));
        assertTrue(source.contains("((PathNavigateGround) this.getNavigator()).setCanSwim(true);"));
        assertTrue(source.contains("if (this.getSpawnTimer() == 150) {"));
        assertFalse(source.contains("if (!this.world.isRemote && this.getSpawnTimer() == 150) {"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
