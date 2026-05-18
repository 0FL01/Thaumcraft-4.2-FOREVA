package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXWispArcing extends Particle {
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final float red;
    private final float green;
    private final float blue;

    public FXWispArcing(World world,
                        double x, double y, double z,
                        double tx, double ty, double tz,
                        float scale,
                        float red, float green, float blue) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.particleScale = Math.max(0.1F, scale);
        this.particleMaxAge = 20;
        this.canCollide = false;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.world == null || !this.world.isRemote) {
            setExpired();
            return;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            setExpired();
            return;
        }

        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.12D) {
            setExpired();
            return;
        }

        double accel = 0.12D;
        this.motionX = MathHelper.clamp(this.motionX + dx / dist * accel, -0.25D, 0.25D);
        this.motionY = MathHelper.clamp(this.motionY + dy / dist * accel, -0.25D, 0.25D);
        this.motionZ = MathHelper.clamp(this.motionZ + dz / dist * accel, -0.25D, 0.25D);
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.9D;
        this.motionY *= 0.9D;
        this.motionZ *= 0.9D;

        this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, this.red, this.green, this.blue);
        if (this.rand.nextInt(3) == 0) {
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, net.minecraft.entity.Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        // Emission particle rendered via world.spawnParticle in onUpdate.
    }
}
