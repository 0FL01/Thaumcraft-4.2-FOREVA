package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBubbleAlt extends Particle {
    public int particle = 25;
    private final float baseScale;

    public FXBubbleAlt(World world, double x, double y, double z, double mx, double my, double mz, int age) {
        super(world, x, y, z, mx, my, mz);
        this.setRBGColorF(1.0F, 0.0F, 0.5F);
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.3F + 0.2F;
        this.baseScale = this.particleScale;
        this.motionX = mx * 0.2D + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.02F;
        this.motionY = my * 0.2D + this.rand.nextFloat() * 0.02F;
        this.motionZ = mz * 0.2D + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.02F;
        this.particleMaxAge = (int) ((age + 2) + 8.0D / (this.rand.nextDouble() * 0.8D + 0.2D));
        this.canCollide = false;
        this.setParticleTextureIndex(this.particle);
    }

    public void setRGB(float r, float g, float b) {
        this.setRBGColorF(r, g, b);
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

        this.motionX += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.001F;
        this.motionZ += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.001F;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.85D;
        this.motionY *= 0.85D;
        this.motionZ *= 0.85D;
        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        if (this.particleAge == this.particleMaxAge - 2) {
            this.particle = 17;
        } else if (this.particleAge == this.particleMaxAge - 1) {
            this.particle = 18;
        }
        this.setParticleTextureIndex(this.particle);
        if (this.particleMaxAge > 0) {
            float progress = (float) this.particleAge / (float) this.particleMaxAge;
            this.particleScale = this.baseScale * Math.max(0.1F, progress);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }
}
