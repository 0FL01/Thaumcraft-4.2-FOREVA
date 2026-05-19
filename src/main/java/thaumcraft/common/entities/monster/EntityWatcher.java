package thaumcraft.common.entities.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.ai.misc.AIWander;

/**
 * Thaumcraft Watcher — floating eyeball mob with gaze attack.
 * Ported from 1.7.10 original (vanilla Guardian AI model adapted by Azanor).
 * Inner classes: AIGuardianAttack, GuardianMoveHelper, GuardianLookHelper.
 */
public class EntityWatcher extends EntityMob {

    // Data watcher keys
    private static final DataParameter<Byte> FLAGS = EntityDataManager.createKey(EntityWatcher.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> TARGET_ENTITY_ID = EntityDataManager.createKey(EntityWatcher.class, DataSerializers.VARINT);
    private static final int FLAG_GAZING = 2;   // bit 1
    private static final int FLAG_ELDER = 4;    // bit 2 (unused in watcher, preserved for parity)

    // Client animation fields
    private float tailAngle;
    private float prevTailAngle;
    private float tailSpeed;
    private float prevFinAngle;
    private float finAngle;
    private EntityLivingBase targetedEntity;
    private int clientGazeTicks;

    // Custom classes
    private AIWander wanderAI;
    private GuardianMoveHelper watcherMoveHelper;
    private GuardianLookHelper watcherLookHelper;

    public EntityWatcher(World world) {
        super(world);
        this.experienceValue = 10;
        this.setSize(0.85F, 0.85F);
        this.isImmuneToFire = true;

        // Tasks
        this.tasks.addTask(4, new AIGuardianAttack());
        EntityAIMoveTowardsRestriction moveRestrict = new EntityAIMoveTowardsRestriction(this, 1.0D);
        this.tasks.addTask(5, moveRestrict);
        this.wanderAI = new AIWander(this, 1.0D);
        this.tasks.addTask(7, this.wanderAI);
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityWatcher.class, 12.0F, 0.01F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.wanderAI.setMutexBits(3);
        moveRestrict.setMutexBits(3);

        // Target tasks
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, (com.google.common.base.Predicate<EntityLivingBase>) null));

