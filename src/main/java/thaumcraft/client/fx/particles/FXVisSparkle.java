package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;

@SideOnly(Side.CLIENT)
public class FXVisSparkle extends Particle implements ITCParticle {
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final int density;
    private final boolean randomizeColor;
    private final boolean trailMode;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private float sizeMod;

    public FXVisSparkle(World world, int x, int y, int z, float red, float green, float blue, int density) {
        this(world, x, y, z, red, green, blue, density, false);
    }

    public FXVisSparkle(World world, int x, int y, int z, float red, float green, float blue, int density, boolean randomizeColor) {
        super(world, x + 0.5D, y + 0.5D, z + 0.5D, 0.0D, 0.0D, 0.0D);
        this.baseX = x;
        this.baseY = y;
        this.baseZ = z;
        this.targetX = 0.0D;
        this.targetY = 0.0D;
        this.targetZ = 0.0D;
        this.density = Math.max(1, density);
        this.randomizeColor = randomizeColor;
        this.trailMode = false;
        this.particleRed = clamp(red);
        this.particleGreen = clamp(green);
        this.particleBlue = clamp(blue);
        this.particleScale = 0.12F + Math.min(0.6F, this.density * 0.02F);
        this.particleMaxAge = Math.max(4, this.density) + this.rand.nextInt(8);
        this.canCollide = false;
        this.setParticleTextureIndex(96 + this.rand.nextInt(8));
        this.sizeMod = 8.0F + this.rand.nextInt(6);
    }

    public FXVisSparkle(World world, double x, double y, double z, double tx, double ty, double tz) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.baseX = MathHelper.floor(x);
        this.baseY = MathHelper.floor(y);
        this.baseZ = MathHelper.floor(z);
        this.density = 1;
        this.randomizeColor = false;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.trailMode = true;
        this.motionX = this.rand.nextGaussian() * 0.01D;
        this.motionY = this.rand.nextGaussian() * 0.01D;
        this.motionZ = this.rand.nextGaussian() * 0.01D;
        this.particleRed = 0.2F;
        this.particleGreen = 0.6F + this.rand.nextFloat() * 0.3F;
        this.particleBlue = 0.2F;
        this.particleScale = 0.0F;
        this.particleMaxAge = 1000;
        this.sizeMod = 45 + this.rand.nextInt(15);
        this.particleGravity = 0.2F;
        this.canCollide = false;
        this.setParticleTextureIndex(128);

        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase player = mc.player;
        int visibleDistance = mc.gameSettings.fancyGraphics ? 64 : 32;
        if (player != null && player.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }
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

        if (this.trailMode) {
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.985D;
            this.motionY *= 0.985D;
            this.motionZ *= 0.985D;

            double dx = this.targetX - this.posX;
            double dy = this.targetY - this.posY;
            double dz = this.targetZ - this.posZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double accel = 0.1D;
            if (distance < 2.0D) {
                this.particleScale *= 0.95F;
            }
            if (distance < 0.2D) this.particleMaxAge = this.particleAge;

            if (this.particleAge < 10) {
                this.particleScale = this.particleAge / this.sizeMod;
            }

            if (distance > 1.0E-6D) {
                this.motionX += (dx / distance) * accel;
                this.motionY += (dy / distance) * accel;
                this.motionZ += (dz / distance) * accel;
                this.motionX = MathHelper.clamp(this.motionX, -0.1D, 0.1D);
                this.motionY = MathHelper.clamp(this.motionY, -0.1D, 0.1D);
                this.motionZ = MathHelper.clamp(this.motionZ, -0.1D, 0.1D);
            }
            this.setParticleTextureIndex(128 + (this.particleAge % 16));
            return;
        }

        this.posX = this.baseX + this.rand.nextFloat();
        this.posY = this.baseY + this.rand.nextFloat();
        this.posZ = this.baseZ + this.rand.nextFloat();
        if (this.randomizeColor) {
            float baseRed = 0.33f + this.rand.nextFloat() * 0.67f;
            float baseGreen = 0.33f + this.rand.nextFloat() * 0.67f;
            float baseBlue = 0.33f + this.rand.nextFloat() * 0.67f;
            this.particleRed = clamp(baseRed - 0.2f + this.rand.nextFloat() * 0.4f);
            this.particleGreen = clamp(baseGreen - 0.2f + this.rand.nextFloat() * 0.4f);
            this.particleBlue = clamp(baseBlue - 0.2f + this.rand.nextFloat() * 0.4f);
        }
        this.particleScale = (0.08F + (this.rand.nextFloat() * 0.02F)) * this.sizeMod;
        this.particleAlpha = 0.35F + Math.min(0.45F, this.density * 0.02F);
        this.setParticleTextureIndex(112 + (this.particleAge % 8));
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float bob = MathHelper.sin(this.particleAge / 3.0F) * 0.3F + (this.trailMode ? 6.0F : 1.0F);
        float scale = this.particleScale;
        float alpha = this.particleAlpha;
        this.particleScale = this.particleScale * bob;
        if (this.trailMode) {
            this.particleAlpha = 0.5F;
        }
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        this.particleScale = scale;
        this.particleAlpha = alpha;
    }

    @Override
    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        this.particleRed = clamp(particleRedIn);
        this.particleGreen = clamp(particleGreenIn);
        this.particleBlue = clamp(particleBlueIn);
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @Override
    public int getTCParticleLayer() {
        return 0;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    private static float clamp(float value) {
        if (value < 0.02f) return 0.02f;
        return Math.min(1.0f, value);
    }
}
