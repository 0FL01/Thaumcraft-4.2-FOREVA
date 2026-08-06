package thaumcraft.common.items.equipment;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EquipmentAttackDamageParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void swordsRestoreTc4DamageWithoutChangingSpeedOrOffhand() {
        assertAttributes(new ItemThaumiumSword(ThaumcraftApi.toolMatThaumium), 6.0D, -2.4D);
        assertAttributes(new ItemElementalSword(ThaumcraftApi.toolMatElemental), 7.0D, -2.4D);
        assertAttributes(new ItemVoidSword(ThaumcraftApi.toolMatVoid), 7.0D, -2.4D);
        ItemCrimsonSword crimson = new ItemCrimsonSword();
        assertAttributes(crimson, 7.5D, -2.4D);
        assertEquals(7.5F, crimson.getAttackDamage(), 0.0F);
    }

    @Test
    public void picksShovelsAndAxesRestoreTc4DamageAndKeepForgeSpeeds() {
        assertAttributes(new ItemThaumiumPickaxe(ThaumcraftApi.toolMatThaumium), 4.0D, -2.8D);
        assertAttributes(new ItemElementalPickaxe(ThaumcraftApi.toolMatElemental), 5.0D, -2.8D);
        assertAttributes(new ItemVoidPickaxe(ThaumcraftApi.toolMatVoid), 5.0D, -2.8D);
        assertAttributes(new ItemThaumiumShovel(ThaumcraftApi.toolMatThaumium), 3.0D, -3.0D);
        assertAttributes(new ItemElementalShovel(ThaumcraftApi.toolMatElemental), 4.0D, -3.0D);
        assertAttributes(new ItemVoidShovel(ThaumcraftApi.toolMatVoid), 4.0D, -3.0D);
        assertAttributes(new ItemThaumiumAxe(ThaumcraftApi.toolMatThaumium), 5.0D, -3.2D);
        assertAttributes(new ItemElementalAxe(ThaumcraftApi.toolMatElemental), 6.0D, -3.0D);
    }

    @Test
    public void crimsonMaterialAndDebuffsMatchTc4() {
        assertEquals(4, ItemCrimsonSword.toolMatCrimsonVoid.getHarvestLevel());
        assertEquals(200, ItemCrimsonSword.toolMatCrimsonVoid.getMaxUses());
        assertEquals(8.0F, ItemCrimsonSword.toolMatCrimsonVoid.getEfficiency(), 0.0F);
        assertEquals(3.5F, ItemCrimsonSword.toolMatCrimsonVoid.getAttackDamage(), 0.0F);
        assertEquals(20, ItemCrimsonSword.toolMatCrimsonVoid.getEnchantability());

        ItemVoidEquipmentParityTest.TestWorld world = new ItemVoidEquipmentParityTest.TestWorld();
        EntityZombie target = new EntityZombie(world);
        EntityZombie attacker = new EntityZombie(world);
        ItemCrimsonSword crimson = new ItemCrimsonSword();
        crimson.hitEntity(new ItemStack(crimson), target, attacker);

        assertTrue(target.isPotionActive(MobEffects.WEAKNESS));
        assertEquals(60, target.getActivePotionEffect(MobEffects.WEAKNESS).getDuration());
        assertTrue(target.isPotionActive(MobEffects.HUNGER));
        assertEquals(120, target.getActivePotionEffect(MobEffects.HUNGER).getDuration());
        assertFalse(target.isPotionActive(MobEffects.WITHER));
    }

    private static void assertAttributes(Item item, double damage, double speed) {
        ItemStack stack = new ItemStack(item);
        Multimap<String, AttributeModifier> main = item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
        assertSingle(main.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()), damage);
        assertSingle(main.get(SharedMonsterAttributes.ATTACK_SPEED.getName()), speed);
        assertTrue(item.getAttributeModifiers(EntityEquipmentSlot.OFFHAND, stack).isEmpty());
    }

    private static void assertSingle(Collection<AttributeModifier> modifiers, double amount) {
        assertEquals(1, modifiers.size());
        assertEquals(amount, modifiers.iterator().next().getAmount(), 0.000001D);
    }
}
