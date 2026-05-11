package thaumcraft.common.entities.projectile;

public class EntityFrostShard extends net.minecraft.entity.projectile.EntityThrowable implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityFrostShard(net.minecraft.world.World world) { super(world); }

    @Override public void onImpact(net.minecraft.util.math.RayTraceResult result) {}
    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
