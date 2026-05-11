package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class ChampionModVampire extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        mob.heal(Math.max(2.0f, amount / 2.0f));
        return amount;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        // TODO: client FX
    }
}
