package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFrostShard extends EntityThrowable implements IEntityAdditionalSpawnData {
    public double bounce = 0.5;
    public int bounceLimit = 3;
    public boolean fragile = false;

    private static final DataParameter<Float> DAMAGE =
        EntityDataManager.createKey(EntityFrostShard.class, DataSerializers.FLOAT);
    private static final DataParameter<Byte> FROSTY =
        EntityDataManager.createKey(EntityFrostShard.class, DataSerializers.BYTE);

    public EntityFrostShard(World world) { super(world); }
    public EntityFrostShard(World world, EntityLivingBase shooter, float scatter) {
        super(world, shooter);
        this.shoot(this.motionX, this.motionY, this.motionZ, 1.5F, scatter);
    }

    @Override
    protected float getGravityVelocity() { return this.fragile ? 0.015f : 0.05f; }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DAMAGE, 0.0f);
        this.dataManager.register(FROSTY, (byte) 0);
    }

    public void setDamage(float d) {
        this.dataManager.set(DAMAGE, d);
        this.setSize(0.15f + d * 0.15f, 0.15f + d * 0.15f);
    }
    public float getDamage() { return this.dataManager.get(DAMAGE); }
    public void setFrosty(int f) { this.dataManager.set(FROSTY, (byte)f); }
    public int getFrosty() { return this.dataManager.get(FROSTY); }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // Rotation
        float h = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
        this.rotationPitch = (float)(Math.atan2(this.motionY, (double)h) * 180.0 / Math.PI);
        // Client sparkle FX if frosty
        if (this.world.isRemote && this.getFrosty() > 0) {
            float s = this.getDamage() / 10.0f;
            for (int a = 0; a < this.getFrosty(); ++a) {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                    this.posX - (double)s + (double)(this.rand.nextFloat() * s * 2.0f),
                    this.posY - (double)s + (double)(this.rand.nextFloat() * s * 2.0f),
                    this.posZ - (double)s + (double)(this.rand.nextFloat() * s * 2.0f),
                    0.0, 0.0, 0.0,
                    Block.getIdFromBlock(Blocks.PACKED_ICE));
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;

        // Bounce physics: reverse velocity on hit side
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            int ox = MathHelper.floor(this.posX) - MathHelper.floor(result.entityHit.posX);
            int oy = MathHelper.floor(this.posY) - MathHelper.floor(result.entityHit.posY);
            int oz = MathHelper.floor(this.posZ) - MathHelper.floor(result.entityHit.posZ);
            if (oz != 0) this.motionZ *= -1.0;
            if (ox != 0) this.motionX *= -1.0;
            if (oy != 0) this.motionY *= -0.9;
            this.motionX *= 0.66;
            this.motionY *= 0.66;
            this.motionZ *= 0.66;
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            EnumFacing side = result.sideHit;
            if (side.getAxis() == EnumFacing.Axis.Z) this.motionZ *= -1.0;
            if (side.getAxis() == EnumFacing.Axis.X) this.motionX *= -1.0;
            if (side.getAxis() == EnumFacing.Axis.Y) this.motionY *= -0.9;
        }

        this.motionX *= this.bounce;
        this.motionY *= this.bounce;
        this.motionZ *= this.bounce;

        float speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        if (speed > 0.01f) {
            this.posX -= this.motionX / (double)speed * 0.05;
            this.posY -= this.motionY / (double)speed * 0.05;
            this.posZ -= this.motionZ / (double)speed * 0.05;
        }
        this.velocityChanged = true;

        // Server-side: damage + slowness
        if (!this.world.isRemote && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            double mx = result.entityHit.motionX;
            double my = result.entityHit.motionY;
            double mz = result.entityHit.motionZ;
            result.entityHit.attackEntityFrom(
                DamageSource.causeIndirectDamage(this, this.getThrower()),
                this.getDamage());
            if (result.entityHit instanceof EntityLivingBase && this.getFrosty() > 0) {
                ((EntityLivingBase)result.entityHit).addPotionEffect(
                    new PotionEffect(MobEffects.SLOWNESS, 200, this.getFrosty() - 1));
            }
            // Fragile: shatter on hit
            if (this.fragile) {
                result.entityHit.hurtResistantTime = 0;
                this.setDead();
                SoundEvent iceBreak = SoundEvent.REGISTRY.getObject(new ResourceLocation("block.glass.break"));
                if (iceBreak == null) iceBreak = SoundEvent.REGISTRY.getObject(new ResourceLocation("block.stone.break"));
                if (iceBreak != null) this.playSound(iceBreak, 0.3f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                result.entityHit.motionX = mx + (result.entityHit.motionX - mx) / 10.0;
                result.entityHit.motionY = my + (result.entityHit.motionY - my) / 10.0;
                result.entityHit.motionZ = mz + (result.entityHit.motionZ - mz) / 10.0;
            }
        }

        // Bounce limit
        if (this.bounceLimit-- <= 0) {
            this.setDead();
            SoundEvent iceBreak = SoundEvent.REGISTRY.getObject(new ResourceLocation("block.glass.break"));
            if (iceBreak == null) iceBreak = SoundEvent.REGISTRY.getObject(new ResourceLocation("block.stone.break"));
            if (iceBreak != null) this.playSound(iceBreak, 0.3f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
            if (this.world.isRemote) {
                for (int a = 0; (float)a < 8.0f * this.getDamage(); ++a) {
                    this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        this.posX, this.posY, this.posZ,
                        4.0 * ((double)this.rand.nextFloat() - 0.5), 0.5,
                        ((double)this.rand.nextFloat() - 0.5) * 4.0,
                        Block.getIdFromBlock(Blocks.PACKED_ICE));
                }
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setDamage(nbt.getFloat("damage"));
        this.fragile = nbt.getBoolean("fragile");
        this.setFrosty(nbt.getInteger("frost"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("damage", this.getDamage());
        nbt.setBoolean("fragile", this.fragile);
        nbt.setInteger("frost", this.getFrosty());
    }

    @Override public void writeSpawnData(ByteBuf buf) {
        buf.writeDouble(this.bounce);
        buf.writeInt(this.bounceLimit);
        buf.writeBoolean(this.fragile);
    }
    @Override public void readSpawnData(ByteBuf buf) {
        this.bounce = buf.readDouble();
        this.bounceLimit = buf.readInt();
        this.fragile = buf.readBoolean();
    }

    @Override protected boolean canTriggerWalking() { return false; }
}
