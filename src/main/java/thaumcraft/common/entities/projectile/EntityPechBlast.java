package thaumcraft.common.entities.projectile;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.EntityPech;

public class EntityPechBlast extends EntityThrowable {
    int strength = 0;
    int duration = 0;
    boolean nightshade = false;

    public EntityPechBlast(World world) { super(world); }
    public EntityPechBlast(World world, EntityLivingBase shooter) { super(world, shooter); }
    public EntityPechBlast(World world, EntityLivingBase shooter, int strength, int duration, boolean nightshade) {
        super(world, shooter);
        this.strength = strength;
        this.duration = duration;
        this.nightshade = nightshade;
    }

    @Override
    protected float getGravityVelocity() { return 0.025f; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 500) this.setDead();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote) {
            // AOE 3x3x3, excludes EntityPech
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(
                this.getThrower(),
                this.getEntityBoundingBox().grow(2.0, 2.0, 2.0));
            for (Entity entity : list) {
                if (entity instanceof EntityPech || !(entity instanceof EntityLivingBase))
                    continue;
                EntityLivingBase living = (EntityLivingBase)entity;
                living.attackEntityFrom(
                    DamageSource.causeIndirectDamage(this, this.getThrower()),
                    (float)(this.strength + 2));
                try {
                    int potDuration = 100 + this.duration * 40;
                    if (this.nightshade) {
                        living.addPotionEffect(new PotionEffect(MobEffects.WITHER, potDuration, this.strength));
                        living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, potDuration, this.strength + 1));
                        living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, potDuration, this.strength));
                    } else {
                        switch (this.rand.nextInt(3)) {
                            case 0: living.addPotionEffect(new PotionEffect(MobEffects.WITHER, potDuration, this.strength)); break;
                            case 1: living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, potDuration, this.strength + 1)); break;
                            case 2: living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, potDuration, this.strength)); break;
                        }
                    }
                } catch (Exception e) {}
            }
        }
        this.setDead();
    }
}
