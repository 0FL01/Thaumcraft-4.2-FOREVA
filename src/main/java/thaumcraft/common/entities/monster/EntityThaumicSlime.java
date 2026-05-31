package thaumcraft.common.entities.monster;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;

public class EntityThaumicSlime extends EntityMob implements IMob, ITaintedMob {
    private static final DataParameter<Integer> SLIME_SIZE =
        EntityDataManager.createKey(EntityThaumicSlime.class, DataSerializers.VARINT);

    // Animation fields
    public float field_70813_a;
    public float field_70811_b;
    public float field_70812_c;

    // State
    private static final int MIN_ATTACK_JUMP_DELAY_TICKS = 8;

    private int slimeJumpDelay = 0;
    public int launched = 10;
    private int spitCounter = 100;

    public EntityThaumicSlime(World world) {
        super(world);
        int initialSize = 1 << this.rand.nextInt(3);
        this.slimeJumpDelay = this.rand.nextInt(20) + 10;
        this.setSlimeSize(initialSize);
    }

    /**
     * 3-arg constructor used for spit projectile.
     */
    public EntityThaumicSlime(World world, EntityLivingBase shooter, EntityLivingBase target) {
        super(world);
        this.setSlimeSize(1);
        this.posY = (shooter.getEntityBoundingBox().minY + shooter.getEntityBoundingBox().maxY) / 2.0;
        double dx = target.posX - shooter.posX;
        double dy = target.getEntityBoundingBox().minY + (double)(target.height / 3.0f) - this.posY;
        double dz = target.posZ - shooter.posZ;
        double dist = MathHelper.sqrt(dx * dx + dz * dz);
        if (dist >= 1.0E-7) {
            float yaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
            float pitch = (float)(-(Math.atan2(dy, dist) * 180.0 / Math.PI));
            double nx = dx / dist;
            double nz = dz / dist;
            this.setLocationAndAngles(shooter.posX + nx, this.posY, shooter.posZ + nz, yaw, pitch);
            // Set velocity (was setThrowableHeading in 1.7.10)
            double len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (len > 0.0) {
                double vx = dx / len;
                double vy = (dy + (float)dist * 0.2f) / len;
                double vz = dz / len;
                vx += this.rand.nextGaussian() * 0.0075;
                vy += this.rand.nextGaussian() * 0.0075;
                vz += this.rand.nextGaussian() * 0.0075;
                this.motionX = vx * 1.5;
                this.motionY = vy * 1.5;
                this.motionZ = vz * 1.5;
            }
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SLIME_SIZE, 1);
    }

