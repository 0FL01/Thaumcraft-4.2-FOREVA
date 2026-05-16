package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.monster.EntityTaintSpider;

public class ChampionModInfested extends java.lang.Object implements IChampionModifierEffect {

    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        if (!mob.world.isRemote && mob.getRNG().nextFloat() < 0.4F) {
            EntityTaintSpider spider = new EntityTaintSpider(mob.world);
            spider.setLocationAndAngles(
                mob.posX, mob.posY + mob.height * 0.5D, mob.posZ,
                mob.world.rand.nextFloat() * 360.0F, 0.0F);
            mob.world.spawnEntity(spider);
            mob.playSound(thaumcraft.common.lib.TCSounds.GORE, 0.5F, 1.0F);
        }
        return amount;
    }

    @Override
    public void showFX(EntityLivingBase boss) {
        if (boss.world.rand.nextBoolean()) {
            Thaumcraft.proxy.slimeJumpFX(boss, 0);
        }
    }
}
