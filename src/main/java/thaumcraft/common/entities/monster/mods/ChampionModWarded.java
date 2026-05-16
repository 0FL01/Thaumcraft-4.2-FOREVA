package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.common.Thaumcraft;

public class ChampionModWarded extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        float f1 = amount * 17.0f;
        return f1 / 20.0f;
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
                0.0, 0.0, 0.0,
                0.5f + boss.world.rand.nextFloat() * 0.1f,
                0.5f + boss.world.rand.nextFloat() * 0.1f,
                0.5f + boss.world.rand.nextFloat() * 0.1f,
                0.6f, true, 21, 4, 1, 4 + boss.world.rand.nextInt(4), 0, 0.8f + boss.world.rand.nextFloat() * 0.3f
        );
    }
}
