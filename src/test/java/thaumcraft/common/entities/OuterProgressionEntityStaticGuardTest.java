package thaumcraft.common.entities;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OuterProgressionEntityStaticGuardTest {

    @Test
    public void guardianKeepsRareEyeWardAndSpawnContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java");

        assertTrue(source.contains("this.rand.nextInt(200) - lootingModifier < 5")
                && source.contains("this.dropItem(ConfigItems.itemEldritchObject, 1);"));
        assertFalse(source.contains("protected void dropEquipment(boolean wasRecentlyHit"));
        assertTrue(source.contains("this.setAbsorptionAmount(this.getAbsorptionAmount() + ward);")
                && source.contains("if (this.getAbsorptionAmount() < ward)"));
        assertTrue(source.contains("return nearby.isEmpty() && super.getCanSpawnHere();")
                && source.contains("protected boolean isValidLightLevel()")
                && source.contains("return true;"));
    }

    @Test
    public void crabKeepsAttachmentPlateBreakAndSpiderEyeContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/monster/EntityEldritchCrab.java");

        assertTrue(source.contains("this.startRiding(target, true);")
                && source.contains("this.attackEntityAsMob(mount);")
                && source.contains("this.dismountRidingEntity();"));
        assertTrue(source.contains("this.renderBrokenItemStack(new ItemStack(ConfigItems.itemChestCultistPlate));"));
        assertFalse(source.contains("this.entityDropItem(new ItemStack(ConfigItems.itemCultistPlate)"));
        assertTrue(source.contains("this.dropItem(Items.SPIDER_EYE, 1);")
                && source.contains("return EnumCreatureAttribute.ARTHROPOD;")
                && source.contains("effect.getPotion() != MobEffects.POISON"));
    }

    @Test
    public void allPearlBossDropsUseSpecialHelperAndWardenHomeFollowsInitialization() throws IOException {
        String boss = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityThaumcraftBoss.java");
        String portal = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java");
        String taint = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityTaintacleGiant.java");
        String lock = read("src/main/java/thaumcraft/common/tiles/TileEldritchLock.java");

        assertTrue(boss.contains("EntityUtils.entityDropSpecialItem"));
        assertTrue(portal.contains("EntityUtils.entityDropSpecialItem"));
        assertTrue(taint.contains("EntityUtils.entityDropSpecialItem"));
        int initialSpawn = lock.indexOf("boss.onInitialSpawn", lock.indexOf("spawnEldritchWarden"));
        int home = lock.indexOf("boss.setHomePosAndDistance(room.getCenterHomePos(), 32);", initialSpawn);
        assertTrue(initialSpawn >= 0 && home > initialSpawn);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
