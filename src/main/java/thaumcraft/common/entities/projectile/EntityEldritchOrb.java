package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEldritchOrb extends EntityThrowable {
    public EntityEldritchOrb(World world) { super(world); }
    public EntityEldritchOrb(World world, EntityLivingBase shooter) { super(world, shooter); }
    public EntityEldritchOrb(World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            return;
        }
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Entity hit: AOE eldritch damage + weakness (Phase 3)
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            // Block hit: AOE damage around impact point (Phase 3)
        }
        this.setDead();
    }
}
