package thaumcraft.common.entities.monster;

import java.awt.Color;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXWispZap;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityWisp extends EntityFlying implements IMob {
    private static final DataParameter<String> WISP_TYPE =
        EntityDataManager.createKey(EntityWisp.class, DataSerializers.STRING);

    // Manual AI fields
    public int courseChangeCooldown = 0;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private EntityLivingBase targetedEntity = null;
    private int aggroCooldown = 0;
    public int prevAttackCounter = 0;
    public int attackCounter = 0;
    private boolean initializedType = false;

    public EntityWisp(World world) {
        super(world);
        this.setSize(0.9f, 0.9f);
        this.experienceValue = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(WISP_TYPE, "");
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(22.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
    }

    public String getWispType() { return this.dataManager.get(WISP_TYPE); }
    public void setWispType(String s) { this.dataManager.set(WISP_TYPE, s); }

    // --- Dim lighting tolerance ---
    @Override
    public int getMaxSpawnedInChunk() { return 3; }

    @Override
    public boolean getCanSpawnHere() {
        List<EntityWisp> nearby = this.world.getEntitiesWithinAABB(EntityWisp.class,
            this.getEntityBoundingBox().grow(16.0, 16.0, 16.0));
        if (nearby.size() >= 8) return false;
        return this.world.getDifficulty().getId() > 0 && super.getCanSpawnHere();
    }

    // --- Manual decision logic (was updateEntityActionState) ---
    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        // Initialize type on first tick (biome-based or random)
        if (!this.world.isRemote && !this.initializedType) {
            this.initializedType = true;
            Biome bg = this.world.getBiome(this.getPosition());
            if (bg == ThaumcraftWorldGenerator.biomeEerie) {
                switch (this.rand.nextInt(6)) {
                    case 0: this.setWispType(Aspect.DARKNESS.getTag()); break;
                    case 1: this.setWispType(Aspect.UNDEAD.getTag()); break;
                    case 2: this.setWispType(Aspect.ENTROPY.getTag()); break;
                    case 3: this.setWispType(Aspect.ELDRITCH.getTag()); break;
                    case 4: this.setWispType(Aspect.POISON.getTag()); break;
                    case 5: this.setWispType(Aspect.DEATH.getTag()); break;
                }
            } else if (this.rand.nextInt(10) != 0) {
                List<Aspect> primals = Aspect.getPrimalAspects();
                this.setWispType(primals.get(this.rand.nextInt(primals.size())).getTag());
            } else {
                List<Aspect> compounds = Aspect.getCompoundAspects();
                this.setWispType(compounds.get(this.rand.nextInt(compounds.size())).getTag());
            }
        }

        // Despawn in peaceful
        if (!this.world.isRemote && this.world.getDifficulty().getId() == 0) {
            this.setDead();
            return;
        }

        // --- Waypoint steering ---
        this.prevAttackCounter = this.attackCounter;
        double attackRange = 16.0;
        double dx = this.waypointX - this.posX;
        double dy = this.waypointY - this.posY;
        double dz = this.waypointZ - this.posZ;
        double dSq = dx * dx + dy * dy + dz * dz;
        if (dSq < 1.0 || dSq > 3600.0) {
            this.waypointX = this.posX + (double)(this.rand.nextFloat() * 2.0f - 1.0f) * 16.0;
            this.waypointY = this.posY + (double)(this.rand.nextFloat() * 2.0f - 1.0f) * 16.0;
            this.waypointZ = this.posZ + (double)(this.rand.nextFloat() * 2.0f - 1.0f) * 16.0;
        }

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown += this.rand.nextInt(5) + 2;
            double dist = MathHelper.sqrt(dSq);
            if (dist > 0.01 && this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, dist)) {
                this.motionX += dx / dist * 0.1;
                this.motionY += dy / dist * 0.1;
                this.motionZ += dz / dist * 0.1;
            } else {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        // --- Target management ---
        if (this.targetedEntity != null && !this.targetedEntity.isEntityAlive()) {
            this.targetedEntity = null;
        }
        --this.aggroCooldown;
        if (this.rand.nextInt(1000) == 0 && (this.targetedEntity == null || this.aggroCooldown-- <= 0)) {
            this.targetedEntity = this.world.getClosestPlayerToEntity(this, 16.0);
            if (this.targetedEntity != null) {
                this.aggroCooldown = 50;
            }
        }

        // --- Attack ---
        if (this.targetedEntity != null && this.getDistanceSq(this.targetedEntity) < attackRange * attackRange) {
            double d5 = this.targetedEntity.posX - this.posX;
            double d6 = this.targetedEntity.getEntityBoundingBox().minY + (double)(this.targetedEntity.height / 2.0f)
                - (this.posY + (double)(this.height / 2.0f));
            double d7 = this.targetedEntity.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = (float)(-Math.atan2(d5, d7)) * 180.0f / (float)Math.PI;
            if (this.canEntityBeSeen(this.targetedEntity)) {
                ++this.attackCounter;
                if (this.attackCounter == 20) {
                    this.playSound(TCSounds.ZAP, 1.0f, 1.1f);
                    if (!this.world.isRemote) {
                        PacketHandler.INSTANCE.sendToAllAround(
                                new PacketFXWispZap(this.getEntityId(), this.targetedEntity.getEntityId()),
                                new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                    }
                    float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                    if (Math.abs(this.targetedEntity.motionX) > 0.1
                        || Math.abs(this.targetedEntity.motionY) > 0.1
                        || Math.abs(this.targetedEntity.motionZ) > 0.1) {
                        if (this.rand.nextFloat() < 0.4f) {
                            this.targetedEntity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), damage);
                        }
                    } else if (this.rand.nextFloat() < 0.66f) {
                        this.targetedEntity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), damage + 1.0f);
                    }
                    this.attackCounter = -20 + this.rand.nextInt(20);
                    if (this.world.isRemote) {
                        Thaumcraft.proxy.burst(this.world, this.targetedEntity.posX, this.targetedEntity.posY + this.targetedEntity.height / 2.0, this.targetedEntity.posZ, 1.0f);
                    }
                }
            } else if (this.attackCounter > 0) {
                --this.attackCounter;
            }
        } else {
            this.renderYawOffset = this.rotationYaw = (float)(-Math.atan2(this.motionX, this.motionZ)) * 180.0f / (float)Math.PI;
            if (this.attackCounter > 0) --this.attackCounter;
        }
    }

    // --- Per-tick living logic ---
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        // Client particles
        if (this.world.isRemote) {
            if (this.ticksExisted <= 1) {
                Thaumcraft.proxy.burst(this.world, this.posX, this.posY + 0.45, this.posZ, 1.0f);
            }
            if (this.world.rand.nextBoolean() && Aspect.getAspect(this.getWispType()) != null) {
                Color color = new Color(Aspect.getAspect(this.getWispType()).getColor());
                Thaumcraft.proxy.wispFX(this.world,
                    this.posX + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f),
                    this.posY + 0.45 + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f),
                    this.posZ + (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f),
                    0.1f,
                    (float)color.getRed() / 255.0f,
                    (float)color.getGreen() / 255.0f,
                    (float)color.getBlue() / 255.0f);
            }
        }
    }

    // --- Death ---
    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (this.world.isRemote) {
            Thaumcraft.proxy.burst(this.world, this.posX, this.posY + 0.45, this.posZ, 1.0f);
        }
    }

    // --- Aggro on damage ---
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getImmediateSource() instanceof EntityLivingBase) {
            this.targetedEntity = (EntityLivingBase)source.getImmediateSource();
            this.aggroCooldown = 200;
        }
        if (source.getTrueSource() instanceof EntityLivingBase) {
            this.targetedEntity = (EntityLivingBase)source.getTrueSource();
            this.aggroCooldown = 200;
        }
        return super.attackEntityFrom(source, amount);
    }

    // --- NBT ---
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setString("Type", this.getWispType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setWispType(nbt.getString("Type"));
        this.initializedType = true;
    }

    // --- Drops ---
    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        Aspect wispAspect = Aspect.getAspect(this.getWispType());
        if (wispAspect != null) {
            ItemStack ess = new ItemStack(ConfigItems.itemWispEssence);
            if (ess.getItem() instanceof ItemWispEssence) {
                ((ItemWispEssence)ess.getItem()).setAspects(
                    ess,
                    new AspectList().add(wispAspect, 2)
                );
            }
            this.entityDropItem(ess, 0.0f);
        }
    }

    @Override public boolean isNotColliding() { return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty(); }

    // --- Waypoint pathability check ---
    private boolean isCourseTraversable(double wx, double wy, double wz, double dist) {
        if (dist <= 0.01) return false;
        double dx = (wx - this.posX) / dist;
        double dy = (wy - this.posY) / dist;
        double dz = (wz - this.posZ) / dist;
        AxisAlignedBB bb = this.getEntityBoundingBox();
        for (int i = 1; (double)i < dist; ++i) {
            bb = bb.offset(dx, dy, dz);
            if (!this.world.getCollisionBoxes(this, bb).isEmpty()) return false;
        }
        int x = MathHelper.floor(wx);
        int y = MathHelper.floor(wy);
        int z = MathHelper.floor(wz);
        if (this.world.getBlockState(new net.minecraft.util.math.BlockPos(x, y, z)).getMaterial().isLiquid())
            return false;
        for (int a = 0; a < 11; ++a) {
            if (!this.world.isAirBlock(new net.minecraft.util.math.BlockPos(x, y - a, z)))
                return true;
        }
        return false;
    }

    // --- Sounds ---
    @Override protected net.minecraft.util.SoundEvent getAmbientSound() {
        return TCSounds.WISPLIVE;
    }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
        return net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() {
        return TCSounds.WISPDEAD;
    }
    @Override protected float getSoundPitch() { return 0.25f; }
}
