package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSlimyBubble extends Particle {
    private int particle = 144;

    public FXSlimyBubble(World world, double x, double y, double z, float scale) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGravity = 0.0f;
        this.particleScale = scale;
        this.particleMaxAge = 15 + world.rand.nextInt(5);
        this.setSize(0.01f, 0.01f);
    }

    @Override
    public int getFXLayer() {
        // Must return 0 because this particle uses setParticleTextureIndex(...).
        // In 1.12.2, setParticleTextureIndex throws "Invalid call to Particle.setMiscTex"
        // when getFXLayer() != 0.
        return 0;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        if (this.particleAge - 1 < 6) {
            this.particle = 144 + this.particleAge / 2;
            if (this.particleAge == 5) {
                this.posY += 0.1;
            }
        } else if (this.particleAge < this.particleMaxAge - 4) {
            this.motionY += 0.005;
            this.particle = 147 + this.particleAge % 4 / 2;
        } else {
            this.motionY /= 2.0;
            this.particle = 150 - (this.particleMaxAge - this.particleAge) / 2;
        }

        this.posY += this.motionY;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.setParticleTextureIndex(this.particle);
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
}
