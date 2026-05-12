package thaumcraft.common.entities.monster;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;

public class EntityEldritchGuardian extends net.minecraft.entity.monster.EntityMob implements net.minecraft.entity.IRangedAttackMob, thaumcraft.api.entities.IEldritchMob {
    public EntityEldritchGuardian(net.minecraft.world.World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 8.0, 1.0, 20, 40, 24.0f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        // TODO: fire EldritchOrb
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: sonic screech attack
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }
}
