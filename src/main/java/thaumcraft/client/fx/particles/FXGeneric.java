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
public class FXGeneric extends Particle implements ITCParticle {
    private boolean loop = false;
    private int delay = 0;
    private int startParticle = 0;
    private int numParticles = 1;
    private int particleInc = 1;

    public FXGeneric(World world, double x, double y, double z,
                     double mx, double my, double mz,
                     float red, float green, float blue, float alpha,
                     boolean loop, int start, int num, int inc, int age, int delay, float scale) {
        super(world, x, y, z, mx, my, mz);
        this.setSize(0.1F, 0.1F);
        this.canCollide = false;
        this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;
        this.setRBGColorF(MathHelper.clamp(red, 0.0F, 1.0F), MathHelper.clamp(green, 0.0F, 1.0F), MathHelper.clamp(blue, 0.0F, 1.0F));
        this.setAlphaF(MathHelper.clamp(alpha, 0.0F, 1.0F));
        this.setScale(Math.max(0.05F, scale));
        this.setLoop(loop);
        this.setMaxAge(Math.max(1, age), Math.max(0, delay));
        this.setParticles(start, Math.max(1, num), Math.max(1, inc));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.isAlive()) {
            return;
        }
        int step = Math.max(1, Math.abs(this.particleInc));
        int frame;
        if (this.loop) {
            frame = this.particleAge / step % this.numParticles;
        } else {
            float fs = this.particleMaxAge <= 0 ? 1.0F : (float) this.particleAge / (float) this.particleMaxAge;
            frame = (int) Math.min(this.numParticles * fs, (float) (this.numParticles - 1));
        }
        if (this.particleInc < 0) {
            frame = this.numParticles - 1 - frame;
        }
        this.setParticleTextureIndex(this.startParticle + frame);
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setScale(float scale) {
        this.particleScale = scale;
    }

    public void setMaxAge(int max, int delay) {
        this.delay = Math.max(0, delay);
        this.particleMaxAge = Math.max(1, max + this.delay);
    }

    public void setParticles(int startParticle, int numParticles, int particleInc) {
        this.startParticle = startParticle;
        this.numParticles = Math.max(1, numParticles);
        this.particleInc = particleInc == 0 ? 1 : particleInc;
        this.setParticleTextureIndex(startParticle);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        if (this.particleAge < this.delay) {
            return;
        }
        float alpha = this.particleAlpha;
        if (this.particleAge <= 1 || this.particleAge >= this.particleMaxAge - 1) {
            this.particleAlpha = alpha * 0.5F;
        }
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        this.particleAlpha = alpha;
    }

    @Override
    public int getTCParticleLayer() {
        return 0;
    }
}
