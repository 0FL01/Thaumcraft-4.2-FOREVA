package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXWispEG extends Particle {
    private final Entity target;

    public FXWispEG(World world, double x, double y, double z, Entity target) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.particleMaxAge = 10 + this.rand.nextInt(6);
        this.canCollide = false;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.world == null || !this.world.isRemote || this.target == null || !this.target.isEntityAlive()) {
            this.setExpired();
            return;
        }

        double tx = this.target.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
        double ty = this.target.posY + this.target.height * 0.22F + (this.rand.nextFloat() - 0.5F) * 0.1F;
        double tz = this.target.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
        double dx = tx - this.posX;
        double dy = ty - this.posY;
        double dz = tz - this.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist > 1.0E-6D) {
            this.motionX += dx / dist * 0.05D;
            this.motionY += dy / dist * 0.05D;
            this.motionZ += dz / dist * 0.05D;
        }

        this.motionX = MathHelper.clamp(this.motionX, -0.18D, 0.18D);
        this.motionY = MathHelper.clamp(this.motionY, -0.18D, 0.18D);
        this.motionZ = MathHelper.clamp(this.motionZ, -0.18D, 0.18D);
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.86D;
        this.motionY *= 0.86D;
        this.motionZ *= 0.86D;

        this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.4D, 0.8D, 1.0D);
        if (this.rand.nextBoolean()) {
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (++this.particleAge >= this.particleMaxAge || dist < 0.25D) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        // Emission-style particle: visuals are spawned in onUpdate.
    }
}
