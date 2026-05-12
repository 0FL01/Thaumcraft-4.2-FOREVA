package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityPechBlast extends EntityThrowable {
    int strength = 0;
    int duration = 0;
    boolean nightshade = false;

    public EntityPechBlast(World world) { super(world); }
    public EntityPechBlast(World world, EntityLivingBase shooter) { super(world, shooter); }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            return;
        }
        // AOE effect on all entities within 3x3x3 (Phase 3)
        this.setDead();
    }
}
