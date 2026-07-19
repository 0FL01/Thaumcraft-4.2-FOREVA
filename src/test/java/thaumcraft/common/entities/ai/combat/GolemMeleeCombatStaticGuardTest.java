package thaumcraft.common.entities.ai.combat;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GolemMeleeCombatStaticGuardTest {

    @Test
    public void golemMeleeKeepsAttackingWhenItsPathEndsInReach() throws IOException {
        String attackSource = readFile("src/main/java/thaumcraft/common/entities/ai/combat/AIGolemAttackOnCollide.java");
        String golemSource = readFile("src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java");

        assertTrue("Melee AI must start without a path when the target is already in attack range",
                attackSource.contains("return this.entityPathEntity != null || this.isWithinAttackRange(target);"));
        assertTrue("Melee AI must continue after its path ends while the target remains in attack range",
                attackSource.contains("return !this.theGolem.getNavigator().noPath() || this.isWithinAttackRange(target);"));
        assertTrue("Melee cooldown and repeated attack must use the same range contract",
                attackSource.contains("this.attackTick = Math.max(this.attackTick - 1, 0);")
                        && attackSource.contains("if (this.isWithinAttackRange(this.entityTarget) && this.attackTick <= 0)")
                        && attackSource.contains("this.attackTick = this.theGolem.getAttackSpeed();")
                        && attackSource.contains("this.theGolem.attackEntityAsMob(this.entityTarget);"));
        assertTrue("Attack range must retain the original TC4 calculation",
                attackSource.contains("double attackRange = (double)(target.width * 2.0F * target.width * 2.0F) + 1.0D;")
                        && attackSource.contains("target.getEntityBoundingBox().minY"));
        assertEquals("Guard and Butcher cores must share the corrected melee AI", 2,
                countOccurrences(golemSource, "new thaumcraft.common.entities.ai.combat.AIGolemAttackOnCollide(this)"));
    }

    private static int countOccurrences(String source, String value) {
        int count = 0;
        int offset = 0;
        while ((offset = source.indexOf(value, offset)) >= 0) {
            count++;
            offset += value.length();
        }
        return count;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
