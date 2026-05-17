package thaumcraft.common.entities.monster;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXSonic;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;

public class EntityEldritchGuardian extends EntityMob implements IRangedAttackMob, IEldritchMob {

    public float armLiftL = 0.0F;
    public float armLiftR = 0.0F;
    private boolean lastBlast = false;

    public EntityEldritchGuardian(World world) {
        super(world);
        if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround) this.getNavigator()).setCanSwim(true);
        }
        this.setSize(0.8F, 2.25F);
        this.experienceValue = 20;
        // AI tasks
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 8.0, 1.0, 20, 40, 24.0F));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        // Target tasks
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, null));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, 0, true, false, null));
    }

    @Override
    public void setSwingingArms(boolean swinging) {
        // Used by IRangedAttackMob — no-op for now
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ARM_LIFT_L, 0);
        this.dataManager.register(ARM_LIFT_R, 0);
        this.dataManager.register(LAST_BLAST, (byte) 0);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 4;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (source.isMagicDamage()) {
            damage /= 2.0F;
        }
        return super.attackEntityFrom(source, damage);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            // Client-side: arm animation decay
            if (this.armLiftL > 0.0F) this.armLiftL -= 0.05F;
            if (this.armLiftR > 0.0F) this.armLiftR -= 0.05F;
            float x = (float) (this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            float z = (float) (this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            Thaumcraft.proxy.wispFXEG(this.world, x, this.posY + 0.22D * this.height, z, this);
        } else if (this.world.provider.getDimension() != Config.dimensionOuterId
                && (this.ticksExisted == 0 || this.ticksExisted % 100 == 0)
                && this.world.getDifficulty() != EnumDifficulty.EASY) {
            // Pulse fog effect to nearby players
            double range = this.world.getDifficulty() == EnumDifficulty.HARD ? 576.0 : 256.0;
            for (EntityPlayer player : this.world.playerEntities) {
                if (!player.isEntityAlive()) continue;
                double dist = player.getDistanceSq(this.posX, this.posY, this.posZ);
                if (dist < range && player instanceof EntityPlayerMP) {
                    PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((short) 2), (EntityPlayerMP) player);
                }
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag && entityIn instanceof EntityLivingBase) {
            int difficulty = this.world.getDifficulty().getId();
            if (this.getHeldItemMainhand().isEmpty()
                    && this.isBurning()
                    && this.rand.nextFloat() < (float) difficulty * 0.3F) {
                entityIn.setFire(2 * difficulty);
            }
        }
        return flag;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.EGIDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GUARDIAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.EGDEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.5F;
    }

    @Override
    public int getExperiencePoints(EntityPlayer player) {
        return 500;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (this.rand.nextBoolean()) {
            ItemStack ess = new ItemStack(ConfigItems.itemWispEssence);
            ((ItemWispEssence) ConfigItems.itemWispEssence).setAspects(ess, new AspectList().add(Aspect.UNDEAD, 2));
            this.entityDropItem(ess, 1.0F);
        }
        if (this.rand.nextBoolean()) {
            ItemStack ess = new ItemStack(ConfigItems.itemWispEssence);
            ((ItemWispEssence) ConfigItems.itemWispEssence).setAspects(ess, new AspectList().add(Aspect.ELDRITCH, 2));
            this.entityDropItem(ess, 1.0F);
        }
        super.dropFewItems(wasRecentlyHit, lootingModifier);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        this.dropItem(ConfigItems.itemEldritchObject, 1);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.hasHome()) {
            nbt.setInteger("HomeD", (int) this.getMaximumHomeDistance());
            nbt.setInteger("HomeX", this.getHomePosition().getX());
            nbt.setInteger("HomeY", this.getHomePosition().getY());
            nbt.setInteger("HomeZ", this.getHomePosition().getZ());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("HomeD")) {
            this.setHomePosAndDistance(
                new net.minecraft.util.math.BlockPos(
                    nbt.getInteger("HomeX"),
                    nbt.getInteger("HomeY"),
                    nbt.getInteger("HomeZ")),
                nbt.getInteger("HomeD"));
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        if (this.world.provider.getDimension() == Config.dimensionOuterId) {
            IAttributeInstance maxHealth = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            double bonusHP = maxHealth.getBaseValue() / 2.0D;
            maxHealth.setBaseValue(maxHealth.getBaseValue() + bonusHP);
            this.setHealth(this.getHealth() + (float) bonusHP);
        }
        return data;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        // Passive HP regen in Outer Lands
        if (this.world.provider.getDimension() == Config.dimensionOuterId
                && this.hurtResistantTime <= 0
                && this.ticksExisted % 25 == 0) {
            int halfHP = (int) this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
            if (this.getHealth() < (float) halfHP) {
                this.heal(1.0F);
            }
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 15) {
            this.armLiftL = 0.5F;
        } else if (id == 16) {
            this.armLiftR = 0.5F;
        } else if (id == 17) {
            this.armLiftL = 0.9F;
            this.armLiftR = 0.9F;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected boolean isMovementBlocked() {
        return !this.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public float getEyeHeight() {
        return 2.1F;
    }

    @Override
    public boolean getCanSpawnHere() {
        java.util.List<EntityEldritchGuardian> nearby = this.world.getEntitiesWithinAABB(
            EntityEldritchGuardian.class,
            this.getEntityBoundingBox().grow(32.0, 16.0, 32.0));
        return nearby.size() <= 1 && super.getCanSpawnHere();
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        if (this.rand.nextFloat() > 0.1F) {
            // 90%: fire Eldritch Orb
            EntityEldritchOrb blast = new EntityEldritchOrb(this.world, this);
            this.lastBlast = !this.lastBlast;
            this.world.setEntityState(this, this.lastBlast ? (byte) 16 : (byte) 15);

            int rr = this.lastBlast ? 90 : 180;
            double xx = MathHelper.cos((this.rotationYaw + (float) rr) % 360.0F / 180.0F * (float) Math.PI) * 0.5;
            double yy = 0.057777777 * (double) this.height;
            double zz = MathHelper.sin((this.rotationYaw + (float) rr) % 360.0F / 180.0F * (float) Math.PI) * 0.5;

            blast.setPosition(blast.posX - xx, blast.posY - yy, blast.posZ - zz);

            double d0 = target.posX + target.motionX - this.posX;
            double d1 = target.posY - this.posY - (double) (target.height / 2.0F);
            double d2 = target.posZ + target.motionZ - this.posZ;

            blast.shoot(d0, d1, d2, 1.0F, 2.0F);
            this.playSound(TCSounds.EGATTACK, 2.0F, 1.0F + this.rand.nextFloat() * 0.1F);
            this.world.spawnEntity(blast);
        } else if (this.canEntityBeSeen(target)) {
            // 10%: sonic screech
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXSonic(this.getEntityId()),
                    new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));

            target.addPotionEffect(new PotionEffect(
                net.minecraft.init.MobEffects.BLINDNESS, 400, 0));

            if (target instanceof EntityPlayer) {
                Thaumcraft.addWarpToPlayer(
                    (EntityPlayer) target, 1 + this.rand.nextInt(3), true);
            }
            this.playSound(TCSounds.EGSCREECH, 3.0F, 1.0F + this.rand.nextFloat() * 0.1F);
        }
    }

    // Data watcher keys
    private static final DataParameter<Integer> ARM_LIFT_L =
        EntityDataManager.createKey(EntityEldritchGuardian.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ARM_LIFT_R =
        EntityDataManager.createKey(EntityEldritchGuardian.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> LAST_BLAST =
        EntityDataManager.createKey(EntityEldritchGuardian.class, DataSerializers.BYTE);
}
