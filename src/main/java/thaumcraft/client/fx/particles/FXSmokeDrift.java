package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSmokeDrift extends Particle {

    public FXSmokeDrift(World world, double x, double y, double z, double mx, double my, double mz, int age) {
        super(world, x, y, z, mx, my, mz);
        this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;
        this.particleMaxAge = Math.max(3, age);
        this.particleScale = 0.6F + this.rand.nextFloat() * 0.3F;
        this.particleAlpha = 0.66F;
        this.setParticleTextureIndex(1);
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

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.85D;
        this.motionY *= 0.92D;
        this.motionZ *= 0.85D;
        this.setAlphaF(0.66F * (this.particleMaxAge - this.particleAge) / (float) this.particleMaxAge);
        this.setParticleTextureIndex(1 + Math.min(4, (int) (4.0F * this.particleAge / (float) this.particleMaxAge)));
        this.particleScale *= 0.98F;

        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getFXLayer() {
        return 0;
    }
}
