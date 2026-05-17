package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemVoidCrimsonToolsStaticGuardTest {

    @Test
    public void voidAndCrimsonToolsKeepRepairRarityAndDebuffContracts() throws IOException {
        String voidSword = readFile("src/main/java/thaumcraft/common/items/equipment/ItemVoidSword.java");
        String voidAxe = readFile("src/main/java/thaumcraft/common/items/equipment/ItemVoidAxe.java");
        String voidPickaxe = readFile("src/main/java/thaumcraft/common/items/equipment/ItemVoidPickaxe.java");
        String voidShovel = readFile("src/main/java/thaumcraft/common/items/equipment/ItemVoidShovel.java");
        String voidHoe = readFile("src/main/java/thaumcraft/common/items/equipment/ItemVoidHoe.java");
        String crimsonSword = readFile("src/main/java/thaumcraft/common/items/equipment/ItemCrimsonSword.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemVoidSword must keep uncommon rarity, charm repair and pvp-gated wither helper contracts",
                voidSword.contains("return EnumRarity.UNCOMMON;")
                        && voidSword.contains("return !repair.isEmpty() && repair.getItem() == ConfigItems.itemResource && repair.getMetadata() == ItemResource.META_CHARM;")
                        && voidSword.contains("canApplyVoidCombatDebuff(")
                        && voidSword.contains("target.addPotionEffect(new PotionEffect(MobEffects.WITHER, durationTicks));")
                        && voidSword.contains("I18n.translateToLocal(\"enchantment.special.sapless\")"));
        assertTrue("Void tools must keep uncommon rarity and toolclass contracts where applicable",
                voidAxe.contains("ImmutableSet.of(\"axe\")")
                        && voidPickaxe.contains("ImmutableSet.of(\"pickaxe\")")
                        && voidShovel.contains("ImmutableSet.of(\"shovel\")")
                        && voidAxe.contains("return EnumRarity.UNCOMMON;")
                        && voidPickaxe.contains("return EnumRarity.UNCOMMON;")
                        && voidShovel.contains("return EnumRarity.UNCOMMON;")
                        && voidHoe.contains("return EnumRarity.UNCOMMON;")
                        && voidHoe.contains("return 5;"));
        assertTrue("Void tools must keep shared wither-on-hit and self-repair contracts",
                voidAxe.contains("ItemVoidSword.tryApplyVoidWither((EntityLivingBase) entity, player, 80);")
                        && voidPickaxe.contains("ItemVoidSword.tryApplyVoidWither((EntityLivingBase) entity, player, 80);")
                        && voidShovel.contains("ItemVoidSword.tryApplyVoidWither((EntityLivingBase) entity, player, 80);")
                        && voidHoe.contains("ItemVoidSword.tryApplyVoidWither((EntityLivingBase) entity, player, 80);")
                        && voidAxe.contains("ItemVoidSword.repairVoid(stack, world, entityIn);")
                        && voidPickaxe.contains("ItemVoidSword.repairVoid(stack, world, entityIn);")
                        && voidShovel.contains("ItemVoidSword.repairVoid(stack, world, entityIn);")
                        && voidHoe.contains("ItemVoidSword.repairVoid(stack, world, entityIn);"));
        assertTrue("ItemCrimsonSword must keep rare rarity, void-charm repair, dual debuff and warp contracts",
                crimsonSword.contains("implements IRepairable, IWarpingGear")
                        && crimsonSword.contains("return EnumRarity.RARE;")
                        && crimsonSword.contains("return ItemVoidSword.isVoidToolRepair(repair) || super.getIsRepairable(toRepair, repair);")
                        && crimsonSword.contains("target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60));")
                        && crimsonSword.contains("target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 120));")
                        && crimsonSword.contains("I18n.translateToLocal(\"enchantment.special.sapgreat\")")
                        && crimsonSword.contains("ItemVoidSword.repairVoid(stack, world, entityIn);")
                        && crimsonSword.contains("return 2;"));
        assertTrue("Void/crimson sword sapping tooltip localization keys must exist in en_us",
                lang.contains("enchantment.special.sapless=Lesser Sapping")
                        && lang.contains("enchantment.special.sapgreat=Greater Sapping"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
