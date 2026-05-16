package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.projectile.EntityEmber;

import javax.annotation.Nullable;

public class RenderEmber extends Render<EntityEmber> {

    private static final ResourceLocation PARTICLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particles.png");

    public RenderEmber(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityEmber entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        bindTexture(PARTICLE_TEXTURE);

        int frame = (int) (8.0F * ((float) entity.ticksExisted / (float) entity.duration));
        float uMin = (7 + frame) / 16.0F;
        float uMax = uMin + 0.0625F;
        float vMin = 0.5625F;
        float vMax = vMin + 0.0625F;
        float origin = 0.5F;
        float lifeFraction = (float) entity.ticksExisted / (float) entity.duration;
        float scale = 0.25F + lifeFraction;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 220.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-origin, -origin, 0.0D).tex(uMin, vMax).color(255, 255, 255, 230).endVertex();
        buffer.pos(1.0F - origin, -origin, 0.0D).tex(uMax, vMax).color(255, 255, 255, 230).endVertex();
        buffer.pos(1.0F - origin, 1.0F - origin, 0.0D).tex(uMax, vMin).color(255, 255, 255, 230).endVertex();
        buffer.pos(-origin, 1.0F - origin, 0.0D).tex(uMin, vMin).color(255, 255, 255, 230).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEmber entity) {
        return PARTICLE_TEXTURE;
    }
}
