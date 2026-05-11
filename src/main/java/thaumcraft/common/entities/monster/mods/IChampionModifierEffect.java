package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;

public interface IChampionModifierEffect {
    float applyEffect(EntityLivingBase entity, EntityLivingBase target, float damage, int tier);
    void onHit(EntityLivingBase entity, EntityLivingBase target, int tier);
    void onSpawn(EntityLivingBase entity, int tier);
    void onDeath(EntityLivingBase entity, int tier);
}
