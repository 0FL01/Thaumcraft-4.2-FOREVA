package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.common.Thaumcraft;

public class ChampionModFire extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        if (target != null && mob.getRNG().nextFloat() < 0.4f) {
            target.setFire(4);
        }
        return amount;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        float w = boss.world.rand.nextFloat() * boss.width;
        float d = boss.world.rand.nextFloat() * boss.width;
        float h = boss.world.rand.nextFloat() * boss.height;
        Thaumcraft.proxy.drawGenericParticles(
                boss.world,
                boss.getEntityBoundingBox().minX + w,
                boss.getEntityBoundingBox().minY + h,
                boss.getEntityBoundingBox().minZ + d,
                0.0, 0.03, 0.0,
                0.9f + boss.world.rand.nextFloat() * 0.1f, 1.0f, 1.0f, 0.7f,
                false, 160, 10, 1, 8 + boss.world.rand.nextInt(4), 0, 0.7f + boss.world.rand.nextFloat() * 0.2f
        );
    }
}
