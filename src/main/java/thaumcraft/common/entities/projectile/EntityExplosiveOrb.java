package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityExplosiveOrb extends EntityThrowable {
    public float strength = 1.0f;
    public boolean onFire = false;

    public EntityExplosiveOrb(World world) { super(world); }
    public EntityExplosiveOrb(World world, EntityLivingBase shooter) { super(world, shooter); }
    public EntityExplosiveOrb(World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected float getGravityVelocity() { return 0.01f; }

    @Override
    public float getCollisionBorderSize() { return 0.1F; }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote) {
            if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
                DamageSource ds = this.getThrower() != null
                    ? new EntityDamageSourceIndirect("fireball", this, this.getThrower())
                        .setFireDamage().setProjectile()
                    : new EntityDamageSourceIndirect("onFire", this, this)
                        .setFireDamage().setProjectile();
                result.entityHit.attackEntityFrom(ds, this.strength * 1.5f);
            }
            this.world.newExplosion(null, this.posX, this.posY, this.posZ,
                this.strength, this.onFire, false);
        }
        this.setDead();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 500) this.setDead();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) return false;
        this.velocityChanged = true;
        if (source.getTrueSource() != null) {
            Vec3d look = source.getTrueSource().getLookVec();
            if (look != null) {
                this.motionX = look.x;
                this.motionY = look.y;
                this.motionZ = look.z;
                this.motionX *= 0.9;
                this.motionY *= 0.9;
                this.motionZ *= 0.9;
            }
            return true;
        }
        return false;
    }
}
