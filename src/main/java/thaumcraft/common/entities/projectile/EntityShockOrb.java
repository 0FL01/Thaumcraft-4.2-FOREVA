package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityShockOrb extends EntityThrowable {
    public EntityShockOrb(World world) { super(world); }
    public EntityShockOrb(World world, EntityLivingBase shooter) { super(world, shooter); }
    public EntityShockOrb(World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            return;
        }
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Entity hit: AOE lightning damage + blockAiry placement (Phase 3)
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            // Block hit: AOE around impact point (Phase 3)
        }
        this.setDead();
    }
}
