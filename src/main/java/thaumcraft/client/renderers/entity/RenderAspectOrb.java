package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.EntityAspectOrb;

import javax.annotation.Nullable;

public class RenderAspectOrb extends Render<EntityAspectOrb> {

    private static final ResourceLocation PARTICLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particles.png");

    public RenderAspectOrb(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.1F;
        this.shadowOpaque = 0.5F;
    }

    @Override
    public void doRender(EntityAspectOrb orb, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableBlend();
        GlStateManager.DestFactor dst = orb.getAspect() != null
                ? mapDestBlendFactor(orb.getAspect().getBlend())
                : GlStateManager.DestFactor.ONE;
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, dst);
        bindTexture(PARTICLE_TEXTURE);

        int frame = (int) (System.nanoTime() / 25000000L % 16L);
        float uMin = frame / 16.0F;
        float uMax = (frame + 1) / 16.0F;
        float vMin = 0.5F;
        float vMax = 0.5625F;
        float originX = 0.5F;
        float originY = 0.25F;

        int packedLight = orb.getBrightnessForRender();
        int lightU = packedLight % 65536;
        int lightV = packedLight / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, lightV);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        float scale = 0.1F + 0.3F * ((float) (orb.orbMaxAge - orb.orbAge) / (float) orb.orbMaxAge);
        GlStateManager.scale(scale, scale, scale);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        int color = orb.getAspect() != null ? orb.getAspect().getColor() : 0xFFFFFF;
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int alpha = 128;

        buffer.pos(0.0F - originX, 0.0F - originY, 0.0D).tex(uMin, vMax).color(red, green, blue, alpha).endVertex();
        buffer.pos(1.0F - originX, 0.0F - originY, 0.0D).tex(uMax, vMax).color(red, green, blue, alpha).endVertex();
        buffer.pos(1.0F - originX, 1.0F - originY, 0.0D).tex(uMax, vMin).color(red, green, blue, alpha).endVertex();
        buffer.pos(0.0F - originX, 1.0F - originY, 0.0D).tex(uMin, vMin).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(orb, x, y, z, entityYaw, partialTicks);
    }

    private GlStateManager.DestFactor mapDestBlendFactor(int glBlendFactor) {
        switch (glBlendFactor) {
            case 0:
                return GlStateManager.DestFactor.ZERO;
            case 1:
                return GlStateManager.DestFactor.ONE;
            case 768:
                return GlStateManager.DestFactor.SRC_COLOR;
            case 769:
                return GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR;
            case 770:
                return GlStateManager.DestFactor.SRC_ALPHA;
            case 771:
                return GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
            case 772:
                return GlStateManager.DestFactor.DST_ALPHA;
            case 773:
                return GlStateManager.DestFactor.ONE_MINUS_DST_ALPHA;
            case 774:
                return GlStateManager.DestFactor.DST_COLOR;
            case 775:
                return GlStateManager.DestFactor.ONE_MINUS_DST_COLOR;
            default:
                return GlStateManager.DestFactor.ONE;
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityAspectOrb entity) {
        return PARTICLE_TEXTURE;
    }
}
