package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityEmber extends EntityThrowable implements IEntityAdditionalSpawnData {
    public int duration = 20;
    public int firey = 0;
    public float damage = 1.0f;

    public EntityEmber(World world) { super(world); }
    public EntityEmber(World world, EntityLivingBase shooter, float scatter) {
        super(world, shooter);
        this.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 1.0F, scatter);
    }

    @Override
    protected float getGravityVelocity() { return 0.0f; }

    @Override
    public void onUpdate() {
        if (this.ticksExisted > this.duration) { this.setDead(); }
        double decay = this.duration <= 20 ? 0.95 : 0.975;
        this.motionX *= decay;
        this.motionY *= decay;
        this.motionZ *= decay;
        if (this.onGround) {
            this.motionX *= 0.66;
            this.motionY *= 0.66;
            this.motionZ *= 0.66;
        }
        super.onUpdate();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote) {
            if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
                Entity target = result.entityHit;
                if (!target.isImmuneToFire()) {
                    DamageSource ds = new EntityDamageSourceIndirect("fireball", this, this.getThrower())
                        .setFireDamage();
                    if (target.attackEntityFrom(ds, this.damage)) {
                        target.setFire(3 + this.firey);
                    }
                }
            } else if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.rand.nextFloat() < 0.025f * (float)this.firey) {
                BlockPos pos = result.getBlockPos();
                EnumFacing side = result.sideHit;
                BlockPos placePos = pos.offset(side);
                if (this.world.isAirBlock(placePos)) {
                    this.world.setBlockState(placePos, Blocks.FIRE.getDefaultState());
                }
            }
        }
        this.setDead();
    }

    @Override public void writeSpawnData(ByteBuf buf) { buf.writeByte(this.duration); }
    @Override public void readSpawnData(ByteBuf buf) { this.duration = buf.readByte(); }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.damage = nbt.getFloat("damage");
        this.firey = nbt.getInteger("firey");
        this.duration = nbt.getInteger("duration");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("damage", this.damage);
        nbt.setInteger("firey", this.firey);
        nbt.setInteger("duration", this.duration);
    }

    @Override public boolean canBeCollidedWith() { return false; }
    @Override public boolean attackEntityFrom(DamageSource source, float amount) { return false; }
}
