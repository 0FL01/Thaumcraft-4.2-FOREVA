package thaumcraft.client.fx.beams;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;

@SideOnly(Side.CLIENT)
public class FXBeam extends Particle {
    private static final ResourceLocation BEAM = new ResourceLocation("thaumcraft", "textures/misc/beam.png");
    private static final ResourceLocation BEAM_1 = new ResourceLocation("thaumcraft", "textures/misc/beam1.png");
    private static final ResourceLocation BEAM_2 = new ResourceLocation("thaumcraft", "textures/misc/beam2.png");
    private static final ResourceLocation BEAM_3 = new ResourceLocation("thaumcraft", "textures/misc/beam3.png");
    private static final ResourceLocation IMPACT = ParticleEngine.particleTexture;

    protected int particle = 16;
    protected boolean updated = false;
    protected double movX = 0.0D;
    protected double movY = 0.0D;
    protected double movZ = 0.0D;
    protected float length = 0.0F;
    protected float rotYaw = 0.0F;
    protected float rotPitch = 0.0F;
    protected float prevYaw = 0.0F;
    protected float prevPitch = 0.0F;
    protected Entity targetEntity = null;
    protected double tX = 0.0D;
    protected double tY = 0.0D;
    protected double tZ = 0.0D;
    protected double ptX = 0.0D;
    protected double ptY = 0.0D;
    protected double ptZ = 0.0D;
    protected int type = 0;
    protected float endMod = 1.0F;
    protected boolean reverse = false;
    protected boolean pulse = true;
    protected int rotationspeed = 5;
    protected float prevSize = 0.0F;
    protected int blendmode = GL11.GL_ONE;
    protected final boolean flicker;
    protected final int density;

