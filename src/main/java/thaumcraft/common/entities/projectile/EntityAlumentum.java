package thaumcraft.common.entities.projectile;

public class EntityAlumentum extends net.minecraft.entity.projectile.EntityThrowable {
    public EntityAlumentum(net.minecraft.world.World world) { super(world); }
    public EntityAlumentum(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter) { super(world, shooter); }
    public EntityAlumentum(net.minecraft.world.World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected float getGravityVelocity() { return 0.75f; }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        if (!this.world.isRemote) {
            boolean griefing = this.world.getGameRules().getBoolean("mobGriefing");
            this.world.createExplosion(null, this.posX, this.posY, this.posZ, 1.66f, griefing);
            this.setDead();
        }
    }
}
