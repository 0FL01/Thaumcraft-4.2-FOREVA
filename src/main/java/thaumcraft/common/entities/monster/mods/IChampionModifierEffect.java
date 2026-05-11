package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public interface IChampionModifierEffect {

    float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount);
    void showFX(EntityLivingBase boss);
}
