package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBubble extends Particle {
    public int particle = 16;
    private double bubbleSpeed = 0.002D;

    public FXBubble(World world, double x, double y, double z, double mx, double my, double mz, int age) {
        super(world, x, y, z, mx, my, mz);
        this.setRBGColorF(1.0F, 0.0F, 0.5F);
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.3F + 0.2F;
        this.motionX = mx * 0.2D + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.02F;
        this.motionY = my * 0.2D + this.rand.nextFloat() * 0.02F;
        this.motionZ = mz * 0.2D + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.02F;
        this.particleMaxAge = (int) ((age + 2) + 8.0D / (this.rand.nextDouble() * 0.8D + 0.2D));
        this.canCollide = false;
        this.setParticleTextureIndex(this.particle);
    }

    public void setFroth() {
        this.particleScale *= 0.75F;
        this.particleMaxAge = 4 + this.rand.nextInt(3);
        this.bubbleSpeed = -0.001D;
        this.motionX /= 5.0D;
        this.motionY /= 10.0D;
        this.motionZ /= 5.0D;
    }

    public void setFroth2() {
        this.particleScale *= 0.75F;
        this.particleMaxAge = 12 + this.rand.nextInt(12);
        this.bubbleSpeed = -0.005D;
        this.motionX /= 5.0D;
        this.motionY /= 10.0D;
        this.motionZ /= 5.0D;
    }

    public void setRGB(float r, float g, float b) {
        this.setRBGColorF(r, g, b);
    }

    public void setBubbleSpeed(double bubbleSpeed) {
        this.bubbleSpeed = bubbleSpeed;
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

        this.motionY += this.bubbleSpeed;
        if (this.bubbleSpeed > 0.0D) {
            this.motionX += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.01F;
            this.motionZ += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.01F;
        }
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.85D;
        this.motionY *= 0.85D;
        this.motionZ *= 0.85D;

        if (this.particleMaxAge-- <= 0) {
            this.setExpired();
            return;
        }
        if (this.particleMaxAge <= 2) {
            this.particle++;
        }
        this.setParticleTextureIndex(this.particle);
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
