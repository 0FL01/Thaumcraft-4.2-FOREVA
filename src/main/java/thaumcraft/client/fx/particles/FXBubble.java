package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FXBubble extends Particle {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    private static final float UV_SPAN = 0.0624375F;
    private static final float RENDER_SCALE = 0.1F;

    public int particle = 16;
    private double bubbleSpeed = 0.002D;
    private int finalParticleStart = 17;
    private int finalParticleCount = 2;
    private double randomMovementX = 0.0D;
    private double randomMovementY = 0.0D;
    private double randomMovementZ = 0.0D;

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

    public FXBubble setParticle(int particle) {
        this.particle = particle;
        return this;
    }

    public FXBubble setFinalParticles(int start, int count) {
        this.finalParticleStart = start;
        this.finalParticleCount = Math.max(0, count);
        return this;
    }

    public FXBubble setScale(float scale) {
        this.particleScale = scale;
        return this;
    }

    public FXBubble setAlpha(float alpha) {
        this.setAlphaF(alpha);
        return this;
    }

    public FXBubble setGravity(float gravity) {
        this.bubbleSpeed = 0.0D;
        this.particleGravity = gravity;
        return this;
    }

    public FXBubble setRandomMovementScale(double x, double y, double z) {
        this.randomMovementX = x;
        this.randomMovementY = y;
        this.randomMovementZ = z;
        return this;
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
        this.motionY -= 0.04D * this.particleGravity;
        if (this.bubbleSpeed > 0.0D) {
            this.motionX += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.01F;
            this.motionZ += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.01F;
        }
        if (this.randomMovementX != 0.0D) {
            this.motionX += (this.rand.nextFloat() - this.rand.nextFloat()) * this.randomMovementX;
        }
        if (this.randomMovementY != 0.0D) {
            this.motionY += (this.rand.nextFloat() - this.rand.nextFloat()) * this.randomMovementY;
        }
        if (this.randomMovementZ != 0.0D) {
            this.motionZ += (this.rand.nextFloat() - this.rand.nextFloat()) * this.randomMovementZ;
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
        if (this.finalParticleCount > 0 && this.particleMaxAge <= this.finalParticleCount) {
            this.particle = this.finalParticleStart + this.finalParticleCount - this.particleMaxAge;
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder customBuffer = tessellator.getBuffer();
        float u0 = (float) (this.particle % 16) / 16.0F;
        float u1 = u0 + UV_SPAN;
        float v0 = (float) (this.particle / 16) / 16.0F;
        float v1 = v0 + UV_SPAN;
        float size = RENDER_SCALE * this.particleScale;
        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightU = brightness & 0xFFFF;
        int lightV = brightness >> 16 & 0xFFFF;

        Minecraft.getMinecraft().renderEngine.bindTexture(PARTICLE_TEXTURE);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        customBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        customBuffer.pos(px - rotationX * size - rotationXY * size, py - rotationZ * size, pz - rotationYZ * size - rotationXZ * size)
                .tex(u1, v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        customBuffer.pos(px - rotationX * size + rotationXY * size, py + rotationZ * size, pz - rotationYZ * size + rotationXZ * size)
                .tex(u1, v0)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        customBuffer.pos(px + rotationX * size + rotationXY * size, py + rotationZ * size, pz + rotationYZ * size + rotationXZ * size)
                .tex(u0, v0)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        customBuffer.pos(px + rotationX * size - rotationXY * size, py - rotationZ * size, pz + rotationYZ * size - rotationXZ * size)
                .tex(u0, v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }
}
