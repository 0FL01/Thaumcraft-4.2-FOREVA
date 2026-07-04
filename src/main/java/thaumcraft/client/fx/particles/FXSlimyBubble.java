package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;

@SideOnly(Side.CLIENT)
public class FXSlimyBubble extends Particle implements ITCParticle {
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
        // Vanilla ParticleManager fallback. TC sheet/layer routing is handled by ParticleEngine.
        return 0;
    }

    @Override
    public int getTCParticleLayer() {
        return 1;
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
        float uMin = (float) (this.particle % 16) / 16.0F;
        float uMax = uMin + 0.0625F;
        float vMin = (float) (this.particle / 16) / 16.0F;
        float vMax = vMin + 0.0625F;
        float scale = this.particleScale;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightX = brightness >> 16 & 65535;
        int lightY = brightness & 65535;

        addVertex(buffer, x - rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z - rotationYZ * scale - rotationXZ * scale, uMax, vMax, lightX, lightY);
        addVertex(buffer, x - rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z - rotationYZ * scale + rotationXZ * scale, uMax, vMin, lightX, lightY);
        addVertex(buffer, x + rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z + rotationYZ * scale + rotationXZ * scale, uMin, vMin, lightX, lightY);
        addVertex(buffer, x + rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z + rotationYZ * scale - rotationXZ * scale, uMin, vMax, lightX, lightY);
    }

    private void addVertex(BufferBuilder buffer, double x, double y, double z, float u, float v, int lightX, int lightY) {
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(lightX, lightY)
                .endVertex();
    }
}
