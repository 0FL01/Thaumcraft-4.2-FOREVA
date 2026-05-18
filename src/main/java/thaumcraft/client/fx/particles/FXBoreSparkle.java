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
public class FXBoreSparkle extends Particle {
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public FXBoreSparkle(World world, double x, double y, double z, double tx, double ty, double tz) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.particleScale = this.rand.nextFloat() * 0.5F + 0.5F;
        this.particleRed = 0.2F;
        this.particleGreen = 0.6F + this.rand.nextFloat() * 0.3F;
        this.particleBlue = 0.2F;
        this.particleGravity = 0.2F;
        this.canCollide = false;

        double dx = tx - x;
        double dy = ty - y;
        double dz = tz - z;
        int base = Math.max(1, (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 3.0D));
        this.particleMaxAge = base / 2 + this.rand.nextInt(base);
        this.motionX = this.rand.nextGaussian() * 0.01D;
        this.motionY = this.rand.nextGaussian() * 0.01D;
        this.motionZ = this.rand.nextGaussian() * 0.01D;
    }

    public void setGravity(float value) {
        this.particleGravity = value;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.world == null || !this.world.isRemote) {
            this.setExpired();
            return;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        if (MathHelper.floor(this.posX) == MathHelper.floor(this.targetX)
                && MathHelper.floor(this.posY) == MathHelper.floor(this.targetY)
                && MathHelper.floor(this.posZ) == MathHelper.floor(this.targetZ)) {
            this.setExpired();
            return;
        }

        this.motionY -= 0.04D * this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985D;
        this.motionY *= 0.985D;
        this.motionZ *= 0.985D;

        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double accel = dist < 4.0D ? 0.6D : 0.3D;
        if (dist > 1.0E-6D) {
            this.motionX = MathHelper.clamp(this.motionX + dx / dist * accel, -0.35D, 0.35D);
            this.motionY = MathHelper.clamp(this.motionY + dy / dist * accel, -0.35D, 0.35D);
            this.motionZ = MathHelper.clamp(this.motionZ + dz / dist * accel, -0.35D, 0.35D);
        }

        this.world.spawnParticle(
                EnumParticleTypes.REDSTONE,
                this.posX, this.posY, this.posZ,
                this.particleRed, this.particleGreen, this.particleBlue);
        this.world.spawnParticle(
                EnumParticleTypes.CRIT_MAGIC,
                this.posX, this.posY, this.posZ,
                this.motionX * 0.05D, this.motionY * 0.05D, this.motionZ * 0.05D);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        // Emission-style particle: visuals are spawned in onUpdate.
    }
}
