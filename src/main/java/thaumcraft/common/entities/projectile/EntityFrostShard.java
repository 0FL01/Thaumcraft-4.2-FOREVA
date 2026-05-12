package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
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
    }

    @Override
    protected float getGravityVelocity() { return this.fragile ? 0.015f : 0.05f; }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DAMAGE, 0.0f);
        this.dataManager.register(FROSTY, (byte) 0);
    }

    public void setDamage(float d) { this.dataManager.set(DAMAGE, d); }
    public float getDamage() { return this.dataManager.get(DAMAGE); }
    public void setFrosty(int f) { this.dataManager.set(FROSTY, (byte)f); }
    public int getFrosty() { return this.dataManager.get(FROSTY); }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;

        // --- Bounce physics (both sides) ---
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Reverse velocity toward entity (Phase 3: precise direction calc)
            this.motionX *= 0.66;
            this.motionY *= 0.66;
            this.motionZ *= 0.66;
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            EnumFacing side = result.sideHit;
            // Reverse velocity based on hit side
            if (side.getAxis() == EnumFacing.Axis.X) this.motionX *= -1.0;
            if (side.getAxis() == EnumFacing.Axis.Y) this.motionY *= -0.9;
            if (side.getAxis() == EnumFacing.Axis.Z) this.motionZ *= -1.0;
        }

        // Apply bounce attenuation
        this.motionX *= this.bounce;
        this.motionY *= this.bounce;
        this.motionZ *= this.bounce;

        // Push back from surface
        float speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        if (speed > 0.01f) {
            this.posX -= this.motionX / (double)speed * 0.05;
            this.posY -= this.motionY / (double)speed * 0.05;
            this.posZ -= this.motionZ / (double)speed * 0.05;
        }

        // --- Server-side effects ---
        if (!this.world.isRemote && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Apply damage + slowness (Phase 3)
            // If fragile: shatter on hit + knockback
        }

        // Bounce limit
        if (this.bounceLimit-- <= 0) {
            this.setDead();
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
}
