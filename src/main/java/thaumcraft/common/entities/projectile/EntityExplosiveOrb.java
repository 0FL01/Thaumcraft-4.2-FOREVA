package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            return;
        }
        // Both entity and block hits trigger explosion
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Direct hit bonus damage (Phase 3)
        }
        // Explosion at impact point (Phase 3)
        this.setDead();
    }
}
