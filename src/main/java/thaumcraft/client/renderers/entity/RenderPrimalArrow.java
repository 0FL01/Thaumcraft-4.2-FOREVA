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
import thaumcraft.common.blocks.ItemBlocks.BlockCustomOreItem;
import thaumcraft.common.entities.projectile.EntityPrimalArrow;

import javax.annotation.Nullable;

public class RenderPrimalArrow extends Render<EntityPrimalArrow> {

    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation("textures/entity/arrow.png");
    private static final ResourceLocation WISP_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/wisp.png");

    public RenderPrimalArrow(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityPrimalArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        int[] colors = BlockCustomOreItem.colors;
        int colorIndex = MathHelper.clamp(entity.getArrowType() + 1, 0, colors.length - 1);
        int color = colors[colorIndex];
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = MathHelper.clamp((100.0F - entity.getPrimalTimeInGround()) / 100.0F, 0.0F, 1.0F);
        this.renderArrowShaft(entity, x, y, z, partialTicks, alpha);
        float previousBrightnessX = OpenGlHelper.lastBrightnessX;
        float previousBrightnessY = OpenGlHelper.lastBrightnessY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(
                (this.renderManager.options.thirdPersonView == 2 ? -1.0F : 1.0F) * -this.renderManager.playerViewX,
                1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        if (entity.getArrowType() < 5) {
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        } else {
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        this.bindTexture(WISP_TEXTURE);

        int frame = entity.ticksExisted % 16;
        float uMin = (frame % 4) / 4.0F;
        float uMax = uMin + 0.25F - 0.01F / 16.0F;
        float vMin = (frame / 4) / 4.0F;
        float vMax = vMin + 0.25F - 0.01F / 16.0F;
        float size = 0.5F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-size, -size, 0.0D).tex(uMax, vMax).color(red, green, blue, alpha).endVertex();
        buffer.pos(size, -size, 0.0D).tex(uMin, vMax).color(red, green, blue, alpha).endVertex();
        buffer.pos(size, size, 0.0D).tex(uMin, vMin).color(red, green, blue, alpha).endVertex();
        buffer.pos(-size, size, 0.0D).tex(uMax, vMin).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit, previousBrightnessX, previousBrightnessY);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderArrowShaft(EntityPrimalArrow entity, double x, double y, double z,
                                  float partialTicks, float alpha) {
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F,
                0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
                0.0F, 0.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        float shake = entity.arrowShake - partialTicks;
        if (shake > 0.0F) {
            GlStateManager.rotate(-MathHelper.sin(shake * 3.0F) * shake, 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.glNormal3f(0.05625F, 0.0F, 0.0F);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        buffer.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        buffer.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        buffer.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();
        GlStateManager.glNormal3f(-0.05625F, 0.0F, 0.0F);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        buffer.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        buffer.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        buffer.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();
        for (int side = 0; side < 4; ++side) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, 0.05625F);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-8.0D, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            buffer.pos(8.0D, -2.0D, 0.0D).tex(0.5D, 0.0D).endVertex();
            buffer.pos(8.0D, 2.0D, 0.0D).tex(0.5D, 0.15625D).endVertex();
            buffer.pos(-8.0D, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
            tessellator.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPrimalArrow entity) {
        return ARROW_TEXTURE;
    }
}
