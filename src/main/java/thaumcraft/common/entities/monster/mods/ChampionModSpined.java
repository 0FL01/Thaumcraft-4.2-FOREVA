package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.common.Thaumcraft;

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
        if (boss.world.rand.nextBoolean()) return;
        float w = boss.world.rand.nextFloat() * boss.width;
        float d = boss.world.rand.nextFloat() * boss.width;
        float h = boss.world.rand.nextFloat() * boss.height;
        int p = 176 + boss.world.rand.nextInt(4) * 3;
        Thaumcraft.proxy.drawGenericParticles(
                boss.world,
                boss.getEntityBoundingBox().minX + w,
                boss.getEntityBoundingBox().minY + h,
                boss.getEntityBoundingBox().minZ + d,
                0.0, 0.0, 0.0,
                0.5f + boss.world.rand.nextFloat() * 0.2f,
                0.1f + boss.world.rand.nextFloat() * 0.2f,
                0.1f + boss.world.rand.nextFloat() * 0.2f,
                0.7f, false, p, 3, 1, 3, 0, 1.2f + boss.world.rand.nextFloat() * 0.3f
        );
    }
}
