package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class ChampionModWarded extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        float f1 = amount * 17.0f;
        return f1 / 20.0f;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        // TODO: client FX
    }
}
