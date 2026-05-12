package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBottleTaint extends EntityThrowable {
    public EntityBottleTaint(World world) { super(world); }
    public EntityBottleTaint(World world, EntityLivingBase shooter) { super(world, shooter); }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            // Client: particles (Phase 3)
            return;
        }
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Entity hit: apply taint poison (Phase 3)
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            // Block hit: convert biome + place taint fibres (Phase 3)
        }
        this.setDead();
    }
}
