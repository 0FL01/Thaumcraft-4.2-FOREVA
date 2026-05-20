package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityChampionStaticGuardTest {

    @Test
    public void championSpawnAndDropsContractsShouldBePresent() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");
        String entityUtils = readFile("src/main/java/thaumcraft/common/lib/utils/EntityUtils.java");

        assertTrue(source.contains("handleChampionSpawn(event, (EntityMob) event.getEntity());"));
        assertTrue(source.contains("public void onEntityConstructing(EntityEvent.EntityConstructing event)"));
        assertTrue(source.contains("EntityUtils.ensureChampionModAttribute((EntityMob) event.getEntity())"));
        assertTrue(source.contains("ensureChampionAttribute(EntityMob mob)"));
        assertTrue(source.contains("return EntityUtils.ensureChampionModAttribute(mob);"));
        assertTrue(source.contains("mod.getAttributeValue() > -1.0D"));
        assertTrue(source.contains("EntityUtils.repairChampionName(mob);"));
        assertTrue(entityUtils.contains("public static IAttributeInstance ensureChampionModAttribute(EntityLivingBase entity)"));
        assertTrue(entityUtils.contains("public static void repairChampionName(EntityLivingBase entity)"));
        assertTrue(entityUtils.contains("public static int getChampionModifierType(EntityLivingBase entity)"));
        assertTrue(entityUtils.contains("registerAttribute(CHAMPION_MOD)"));
        assertTrue(entityUtils.contains("instance.setBaseValue(-2.0D);"));
        assertTrue(source.contains("ChampionModifier.mods[type].type == 0"));
        assertTrue(source.contains("ChampionModifier.mods[type].effect.performEffect(mob, null, null, 0.0F);"));
        assertTrue(source.contains("ConfigEntities.CHAMPION_WHITELIST"));
        assertTrue(source.contains("Config.championMobs"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemLootBag, 1, lb)"));
        assertTrue(source.contains("EntityXPOrb.getXPSplit(i)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
