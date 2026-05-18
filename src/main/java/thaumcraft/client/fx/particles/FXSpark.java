package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSpark extends Particle {
    private int particle = 0;
    private final boolean flip;

    public FXSpark(World world, double x, double y, double z, float scale) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGravity = 0.0f;
        this.particleScale = scale;
        this.particleMaxAge = 5 + world.rand.nextInt(5);
        this.setSize(0.01f, 0.01f);
        this.particle = world.rand.nextInt(3) * 8;
        this.flip = world.rand.nextBoolean();
        this.setParticleTextureIndex(this.particle);
    }

    @Override
    public int getFXLayer() {
        return 2;
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
        int part = this.particle + (int) ((float) this.particleAge / (float) this.particleMaxAge * 7.0F);
        if (this.flip) {
            part = this.particle + Math.max(0, 7 - (int) ((float) this.particleAge / (float) this.particleMaxAge * 7.0F));
        }
        this.setParticleTextureIndex(part);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
}
