package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import thaumcraft.common.Thaumcraft;

public class ChampionModWarp extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        if (target instanceof EntityPlayer && mob.getRNG().nextFloat() < 0.33f) {
            Thaumcraft.addWarpToPlayer((EntityPlayer)target, 1 + mob.getRNG().nextInt(3), true);
        }
        return amount;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        // TODO: client FX
    }
}
