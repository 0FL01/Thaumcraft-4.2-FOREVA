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
public class FXVisSparkle extends Particle {
    private int baseX;
    private int baseY;
    private int baseZ;
    private float red;
    private float green;
    private float blue;
    private int density;
    private boolean randomizeColor;
    private boolean trailMode;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float sizeMod;

    public FXVisSparkle(World world, int x, int y, int z, float red, float green, float blue, int density) {
        this(world, x, y, z, red, green, blue, density, false);
    }

    public FXVisSparkle(World world, int x, int y, int z, float red, float green, float blue, int density, boolean randomizeColor) {
        super(world, x + 0.5D, y + 0.5D, z + 0.5D, 0.0D, 0.0D, 0.0D);
        this.baseX = x;
        this.baseY = y;
        this.baseZ = z;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.density = Math.max(1, density);
        this.randomizeColor = randomizeColor;
        this.particleMaxAge = 6 + this.rand.nextInt(5);
        this.canCollide = false;
    }

    public FXVisSparkle(World world, double x, double y, double z, double tx, double ty, double tz) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.trailMode = true;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.motionX = this.rand.nextGaussian() * 0.01D;
        this.motionY = this.rand.nextGaussian() * 0.01D;
        this.motionZ = this.rand.nextGaussian() * 0.01D;
        this.red = 0.6F;
        this.green = 0.6F;
        this.blue = 0.6F;
        this.particleScale = 0.2F;
        this.particleMaxAge = 1000;
        this.sizeMod = 45 + this.rand.nextInt(15);
        this.canCollide = false;
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

        if (this.trailMode) {
            if (this.particleAge++ >= this.particleMaxAge) {
                this.setExpired();
                return;
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.motionX *= 0.985D;
            this.motionY *= 0.985D;
            this.motionZ *= 0.985D;

            double dx = this.targetX - this.posX;
            double dy = this.targetY - this.posY;
            double dz = this.targetZ - this.posZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance < 0.2D) {
                this.setExpired();
                return;
            }

            if (distance > 0.0D) {
                this.motionX += (dx / distance) * 0.1D;
                this.motionY += (dy / distance) * 0.1D;
                this.motionZ += (dz / distance) * 0.1D;
                this.motionX = MathHelper.clamp(this.motionX, -0.1D, 0.1D);
                this.motionY = MathHelper.clamp(this.motionY, -0.1D, 0.1D);
                this.motionZ = MathHelper.clamp(this.motionZ, -0.1D, 0.1D);
            }

            if (this.particleAge < 10) {
                this.particleScale = this.particleAge / Math.max(1.0F, this.sizeMod);
            }

            this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, this.red, this.green, this.blue);
            if (this.rand.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
            return;
        }

        for (int i = 0; i < this.density; i++) {
            double px = this.baseX + this.rand.nextFloat();
            double py = this.baseY + this.rand.nextFloat();
            double pz = this.baseZ + this.rand.nextFloat();
            float spawnRed = this.red;
            float spawnGreen = this.green;
            float spawnBlue = this.blue;
            if (this.randomizeColor) {
                float baseRed = 0.33f + this.rand.nextFloat() * 0.67f;
                float baseGreen = 0.33f + this.rand.nextFloat() * 0.67f;
                float baseBlue = 0.33f + this.rand.nextFloat() * 0.67f;
                spawnRed = clamp(baseRed - 0.2f + this.rand.nextFloat() * 0.4f);
                spawnGreen = clamp(baseGreen - 0.2f + this.rand.nextFloat() * 0.4f);
                spawnBlue = clamp(baseBlue - 0.2f + this.rand.nextFloat() * 0.4f);
            }
            this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, spawnRed, spawnGreen, spawnBlue);
            if (this.rand.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 0.0D, 0.0D, 0.0D);
            }
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

    @Override
    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        this.red = clamp(particleRedIn);
        this.green = clamp(particleGreenIn);
        this.blue = clamp(particleBlueIn);
    }

    private static float clamp(float value) {
        if (value < 0.02f) return 0.02f;
        return Math.min(1.0f, value);
    }
}
