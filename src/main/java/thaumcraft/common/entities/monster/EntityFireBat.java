package thaumcraft.common.entities.monster;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;

public class EntityFireBat extends EntityMob {
    public static final byte FLAG_HANGING = 1;
    public static final byte FLAG_SUMMONED = 2;
    public static final byte FLAG_EXPLOSIVE = 4;
    public static final byte FLAG_DEVIL = 8;
    public static final byte FLAG_VAMPIRE = 16;

    private static final DataParameter<Byte> FLAGS =
        EntityDataManager.createKey(EntityFireBat.class, DataSerializers.BYTE);

    private BlockPos currentFlightTarget;
    public EntityPlayer owner = null;
    public int damBonus = 0;
    private int attackCooldown = 0;

    public EntityFireBat(World world) {
        super(world);
        this.setSize(0.5f, 0.9f);
        this.setIsBatHanging(true);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FLAGS, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        double hp = this.getIsDevil() ? 15.0 : 5.0;
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(hp);
        double dmg = this.getIsSummoned() ? (double)((this.getIsDevil() ? 3 : 2) + this.damBonus) : 1.0;
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(dmg);
    }

    // --- Flag helpers ---
    public boolean getFlag(byte flag) { return (this.dataManager.get(FLAGS) & flag) != 0; }
    public void setFlag(byte flag, boolean value) {
        byte b = this.dataManager.get(FLAGS);
        if (value) b |= flag; else b &= ~flag;
        this.dataManager.set(FLAGS, b);
    }

    public boolean getIsBatHanging() { return getFlag(FLAG_HANGING); }
    public void setIsBatHanging(boolean v) {
        setFlag(FLAG_HANGING, v);
        if (v) this.motionY = 0;
    }
    public boolean getIsSummoned() { return getFlag(FLAG_SUMMONED); }
    public void setIsSummoned(boolean v) {
        setFlag(FLAG_SUMMONED, v);
        if (v) {
            double dmg = (double)((this.getIsDevil() ? 3 : 2) + this.damBonus);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(dmg);
        }
    }
    public boolean getIsExplosive() { return getFlag(FLAG_EXPLOSIVE); }
    public void setIsExplosive(boolean v) { setFlag(FLAG_EXPLOSIVE, v); }
    public boolean getIsDevil() { return getFlag(FLAG_DEVIL); }
    public void setIsDevil(boolean v) { setFlag(FLAG_DEVIL, v); }
    public boolean getIsVampire() { return getFlag(FLAG_VAMPIRE); }
    public void setIsVampire(boolean v) { setFlag(FLAG_VAMPIRE, v); }

