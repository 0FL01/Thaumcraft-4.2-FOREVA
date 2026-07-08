package thaumcraft.common.entities.monster;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityTaintSwarm extends EntityMob implements ITaintedMob {
    private static final byte FLAG_SUMMONED = 2;
    private static final DataParameter<Byte> FLAGS =
        EntityDataManager.createKey(EntityTaintSwarm.class, DataSerializers.BYTE);

    private BlockPos currentFlightTarget;
    private int attackCooldown;
    public int damBonus = 0;
    public final ArrayList<Object> swarm = new ArrayList<>();

    public EntityTaintSwarm(net.minecraft.world.World world) {
        super(world);
        this.setSize(2.0F, 2.0F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FLAGS, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D + this.damBonus);
    }

    public boolean getIsSummoned() {
        return (this.dataManager.get(FLAGS) & FLAG_SUMMONED) != 0;
    }

    public void setIsSummoned(boolean summoned) {
        byte flags = this.dataManager.get(FLAGS);
        if (summoned) {
            this.dataManager.set(FLAGS, (byte) (flags | FLAG_SUMMONED));
        } else {
            this.dataManager.set(FLAGS, (byte) (flags & ~FLAG_SUMMONED));
        }
        if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D + this.damBonus);
        }
    }

    @Override
    public int getBrightnessForRender() {
        return 15728880;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean isNotColliding() {
        return true;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        this.motionY *= 0.6000000238418579D;
        if (this.world.isRemote) {
            for (int i = 0; i < this.swarm.size();) {
                if (!Thaumcraft.proxy.isParticleAlive(this.swarm.get(i))) {
                    this.swarm.remove(i);
                } else {
                    i++;
                }
            }
            int maxParticles = Math.max(10, Thaumcraft.proxy.particleCount(25));
            if (this.swarm.size() < maxParticles) {
                this.swarm.add(Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.22F, 15.0F, 0.08F));
            }
        }
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        EntityLivingBase target = this.getAttackTarget();

        if (target == null) {
            if (this.getIsSummoned()) {
                this.attackEntityFrom(DamageSource.STARVE, 5.0F);
            }
            if (!this.getIsSummoned()) {
                EntityPlayer closest = this.world.getClosestPlayerToEntity(this, 12.0D);
                if (closest != null) {
                    this.setAttackTarget(closest);
                    target = closest;
                }
            }

            if (!isValidFlightTarget(this.currentFlightTarget)) {
                this.currentFlightTarget = null;
            }
            if (this.currentFlightTarget == null || this.rand.nextInt(30) == 0 || this.getDistanceSq(this.currentFlightTarget) < 4.0D) {
                this.currentFlightTarget = new BlockPos(
                    (int) this.posX + this.rand.nextInt(7) - this.rand.nextInt(7),
                    (int) this.posY + this.rand.nextInt(6) - 2,
                    (int) this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
            }

            double tx = (double) this.currentFlightTarget.getX() + 0.5D - this.posX;
            double ty = (double) this.currentFlightTarget.getY() + 0.1D - this.posY;
            double tz = (double) this.currentFlightTarget.getZ() + 0.5D - this.posZ;
            this.motionX += (Math.signum(tx) * 0.5D - this.motionX) * 0.015000000014901161D;
            this.motionY += (Math.signum(ty) * 0.699999988079071D - this.motionY) * 0.10000000149011612D;
            this.motionZ += (Math.signum(tz) * 0.5D - this.motionZ) * 0.015000000014901161D;
            float yaw = (float) (Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
            this.moveForward = 0.1F;
            this.rotationYaw += MathHelper.wrapDegrees(yaw - this.rotationYaw);
            return;
        }

        double tx = target.posX - this.posX;
        double ty = target.posY + (double) target.getEyeHeight() - this.posY;
        double tz = target.posZ - this.posZ;
        this.motionX += (Math.signum(tx) * 0.5D - this.motionX) * 0.025000000149011613D;
        this.motionY += (Math.signum(ty) * 0.699999988079071D - this.motionY) * 0.10000000149011612D;
        this.motionZ += (Math.signum(tz) * 0.5D - this.motionZ) * 0.02500000001490116D;
        float yaw = (float) (Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
        this.moveForward = 0.1F;
        this.rotationYaw += MathHelper.wrapDegrees(yaw - this.rotationYaw);

        if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage) {
            this.setAttackTarget(null);
            return;
        }

        float dist = this.getDistance(target);
        if (this.attackCooldown <= 0
            && dist < 3.0F
            && target.getEntityBoundingBox().maxY > this.getEntityBoundingBox().minY
            && target.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
            if (this.getIsSummoned()) {
                EntityUtils.setRecentlyHit(target, 100);
            }
            this.attackCooldown = 10 + this.rand.nextInt(5);
            double mx = target.motionX;
            double my = target.motionY;
            double mz = target.motionZ;
            if (this.attackEntityAsMob(target) && !this.world.isRemote) {
                target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 0));
            }
            target.velocityChanged = false;
            target.motionX = mx;
            target.motionY = my;
            target.motionZ = mz;
            this.playSound(TCSounds.SWARMATTACK, 0.3F, 0.9F + this.world.rand.nextFloat() * 0.2F);
        }
    }

    private boolean isValidFlightTarget(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        if (!this.world.isAirBlock(pos)) {
            return false;
        }
        if (pos.getY() < 1) {
            return false;
        }
        int top = this.world.getHeight(pos).getY() + 8;
        if (pos.getY() > top) {
            return false;
        }
        return this.world.getBiome(pos) == ThaumcraftWorldGenerator.biomeTaint;
    }

    @Override
    public boolean getCanSpawnHere() {
        int light = this.world.getLight(new BlockPos(this));
        int threshold = 7;
        return light <= this.rand.nextInt(threshold) && super.getCanSpawnHere();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.dataManager.set(FLAGS, nbt.getByte("Flags"));
        this.damBonus = nbt.getByte("damBonus");
        if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D + this.damBonus);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("Flags", this.dataManager.get(FLAGS));
        nbt.setByte("damBonus", (byte) this.damBonus);
    }

    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return TCSounds.SWARMATTACK; }
    @Override protected SoundEvent getDeathSound() { return TCSounds.SWARMATTACK; }
    @Override protected float getSoundVolume() { return 0.1F; }

    @Override
    protected Item getDropItem() {
        return ConfigItems.itemResource;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 11), this.height / 2.0F);
        }
    }
}
