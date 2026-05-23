package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXVent extends Particle {
    private float psm = 1.0F;

    public FXVent(World world, double x, double y, double z, double mx, double my, double mz, float red, float green, float blue) {
        super(world, x, y, z, mx, my, mz);
        this.setSize(0.02F, 0.02F);
        this.particleScale = this.rand.nextFloat() * 0.1F + 0.05F;
        this.setRBGColorF(red, green, blue);
        this.particleMaxAge = 40;
        this.setHeading(mx, my, mz, 0.125F, 5.0F);
        this.canCollide = false;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    public FXVent(World world, double x, double y, double z, double mx, double my, double mz, int color) {
        this(world, x, y, z, mx, my, mz,
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F);
    }

    public void setScale(float scale) {
        this.particleScale *= scale;
        this.psm *= scale;
    }

    public void setRGB(float r, float g, float b) {
        this.setRBGColorF(r, g, b);
    }

    private void setHeading(double x, double y, double z, float speed, float spread) {
        float norm = MathHelper.sqrt(x * x + y * y + z * z);
        if (norm < 1.0E-4F) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            return;
        }
        x /= norm;
        y /= norm;
        z /= norm;
        x += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * spread;
        y += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * spread;
        z += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075F * spread;
        this.motionX = x * speed;
        this.motionY = y * speed;
        this.motionZ = z * speed;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.world == null || !this.world.isRemote || this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        if (this.particleScale > this.psm) {
            this.setExpired();
            return;
        }

        this.motionY += 0.0025D;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.85D;
        this.motionY *= 0.85D;
        this.motionZ *= 0.85D;

        if (this.particleScale < this.psm) {
            this.particleScale *= 1.15F;
        }
        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
        }

        int part = 1 + (int) (this.particleScale / this.psm * 4.0F);
        this.setParticleTextureIndex(part);
        float fade = this.psm <= 0.0F ? 0.0F : (this.psm - this.particleScale) / this.psm;
        this.setAlphaF(MathHelper.clamp(fade, 0.0F, 1.0F));
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getFXLayer() {
        // Must return 0 because this particle uses setParticleTextureIndex(...).
        // In 1.12.2, setParticleTextureIndex throws "Invalid call to Particle.setMiscTex"
        // when getFXLayer() != 0.
        return 0;
    }
}
