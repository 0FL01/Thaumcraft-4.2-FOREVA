package thaumcraft.common.entities.monster;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;

public class EntityEldritchCrab extends net.minecraft.entity.monster.EntityMob {
    private static final net.minecraft.network.datasync.DataParameter<Byte> HELM = 
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityEldritchCrab.class, net.minecraft.network.datasync.DataSerializers.BYTE);
    private int ridingAttackTime;

    public EntityEldritchCrab(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.8F, 0.6F);
        this.experienceValue = 6;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.63f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
        if (this.getNavigator() instanceof net.minecraft.pathfinding.PathNavigateGround) {
            net.minecraft.pathfinding.PathNavigateGround navigator =
                    (net.minecraft.pathfinding.PathNavigateGround) this.getNavigator();
            navigator.setCanSwim(true);
            navigator.setEnterDoors(true);
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HELM, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.hasHelm() ? 0.275 : 0.3);
    }

    public boolean hasHelm() { return this.dataManager.get(HELM) == 1; }
    public void setHelm(boolean b) {
        this.dataManager.set(HELM, (byte) (b ? 1 : 0));
        if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null) {
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(b ? 0.275 : 0.3);
        }
    }

    @Override
    public int getTotalArmorValue() {
        return this.hasHelm() ? 5 : 0;
    }

    @Override
    public double getYOffset() {
        return this.isRiding() ? 0.5D : 0.0D;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ridingAttackTime > 0) --this.ridingAttackTime;
        if (this.ticksExisted < 20) this.fallDistance = 0.0F;
        EntityLivingBase target = this.getAttackTarget();
        if (!this.isRiding() && target != null && !target.isBeingRidden() && !this.onGround
                && !this.hasHelm() && !target.isDead
                && this.posY - target.posY >= target.height / 2.0F
                && this.getDistanceSq(target) < 4.0D) {
            this.startRiding(target, true);
        }
        if (!this.world.isRemote && this.isRiding() && this.ridingAttackTime <= 0) {
            this.ridingAttackTime = 10 + this.rand.nextInt(10);
            Entity mount = this.getRidingEntity();
            if (mount != null) {
                this.attackEntityAsMob(mount);
                if (this.rand.nextFloat() < 0.2F) this.dismountRidingEntity();
            }
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData) {
        if (this.world.getDifficulty() == EnumDifficulty.HARD) {
            this.setHelm(true);
        } else {
            this.setHelm(this.rand.nextFloat() < 0.33F);
        }
        return super.onInitialSpawn(difficulty, livingData);
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        boolean hit = super.attackEntityFrom(source, amount);
        if (!this.world.isRemote && hit && this.hasHelm() && this.getHealth() / this.getMaxHealth() <= 0.5F) {
            this.setHelm(false);
            this.renderBrokenItemStack(new ItemStack(ConfigItems.itemChestCultistPlate));
        }
        return hit;
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("Flags")) {
            this.dataManager.set(HELM, nbt.getByte("Flags"));
            this.setHelm(this.hasHelm());
        }
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("Flags", this.dataManager.get(HELM));
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return thaumcraft.common.lib.TCSounds.CRABTALK; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return net.minecraft.init.SoundEvents.ENTITY_GUARDIAN_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return thaumcraft.common.lib.TCSounds.CRABDEATH; }

    @Override
    public int getTalkInterval() {
        return 160;
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
        if (wasRecentlyHit && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + looting) > 0)) {
            this.dropItem(Items.SPIDER_EYE, 1);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            this.playSound(thaumcraft.common.lib.TCSounds.CRABCLAW,
                    1.0F, 0.9F + this.world.rand.nextFloat() * 0.2F);
            return true;
        }
        return false;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect.getPotion() != MobEffects.POISON && super.isPotionApplicable(effect);
    }
}
