package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.lib.TCSounds;

public class EntityGolemOrb extends EntityThrowable implements IEntityAdditionalSpawnData {
    private int targetId = 0;
    private EntityLivingBase target;
    public boolean red = false;

    public EntityGolemOrb(World world) { super(world); }
    public EntityGolemOrb(World world, EntityLivingBase shooter, EntityLivingBase t, boolean r) {
        super(world, shooter);
        this.target = t;
        this.red = r;
    }

    @Override
    protected float getGravityVelocity() { return 0.0f; }

    @Override
    public float getCollisionBorderSize() { return 0.1F; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > (this.red ? 240 : 160)) { this.setDead(); return; }
        // Homing
        if (this.target != null) {
            double d = this.getDistanceSq(this.target);
            if (d > 0.01) {
                double dx = (this.target.posX - this.posX) / d;
                double dy = (this.target.getEntityBoundingBox().minY + (double)this.target.height * 0.6 - this.posY) / d;
                double dz = (this.target.posZ - this.posZ) / d;
                double accel = 0.2;
                this.motionX += dx * accel;
                this.motionY += dy * accel;
                this.motionZ += dz * accel;
                this.motionX = Math.min(0.25, Math.max(-0.25, this.motionX));
                this.motionY = Math.min(0.25, Math.max(-0.25, this.motionY));
                this.motionZ = Math.min(0.25, Math.max(-0.25, this.motionZ));
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote && this.getThrower() != null
                && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            float atk = (float)this.getThrower()
                .getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            result.entityHit.attackEntityFrom(
                DamageSource.causeIndirectDamage(this, this.getThrower()),
                atk * (this.red ? 1.0f : 0.6f));
        }
        this.playSound(TCSounds.SHOCK, 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
        this.setDead();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) return false;
        this.velocityChanged = true;
        if (source.getTrueSource() != null) {
            this.motionX = source.getTrueSource().getLookVec().x * 0.9;
            this.motionY = source.getTrueSource().getLookVec().y * 0.9;
            this.motionZ = source.getTrueSource().getLookVec().z * 0.9;
            this.playSound(TCSounds.ZAP, 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            return true;
        }
        return false;
    }

    @Override public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(this.target != null ? this.target.getEntityId() : -1);
        buf.writeBoolean(this.red);
    }
    @Override public void readSpawnData(ByteBuf buf) {
        int id = buf.readInt();
        try {
            if (id >= 0 && this.world != null)
                this.target = (EntityLivingBase)this.world.getEntityByID(id);
        } catch (Exception e) {}
        this.red = buf.readBoolean();
    }
}
