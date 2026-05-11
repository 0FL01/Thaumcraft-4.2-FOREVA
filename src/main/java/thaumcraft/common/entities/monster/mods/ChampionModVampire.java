package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;

public class ChampionModVampire implements IChampionModifierEffect {
    public ChampionModVampire() {}

    @Override
    public float applyEffect(EntityLivingBase entity, EntityLivingBase target, float damage, int tier) { return damage; }

    @Override
    public void onHit(EntityLivingBase entity, EntityLivingBase target, int tier) {}

    @Override
    public void onSpawn(EntityLivingBase entity, int tier) {}

    @Override
    public void onDeath(EntityLivingBase entity, int tier) {}
}
