package thaumcraft.common.entities.monster;

import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;

public class EntityTaintCow extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    public EntityTaintCow(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.9F, 1.3F);
        PathNavigate navigate = this.getNavigator();
        if (navigate instanceof PathNavigateGround) {
            ((PathNavigateGround) navigate).setCanSwim(true);
        }
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(8, new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, false));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
        this.targetTasks.addTask(8, new EntityAINearestAttackableTarget(this, EntityAnimal.class, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.world.isRemote && this.ticksExisted < 5) {
            for (int i = 0; i < thaumcraft.common.Thaumcraft.proxy.particleCount(10); i++) {
                thaumcraft.common.Thaumcraft.proxy.splooshFX(this);
            }
        }
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return SoundEvents.ENTITY_COW_AMBIENT; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return SoundEvents.ENTITY_COW_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return SoundEvents.ENTITY_COW_HURT; }
    @Override protected float getSoundVolume() { return 0.4f; }
    @Override protected float getSoundPitch() { return 0.7f; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
    }
}
