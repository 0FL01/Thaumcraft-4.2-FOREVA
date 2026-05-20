package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.common.Thaumcraft;

public class ChampionModBold extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        return 0.0F;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        if (boss.world.rand.nextBoolean()) return;
        float w = boss.world.rand.nextFloat() * boss.width;
        float d = boss.world.rand.nextFloat() * boss.width;
        float h = boss.world.rand.nextFloat() * boss.height / 3.0f;
        Thaumcraft.proxy.sparkle(
                (float) (boss.getEntityBoundingBox().minX + w),
                (float) (boss.getEntityBoundingBox().minY + h),
                (float) (boss.getEntityBoundingBox().minZ + d),
                0.7f,
                0xCC44FF,
                0.04f
        );
    }
}