    public FXBeam(World world, double x, double y, double z,
                  double tx, double ty, double tz,
                  float red, float green, float blue,
                  int age, boolean flicker, int density) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.flicker = flicker;
        this.density = Math.max(4, density);
        this.setSize(0.02F, 0.02F);
        this.canCollide = false;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.tX = tx;
        this.tY = ty;
        this.tZ = tz;
        this.particleScale = 1.0F;
        this.particleMaxAge = Math.max(2, age);
        updateOrientationFromEndpoints();
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;
    }

    public FXBeam(World world, double x, double y, double z, Entity target,
                  float red, float green, float blue, int age, boolean flicker, int density) {
        this(world, x, y, z, target.posX, target.posY + target.getEyeHeight() - target.height / 2.0F, target.posZ,
                red, green, blue, age, flicker, density);
        this.targetEntity = target;
    }

    protected void updateOrientationFromEndpoints() {
        float xd = (float) (this.posX - this.tX);
        float yd = (float) (this.posY - this.tY);
        float zd = (float) (this.posZ - this.tZ);
        this.length = MathHelper.sqrt(xd * xd + yd * yd + zd * zd);
        double horizontal = MathHelper.sqrt(xd * xd + zd * zd);
        this.rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
        this.rotPitch = (float) (Math.atan2(yd, horizontal) * 180.0D / Math.PI);
    }

    public void updateBeam(double xs, double ys, double zs, double x, double y, double z) {
        this.movX = xs;
        this.movY = ys;
        this.movZ = zs;
        this.tX = x;
        this.tY = y;
        this.tZ = z;
        while (this.particleMaxAge - this.particleAge < 4) {
            ++this.particleMaxAge;
        }
        this.updated = true;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setBlendMode(int blendmode) {
        this.blendmode = blendmode;
    }

    public void setBeamWidth(float width) {
        this.particleScale = Math.max(0.1F, width);
    }

    public void setEndMod(float endMod) {
        this.endMod = endMod;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void setPulse(boolean pulse) {
        this.pulse = pulse;
    }

    public void setRotationspeed(int rotationspeed) {
        this.rotationspeed = Math.max(1, rotationspeed);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.ptX = this.tX;
        this.ptY = this.tY;
        this.ptZ = this.tZ;

        if (this.updated) {
            this.setPosition(this.movX, this.movY, this.movZ);
            this.updated = false;
        }
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        if (this.targetEntity != null) {
            this.tX = this.targetEntity.posX;
            this.tY = this.targetEntity.posY + this.targetEntity.getEyeHeight() - this.targetEntity.height / 2.0F;
            this.tZ = this.targetEntity.posZ;
        }
        updateOrientationFromEndpoints();

        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        ResourceLocation beamTexture = getBeamTexture();

        float ageSlide = entityIn.ticksExisted + partialTicks;
        float size = this.particleScale;
        if (this.pulse) {
            size = Math.min(this.particleAge / 4.0F, this.particleScale);
            size = this.prevSize + (size - this.prevSize) * partialTicks;
        }
        if (size <= 0.0F) {
            return;
        }
        float alpha = 0.4F;
        if (this.pulse && this.particleMaxAge - this.particleAge <= 4) {
            alpha -= (4 - (this.particleMaxAge - this.particleAge)) * 0.1F;
        }
        if (this.flicker) {
            alpha = Math.min(1.0F, alpha + (this.rand.nextFloat() - 0.5F) * 0.15F);
        }
        alpha = Math.max(0.05F, alpha);
        alpha = getBeamAlpha(alpha);

        float scroll = this.reverse ? -(ageSlide + partialTicks) : (ageSlide + partialTicks);
        float uvSlide = -scroll * 0.2F - MathHelper.floor(-scroll * 0.1F);
        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);
        float ry = this.prevYaw + (this.rotYaw - this.prevYaw) * partialTicks;
        float rp = this.prevPitch + (this.rotPitch - this.prevPitch) * partialTicks;

        Minecraft.getMinecraft().renderEngine.bindTexture(beamTexture);
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, this.blendmode);
        GlStateManager.depthMask(false);
        GlStateManager.translate(xx, yy, zz);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F + ry, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate(rp, 1.0F, 0.0F, 0.0F);

        double w0 = -0.15D * size;
        double w1 = 0.15D * size;
        double e0 = -0.15D * size * this.endMod;
        double e1 = 0.15D * size * this.endMod;
        float rot = (this.world.getTotalWorldTime() % Math.max(1, 360 / this.rotationspeed)) * this.rotationspeed + this.rotationspeed * partialTicks;
        GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);

        int strips = getBeamStripCount();
        for (int t = 0; t < strips; t++) {
            double beamLen = this.length * size / Math.max(0.001F, this.particleScale);
            double u0 = 0.0D;
            double u1 = 1.0D;
            double v0 = -1.0F + uvSlide + getBeamStripVOffset(t, strips);
            double v1 = beamLen + v0;
            GlStateManager.rotate(getBeamStripRotation(), 0.0F, 1.0F, 0.0F);

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            addLitVertex(buf, e0, beamLen, 0.0D, u1, v1, alpha);
            addLitVertex(buf, w0, 0.0D, 0.0D, u1, v0, alpha);
            addLitVertex(buf, w1, 0.0D, 0.0D, u0, v0, alpha);
            addLitVertex(buf, e1, beamLen, 0.0D, u0, v1, alpha);
            tess.draw();
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        renderImpact(partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        this.prevSize = size;
    }

    protected float getBeamAlpha(float alpha) {
        return alpha;
    }

    protected int getBeamStripCount() {
        return Math.max(3, this.density / 8);
    }

    protected float getBeamStripVOffset(int strip, int strips) {
        return (float) strip / (float) strips;
    }

    protected float getBeamStripRotation() {
        return 60.0F;
    }

    protected void renderImpact(float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(IMPACT);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.66F);

        int part = this.particleAge % 16;
        float u0 = part / 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = 0.3125F;
        float v1 = v0 + 0.0624375F;
        float size = this.endMod / 4.0F * this.particleScale;

        float tx = (float) (this.ptX + (this.tX - this.ptX) * partialTicks - Particle.interpPosX);
        float ty = (float) (this.ptY + (this.tY - this.ptY) * partialTicks - Particle.interpPosY);
        float tz = (float) (this.ptZ + (this.tZ - this.ptZ) * partialTicks - Particle.interpPosZ);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        addLitVertex(buf, tx - rotationX * size - rotationXY * size, ty - rotationZ * size, tz - rotationYZ * size - rotationXZ * size, u1, v1, 0.66F);
        addLitVertex(buf, tx - rotationX * size + rotationXY * size, ty + rotationZ * size, tz - rotationYZ * size + rotationXZ * size, u1, v0, 0.66F);
        addLitVertex(buf, tx + rotationX * size + rotationXY * size, ty + rotationZ * size, tz + rotationYZ * size + rotationXZ * size, u0, v0, 0.66F);
        addLitVertex(buf, tx + rotationX * size - rotationXY * size, ty - rotationZ * size, tz + rotationYZ * size - rotationXZ * size, u0, v1, 0.66F);
        tess.draw();

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    protected ResourceLocation getBeamTexture() {
        switch (this.type) {
            case 1:
                return BEAM_1;
            case 2:
                return BEAM_2;
            case 3:
                return BEAM_3;
            default:
                return BEAM;
        }
    }

    private void addLitVertex(BufferBuilder buf, double x, double y, double z, double u, double v, float alpha) {
        buf.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
                .lightmap(240, 240)
                .endVertex();
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
