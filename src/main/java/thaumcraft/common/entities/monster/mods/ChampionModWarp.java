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
                0.8f + boss.world.rand.nextFloat() * 0.2f, 0.0f, 0.9f + boss.world.rand.nextFloat() * 0.1f,
                0.7f, true, 72, 8, 1, 10 + boss.world.rand.nextInt(4), 0, 0.6f + boss.world.rand.nextFloat() * 0.4f
        );
    }
}
