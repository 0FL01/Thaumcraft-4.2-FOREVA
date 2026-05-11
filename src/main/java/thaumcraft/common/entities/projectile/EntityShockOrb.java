package thaumcraft.common.entities.projectile;

public class EntityShockOrb extends net.minecraft.entity.projectile.EntityThrowable {
    public EntityShockOrb(net.minecraft.world.World world) { super(world); }
    public EntityShockOrb(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter) { super(world, shooter); }
    public EntityShockOrb(net.minecraft.world.World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        if (!this.world.isRemote) {
            this.setDead();
        }
    }
}
