package thaumcraft.common.entities.monster.boss;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;

public class EntityEldritchGolem extends EntityThaumcraftBoss implements thaumcraft.api.entities.IEldritchMob, net.minecraft.entity.IRangedAttackMob {
    private boolean headless = false;

    public EntityEldritchGolem(net.minecraft.world.World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.1, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    public boolean isHeadless() { return this.headless; }

    public void makeHeadless() {
        this.headless = true;
        // Add ranged attack when headless — use AILongRangeAttack
        this.tasks.addTask(2, new thaumcraft.common.entities.ai.combat.AILongRangeAttack(this, 3.0, 1.0, 5, 5, 24.0f));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        // TODO: beam attack
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return net.minecraft.init.SoundEvents.ENTITY_IRONGOLEM_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return net.minecraft.init.SoundEvents.ENTITY_IRONGOLEM_DEATH; }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        // TODO: go headless at lethal damage
        return super.attackEntityFrom(source, amount);
    }
}
