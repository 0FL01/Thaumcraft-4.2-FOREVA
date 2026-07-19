package thaumcraft.common.entities.monster;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WarpManifestationParityStaticGuardTest {

    @Test
    public void harmlessMindSpidersKeepAiAndOriginalInteractionContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityMindSpider.java");

        assertFalse(source.contains("boolean isAIDisabled()"));
        assertFalse(source.contains("boolean isEntityInvulnerable("));
        assertTrue(source.contains("new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, true, false, null)"));
        assertTrue(source.contains("SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D)"));
        assertTrue(source.contains("boolean doesEntityNotTriggerPressurePlate() { return true; }"));
        assertTrue(source.contains("protected ResourceLocation getLootTable() { return null; }"));
        assertTrue(source.contains("if (this.isHarmless())") && source.contains("return false;"));
    }

    @Test
    public void warpGuardiansOnlyPersistWhenAssignedAHome() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java");

        assertTrue(source.contains("protected boolean canDespawn()")
                && source.contains("return !this.hasHome();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