    public void setSlimeSize(int size) {
        this.dataManager.set(SLIME_SIZE, size);
        float ss = (float)Math.sqrt((double)size);
        this.setSize(0.25f * ss + 0.25f, 0.25f * ss + 0.25f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)size);
        this.setHealth(this.getMaxHealth());
        this.experienceValue = (int)ss;
    }

    public int getSlimeSize() {
        return this.dataManager.get(SLIME_SIZE);
    }

    protected int getAttackStrength() { return this.getSlimeSize(); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }

    // === Manual AI (was updateEntityActionState) ===
    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.despawnEntity();

        EntityPlayer player = this.world.getClosestPlayerToEntity(this, 16.0);
        if (player != null && !player.capabilities.disableDamage) {
            if (this.spitCounter > 0) --this.spitCounter;
            this.faceEntity(player, 10.0f, 20.0f);
            if (this.getDistance(player) > 4.0f && this.spitCounter <= 0 && this.getSlimeSize() > 3) {
                this.spitCounter = 101;
                if (!this.world.isRemote) {
                    EntityThaumicSlime spit = new EntityThaumicSlime(this.world, this, player);
                    this.world.spawnEntity(spit);
                }
                SoundEvent squish = SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.slime.squish"));
                if (squish == null) squish = SoundEvents.ENTITY_SLIME_SQUISH;
                this.playSound(squish, 1.0f,
                    ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                this.setSlimeSize(this.getSlimeSize() - 1);
            }
        } else {
            // Merge
            EntityThaumicSlime mergeTarget = this.getClosestMergableSlime();
            if (mergeTarget != null) {
                this.faceEntity(mergeTarget, 10.0f, 20.0f);
                if (this.getDistance(mergeTarget) < this.width + mergeTarget.width) {
                    mergeTarget.setSlimeSize(Math.min(100, mergeTarget.getSlimeSize() + this.getSlimeSize()));
                    this.setDead();
                }
            }
        }

        // Jump logic
        if (this.onGround && this.slimeJumpDelay-- <= 0) {
            this.slimeJumpDelay = this.getJumpDelayTicks();
            if (player != null) this.slimeJumpDelay = Math.max(MIN_ATTACK_JUMP_DELAY_TICKS, this.slimeJumpDelay / 2);
            this.getJumpHelper().setJumping();
            if (this.makesSoundOnJump()) {
                SoundEvent jumpSound = this.getSlimeSize() > 3
                    ? SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.slime.squish"))
                    : SoundEvents.ENTITY_SLIME_SQUISH;
                if (jumpSound != null) this.playSound(jumpSound, this.getSoundVolume(),
                    ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
            }
            this.moveStrafing = 1.0f - this.rand.nextFloat() * 2.0f;
            this.moveForward = (float)(1.0 * Math.sqrt((double)this.getSlimeSize()));
        } else {
            this.isJumping = false;
            if (this.onGround) {
                this.moveForward = 0.0f;
                this.moveStrafing = 0.0f;
            }
        }
    }

    // === Per-tick update ===
    @Override
    public void onUpdate() {
        if (!this.world.isRemote && this.world.getDifficulty().getId() == 0 && this.getSlimeSize() > 0) {
            this.isDead = true;
        }
        this.field_70811_b += (this.field_70813_a - this.field_70811_b) * 0.5f;
        this.field_70812_c = this.field_70811_b;
        boolean wasOnGround = this.onGround;
        super.onUpdate();
        int sizeSqrt = (int)Math.sqrt((double)this.getSlimeSize());
        if (this.launched > 0) {
            --this.launched;
            if (this.world.isRemote) {
                for (int j = 0; j < sizeSqrt * (this.launched + 1); ++j) {
                    Thaumcraft.proxy.slimeJumpFX(this, sizeSqrt);
                }
            }
        }
        if (this.onGround && !wasOnGround) {
            if (this.world.isRemote) {
                for (int j = 0; j < sizeSqrt * 8; ++j) {
                    Thaumcraft.proxy.slimeJumpFX(this, sizeSqrt);
                }
            }
            if (this.makesSoundOnLand()) {
                SoundEvent landSound = this.getSlimeSize() > 5
                    ? SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.slime.squish"))
                    : SoundEvents.ENTITY_SLIME_SQUISH;
                if (landSound != null) this.playSound(landSound, this.getSoundVolume(),
                    ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            }
            this.field_70813_a = -0.5f;
        } else if (!this.onGround && wasOnGround) {
            this.field_70813_a = 1.0f;
        }
        this.field_70813_a *= 0.6f;
        if (this.world.isRemote) {
            float ff = (float)Math.sqrt((double)this.getSlimeSize());
            this.setSize(0.6f * ff, 0.6f * ff);
        }
    }

    // === Split on death ===
    @Override
    public void setDead() {
        int size = this.getSlimeSize();
        int numSplits = (int)Math.sqrt((double)size);
        if (!this.world.isRemote && numSplits > 1 && this.getHealth() <= 0.0f) {
            for (int k = 0; k < numSplits; ++k) {
                float offsetX = ((float)(k % 2) - 0.5f) * (float)size / 4.0f;
                float offsetZ = ((float)(k / 2) - 0.5f) * (float)size / 4.0f;
                EntityThaumicSlime baby = new EntityThaumicSlime(this.world);
                baby.setSlimeSize(1);
                baby.setLocationAndAngles(
                    this.posX + (double)offsetX, this.posY + 0.5, this.posZ + (double)offsetZ,
                    this.rand.nextFloat() * 360.0f, 0.0f);
                this.world.spawnEntity(baby);
            }
        }
        super.setDead();
    }

    // === Merge helper ===
    private EntityThaumicSlime getClosestMergableSlime() {
        EntityThaumicSlime closest = null;
        double closestDist = Double.MAX_VALUE;
        List<EntityThaumicSlime> list = this.world.getEntitiesWithinAABB(EntityThaumicSlime.class,
            this.getEntityBoundingBox().grow(16.0, 8.0, 16.0));
        for (EntityThaumicSlime slime : list) {
            if (slime.getEntityId() != this.getEntityId()
                && slime.ticksExisted > 100
                && slime.getSlimeSize() < 100
                && this.getDistanceSq(slime) < closestDist) {
                closest = slime;
                closestDist = this.getDistanceSq(slime);
            }
        }
        return closest;
    }

    // === Player collision damage ===
    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.canDamagePlayer()) {
            int i = (int)Math.max(1.0, Math.sqrt((double)this.getSlimeSize()));
            if (this.launched > 0 && i == 2) i = 3;
            if (this.isEntityAlive()
                && this.getDistance(player) < 0.8 * (double)i * 0.8 * (double)i
                && player.attackEntityFrom(DamageSource.causeMobDamage(this),
                    (float)this.getAttackStrength())) {
                this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f,
                    (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            }
        }
    }

    protected boolean canDamagePlayer() { return this.getSlimeSize() > 0; }

    // === NBT ===
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Size", this.getSlimeSize() - 1);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSlimeSize(nbt.getInteger("Size") + 1);
    }

    // === Misc ===
    @Override public boolean canBeLeashedTo(EntityPlayer player) { return false; }
    @Override public int getVerticalFaceSpeed() { return 0; }

    // === Drops ===
    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.getSlimeSize() < 3 && this.rand.nextInt(3) == 0) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        }
        super.dropFewItems(wasRecentlyHit, looting);
    }

    // === Sounds ===
    private int getJumpDelayTicks() {
        return this.rand.nextInt(16) + 8;
    }

    protected boolean makesSoundOnJump() { return this.getSlimeSize() > 3; }
    protected boolean makesSoundOnLand() { return this.getSlimeSize() > 5; }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_SLIME_SQUISH; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.ENTITY_SLIME_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_SLIME_DEATH; }

    @Override
    protected float getSoundVolume() {
        return 0.1f * (float)Math.sqrt((double)this.getSlimeSize());
    }
}
