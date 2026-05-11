package thaumcraft.common.entities.projectile;

public class EntityBottleTaint extends net.minecraft.entity.projectile.EntityThrowable {
    public EntityBottleTaint(net.minecraft.world.World world) { super(world); }
    public EntityBottleTaint(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter) { super(world, shooter); }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        if (!this.world.isRemote) {
            this.setDead();
        }
    }
}
