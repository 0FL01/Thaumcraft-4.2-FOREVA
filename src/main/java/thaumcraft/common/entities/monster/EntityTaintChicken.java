package thaumcraft.common.entities.monster;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.init.SoundEvents;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;

public class EntityTaintChicken extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    public boolean field_753_a = false;
    public float field_752_b = 0.0f;
    public float destPos = 0.0f;
    public float field_757_d;
    public float field_756_e;
    public float field_755_h = 1.0f;

    public EntityTaintChicken(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.5f, 0.8f);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.3F));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, true));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityAnimal.class, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4);
    }

    @Override
    public int getMaxSpawnedInChunk() { return 2; }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.field_756_e = this.field_752_b;
        this.field_757_d = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3);
        if (this.destPos < 0.0f) this.destPos = 0.0f;
        if (this.destPos > 1.0f) this.destPos = 1.0f;
        if (!this.onGround && this.field_755_h < 1.0f) this.field_755_h = 1.0f;
        this.field_755_h = (float)((double)this.field_755_h * 0.9);
        if (!this.onGround && this.motionY < 0.0) this.motionY *= 0.9;
        this.field_752_b += this.field_755_h * 2.0f;
        if (this.world.isRemote && this.ticksExisted < 5) {
            for (int i = 0; i < thaumcraft.common.Thaumcraft.proxy.particleCount(10); i++) {
                thaumcraft.common.Thaumcraft.proxy.splooshFX(this);
            }
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected net.minecraft.util.SoundEvent getAmbientSound() { return SoundEvents.ENTITY_CHICKEN_AMBIENT; }
    @Override
    protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource source) { return SoundEvents.ENTITY_CHICKEN_HURT; }
    @Override
    protected net.minecraft.util.SoundEvent getDeathSound() { return SoundEvents.ENTITY_CHICKEN_HURT; }
    @Override
    protected float getSoundPitch() { return 0.7f; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextInt(4) == 0) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
    }
}
