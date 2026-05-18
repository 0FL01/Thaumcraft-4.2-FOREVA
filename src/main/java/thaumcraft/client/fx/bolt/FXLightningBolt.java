package thaumcraft.client.fx.bolt;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FXLightningBolt extends Particle {
    private static final ResourceLocation LARGE = new ResourceLocation("thaumcraft", "textures/misc/p_large.png");
    private static final ResourceLocation SMALL = new ResourceLocation("thaumcraft", "textures/misc/p_small.png");

    private final double sourceX;
    private final double sourceY;
    private final double sourceZ;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final float red;
    private final float green;
    private final float blue;
    private final int segmentCount;
    private final long seed;
    private float width = 0.03F;

    public FXLightningBolt(World world, double x, double y, double z,
                           double tx, double ty, double tz,
                           float red, float green, float blue,
                           int duration, int segmentCount) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sourceX = x;
        this.sourceY = y;
        this.sourceZ = z;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.segmentCount = Math.max(8, segmentCount);
        this.seed = world.rand.nextLong();
        this.particleMaxAge = Math.max(3, duration);
        this.canCollide = false;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float ageNorm = this.particleMaxAge <= 0 ? 1.0F : (this.particleAge + partialTicks) / (float) this.particleMaxAge;
        float alphaMain = Math.max(0.05F, (1.0F - ageNorm) * 0.5F);

        Vec3d[] points = buildPath(this.seed + this.particleAge * 31L + (long) this.typeSalt());
        renderPass(points, LARGE, this.width * 1.25F, alphaMain, 1.0F, 1.0F, 1.0F, false);
        renderPass(points, SMALL, this.width, alphaMain, this.red, this.green, this.blue, true);
    }

    private int typeSalt() {
        int c = ((int) (this.red * 255.0F) << 16) | ((int) (this.green * 255.0F) << 8) | (int) (this.blue * 255.0F);
        return c & 0xFFFF;
    }

    private Vec3d[] buildPath(long jitterSeed) {
        Vec3d[] points = new Vec3d[this.segmentCount + 1];
        Random jitterRandom = new Random(jitterSeed);
        double dx = this.targetX - this.sourceX;
        double dy = this.targetY - this.sourceY;
        double dz = this.targetZ - this.sourceZ;

        for (int i = 0; i <= this.segmentCount; i++) {
            double t = (double) i / (double) this.segmentCount;
            double taper = 1.0D - Math.abs(0.5D - t) * 2.0D;
            double jitter = 0.24D * taper;
            double px = this.sourceX + dx * t + (jitterRandom.nextFloat() - 0.5F) * jitter;
            double py = this.sourceY + dy * t + (jitterRandom.nextFloat() - 0.5F) * jitter;
            double pz = this.sourceZ + dz * t + (jitterRandom.nextFloat() - 0.5F) * jitter;
            points[i] = new Vec3d(px, py, pz);
        }
        return points;
    }

    private void renderPass(Vec3d[] points, ResourceLocation texture, float baseWidth, float alpha,
                            float r, float g, float b, boolean additive) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, additive ? GL11.GL_ONE : GL11.GL_ONE_MINUS_SRC_ALPHA);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        Vec3d view = Particle.cameraViewDir == null ? new Vec3d(0.0D, 1.0D, 0.0D) : Particle.cameraViewDir;

        for (int i = 0; i < points.length - 1; i++) {
            Vec3d p1 = points[i];
            Vec3d p2 = points[i + 1];
            Vec3d seg = p2.subtract(p1);
            if (seg.lengthSquared() < 1.0E-6D) {
                continue;
            }

            Vec3d n1 = seg.crossProduct(view);
            if (n1.lengthSquared() < 1.0E-6D) {
                n1 = seg.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
                if (n1.lengthSquared() < 1.0E-6D) {
                    n1 = new Vec3d(1.0D, 0.0D, 0.0D);
                }
            }
            n1 = n1.normalize();

            double w1 = baseWidth * (1.0D - (double) i / (double) this.segmentCount * 0.35D);
            double w2 = baseWidth * (1.0D - (double) (i + 1) / (double) this.segmentCount * 0.35D);
            Vec3d o1 = n1.scale(w1);
            Vec3d o2 = n1.scale(w2);

            Vec3d a = p1.subtract(o1);
            Vec3d b0 = p1.add(o1);
            Vec3d c = p2.add(o2);
            Vec3d d = p2.subtract(o2);

            addVertex(buf, a, 0.0D, 1.0D, r, g, b, alpha);
            addVertex(buf, b0, 0.0D, 0.0D, r, g, b, alpha);
            addVertex(buf, c, 1.0D, 0.0D, r, g, b, alpha);
            addVertex(buf, d, 1.0D, 1.0D, r, g, b, alpha);
        }

        tess.draw();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private static void addVertex(BufferBuilder buf, Vec3d worldPos, double u, double v,
                                  float r, float g, float b, float a) {
        buf.pos(worldPos.x - Particle.interpPosX, worldPos.y - Particle.interpPosY, worldPos.z - Particle.interpPosZ)
                .tex(u, v)
                .color(r, g, b, a)
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
