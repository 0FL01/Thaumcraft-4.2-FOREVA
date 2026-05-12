package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityPrimalOrb extends EntityThrowable implements IEntityAdditionalSpawnData {
    public EntityPrimalOrb(World world) { super(world); }
    public EntityPrimalOrb(World world, EntityLivingBase shooter) { super(world, shooter); }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            return;
        }
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Entity hit: primal aspect damage (Phase 3)
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            // Block hit: explosion + special effects (Phase 3)
        }
        this.setDead();
    }

    @Override public void writeSpawnData(ByteBuf buf) {}
    @Override public void readSpawnData(ByteBuf buf) {}
}