    // --- Manual decision logic (was updateEntityActionState) ---
    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        if (this.getIsBatHanging()) {
            // Hanging mode: check ceiling, wake on player proximity
            BlockPos above = new BlockPos(MathHelper.floor(this.posX), (int)this.posY + 1, MathHelper.floor(this.posZ));
            if (this.world.isAirBlock(above) || this.world.getBlockState(above).getMaterial().isLiquid()) {
                this.setIsBatHanging(false);
                this.world.playEvent(null, 1015, this.getPosition(), 0);
            } else {
                if (this.rand.nextInt(200) == 0) {
                    this.rotationYaw = (float)this.rand.nextInt(360);
                }
                if (this.world.getClosestPlayerToEntity(this, 4.0) != null) {
                    this.setIsBatHanging(false);
                    this.world.playEvent(null, 1015, this.getPosition(), 0);
                }
            }
        } else {
            // Flying mode
            if (this.getAttackTarget() == null) {
                // Summoned bats take damage over time
                if (this.getIsSummoned()) {
                    this.attackEntityFrom(DamageSource.STARVE, 2.0f);
                }
                // Refresh flight target
                if (this.currentFlightTarget == null
                    || this.rand.nextInt(30) == 0
                    || this.getDistanceSq(this.currentFlightTarget) < 4.0) {
                    this.currentFlightTarget = new BlockPos(
                        (int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7),
                        (int)this.posY + this.rand.nextInt(6) - 2,
                        (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
                }
                // Steer toward flight target
                double tx = (double)this.currentFlightTarget.getX() + 0.5 - this.posX;
                double ty = (double)this.currentFlightTarget.getY() + 0.1 - this.posY;
                double tz = (double)this.currentFlightTarget.getZ() + 0.5 - this.posZ;
                this.motionX += (Math.signum(tx) * 0.5 - this.motionX) * 0.1;
                this.motionY += (Math.signum(ty) * 0.7 - this.motionY) * 0.1;
                this.motionZ += (Math.signum(tz) * 0.5 - this.motionZ) * 0.1;
                float yaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) - 90.0f;
                this.rotationYaw += MathHelper.wrapDegrees(yaw - this.rotationYaw);
                // Hang again if ceiling available
                if (this.rand.nextInt(100) == 0) {
                    BlockPos above2 = new BlockPos(MathHelper.floor(this.posX), (int)this.posY + 1, MathHelper.floor(this.posZ));
                    if (!this.world.isAirBlock(above2) && !this.world.getBlockState(above2).getMaterial().isLiquid()) {
                        this.setIsBatHanging(true);
                    }
                }
            } else {
                // Has target: steer toward it
                double tx = this.getAttackTarget().posX - this.posX;
                double ty = this.getAttackTarget().posY + (double)(this.getAttackTarget().getEyeHeight() * 0.66f) - this.posY;
                double tz = this.getAttackTarget().posZ - this.posZ;
                this.motionX += (Math.signum(tx) * 0.5 - this.motionX) * 0.1;
                this.motionY += (Math.signum(ty) * 0.7 - this.motionY) * 0.1;
                this.motionZ += (Math.signum(tz) * 0.5 - this.motionZ) * 0.1;
                float yaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) - 90.0f;
                this.rotationYaw += MathHelper.wrapDegrees(yaw - this.rotationYaw);
                // Melee attack when close
                float dist = this.getDistance(this.getAttackTarget());
                if (this.attackCooldown <= 0 && dist < Math.max(2.5f, this.getAttackTarget().width * 1.1f)
                    && this.getAttackTarget().getEntityBoundingBox().maxY > this.getEntityBoundingBox().minY
                    && this.getAttackTarget().getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
                    this.onHitTarget(this.getAttackTarget());
                }
            }
            // Nullify creative mode target
            if (this.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.getAttackTarget()).capabilities.disableDamage) {
                this.setAttackTarget(null);
            }
        }
    }

    // --- Melee attack logic (was attackEntity(Entity, float)) ---
    protected void onHitTarget(Entity entity) {
        if (this.getIsSummoned() && entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).hurtResistantTime = 100;
        }
        if (this.getIsVampire()) {
            if (this.owner != null && !this.owner.isPotionActive(net.minecraft.init.MobEffects.REGENERATION)) {
                this.owner.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.REGENERATION, 26, 1));
            }
            this.heal(1.0f);
        }
        this.attackCooldown = 20;
        boolean explode = this.getIsExplosive() && this.rand.nextInt(10) == 0 && !this.world.isRemote && !this.getIsDevil();
        if (explode) {
            entity.hurtResistantTime = 0;
            this.world.createExplosion(this, this.posX, this.posY, this.posZ,
                1.5f + (this.getIsExplosive() ? (float)this.damBonus * 0.33f : 0.0f), false);
            this.setDead();
        } else {
            this.attackEntityAsMob(entity);
            if (!(this.getIsVampire() || this.rand.nextBoolean())) {
                entity.setFire(this.getIsSummoned() ? 4 : 2);
            }
        }
        this.playSound(SoundEvents.ENTITY_BAT_HURT, 0.5f, 0.9f + this.rand.nextFloat() * 0.2f);
    }

    // --- Per-tick living update ---
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.attackCooldown > 0) --this.attackCooldown;

        if (this.world.isRemote && this.getIsExplosive()) {
            Thaumcraft.proxy.sparkle((float)this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f,
                (float)this.posY + this.height / 2.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f,
                (float)this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f,
                1.0f, 151, 1.0f + this.rand.nextFloat() * 0.5f);
        }

        if (this.getIsBatHanging()) {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.posY = (double)MathHelper.floor(this.posY) + 1.0 - (double)this.height;
        } else {
            this.motionY *= 0.6;
        }

        if (this.world.isRemote && !this.getIsVampire()) {
            Thaumcraft.proxy.drawGenericParticles(
                this.world,
                this.lastTickPosX + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f),
                this.lastTickPosY + (double)(this.height / 2.0f) + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f),
                this.lastTickPosZ + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f),
                0.0, 0.0, 0.0,
                0.25F, 0.25F, 0.25F, 0.65F,
                false, 0, 8, -1, 8, 0, 0.5F, 1);
            Thaumcraft.proxy.drawGenericParticles(
                this.world,
                this.lastTickPosX + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f),
                this.lastTickPosY + (double)(this.height / 2.0f) + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f),
                this.lastTickPosZ + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f),
                0.0, 0.0, 0.0,
                1.0F, 1.0F, 1.0F, 0.85F,
                false, 48, 1, 1, 12, 0, 0.35F, 1);
        }

        // Explosive timeout
        if (!this.world.isRemote && this.getIsExplosive() && this.getIsSummoned()) {
            if (this.ticksExisted > 100) {
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, 1.5f + (float)this.damBonus * 0.33f, false);
                this.setDead();
            }
        }
    }

    // --- Misc overrides ---
    @Override public boolean canBePushed() { return false; }
    @Override public boolean isEntityInvulnerable(DamageSource source) {
        return this.getFlag(FLAG_VAMPIRE) && source == DamageSource.IN_WALL;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source) || source.isExplosion() || source.isFireDamage())
            return false;
        if (!this.world.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override public int getMaxSpawnedInChunk() { return 3; }
    @Override protected boolean canTriggerWalking() { return false; }

    // --- NBT ---
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.dataManager.set(FLAGS, nbt.getByte("BatFlags"));
        this.damBonus = nbt.getByte("damBonus");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("BatFlags", this.dataManager.get(FLAGS));
        nbt.setByte("damBonus", (byte)this.damBonus);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_BAT_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.ENTITY_BAT_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_BAT_DEATH; }
    @Override protected float getSoundVolume() { return 0.1f; }
    @Override protected float getSoundPitch() { return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f; }

    // --- Drops ---
    @Override
    protected Item getDropItem() {
        return this.getIsSummoned() ? null : Items.COAL;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (!this.getIsSummoned() && this.rand.nextInt(3) == 0)
            this.dropItem(Items.COAL, 1);
    }
}
