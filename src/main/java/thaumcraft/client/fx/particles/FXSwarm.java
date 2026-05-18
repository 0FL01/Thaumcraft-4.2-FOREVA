package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSwarm extends Particle {
    private static final float MAX_SPEED = 0.35F;

    private final Entity target;
    private float turnSpeed = 10.0F;
    private float speed = 0.2F;
    private int deathTimer = 0;
    private float pitch = 0.0F;

    public FXSwarm(World world, double x, double y, double z, Entity target, float red, float green, float blue) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.particleScale = this.rand.nextFloat() * 0.5F + 1.0F;
        this.particleGravity = 0.1F;
        this.canCollide = false;
        this.particleMaxAge = 200 + this.rand.nextInt(80);

        float spread = 0.2F;
        this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
        this.motionY = (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
        this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
    }

    public FXSwarm(World world,
                   double x, double y, double z,
                   Entity target,
                   float red, float green, float blue,
                   float speed, float turnSpeed, float particleGravity) {
        this(world, x, y, z, target, red, green, blue);
        this.speed = speed;
        this.turnSpeed = turnSpeed;
        this.particleGravity = particleGravity;
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

        boolean targetAlive = this.target != null
                && !this.target.isDead
                && (!(this.target instanceof EntityLivingBase) || ((EntityLivingBase) this.target).deathTime <= 0);

        if (!targetAlive) {
            this.deathTimer++;
            this.motionY -= this.particleGravity * 0.5F;
            if (this.deathTimer > 50) {
                this.setExpired();
                return;
            }
        } else {
            this.motionY += this.particleGravity;
            steerTowardsTarget();
        }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985D;
        this.motionY *= 0.985D;
        this.motionZ *= 0.985D;

        float fade = 1.0F - (this.deathTimer / 50.0F);
        this.world.spawnParticle(
                EnumParticleTypes.REDSTONE,
                this.posX, this.posY, this.posZ,
                this.particleRed * fade, this.particleGreen * fade, this.particleBlue * fade);
        if (this.rand.nextInt(3) == 0) {
            this.world.spawnParticle(
                    EnumParticleTypes.CRIT_MAGIC,
                    this.posX, this.posY, this.posZ,
                    this.motionX * 0.06D, this.motionY * 0.06D, this.motionZ * 0.06D);
        }
    }

    private void steerTowardsTarget() {
        double tx = this.target.posX - this.posX;
        double ty = this.target.posY + this.target.height * 0.5D - this.posY;
        double tz = this.target.posZ - this.posZ;
        double distSq = tx * tx + ty * ty + tz * tz;
        if (distSq < 1.0E-6D) {
            return;
        }

        float maxTurn = Math.max(1.0F, this.turnSpeed);
        if (distSq > this.target.width * this.target.width) {
            rotateToward(tx, ty, tz, maxTurn);
        } else {
            rotateToward(-tx, -ty, -tz, maxTurn);
        }
    }

    private void rotateToward(double tx, double ty, double tz, float maxTurn) {
        double flat = Math.sqrt(tx * tx + tz * tz);
        float targetYaw = (float) (Math.atan2(tz, tx) * 180.0D / Math.PI) - 90.0F;
        float targetPitch = (float) (-(Math.atan2(ty, flat) * 180.0D / Math.PI));

        this.particleAngle = updateRotation(this.particleAngle, targetYaw, maxTurn);
        this.pitch = updateRotation(this.pitch, targetPitch, maxTurn);

        float yawRad = this.particleAngle * 0.017453292F;
        float pitchRad = this.pitch * 0.017453292F;
        double headingX = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        double headingZ = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);
        double headingY = -MathHelper.sin(pitchRad);
        setHeading(headingX, headingY, headingZ, Math.max(0.01F, this.speed), 15.0F);
    }

    private float updateRotation(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        if (delta > maxDelta) delta = maxDelta;
        if (delta < -maxDelta) delta = -maxDelta;
        return current + delta;
    }

    private void setHeading(double x, double y, double z, float speed, float variance) {
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len < 1.0E-6D) {
            return;
        }
        x /= len;
        y /= len;
        z /= len;
        double jitter = 0.0075D * variance;
        x += this.rand.nextGaussian() * jitter;
        y += this.rand.nextGaussian() * jitter;
        z += this.rand.nextGaussian() * jitter;

        this.motionX = MathHelper.clamp((float) (x * speed), -MAX_SPEED, MAX_SPEED);
        this.motionY = MathHelper.clamp((float) (y * speed), -MAX_SPEED, MAX_SPEED);
        this.motionZ = MathHelper.clamp((float) (z * speed), -MAX_SPEED, MAX_SPEED);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        // Emission-style particle: visuals are spawned in onUpdate.
    }
}
