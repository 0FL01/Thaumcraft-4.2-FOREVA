package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;

@SideOnly(Side.CLIENT)
public class FXVent extends Particle implements ITCParticle {
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

    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float ratio = this.psm <= 0.0F ? 0.0F : this.particleScale / this.psm;
        int part = 1 + (int) (ratio * 4.0F);
        float uMin = (float) (part % 16) / 16.0F;
        float uMax = uMin + 0.0624375F;
        float vMin = (float) (part / 16) / 16.0F;
        float vMax = vMin + 0.0624375F;
        float scale = 0.3F * this.particleScale;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightX = brightness >> 16 & 65535;
        int lightY = brightness & 65535;
        float alpha = this.particleAlpha * MathHelper.clamp(this.psm <= 0.0F ? 0.0F : (this.psm - this.particleScale) / this.psm, 0.0F, 1.0F);

        addVertex(buffer, x - rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z - rotationYZ * scale - rotationXZ * scale, uMax, vMax, lightX, lightY, alpha);
        addVertex(buffer, x - rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z - rotationYZ * scale + rotationXZ * scale, uMax, vMin, lightX, lightY, alpha);
        addVertex(buffer, x + rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z + rotationYZ * scale + rotationXZ * scale, uMin, vMin, lightX, lightY, alpha);
        addVertex(buffer, x + rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z + rotationYZ * scale - rotationXZ * scale, uMin, vMax, lightX, lightY, alpha);
    }

    private void addVertex(BufferBuilder buffer, double x, double y, double z, float u, float v, int lightX, int lightY, float alpha) {
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
                .lightmap(lightX, lightY)
                .endVertex();
    }

    @Override
    public int getFXLayer() {
        // Vanilla ParticleManager fallback. TC sheet/layer routing is handled by ParticleEngine.
        return 0;
    }

    @Override
    public int getTCParticleLayer() {
        return 1;
    }
}
