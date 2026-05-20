package thaumcraft.client.fx.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FXBlockWard extends Particle {
    private static final int LIGHTMAP_FULLBRIGHT = 0x00F000F0;

    private final EnumFacing side;
    private final int rotation;
    private final float sx;
    private final float sy;
    private final float sz;

    public FXBlockWard(World world, int x, int y, int z, int color, int count) {
        this(world,
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                EnumFacing.random(world.rand),
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F,
                count);
    }

    public FXBlockWard(World world, double x, double y, double z, EnumFacing side, float red, float green, float blue) {
        this(world, x, y, z, side, red, green, blue, 0);
    }

    private FXBlockWard(World world, double x, double y, double z, EnumFacing side, float red, float green, float blue, int count) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.side = side;
        this.rotation = this.rand.nextInt(360);
        this.particleMaxAge = 12 + this.rand.nextInt(5);
        this.canCollide = false;
        this.particleGravity = 0.0F;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.setSize(0.01F, 0.01F);
        this.particleScale = (float) (1.4D + this.rand.nextGaussian() * 0.3D) + Math.min(0.6F, Math.max(0, count) * 0.03F);

        if (red == 0.0F && green == 0.0F && blue == 0.0F) {
            red = 0.8F;
            green = 0.8F;
            blue = 1.0F;
        }
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;

        float ox = MathHelper.clamp(red - 0.6F + this.rand.nextFloat() * 0.2F, -0.4F, 0.4F);
        float oy = MathHelper.clamp(green - 0.6F + this.rand.nextFloat() * 0.2F, -0.4F, 0.4F);
        float oz = MathHelper.clamp(blue - 0.6F + this.rand.nextFloat() * 0.2F, -0.4F, 0.4F);
        if (this.side.getXOffset() != 0) ox = 0.0F;
        if (this.side.getYOffset() != 0) oy = 0.0F;
        if (this.side.getZOffset() != 0) oz = 0.0F;
        this.sx = ox;
        this.sy = oy;
        this.sz = oz;
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
        float threshold = this.particleMaxAge / 5.0F;
        this.particleAlpha = this.particleAge <= threshold
                ? this.particleAge / Math.max(1.0F, threshold)
                : (this.particleMaxAge - this.particleAge) / (float) this.particleMaxAge;
        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
        this.motionY -= 0.04D * this.particleGravity;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float progress = (this.particleAge + partialTicks) / (float) this.particleMaxAge;
        int frame = Math.min(15, (int) (15.0F * progress));
        ResourceLocation frameTexture = new ResourceLocation("thaumcraft", "textures/models/hemis" + frame + ".png");

        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX) + this.sx;
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY) + this.sy;
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ) + this.sz;

        Minecraft.getMinecraft().renderEngine.bindTexture(frameTexture);
        GlStateManager.pushMatrix();
        GlStateManager.translate(px, py, pz);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.rotate(90.0F, this.side.getYOffset(), -this.side.getXOffset(), this.side.getZOffset());
        GlStateManager.rotate((float) this.rotation, 0.0F, 0.0F, 1.0F);
        if (this.side.getZOffset() > 0) {
            GlStateManager.translate(0.0D, 0.0D, 0.505D);
            GlStateManager.rotate(180.0F, 0.0F, -1.0F, 0.0F);
        } else {
            GlStateManager.translate(0.0D, 0.0D, -0.505D);
        }

        float size = this.particleScale;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        addLitVertex(buffer, -0.5D * size, 0.5D * size, 0.0D, 0.0D, 1.0D, this.particleAlpha * 0.5F);
        addLitVertex(buffer, 0.5D * size, 0.5D * size, 0.0D, 1.0D, 1.0D, this.particleAlpha * 0.5F);
        addLitVertex(buffer, 0.5D * size, -0.5D * size, 0.0D, 1.0D, 0.0D, this.particleAlpha * 0.5F);
        addLitVertex(buffer, -0.5D * size, -0.5D * size, 0.0D, 0.0D, 0.0D, this.particleAlpha * 0.5F);
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void addLitVertex(BufferBuilder buffer, double x, double y, double z,
                              double u, double v, float alpha) {
        int lightU = LIGHTMAP_FULLBRIGHT & 0xFFFF;
        int lightV = LIGHTMAP_FULLBRIGHT >> 16 & 0xFFFF;
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
                .lightmap(lightU, lightV)
                .endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
