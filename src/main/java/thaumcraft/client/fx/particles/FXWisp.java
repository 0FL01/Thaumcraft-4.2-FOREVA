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
public class FXWisp extends Particle {
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final boolean hasTarget;
    private final float red;
    private final float green;
    private final float blue;

    public FXWisp(World world, double x, double y, double z, double tx, double ty, double tz, float size, boolean flag, float speed) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.hasTarget = tx != 0.0D || ty != 0.0D || tz != 0.0D;
        this.particleMaxAge = 8 + Math.max(1, (int) (size * 10.0F));
        this.canCollide = false;
        this.motionX = (this.rand.nextFloat() - 0.5F) * 0.02F;
        this.motionY = (this.rand.nextFloat() - 0.5F) * 0.02F;
        this.motionZ = (this.rand.nextFloat() - 0.5F) * 0.02F;

        float r = MathHelper.clamp(speed, 0.0F, 1.0F);
        this.red = r;
        this.green = flag ? 0.8F : 0.4F;
        this.blue = 1.0F - r * 0.5F;
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

        if (this.hasTarget) {
            double dx = this.targetX - this.posX;
            double dy = this.targetY - this.posY;
            double dz = this.targetZ - this.posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 1.0E-5D) {
                this.motionX += dx / dist * 0.03D;
                this.motionY += dy / dist * 0.03D;
                this.motionZ += dz / dist * 0.03D;
            }
        } else {
            this.motionX += (this.rand.nextFloat() - 0.5F) * 0.01F;
            this.motionY += (this.rand.nextFloat() - 0.5F) * 0.01F;
            this.motionZ += (this.rand.nextFloat() - 0.5F) * 0.01F;
        }

        this.motionX = MathHelper.clamp(this.motionX, -0.15D, 0.15D);
        this.motionY = MathHelper.clamp(this.motionY, -0.15D, 0.15D);
        this.motionZ = MathHelper.clamp(this.motionZ, -0.15D, 0.15D);
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.88D;
        this.motionY *= 0.88D;
        this.motionZ *= 0.88D;

        this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, this.red, this.green, this.blue);
        if (this.rand.nextBoolean()) {
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (++this.particleAge >= this.particleMaxAge) {
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
