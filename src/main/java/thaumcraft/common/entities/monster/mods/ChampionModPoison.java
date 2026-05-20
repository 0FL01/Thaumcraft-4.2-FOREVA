package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.common.Thaumcraft;

public class ChampionModPoison extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        if (target != null && mob.getRNG().nextFloat() < 0.4f) {
            target.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.POISON, 100));
        }
        return amount;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        if (boss.world.rand.nextBoolean()) return;
        float w = boss.world.rand.nextFloat() * boss.width;
        float d = boss.world.rand.nextFloat() * boss.width;
        float h = boss.world.rand.nextFloat() * boss.height;
        Thaumcraft.proxy.drawGenericParticles(
                boss.world,
                boss.getEntityBoundingBox().minX + w,
                boss.getEntityBoundingBox().minY + h,
                boss.getEntityBoundingBox().minZ + d,
                0.0, 0.02, 0.0,
                0.2f, 0.6f + boss.world.rand.nextFloat() * 0.1f, 0.2f + boss.world.rand.nextFloat() * 0.1f,
                0.7f, false, 147, 4, 1, 8 + boss.world.rand.nextInt(4), 0, 0.5f + boss.world.rand.nextFloat() * 0.2f
        );
    }
}
