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
public class FXBlockRunes extends Particle {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
    private static final int LIGHTMAP_FULLBRIGHT = 0x00F000F0;

    private final double ofx;
    private final double ofy;
    private final int rotation;
    private final int runeIndex;
    private float gravity;

    public FXBlockRunes(World world, double x, double y, double z, float red, float green, float blue, int duration) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleRed = red == 0.0F ? 1.0F : red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.rotation = this.rand.nextInt(4) * 90;
        this.gravity = 0.0F;
        this.particleMaxAge = Math.max(6, 3 * duration);
        this.canCollide = false;
        this.setSize(0.01F, 0.01F);
        this.runeIndex = 224 + this.rand.nextInt(16);
        this.ofx = this.rand.nextFloat() * 0.2D;
        this.ofy = -0.3D + this.rand.nextFloat() * 0.6D;
        this.particleScale = (float) (1.0D + this.rand.nextGaussian() * 0.1D);
        this.particleAlpha = 0.0F;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.world == null || !this.world.isRemote) {
            setExpired();
            return;
        }
        float threshold = this.particleMaxAge / 5.0F;
        this.particleAlpha = this.particleAge <= threshold
                ? this.particleAge / Math.max(1.0F, threshold)
                : (this.particleMaxAge - this.particleAge) / (float) this.particleMaxAge;
        if (this.particleAge++ >= this.particleMaxAge) {
            setExpired();
            return;
        }
        this.motionY -= 0.04D * this.gravity;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);
        float u0 = (this.runeIndex % 16) / 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = 0.375F;
        float v1 = v0 + 0.0624375F;
        float size = 0.3F * this.particleScale;

        Minecraft.getMinecraft().renderEngine.bindTexture(PARTICLE_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(px, py, pz);
        GlStateManager.rotate((float) this.rotation, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(this.ofx, this.ofy, -0.51D);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        addLitVertex(buffer, -0.5D * size, 0.5D * size, 0.0D, u1, v1);
        addLitVertex(buffer, 0.5D * size, 0.5D * size, 0.0D, u1, v0);
        addLitVertex(buffer, 0.5D * size, -0.5D * size, 0.0D, u0, v0);
        addLitVertex(buffer, -0.5D * size, -0.5D * size, 0.0D, u0, v1);
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private void addLitVertex(BufferBuilder buffer, double x, double y, double z, double u, double v) {
        int lightU = LIGHTMAP_FULLBRIGHT & 0xFFFF;
        int lightV = LIGHTMAP_FULLBRIGHT >> 16 & 0xFFFF;
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * 0.5F)
                .lightmap(lightU, lightV)
                .endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
