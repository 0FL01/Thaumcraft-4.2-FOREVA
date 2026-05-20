package thaumcraft.common.entities.monster.boss;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockLoot;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.TCSounds;

public class EntityEldritchGolem extends EntityThaumcraftBoss implements thaumcraft.api.entities.IEldritchMob, net.minecraft.entity.IRangedAttackMob {
    private static final DataParameter<Boolean> HEADLESS = EntityDataManager.createKey(EntityEldritchGolem.class, DataSerializers.BOOLEAN);
    private int beamCharge = 0;
    private boolean chargingBeam = false;
    private int arcing = 0;
    private int ax = 0;
    private int ay = 0;
    private int az = 0;
    private boolean headlessAttackAdded = false;
    private int attackTimer = 0;

    public EntityEldritchGolem(net.minecraft.world.World world) {
        super(world);
        this.setSize(1.75F, 3.5F);
        this.isImmuneToFire = true;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.1, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HEADLESS, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    public boolean isHeadless() {
        return this.dataManager.get(HEADLESS);
    }

    public void setHeadless(boolean headless) {
        this.dataManager.set(HEADLESS, headless);
    }

    public void makeHeadless() {
        this.setHeadless(true);
        if (!this.headlessAttackAdded) {
            this.tasks.addTask(2, new AILongRangeAttack(this, 3.0, 1.0, 5, 5, 24.0f));
            this.headlessAttackAdded = true;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("headless", this.isHeadless());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setHeadless(compound.getBoolean("headless"));
        if (this.isHeadless()) {
            this.makeHeadless();
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        this.spawnTimer = 100;
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public float getEyeHeight() {
        return this.isHeadless() ? 3.33F : 3.0F;
    }

    @Override
    public int getTotalArmorValue() {
        return super.getTotalArmorValue() + 6;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        if (this.canEntityBeSeen(target) && !this.chargingBeam && this.beamCharge > 0) {
            this.beamCharge -= 15 + this.rand.nextInt(5);
            this.getLookHelper().setLookPosition(target.posX, target.getEntityBoundingBox().minY + (double) (target.height / 2.0F), target.posZ, 30.0F, 30.0F);
            Vec3d look = this.getLook(1.0F);
            EntityGolemOrb blast = new EntityGolemOrb(this.world, this, target, false);
            blast.posX += look.x;
            blast.posZ += look.z;
            blast.setPosition(blast.posX, blast.posY, blast.posZ);
            double d0 = target.posX + target.motionX - this.posX;
            double d1 = target.posY - this.posY - (double) (target.height / 2.0F);
            double d2 = target.posZ + target.motionZ - this.posZ;
            blast.shoot(d0, d1, d2, 0.66F, 5.0F);
            this.playSound(TCSounds.EGATTACK, 1.0F, 1.0F + this.rand.nextFloat() * 0.1F);
            if (!this.world.isRemote) {
                this.world.spawnEntity(blast);
            }
        }
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return net.minecraft.init.SoundEvents.ENTITY_IRONGOLEM_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return net.minecraft.init.SoundEvents.ENTITY_IRONGOLEM_DEATH; }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (!this.world.isRemote && amount > this.getHealth() && !this.isHeadless()) {
            this.setHeadless(true);
            this.spawnTimer = 100;
            double xx = MathHelper.cos(this.rotationYaw % 360.0F / 180.0F * (float) Math.PI) * 0.75F;
            double zz = MathHelper.sin(this.rotationYaw % 360.0F / 180.0F * (float) Math.PI) * 0.75F;
            this.world.createExplosion(this, this.posX + xx, this.posY + (double) this.getEyeHeight(), this.posZ + zz, 2.0F, false);
            this.makeHeadless();
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        if (this.attackTimer > 0) {
            return false;
        }
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);
        boolean hit = target.attackEntityFrom(net.minecraft.util.DamageSource.causeMobDamage(this), (float) this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.75F);
        if (hit) {
            target.motionY += 0.2000000059604645D;
            if (this.isHeadless()) {
                target.addVelocity(-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * 1.5F, 0.1D, MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * 1.5F);
            }
        }
        return hit;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.attackTimer > 0) {
            --this.attackTimer;
        }
        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7D && this.rand.nextInt(5) == 0) {
            BlockPos foot = new BlockPos(
                    MathHelper.floor(this.posX),
                    MathHelper.floor(this.getEntityBoundingBox().minY - 0.2D),
                    MathHelper.floor(this.posZ));
            IBlockState footState = this.world.getBlockState(foot);
            if (footState.getMaterial() != Material.AIR) {
                Thaumcraft.proxy.boreDigFx(
                        this.world,
                        this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
                        this.getEntityBoundingBox().minY + 0.1D,
                        this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
                        this.posX + 4.0D * ((double) this.rand.nextFloat() - 0.5D),
                        this.getEntityBoundingBox().minY + 0.6D,
                        this.posZ + ((double) this.rand.nextFloat() - 0.5D) * 4.0D,
                        footState,
                        null,
                        0);
            }
            if (!this.world.isRemote && footState.getBlock() instanceof BlockLoot) {
                this.world.destroyBlock(foot, true);
            }
        }
        if (!this.world.isRemote) {
            BlockPos ahead = new BlockPos(
                    MathHelper.floor(this.posX + this.motionX),
                    MathHelper.floor(this.getEntityBoundingBox().minY),
                    MathHelper.floor(this.posZ + this.motionZ));
            IBlockState aheadState = this.world.getBlockState(ahead);
            float hardness = aheadState.getBlockHardness(this.world, ahead);
            if (hardness >= 0.0F && hardness <= 0.15F) {
                this.world.destroyBlock(ahead, true);
            }
        }
    }

    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void onUpdate() {
        if (this.getSpawnTimer() > 0) {
            this.heal(2.0F);
        }
        super.onUpdate();
        if (this.world.isRemote) {
            if (this.isHeadless()) {
                this.rotationPitch = 0.0F;
                float f1 = MathHelper.cos((-this.renderYawOffset * ((float) Math.PI / 180.0F)) - (float) Math.PI);
                float f2 = MathHelper.sin((-this.renderYawOffset * ((float) Math.PI / 180.0F)) - (float) Math.PI);
                float f3 = -MathHelper.cos((-this.rotationPitch * ((float) Math.PI / 180.0F)));
                float f4 = MathHelper.sin((-this.rotationPitch * ((float) Math.PI / 180.0F)));
                Vec3d v = new Vec3d(f2 * f3, f4, f1 * f3);
                if (this.rand.nextInt(20) == 0) {
                    float a = (this.rand.nextFloat() - this.rand.nextFloat()) / 2.0F;
                    float b = (this.rand.nextFloat() - this.rand.nextFloat()) / 2.0F;
                    Thaumcraft.proxy.spark(
                            (float) (this.posX + v.x + (double) a),
                            (float) this.posY + this.getEyeHeight() - 0.25F,
                            (float) (this.posZ + v.z + (double) b),
                            0.3F,
                            0.65F + this.rand.nextFloat() * 0.1F,
                            1.0F,
                            1.0F,
                            0.8F);
                }
                Thaumcraft.proxy.drawVentParticles(
                        this.world,
                        this.posX + v.x * 0.66D,
                        (double) ((float) this.posY + this.getEyeHeight() - 0.75F),
                        this.posZ + v.z * 0.66D,
                        0.0D,
                        0.001D,
                        0.0D,
                        0x555555,
                        4.0F);
                if (this.arcing > 0) {
                    Thaumcraft.proxy.arcLightning(
                            this.world,
                            this.posX,
                            this.posY + (double) (this.height / 2.0F),
                            this.posZ,
                            this.ax + 0.5D,
                            this.ay + 1.0D,
                            this.az + 0.5D,
                            0.65F + this.rand.nextFloat() * 0.1F,
                            1.0F,
                            1.0F,
                            1.0F - (float) this.arcing / 10.0F);
                    --this.arcing;
                }
            }
        } else {
            if (this.isHeadless() && this.beamCharge <= 0) {
                this.chargingBeam = true;
            }
            if (this.isHeadless() && this.chargingBeam) {
                ++this.beamCharge;
                this.world.setEntityState(this, (byte) 19);
                if (this.beamCharge == 150) {
                    this.chargingBeam = false;
                }
            }
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
            this.playSound(net.minecraft.init.SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
        } else if (id == 19) {
            if (this.arcing == 0) {
                float radius = 2.0F + this.rand.nextFloat() * 2.0F;
                double radians = Math.toRadians(this.rand.nextInt(360));
                double deltaX = radius * Math.cos(radians);
                double deltaZ = radius * Math.sin(radians);
                int bx = MathHelper.floor(this.posX + deltaX);
                int by = MathHelper.floor(this.posY);
                int bz = MathHelper.floor(this.posZ + deltaZ);
                int c = 0;
                while (c < 5 && this.world.isAirBlock(new BlockPos(bx, by, bz))) {
                    ++c;
                    --by;
                }
                if (this.world.isAirBlock(new BlockPos(bx, by + 1, bz)) && !this.world.isAirBlock(new BlockPos(bx, by, bz))) {
                    this.ax = bx;
                    this.ay = by;
                    this.az = bz;
                    this.arcing = 8 + this.rand.nextInt(5);
                    this.world.playSound(
                            this.posX,
                            this.posY,
                            this.posZ,
                            TCSounds.JACOBS,
                            SoundCategory.HOSTILE,
                            0.8F,
                            1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F,
                            false);
                }
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }
}
