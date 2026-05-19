package thaumcraft.common.entities.projectile;

public class EntityDart extends net.minecraft.entity.projectile.EntityArrow implements net.minecraft.entity.IProjectile, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    private boolean first = true;

    public EntityDart(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityDart(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter) {
        super(world, shooter);
        this.setSize(0.5F, 0.5F);
    }

    public EntityDart(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter,
                      net.minecraft.entity.EntityLivingBase target, float velocity, float inaccuracy) {
        super(world);
        this.shootingEntity = shooter;
        this.posY = shooter.posY + (double) shooter.getEyeHeight() - 0.1D;
        double dx = target.posX - shooter.posX;
        double dy = target.posY + (double) target.getEyeHeight() - 0.7D - this.posY;
        double dz = target.posZ - shooter.posZ;
        double horizontal = net.minecraft.util.math.MathHelper.sqrt(dx * dx + dz * dz);
        if (horizontal >= 1.0E-7D) {
            float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
            float pitch = (float) (-(Math.atan2(dy, horizontal) * 180.0D / Math.PI));
            double normX = dx / horizontal;
            double normZ = dz / horizontal;
            this.setLocationAndAngles(shooter.posX + normX / 5.0D, this.posY, shooter.posZ + normZ / 5.0D, yaw, pitch);
            float lead = (float) horizontal * 0.2F;
            this.shoot(dx, dy + (double) lead, dz, velocity, inaccuracy);
        }
    }

    @Override
    protected net.minecraft.item.ItemStack getArrowStack() { return net.minecraft.item.ItemStack.EMPTY; }

    @Override
    public void writeSpawnData(io.netty.buffer.ByteBuf buf) {
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeDouble(this.motionZ);
        buf.writeFloat(this.rotationYaw);
        buf.writeFloat(this.rotationPitch);
    }

    @Override
    public void readSpawnData(io.netty.buffer.ByteBuf buf) {
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.motionZ = buf.readDouble();
        this.rotationYaw = buf.readFloat();
        this.rotationPitch = buf.readFloat();
    }

    @Override
    public void onUpdate() {
        if (this.first && this.world.isRemote) {
            this.first = false;
            for (int i = 0; i < 5; ++i) {
                thaumcraft.common.Thaumcraft.proxy.drawGenericParticles(this.world,
                        this.posX - this.motionX / 1.5D,
                        this.posY - this.motionY / 1.5D,
                        this.posZ - this.motionZ / 1.5D,
                        this.motionX / 9.0D + this.rand.nextGaussian() * 0.01D,
                        this.motionY / 9.0D + this.rand.nextGaussian() * 0.01D,
                        this.motionZ / 9.0D + this.rand.nextGaussian() * 0.01D,
                        0.25F, 0.25F, 0.25F, 0.75F,
                        false, 0, 8, -1, 8, 0, 0.65F, 1);
            }
        }
        super.onUpdate();
    }
}
