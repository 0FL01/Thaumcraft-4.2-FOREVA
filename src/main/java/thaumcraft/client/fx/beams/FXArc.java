package thaumcraft.client.fx.beams;

import java.util.ArrayList;
import java.util.List;
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
public class FXArc extends Particle {
    private static final ResourceLocation ARC_TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/beamh.png");

    public int particle = 16;
    public int blendmode = GL11.GL_ONE;
    public float length = 1.0F;
    private final List<Vec3d> points = new ArrayList<>();

    public FXArc(World world, double x, double y, double z,
                 double tx, double ty, double tz,
                 float red, float green, float blue, double heightGain) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.setSize(0.02F, 0.02F);
        this.particleMaxAge = 1;
        this.canCollide = false;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        buildArcPoints(tx - x, ty - y, tz - z, heightGain);
    }

    private void buildArcPoints(double tx, double ty, double tz, double heightGain) {
        Vec3d start = new Vec3d(0.0D, 0.0D, 0.0D);
        Vec3d end = new Vec3d(tx, ty, tz);
        Vec3d delta = end.subtract(start);
        this.length = Math.max(1.0F, (float) delta.length());
        int segments = Math.max(8, Math.min(50, (int) (this.length * 7.0D)));

        this.points.clear();
        points.add(start);
        for (int i = 1; i < segments; i++) {
            double t = (double) i / (double) segments;
            double px = delta.x * t;
            double py = delta.y * t;
            double pz = delta.z * t;

            double parabola = 4.0D * t * (1.0D - t);
            py += parabola * heightGain;
            double noise = 0.25D;
            px += (rand.nextDouble() - rand.nextDouble()) * noise;
            py += (rand.nextDouble() - rand.nextDouble()) * noise;
            pz += (rand.nextDouble() - rand.nextDouble()) * noise;
            this.points.add(new Vec3d(px, py, pz));
        }
        this.points.add(end);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.world == null || !this.world.isRemote || ++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.points.isEmpty()) {
            return;
        }

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);
        float size = 0.25F;

        Minecraft.getMinecraft().renderEngine.bindTexture(ARC_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, this.blendmode);
        GlStateManager.disableCull();

        buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        for (int i = 0; i < this.points.size(); i++) {
            Vec3d point = this.points.get(i);
            float u = i / this.length;
            addLitVertex(buf, point.x, point.y - size, point.z, u, 1.0D, 0.8F);
            addLitVertex(buf, point.x, point.y + size, point.z, u, 0.0D, 0.8F);
        }
        tess.draw();

        buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        for (int i = 0; i < this.points.size(); i++) {
            Vec3d point = this.points.get(i);
            float u = i / this.length;
            addLitVertex(buf, point.x - size, point.y, point.z - size, u, 1.0D, 0.8F);
            addLitVertex(buf, point.x + size, point.y, point.z + size, u, 0.0D, 0.8F);
        }
        tess.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
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
