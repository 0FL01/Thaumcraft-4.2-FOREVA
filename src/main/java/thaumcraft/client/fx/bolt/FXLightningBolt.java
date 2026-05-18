package thaumcraft.client.fx.bolt;

import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.WRVector3;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FXLightningBolt extends Particle {
    private static final ResourceLocation LARGE = new ResourceLocation("thaumcraft", "textures/misc/p_large.png");
    private static final ResourceLocation SMALL = new ResourceLocation("thaumcraft", "textures/misc/p_small.png");

    private double sourceX;
    private double sourceY;
    private double sourceZ;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float red;
    private float green;
    private float blue;
    private int segmentCount;
    private final long seed;
    private FXLightningBoltCommon main;
    private int type = -1;
    private boolean useCommonBoltSegments = false;
    private boolean boltFinalized = false;
    private boolean queuedAfterFinalize = false;
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

    public FXLightningBolt(World world, double x1, double y1, double z1,
                           double x2, double y2, double z2,
                           long seed, int duration, float multi, int speed) {
        super(world, x1, y1, z1, 0.0D, 0.0D, 0.0D);
        this.main = new FXLightningBoltCommon(world, x1, y1, z1, x2, y2, z2, seed, duration, multi, speed);
        this.seed = seed;
        setupFromMain();
    }

    public FXLightningBolt(World world, WRVector3 source, WRVector3 target, long seed) {
        super(world, source.x, source.y, source.z, 0.0D, 0.0D, 0.0D);
        this.main = new FXLightningBoltCommon(world, source, target, seed);
        this.seed = seed;
        setupFromMain();
    }

    public FXLightningBolt(World world, Entity source, Entity target, long seed) {
        super(world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.main = new FXLightningBoltCommon(world, source, target, seed);
        this.seed = seed;
        setupFromMain();
    }

    public FXLightningBolt(World world, Entity source, Entity target, long seed, int speed) {
        super(world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.main = new FXLightningBoltCommon(world, source, target, seed, speed);
        this.seed = seed;
        setupFromMain();
    }

    public FXLightningBolt(World world, TileEntity source, Entity target, long seed) {
        super(world, source.getPos().getX() + 0.5D, source.getPos().getY() + 0.5D, source.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
        this.main = new FXLightningBoltCommon(world, source, target, seed);
        this.seed = seed;
        setupFromMain();
    }

    public FXLightningBolt(World world, TileEntity source, double x, double y, double z, long seed) {
        super(world, source.getPos().getX() + 0.5D, source.getPos().getY() + 0.5D, source.getPos().getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
        this.main = new FXLightningBoltCommon(world, source, x, y, z, seed);
        this.seed = seed;
        setupFromMain();
    }

    public FXLightningBolt(World world, double x1, double y1, double z1,
                           double x2, double y2, double z2,
                           long seed, int duration, float multi) {
        this(world, x1, y1, z1, x2, y2, z2, seed, duration, multi, 1);
    }

    public FXLightningBolt(World world, double x1, double y1, double z1,
                           double x2, double y2, double z2,
                           long seed, int duration) {
        this(world, x1, y1, z1, x2, y2, z2, seed, duration, 1.0F, 1);
    }

    private void setupFromMain() {
        this.sourceX = this.main.start.x;
        this.sourceY = this.main.start.y;
        this.sourceZ = this.main.start.z;
        this.targetX = this.main.end.x;
        this.targetY = this.main.end.y;
        this.targetZ = this.main.end.z;
        this.segmentCount = Math.max(8, this.main.numsegments0);
        this.particleAge = this.main.particleAge;
        this.particleMaxAge = Math.max(3, this.main.particleMaxAge);
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.canCollide = false;
        this.useCommonBoltSegments = true;
    }

    public void defaultFractal() {
        if (this.main != null) {
            this.main.defaultFractal();
            this.segmentCount = Math.max(8, this.main.numsegments0);
        }
    }

    public void fractal(int splits, float amount, float splitchance, float splitlength, float splitangle) {
        if (this.main != null) {
            this.main.fractal(splits, amount, splitchance, splitlength, splitangle);
            this.segmentCount = Math.max(8, this.main.numsegments0);
        }
    }

    public void finalizeBolt() {
        if (this.main != null) {
            this.main.finalizeBolt();
            this.boltFinalized = true;
            if (!this.queuedAfterFinalize && this.world != null && this.world.isRemote) {
                this.queuedAfterFinalize = true;
                ParticleEngine.addEffect(this.world, this);
            }
        }
    }

    public void setType(int type) {
        this.type = type;
        applyTypeColor(type);
        if (this.main != null) {
            this.main.type = type;
        }
    }

    public void setWidth(float width) {
        this.width = Math.max(0.005F, width);
    }

    public void setMultiplier(float multiplier) {
        if (this.main != null) {
            this.main.setMultiplier(multiplier);
        }
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.main != null && this.useCommonBoltSegments) {
            this.main.onUpdate();
            this.particleAge = this.main.particleAge;
            this.particleMaxAge = Math.max(1, this.main.particleMaxAge);
            if (this.main.particleAge >= this.main.particleMaxAge) {
                this.setExpired();
            }
            return;
        }
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

        if (this.main != null && this.useCommonBoltSegments && this.boltFinalized) {
            renderCommonBolt(partialTicks);
            return;
        }

        Vec3d[] points = buildPath(this.seed + this.particleAge * 31L + (long) this.typeSalt());
        renderPass(points, LARGE, this.width * 1.25F, alphaMain, 1.0F, 1.0F, 1.0F, false);
        renderPass(points, SMALL, this.width, alphaMain, this.red, this.green, this.blue, true);
    }

    private void renderCommonBolt(float partialTicks) {
        float boltAge = this.main.particleMaxAge <= 0 ? 1.0F : Math.max(0.0F, (float) this.main.particleAge / (float) this.main.particleMaxAge);
        float alphaLarge = Math.max(0.05F, (1.0F - boltAge) * 0.4F);
        float alphaSmall = Math.max(0.05F, 1.0F - boltAge * 0.5F);
        int maxSegment = computeVisibleSegmentMax(partialTicks);
        renderSegmentPass(this.main.segments, maxSegment, LARGE, this.width * 1.25F, alphaLarge, 1.0F, 1.0F, 1.0F, false);
        renderSegmentPass(this.main.segments, maxSegment, SMALL, this.width, alphaSmall, this.red, this.green, this.blue, true);
    }

    private int computeVisibleSegmentMax(float partialTicks) {
        if (this.main == null) {
            return Integer.MAX_VALUE;
        }
        int travelWindow = Math.max(1, (int) (this.main.length * 3.0F));
        float progress = ((float) this.main.particleAge + partialTicks + (float) travelWindow) / (float) travelWindow;
        int maxSegment = (int) (progress * (float) this.main.numsegments0);
        if (maxSegment < 0) {
            return 0;
        }
        return maxSegment;
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

    private void renderSegmentPass(List<FXLightningBoltCommon.Segment> segments, int maxSegmentNo, ResourceLocation texture, float baseWidth, float alpha,
                                   float r, float g, float b, boolean additive) {
        if (segments == null || segments.isEmpty()) return;
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

        for (FXLightningBoltCommon.Segment segment : segments) {
            if (segment.segmentno > maxSegmentNo) {
                continue;
            }
            WRVector3 start = segment.startpoint.point;
            WRVector3 end = segment.endpoint.point;
            Vec3d p1 = new Vec3d(start.x, start.y, start.z);
            Vec3d p2 = new Vec3d(end.x, end.y, end.z);
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

            double widthScale = Math.max(0.4D, segment.light);
            Vec3d offset = n1.scale(baseWidth * widthScale);

            Vec3d a = p1.subtract(offset);
            Vec3d b0 = p1.add(offset);
            Vec3d c = p2.add(offset);
            Vec3d d = p2.subtract(offset);

            float segAlpha = Math.max(0.05F, alpha * segment.light);
            addVertex(buf, a, 0.0D, 1.0D, r, g, b, segAlpha);
            addVertex(buf, b0, 0.0D, 0.0D, r, g, b, segAlpha);
            addVertex(buf, c, 1.0D, 0.0D, r, g, b, segAlpha);
            addVertex(buf, d, 1.0D, 1.0D, r, g, b, segAlpha);
        }

        tess.draw();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void applyTypeColor(int type) {
        switch (type) {
            case 0:
                this.red = 1.0F;
                this.green = 0.6F;
                this.blue = 1.0F;
                break;
            case 1:
                this.red = 1.0F;
                this.green = 1.0F;
                this.blue = 0.1F;
                break;
            case 2:
                this.red = 0.1F;
                this.green = 0.1F;
                this.blue = 1.0F;
                break;
            case 3:
                this.red = 0.1F;
                this.green = 0.6F;
                this.blue = 0.1F;
                break;
            case 4:
                this.red = 1.0F;
                this.green = 0.1F;
                this.blue = 0.1F;
                break;
            case 5:
                this.red = 0.6F;
                this.green = 0.2F;
                this.blue = 0.6F;
                break;
            case 6:
                this.red = 0.75F;
                this.green = 1.0F;
                this.blue = 1.0F;
                break;
            default:
                break;
        }
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
