package thaumcraft.common.entities.monster;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityEldritchGuardianStaticGuardTest {

    @Test
    public void eldritchGuardianShouldKeepReferenceFogAndMeleeFireContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java");

        assertTrue(source.contains("this.getNavigator() instanceof PathNavigateGround"));
        assertTrue(source.contains("((PathNavigateGround) this.getNavigator()).setCanSwim(true);"));
        assertTrue(source.contains("this.world.getDifficulty() != EnumDifficulty.EASY"));
        assertTrue(source.contains("int difficulty = this.world.getDifficulty().getId();"));
        assertTrue(source.contains("this.getHeldItemMainhand().isEmpty()"));
        assertTrue(source.contains("this.isBurning()"));
        assertTrue(source.contains("this.rand.nextFloat() < (float) difficulty * 0.3F"));
        assertTrue(source.contains("entityIn.setFire(2 * difficulty);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
