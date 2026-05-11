package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class ChampionModSpined extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        if (source.getTrueSource() instanceof EntityLivingBase && mob.getRNG().nextFloat() < 0.5f) {
            source.getTrueSource().attackEntityFrom(DamageSource.causeThornsDamage(mob), 2.0f);
        }
        return amount;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        // TODO: client FX
    }
}
