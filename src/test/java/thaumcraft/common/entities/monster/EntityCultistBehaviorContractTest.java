package thaumcraft.common.entities.monster;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityCultistBehaviorContractTest {

    @Test
    public void cultistKeepsHomeNbtAndFactionTargetingContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityCultist.java");

        assertTrue("EntityCultist must restore home from HomeD/HomeX/HomeY/HomeZ NBT keys",
                source.contains("if (nbt.hasKey(\"HomeD\"))")
                        && source.contains("new BlockPos(nbt.getInteger(\"HomeX\"), nbt.getInteger(\"HomeY\"), nbt.getInteger(\"HomeZ\"))")
                        && source.contains("this.setHomePosAndDistance("));
        assertTrue("EntityCultist must persist home with HomeD/HomeX/HomeY/HomeZ keys",
                source.contains("nbt.setInteger(\"HomeD\"")
                        && source.contains("nbt.setInteger(\"HomeX\"")
                        && source.contains("nbt.setInteger(\"HomeY\"")
                        && source.contains("nbt.setInteger(\"HomeZ\""));
        assertTrue("EntityCultist must treat cultists and cultist leader as same team",
                source.contains("entityIn instanceof EntityCultist || entityIn instanceof EntityCultistLeader"));
        assertTrue("EntityCultist must not attack cultist subclasses",
                source.contains("if (cls == EntityCultistCleric.class || cls == EntityCultistLeader.class || cls == EntityCultistKnight.class)"));
    }

    @Test
    public void cultistClericPortsOriginalRobeEquipmentOnInitialSpawn() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityCultistCleric.java");

        assertTrue("Cultist clerics must assign original robe pieces during initial spawn",
                source.contains("public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)")
                        && source.contains("this.setEquipmentBasedOnDifficulty(difficulty);")
                        && source.contains("protected void setEquipmentBasedOnDifficulty")
                        && source.contains("EntityEquipmentSlot.HEAD, new ItemStack(ConfigItems.itemHelmetCultistRobe)")
                        && source.contains("EntityEquipmentSlot.CHEST, new ItemStack(ConfigItems.itemChestCultistRobe)")
                        && source.contains("EntityEquipmentSlot.LEGS, new ItemStack(ConfigItems.itemLegsCultistRobe)"));
        assertTrue("Cultist cleric boots chance must match original HARD/non-HARD probabilities",
                source.contains("this.world.getDifficulty() == EnumDifficulty.HARD ? 0.3F : 0.1F")
                        && source.contains("EntityEquipmentSlot.FEET, new ItemStack(ConfigItems.itemCultistBoots)"));
    }

    @Test
    public void cultistKnightPortsOriginalArmorWeaponAndEnchantSpawnLogic() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityCultistKnight.java");

        assertTrue("Cultist knights must assign original plate armor and boots during initial spawn",
                source.contains("public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)")
                        && source.contains("this.setEquipmentBasedOnDifficulty(difficulty);")
                        && source.contains("this.setEnchantmentBasedOnDifficulty(difficulty);")
                        && source.contains("protected void setEquipmentBasedOnDifficulty")
                        && source.contains("EntityEquipmentSlot.HEAD, new ItemStack(ConfigItems.itemHelmetCultistPlate)")
                        && source.contains("EntityEquipmentSlot.CHEST, new ItemStack(ConfigItems.itemChestCultistPlate)")
                        && source.contains("EntityEquipmentSlot.LEGS, new ItemStack(ConfigItems.itemLegsCultistPlate)")
                        && source.contains("EntityEquipmentSlot.FEET, new ItemStack(ConfigItems.itemCultistBoots)"));
        assertTrue("Cultist knight rare weapon logic must match original probabilities and outputs",
                source.contains("this.world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F")
                        && source.contains("this.rand.nextInt(5)")
                        && source.contains("EntityEquipmentSlot.MAINHAND, new ItemStack(ConfigItems.itemSwordVoid)")
                        && source.contains("EntityEquipmentSlot.HEAD, new ItemStack(ConfigItems.itemHelmetCultistRobe)")
                        && source.contains("EntityEquipmentSlot.MAINHAND, new ItemStack(ConfigItems.itemSwordThaumium)")
                        && source.contains("EntityEquipmentSlot.HEAD, ItemStack.EMPTY")
                        && source.contains("EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD)"));
        assertTrue("Cultist knight must preserve original mainhand enchant chance",
                source.contains("protected void setEnchantmentBasedOnDifficulty")
                        && source.contains("this.rand.nextFloat() < 0.25F * localDifficulty")
                        && source.contains("5.0F + localDifficulty * (float) this.rand.nextInt(18)"));
    }

    @Test
    public void cultistArmorItemsRemainSlotCorrectForVanillaArmorLayer() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigItems.java");

        assertTrue("Cultist robe pieces must use slot-correct armor items and retain chest alias",
                source.contains("itemHelmetCultistRobe = (ItemCultistRobeArmor) new ItemCultistRobeArmor(ARMOR_CULTIST, 0, EntityEquipmentSlot.HEAD)")
                        && source.contains("itemChestCultistRobe = (ItemCultistRobeArmor) new ItemCultistRobeArmor(ARMOR_CULTIST, 0, EntityEquipmentSlot.CHEST)")
                        && source.contains("itemLegsCultistRobe = (ItemCultistRobeArmor) new ItemCultistRobeArmor(ARMOR_CULTIST, 0, EntityEquipmentSlot.LEGS)")
                        && source.contains("itemCultistRobe = itemChestCultistRobe"));
        assertTrue("Cultist plate pieces must use slot-correct armor items and retain chest alias",
                source.contains("itemHelmetCultistPlate = (ItemCultistPlateArmor) new ItemCultistPlateArmor(ARMOR_CULTIST_PLATE, 0, EntityEquipmentSlot.HEAD)")
                        && source.contains("itemChestCultistPlate = (ItemCultistPlateArmor) new ItemCultistPlateArmor(ARMOR_CULTIST_PLATE, 0, EntityEquipmentSlot.CHEST)")
                        && source.contains("itemLegsCultistPlate = (ItemCultistPlateArmor) new ItemCultistPlateArmor(ARMOR_CULTIST_PLATE, 0, EntityEquipmentSlot.LEGS)")
                        && source.contains("itemCultistPlate = itemChestCultistPlate"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
