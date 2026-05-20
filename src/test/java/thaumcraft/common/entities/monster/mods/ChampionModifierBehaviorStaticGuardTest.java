package thaumcraft.common.entities.monster.mods;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ChampionModifierBehaviorStaticGuardTest {

    @Test
    public void championModifierPerformEffectsShouldStayReferenceShaped() throws IOException {
        String bold = readFile("src/main/java/thaumcraft/common/entities/monster/mods/ChampionModBold.java");
        assertTrue(bold.contains("return 0.0F;"));

        String grim = readFile("src/main/java/thaumcraft/common/entities/monster/mods/ChampionModGrim.java");
        assertTrue(grim.contains("mob.getRNG().nextFloat() < 0.4f")
                && grim.contains("MobEffects.WEAKNESS, 200"));

        String poison = readFile("src/main/java/thaumcraft/common/entities/monster/mods/ChampionModPoison.java");
        assertTrue(poison.contains("mob.getRNG().nextFloat() < 0.4f")
                && poison.contains("MobEffects.POISON, 100"));

        String sickly = readFile("src/main/java/thaumcraft/common/entities/monster/mods/ChampionModSickly.java");
        assertTrue(sickly.contains("mob.getRNG().nextFloat() < 0.4f")
                && sickly.contains("MobEffects.HUNGER, 500"));

        String spined = readFile("src/main/java/thaumcraft/common/entities/monster/mods/ChampionModSpined.java");
        assertTrue(spined.contains("target == null || source == null || \"thorns\".equalsIgnoreCase(source.getDamageType())")
                && spined.contains("DamageSource.causeThornsDamage(mob)")
                && spined.contains("1 + mob.world.rand.nextInt(3)")
                && spined.contains("SoundEvents.ENCHANT_THORNS_HIT"));

        String warded = readFile("src/main/java/thaumcraft/common/entities/monster/mods/ChampionModWarded.java");
        assertTrue(warded.contains("mob.hurtResistantTime <= 0 && mob.ticksExisted % 25 == 0")
                && warded.contains("SharedMonsterAttributes.MAX_HEALTH")
                && warded.contains("getBaseValue()")
                && warded.contains("mob.setHealth(Math.min((float) bh, mob.getHealth() + 1.0f));")
                && warded.contains("return amount;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