        // Custom look/move helpers
        this.watcherLookHelper = new GuardianLookHelper(this);
        this.watcherMoveHelper = new GuardianMoveHelper();
        this.moveHelper = this.watcherMoveHelper;
        this.tailAngle = this.prevTailAngle = this.rand.nextFloat();
    }

    @Override
    public EntityLookHelper getLookHelper() {
        return this.watcherLookHelper != null ? this.watcherLookHelper : super.getLookHelper();
    }

    @Override
    public EntityMoveHelper getMoveHelper() {
        return this.watcherMoveHelper != null ? this.watcherMoveHelper : super.getMoveHelper();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FLAGS, (byte) 0);
        this.dataManager.register(TARGET_ENTITY_ID, 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    // ------------------------------------------------------------------
    // Data watcher helpers
    // ------------------------------------------------------------------

    private boolean getFlagBit(int bit) {
        return (this.dataManager.get(FLAGS) & bit) != 0;
    }

    private void setFlagBit(int bit, boolean value) {
        byte flags = this.dataManager.get(FLAGS);
        if (value) {
            this.dataManager.set(FLAGS, (byte) (flags | bit));
        } else {
            this.dataManager.set(FLAGS, (byte) (flags & ~bit));
        }
    }

    public boolean isGazing() {
        return this.getFlagBit(FLAG_GAZING);
    }

    private void setGazing(boolean gazing) {
        this.setFlagBit(FLAG_GAZING, gazing);
    }

    public boolean isElder() {
        return this.getFlagBit(FLAG_ELDER);
    }

    public int getGazeDuration() {
        return this.isElder() ? 60 : 80;
    }

    public boolean hasTargetedEntity() {
        return this.dataManager.get(TARGET_ENTITY_ID) != 0;
    }

    public EntityLivingBase getTargetedEntity() {
        if (!this.hasTargetedEntity()) return null;

        if (this.world.isRemote) {
            if (this.targetedEntity != null) return this.targetedEntity;
            Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY_ID));
            if (entity instanceof EntityLivingBase) {
                this.targetedEntity = (EntityLivingBase) entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getAttackTarget();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (FLAGS.equals(key)) {
            if (this.isElder() && this.width < 1.0F) {
                this.setSize(1.9975F, 1.9975F);
            }
        } else if (TARGET_ENTITY_ID.equals(key)) {
            this.clientGazeTicks = 0;
            this.targetedEntity = null;
        }
    }

    // ------------------------------------------------------------------
    // Sounds
    // ------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
    }

    // ------------------------------------------------------------------
    // Overrides
    // ------------------------------------------------------------------

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.5F;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return this.world.canBlockSeeSky(pos) ? 10.0F : super.getBlockPathWeight(pos);
    }

    @Override
    public void onLivingUpdate() {
        // Client animations
        if (this.world.isRemote) {
            this.prevTailAngle = this.tailAngle;
            this.prevFinAngle = this.finAngle;

            // Tail speed and angle
            this.tailSpeed = this.isGazing()
                ? (this.tailSpeed < 0.5F ? 4.0F : this.tailSpeed + (0.5F - this.tailSpeed) * 0.1F)
                : (this.tailSpeed += (0.125F - this.tailSpeed) * 0.2F);
            this.tailAngle += this.tailSpeed;

            // Fin angle (closes when gazing)
            this.finAngle = this.isGazing()
                ? (this.finAngle += (0.0F - this.finAngle) * 0.25F)
                : (this.finAngle += (1.0F - this.finAngle) * 0.06F);

            // Idle bubble particles
            if (this.isGazing()) {
                Vec3d lookVec = this.getLook(0.0F);
                for (int i = 0; i < 2; i++) {
                    Thaumcraft.proxy.drawGenericParticles(
                            this.world,
                            this.posX + (this.rand.nextDouble() - 0.5) * (double) this.width - lookVec.x * 1.5,
                            this.posY + this.rand.nextDouble() * (double) this.height - lookVec.y * 1.5,
                            this.posZ + (this.rand.nextDouble() - 0.5) * (double) this.width - lookVec.z * 1.5,
                            0.0, 0.02, 0.0,
                            1.0F, 1.0F, 1.0F, 1.0F,
                            false, 32, 1, 1, 8, 0, 0.2F, 1);
                }
            }

            // Gaze beam particle trail
            if (this.hasTargetedEntity()) {
                if (this.clientGazeTicks < this.getGazeDuration()) {
                    this.clientGazeTicks++;
                }
                EntityLivingBase target = this.getTargetedEntity();
                if (target != null) {
                    this.getLookHelper().setLookPositionWithEntity(target, 90.0F, 90.0F);
                    this.getLookHelper().onUpdateLook();
                    double progress = this.getGazeProgress(0.0F);
                    double dx = target.posX - this.posX;
                    double dy = target.posY + (double) (target.height * 0.5F) - (this.posY + (double) this.getEyeHeight());
                    double dz = target.posZ - this.posZ;
                    double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                    dx /= dist;
                    dy /= dist;
                    dz /= dist;
                    double d = this.rand.nextDouble();
                    while (d < dist) {
                        d += 1.8D - progress + this.rand.nextDouble() * (1.7D - progress);
                        Thaumcraft.proxy.drawGenericParticles(
                                this.world,
                                this.posX + dx * d,
                                this.posY + dy * d + (double) this.getEyeHeight(),
                                this.posZ + dz * d,
                                0.0, 0.02, 0.0,
                                1.0F, 1.0F, 1.0F, 1.0F,
                                false, 32, 1, 1, 8, 0, 0.2F, 1);
                    }
                }
            }
        }

        // Server: keep rotation synced with renderYawOffset when gazing
        if (this.hasTargetedEntity()) {
            this.rotationYaw = this.renderYawOffset;
        }

        super.onLivingUpdate();
        if (this.watcherLookHelper != null) {
            this.watcherLookHelper.onUpdateLook();
        }
    }

    public float getTailAngle(float partialTicks) {
        return this.prevTailAngle + (this.tailAngle - this.prevTailAngle) * partialTicks;
    }

    public float getFinAngle(float partialTicks) {
        return this.prevFinAngle + (this.finAngle - this.prevFinAngle) * partialTicks;
    }

    public float getGazeProgress(float partialTicks) {
        return ((float) this.clientGazeTicks + partialTicks) / (float) this.getGazeDuration();
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        // Elder watcher home range (not used currently, kept for parity)
        if (this.isElder() && !this.hasHome()) {
            this.setHomePosAndDistance(new BlockPos(this), 16);
        }
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        // Thorns retaliation when not gazing
        if (!this.isGazing()
            && !source.isUnblockable()
            && source.getImmediateSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) source.getImmediateSource();
            if (!source.isProjectile()) {
                attacker.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
                attacker.playSound(SoundEvents.ENCHANT_THORNS_HIT, 0.5F, 1.0F);
            }
        }
        this.wanderAI.setWander();
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 180;
    }

    @Override
    public void travel(float strafe, float forward, float friction) {
        this.moveRelative(strafe, forward, friction, 0.1F);
        this.move(net.minecraft.entity.MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9D;
        this.motionY *= 0.9D;
        this.motionZ *= 0.9D;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }

    // ------------------------------------------------------------------
    // Custom look helper
    // ------------------------------------------------------------------

    class GuardianLookHelper extends EntityLookHelper {
        public GuardianLookHelper(EntityLiving entity) {
            super(entity);
        }

        @Override
        public void onUpdateLook() {
            // Uses public getters from EntityLookHelper (1.12.2 MCP)
            if (this.getIsLooking()) {
                // delegate to super
            }
            super.onUpdateLook();
        }
    }

    // ------------------------------------------------------------------
    // Custom move helper — floating sinusoidal motion
    // ------------------------------------------------------------------

    class GuardianMoveHelper extends EntityMoveHelper {
        private final EntityWatcher watcher = EntityWatcher.this;

        public GuardianMoveHelper() {
            super(EntityWatcher.this);
        }

        @Override
        public void onUpdateMoveHelper() {
            if (this.isUpdating() && !this.watcher.getNavigator().noPath()) {
                // posX/posY/posZ are protected in EntityMoveHelper (1.12.2) — direct access
                double dx = this.posX - this.watcher.posX;
                double dy = this.posY - this.watcher.posY;
                double dz = this.posZ - this.watcher.posZ;
                double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                dy /= dist;
                float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
                this.watcher.rotationYaw = this.limitAngle(this.watcher.rotationYaw, yaw, 30.0F);                float speed = (float) (this.getSpeed() * this.watcher.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                this.watcher.setAIMoveSpeed(this.watcher.getAIMoveSpeed() + (speed - this.watcher.getAIMoveSpeed()) * 0.125F);
                double h = Math.sin((double) (this.watcher.ticksExisted + this.watcher.getEntityId()) * 0.5D) * 0.05D;
                double cosYaw = Math.cos(this.watcher.rotationYaw * (float) Math.PI / 180.0F);
                double sinYaw = Math.sin(this.watcher.rotationYaw * (float) Math.PI / 180.0F);
                this.watcher.motionX += h * cosYaw;
                this.watcher.motionZ += h * sinYaw;
                h = Math.sin((double) (this.watcher.ticksExisted + this.watcher.getEntityId()) * 0.75D) * 0.05D;
                this.watcher.motionY += h * (sinYaw + cosYaw) * 0.25D;
                this.watcher.motionY += (double) this.watcher.getAIMoveSpeed() * dy * 0.1D;

                // Look helper targeting
                GuardianLookHelper lookHelper = (GuardianLookHelper) this.watcher.getLookHelper();
                double targetX = this.watcher.posX + dx / dist * 2.0D;
                double targetY = (double) this.watcher.getEyeHeight() + this.watcher.posY + dy / dist * 1.0D;
                double targetZ = this.watcher.posZ + dz / dist * 2.0D;
                double lx = lookHelper.getLookPosX();
                double ly = lookHelper.getLookPosY();
                double lz = lookHelper.getLookPosZ();
                if (!lookHelper.getIsLooking()) {
                    lx = targetX;
                    ly = targetY;
                    lz = targetZ;
                }
                lookHelper.setLookPosition(
                    lx + (targetX - lx) * 0.125D,
                    ly + (targetY - ly) * 0.125D,
                    lz + (targetZ - lz) * 0.125D,
                    10.0F, 40.0F
                );
                this.watcher.setGazing(true);
            } else {
                this.watcher.setAIMoveSpeed(0.0F);
                this.watcher.setGazing(false);
            }
        }

    }

    // ------------------------------------------------------------------
    // Gaze attack AI
    // ------------------------------------------------------------------

    class AIGuardianAttack extends EntityAIBase {
        private int attackTimer;

        public AIGuardianAttack() {
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = EntityWatcher.this.getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting()
                && (EntityWatcher.this.isElder()
                    || EntityWatcher.this.getDistanceSq(EntityWatcher.this.getAttackTarget()) > 9.0D);
        }

        @Override
        public void startExecuting() {
            this.attackTimer = -10;
            EntityWatcher.this.getNavigator().clearPath();
            EntityWatcher.this.getLookHelper().setLookPositionWithEntity(
                EntityWatcher.this.getAttackTarget(), 90.0F, 90.0F);
            EntityWatcher.this.setNoAI(false);
        }

        @Override
        public void resetTask() {
            EntityWatcher.this.dataManager.set(TARGET_ENTITY_ID, 0);
            EntityWatcher.this.setAttackTarget(null);
            EntityWatcher.this.wanderAI.setWander();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = EntityWatcher.this.getAttackTarget();
            EntityWatcher.this.getNavigator().clearPath();
            EntityWatcher.this.getLookHelper().setLookPositionWithEntity(target, 90.0F, 90.0F);

            if (!EntityWatcher.this.canEntityBeSeen(target)) {
                EntityWatcher.this.setAttackTarget(null);
            } else {
                this.attackTimer++;

                if (this.attackTimer == 0) {
                    // Broadcast target entity ID to client + trigger byte 21 status
                    EntityWatcher.this.dataManager.set(TARGET_ENTITY_ID, target.getEntityId());
                    EntityWatcher.this.world.setEntityState(EntityWatcher.this, (byte) 21);
                } else if (this.attackTimer >= EntityWatcher.this.getGazeDuration()) {
                    // Apply damage
                    float bonus = 1.0F;
                    if (EntityWatcher.this.world.getDifficulty() == EnumDifficulty.HARD) {
                        bonus += 2.0F;
                    }
                    if (EntityWatcher.this.isElder()) {
                        bonus += 2.0F;
                    }
                    target.attackEntityFrom(
                        DamageSource.causeIndirectMagicDamage(EntityWatcher.this, null), bonus);
                    target.attackEntityFrom(
                        DamageSource.causeIndirectDamage(EntityWatcher.this, EntityWatcher.this),
                        (float) EntityWatcher.this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                    EntityWatcher.this.setAttackTarget(null);
                }

                super.updateTask();
            }
        }
    }
}
