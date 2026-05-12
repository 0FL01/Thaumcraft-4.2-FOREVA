package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
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
    }

    @Override
    protected float getGravityVelocity() { return 0.0f; }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            return;
        }
        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
            // Entity hit: fire damage + setFire (Phase 3)
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            EnumFacing side = result.sideHit;
            // Block hit: chance to place fire (Phase 3)
        }
        this.setDead();
    }

    @Override public void writeSpawnData(ByteBuf buf) { buf.writeByte(this.duration); }
    @Override public void readSpawnData(ByteBuf buf) { this.duration = buf.readByte(); }
}
