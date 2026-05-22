package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.EntityCultistCleric;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Random;

public class RenderCultist<T extends EntityLiving> extends RenderBiped<T> {

    private static final ResourceLocation CULTIST_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/cultist.png");
    private static final ResourceLocation WISPY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

    public RenderCultist(RenderManager renderManager, float shadowSize) {
        super(renderManager, new ModelBiped(), shadowSize);
        this.addLayer(new LayerBipedArmor(this));
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        boolean ritualist = entity instanceof EntityCultistCleric
                && ((EntityCultistCleric) entity).getIsRitualist();
        float bob = 0.0F;
        if (ritualist) {
            int seededOffset = new Random(entity.getEntityId()).nextInt(1000);
            float cycle = entity.ticksExisted + partialTicks + seededOffset;
            bob = MathHelper.sin(cycle / 9.0F) * 0.1F + 0.21F;
        }

        super.doRender(entity, x, y + bob, z, entityYaw, partialTicks);

        if (ritualist) {
            EntityCultistCleric cleric = (EntityCultistCleric) entity;
            BlockPos home = cleric.getHomePosition();
            if (home != null) {
                drawFloatyLine(
                        x,
                        y + entity.getEyeHeight() * 1.2F + bob,
                        z,
                        home.getX() + 0.5D - this.renderManager.viewerPosX,
                        home.getY() + 1.5D - bob - this.renderManager.viewerPosY,
                        home.getZ() + 0.5D - this.renderManager.viewerPosZ,
                        partialTicks,
                        0x110011,
                        -0.03F,
                        Math.min(entity.ticksExisted, 10) / 10.0F,
                        0.25F);
            }
        }
    }

    @Override
    protected void preRenderCallback(T entity, float partialTickTime) {
        if (entity instanceof EntityCultistLeader) {
            GlStateManager.scale(1.25F, 1.25F, 1.25F);
        }
    }

    private void drawFloatyLine(double x, double y, double z, double x2, double y2, double z2,
                                float partialTicks, int color, float speed, float distance, float width) {
        if (distance <= 0.0F) {
            return;
        }
        Color tint = new Color(color);
        float red = tint.getRed() / 255.0F;
        float green = tint.getGreen() / 255.0F;
        float blue = tint.getBlue() / 255.0F;
        double dx = x - x2;
        double dy = y - y2;
        double dz = z - z2;
        float dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        float blocks = Math.round(dist);
        float length = Math.max(1.0F, blocks * (Config.golemLinkQuality / 2.0F));
        float time = System.nanoTime() / 30000000L;

        bindTexture(WISPY_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        drawFloatyStrip(x, y, z, x2, y2, z2, dist, length, time, red, green, blue, speed, distance, width, true);
        drawFloatyStrip(x, y, z, x2, y2, z2, dist, length, time, red, green, blue, speed, distance, width, false);

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawFloatyStrip(double x, double y, double z, double x2, double y2, double z2,
                                 float dist, float length, float time,
                                 float red, float green, float blue,
                                 float speed, float distance, float width, boolean vertical) {
        double deltaX = x - x2;
        double deltaY = y - y2;
        double deltaZ = z - z2;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);

        int maxSegment = Math.max(1, (int) (length * distance));
        for (int i = 0; i <= maxSegment; i++) {
            float beamPos = i / length;
            float centerWeight = 1.0F - Math.abs(i - length / 2.0F) / (length / 2.0F);
            centerWeight = Math.max(0.0F, centerWeight);
            double waveX = MathHelper.sin((float) ((z % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 4.0D)) * 0.5F * centerWeight;
            double waveY = MathHelper.sin((float) ((x % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 3.0D)) * 0.5F * centerWeight;
            double waveZ = MathHelper.sin((float) ((y % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 2.0D)) * 0.5F * centerWeight;
            double beamX = deltaX + waveX;
            double beamY = deltaY + waveY;
            double beamZ = deltaZ + waveZ;
            float texU = (1.0F - beamPos) * dist - time * speed;

            if (vertical) {
                buffer.pos(x2 + beamX * beamPos, y2 + beamY * beamPos - width, z2 + beamZ * beamPos)
                        .tex(texU, 1.0F).color(red, green, blue, 0.8F).endVertex();
                buffer.pos(x2 + beamX * beamPos, y2 + beamY * beamPos + width, z2 + beamZ * beamPos)
                        .tex(texU, 0.0F).color(red, green, blue, 0.8F).endVertex();
            } else {
                buffer.pos(x2 + beamX * beamPos - width, y2 + beamY * beamPos, z2 + beamZ * beamPos)
                        .tex(texU, 1.0F).color(red, green, blue, 0.8F).endVertex();
                buffer.pos(x2 + beamX * beamPos + width, y2 + beamY * beamPos, z2 + beamZ * beamPos)
                        .tex(texU, 0.0F).color(red, green, blue, 0.8F).endVertex();
            }
        }

        tessellator.draw();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return CULTIST_TEXTURE;
    }
}
