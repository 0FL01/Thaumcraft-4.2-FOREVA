package thaumcraft.common.entities.monster;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.ai.misc.AIAltarFocus;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.TCSounds;

public class EntityCultistCleric extends EntityCultist implements IRangedAttackMob, IEntityAdditionalSpawnData {

    private static final DataParameter<Boolean> RITUALIST = EntityDataManager.createKey(EntityCultistCleric.class, DataSerializers.BOOLEAN);

    public EntityCultistCleric(World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIAltarFocus(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 2.0, 1.0, 20, 40, 24.0f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
        this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, true, false, (com.google.common.base.Predicate<EntityPlayer>) null));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        this.setEquipmentBasedOnDifficulty(difficulty);
        return data;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ConfigItems.itemHelmetCultistRobe));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ConfigItems.itemChestCultistRobe));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ConfigItems.itemLegsCultistRobe));
        float bootsChance = this.world.getDifficulty() == EnumDifficulty.HARD ? 0.3F : 0.1F;
        if (this.rand.nextFloat() < bootsChance) {
            this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ConfigItems.itemCultistBoots));
        }
    }

    // ------------------------------------------------------------------
    // Data watcher
    // ------------------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(RITUALIST, false);
    }

    public boolean getIsRitualist() {
        return this.dataManager.get(RITUALIST);
    }

    public void setIsRitualist(boolean value) {
        this.dataManager.set(RITUALIST, value);
    }

    // ------------------------------------------------------------------
    // NBT persistence
    // ------------------------------------------------------------------

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setIsRitualist(compound.getBoolean("Ritualist"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Ritualist", this.getIsRitualist());
    }

    // ------------------------------------------------------------------
    // Spawn data (home position sync for AI movement restriction)
    // ------------------------------------------------------------------

    @Override
    public void writeSpawnData(ByteBuf buf) {
        BlockPos home = this.getHomePosition();
        buf.writeInt(home.getX());
        buf.writeInt(home.getY());
        buf.writeInt(home.getZ());
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.setHomePosAndDistance(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), 8);
    }

    // ------------------------------------------------------------------
    // Ranged attack — 66% GolemOrb (homing), 33% triple fireball
    // ------------------------------------------------------------------

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        double dx = target.posX - this.posX;
        double dy = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F)
            - (this.posY + (double) (this.height / 2.0F));
        double dz = target.posZ - this.posZ;

        this.swingArm(EnumHand.MAIN_HAND);

        if (this.rand.nextFloat() > 0.66F) {
            // 33%: homing GolemOrb
            EntityGolemOrb blast = new EntityGolemOrb(this.world, this, target, true);
            // Adjust position by half the initial velocity (0 at construction time — kept for parity)
            blast.posX += blast.motionX / 2.0D;
            blast.posZ += blast.motionZ / 2.0D;
            blast.setPosition(blast.posX, blast.posY, blast.posZ);
            blast.shoot(dx, dy + 2.0D, dz, 0.66F, 3.0F);
            this.playSound(TCSounds.EGATTACK, 1.0F, 1.0F + this.rand.nextFloat() * 0.1F);
            this.world.spawnEntity(blast);
        } else {
            // 66%: triple fireball
            float spread = MathHelper.sqrt(distance) * 0.5F;
            this.world.playEvent((EntityPlayer) null, 1009, new BlockPos(this), 0);
            for (int i = 0; i < 3; i++) {
                EntitySmallFireball fireball = new EntitySmallFireball(
                    this.world, this,
                    dx + this.rand.nextGaussian() * (double) spread,
                    dy,
                    dz + this.rand.nextGaussian() * (double) spread
                );
                fireball.posY = this.posY + (double) (this.height / 2.0F) + 0.5D;
                this.world.spawnEntity(fireball);
            }
        }
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    // ------------------------------------------------------------------
    // Overrides
    // ------------------------------------------------------------------

    @Override
    public boolean isPreventingPlayerRest(EntityPlayer player) {
        return !this.getIsRitualist();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.getIsInvulnerable()) {
            return false;
        }
        this.setIsRitualist(false);
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // Ritualist rotation animation (client-only)
        if (this.world.isRemote && this.getIsRitualist()) {
            BlockPos home = this.getHomePosition();
            double dx = (double) home.getX() + 0.5D - this.posX;
            double dy = (double) home.getY() + 1.5D - (this.posY + (double) this.getEyeHeight());
            double dz = (double) home.getZ() + 0.5D - this.posZ;
            double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
            float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
            float pitch = (float) (-(MathHelper.atan2(dy, horizDist) * 180.0D / Math.PI));
            this.rotationPitch = this.updateRotation(this.rotationPitch, pitch, 10.0F);
            this.renderYawOffset = this.updateRotation(this.renderYawOffset, yaw, (float) this.getVerticalFaceSpeed());
        }
    }

    @Override
    public SoundEvent getAmbientSound() {
        return TCSounds.CHANT;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 500;
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    private float updateRotation(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        if (delta > maxDelta) delta = maxDelta;
        if (delta < -maxDelta) delta = -maxDelta;
        return current + delta;
    }
}
