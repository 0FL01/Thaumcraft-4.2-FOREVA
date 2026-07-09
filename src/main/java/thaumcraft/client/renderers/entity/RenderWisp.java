package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;

import javax.annotation.Nullable;
import java.awt.Color;

public class RenderWisp extends Render<EntityWisp> {

    private static final float FULL_BRIGHT = 240.0F;

    private static final ResourceLocation WISP_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/wisp.png");
    private static final ResourceLocation PARTICLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particles.png");

    public RenderWisp(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
        this.shadowOpaque = 0.0F;
    }

    @Override
    public void doRender(EntityWisp entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!entity.isEntityAlive()) {
            return;
        }
        Aspect aspect = Aspect.getAspect(entity.getWispType());
        Color color = aspect != null ? new Color(aspect.getColor()) : Color.WHITE;
        float red = color.getRed() / 255.0F;
        float green = color.getGreen() / 255.0F;
        float blue = color.getBlue() / 255.0F;
        if (entity.hurtTime > 0) {
            red = 1.0F;
            green = color.getGreen() / 300.0F;
            blue = color.getBlue() / 300.0F;
        }

        float previousBrightnessX = OpenGlHelper.lastBrightnessX;
        float previousBrightnessY = OpenGlHelper.lastBrightnessY;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.45D, z);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        renderCore(entity, red, green, blue);
        renderHalo(entity, partialTicks);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit, previousBrightnessX, previousBrightnessY);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderCore(EntityWisp entity, float red, float green, float blue) {
        bindTexture(WISP_TEXTURE);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, FULL_BRIGHT, FULL_BRIGHT);

        int frame = entity.ticksExisted % 16;
        float uMin = (frame % 4) / 4.0F;
        float uMax = uMin + 0.25F;
        float vMin = (frame / 4) / 4.0F;
        float vMax = vMin + 0.25F;
        drawBillboard(1.0F, uMin, uMax, vMin, vMax, red, green, blue, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    private void renderHalo(EntityWisp entity, float partialTicks) {
        bindTexture(PARTICLE_TEXTURE);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, FULL_BRIGHT, FULL_BRIGHT);

        int frame = entity.ticksExisted % 16;
        float uMin = frame / 16.0F;
        float uMax = uMin + 1.0F / 16.0F;
        float pulse = 0.4F + MathHelper.sin((entity.ticksExisted + partialTicks) / 10.0F) * 0.1F;
        drawBillboard(pulse, uMin, uMax, 5.0F / 16.0F, 6.0F / 16.0F, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(516, 0.1F);
    }

    private void drawBillboard(float scale, float uMin, float uMax, float vMin, float vMax,
                               float red, float green, float blue, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(
                (this.renderManager.options.thirdPersonView == 2 ? -1.0F : 1.0F) * -this.renderManager.playerViewX,
                1.0F,
                0.0F,
                0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-scale, -scale, 0.0D).tex(uMax, vMax).color(red, green, blue, alpha).endVertex();
        buffer.pos(scale, -scale, 0.0D).tex(uMin, vMax).color(red, green, blue, alpha).endVertex();
        buffer.pos(scale, scale, 0.0D).tex(uMin, vMin).color(red, green, blue, alpha).endVertex();
        buffer.pos(-scale, scale, 0.0D).tex(uMax, vMin).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityWisp entity) {
        return WISP_TEXTURE;
    }
}
