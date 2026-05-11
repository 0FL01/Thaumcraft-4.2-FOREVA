package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;

public class ChampionModFire implements IChampionModifierEffect {
    public ChampionModFire() {}

    @Override
    public float applyEffect(EntityLivingBase entity, EntityLivingBase target, float damage, int tier) { return damage; }

    @Override
    public void onHit(EntityLivingBase entity, EntityLivingBase target, int tier) {}

    @Override
    public void onSpawn(EntityLivingBase entity, int tier) {}

    @Override
    public void onDeath(EntityLivingBase entity, int tier) {}
}
