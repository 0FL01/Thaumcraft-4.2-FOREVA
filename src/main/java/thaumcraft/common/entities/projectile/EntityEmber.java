package thaumcraft.common.entities.projectile;

public class EntityEmber extends net.minecraft.entity.projectile.EntityThrowable implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityEmber(net.minecraft.world.World world) { super(world); }
    public EntityEmber(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter) { super(world, shooter); }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        if (!this.world.isRemote) {
            this.setDead();
        }
    }

    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
