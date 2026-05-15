package thaumcraft.common.entities.monster;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.lib.TCSounds;

public class EntityInhabitedZombie extends net.minecraft.entity.monster.EntityZombie {
    public EntityInhabitedZombie(net.minecraft.world.World world) {
        super(world);
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        this.getEntityAttribute(EntityZombie.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0);
    }

    @Override
    public void onDeathUpdate() {
        if (!this.world.isRemote) {
            EntityEldritchCrab crab = new EntityEldritchCrab(this.world);
            crab.setLocationAndAngles(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ, this.rotationYaw, this.rotationPitch);
            crab.setHelm(true);
            this.world.spawnEntity(crab);
            if ((this.recentlyHit > 0 || this.isPlayer()) && this.world.getGameRules().getBoolean("doMobLoot")) {
                int xp = this.getExperiencePoints(this.attackingPlayer);
                while (xp > 0) {
                    int split = EntityXPOrb.getXPSplit(xp);
                    xp -= split;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, split));
                }
            }
        }
        for (int i = 0; i < 20; ++i) {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
                    this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    this.posY + (double) (this.rand.nextFloat() * this.height),
                    this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    this.rand.nextGaussian() * 0.02D,
                    this.rand.nextGaussian() * 0.02D,
                    this.rand.nextGaussian() * 0.02D);
        }
        this.setDead();
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {}
    @Override public void onKillEntity(net.minecraft.entity.EntityLivingBase entity) {}

    @Override
    public boolean getCanSpawnHere() {
        List<EntityInhabitedZombie> nearby = this.world.getEntitiesWithinAABB(
                EntityInhabitedZombie.class,
                new AxisAlignedBB(this.posX, this.posY, this.posZ,
                        this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)
                        .grow(32.0D, 16.0D, 32.0D));
        return nearby.isEmpty() && super.getCanSpawnHere();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.CRABTALK;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_HOSTILE_HURT;
    }
}
