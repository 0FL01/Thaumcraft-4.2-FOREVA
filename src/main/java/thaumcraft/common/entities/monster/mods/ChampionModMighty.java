package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class ChampionModMighty extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        return 0.0F; // Mighty champions are immune to damage
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        // TODO: client FX
    }
}
