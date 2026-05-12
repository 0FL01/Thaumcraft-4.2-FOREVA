package thaumcraft.common.lib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityUtils {
    public static EntityItem dropItemStack(World world, BlockPos pos, ItemStack stack) {
        return dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }
    
    public static EntityItem dropItemStack(World world, double x, double y, double z, ItemStack stack) {
        return new EntityItem(world, x, y, z, stack);
    }

    public static void setRecentlyHit(Entity entity, int time) {
        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).hurtResistantTime = time;
        }
    }

    /**
     * Mark an entity as a champion mob with increased attributes.
     * Full champion modifier system is in Phase 6.5 (EntityChampionModifier).
     * This is a minimal implementation for Eldritch dimension room gen.
     */
    public static void makeChampion(EntityLivingBase entity, boolean isBoss) {
        if (entity == null) return;
        // Scale max health
        double healthMultiplier = isBoss ? 3.0 : 2.0;
        entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH)
                .setBaseValue(entity.getMaxHealth() * healthMultiplier);
        entity.setHealth(entity.getMaxHealth());
        // Scale attack damage
        if (entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
            entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE)
                    .setBaseValue(entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue() * 1.5);
        }
        // Add fire resistance for boss champions
        if (isBoss) {
            entity.addPotionEffect(new net.minecraft.potion.PotionEffect(
                    net.minecraft.init.MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        }
    }
}