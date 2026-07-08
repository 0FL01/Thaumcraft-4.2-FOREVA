package thaumcraft.common.entities.monster;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;

public class EntityTaintVillager extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    private int randomTickDivider;
    private boolean isMatingFlag;
    private boolean isPlayingFlag;
    private Village villageObj;

    public EntityTaintVillager(net.minecraft.world.World world) {
        this(world, 0);
    }

    public EntityTaintVillager(net.minecraft.world.World world, int professionId) {
        super(world);
        this.randomTickDivider = 0;
        this.isMatingFlag = false;
        this.isPlayingFlag = false;
        this.villageObj = null;
        PathNavigate navigate = this.getNavigator();
        if (navigate instanceof PathNavigateGround) {
            ((PathNavigateGround) navigate).setBreakDoors(true);
            ((PathNavigateGround) navigate).setCanSwim(true);
        }
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(2, new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F));
        this.tasks.addTask(9, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void updateAITasks() {
        if (--this.randomTickDivider <= 0) {
            BlockPos pos = new BlockPos(this);
            this.world.getVillageCollection().addToVillagerPositionList(pos);
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.villageObj = this.world.getVillageCollection().getNearestVillage(pos, 32);
            if (this.villageObj == null) {
                this.detachHome();
            } else {
                BlockPos center = this.villageObj.getCenter();
                this.setHomePosAndDistance(center, (int) (this.villageObj.getVillageRadius() * 0.6F));
            }
        }
        super.updateAITasks();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.world.isRemote && this.ticksExisted < 5) {
            for (int i = 0; i < Thaumcraft.proxy.particleCount(10); i++) {
                Thaumcraft.proxy.splooshFX(this);
            }
        }
    }

    @Override
    public void setRevengeTarget(EntityLivingBase livingBase) {
        super.setRevengeTarget(livingBase);
        if (this.villageObj != null && livingBase != null) {
            this.villageObj.addOrRenewAgressor(livingBase);
        }
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return SoundEvents.ENTITY_VILLAGER_AMBIENT; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return SoundEvents.ENTITY_VILLAGER_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return SoundEvents.ENTITY_VILLAGER_DEATH; }
    @Override protected float getSoundPitch() { return 0.7f; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextInt(2) == 0) {
            if (this.world.rand.nextBoolean()) {
                this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
            } else {
                this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
            }
        }
        if (this.world.rand.nextInt(13) < 1 + looting) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 18), 1.5F);
        }
    }
}
