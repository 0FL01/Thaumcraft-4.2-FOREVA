package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.blocks.ItemBlocks.BlockCustomOreItem;

import javax.annotation.Nullable;
import java.util.Random;

public class RenderEldritchOrb extends Render<Entity> {

    private static final ResourceLocation PARTICLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particles.png");

    public RenderEldritchOrb(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        renderSpikeBurst(entity, x, y, z);
        renderBillboard(entity, x, y, z);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderSpikeBurst(Entity entity, double x, double y, double z) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Random random = new Random(entity.getEntityId());
        float ageSpin = entity.ticksExisted / 80.0F;

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);

        for (int i = 0; i < 12; ++i) {
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F + ageSpin * 360.0F, 0.0F, 0.0F, 1.0F);

            float scaleAge = Math.min(entity.ticksExisted, 10) / 10.0F;
            float length = (random.nextFloat() * 20.0F + 5.0F) * (scaleAge / 30.0F);
            float width = (random.nextFloat() * 2.0F + 1.0F) * (scaleAge / 30.0F);

            int inner = 0xFFFFFF;
            int outer = BlockCustomOreItem.colors[5];

            buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            addColorVertex(buffer, 0.0D, 0.0D, 0.0D, inner, 255);
            addColorVertex(buffer, -0.866D * width, length, -0.5D * width, outer, 0);
            addColorVertex(buffer, 0.866D * width, length, -0.5D * width, outer, 0);
            addColorVertex(buffer, 0.0D, length, 1.0D * width, outer, 0);
            addColorVertex(buffer, -0.866D * width, length, -0.5D * width, outer, 0);
            tessellator.draw();
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void renderBillboard(Entity entity, double x, double y, double z) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(PARTICLE_TEXTURE);

        float uMin = (entity.ticksExisted % 13) / 16.0F;
        float uMax = uMin + 0.0624375F;
        float vMin = 0.1875F;
        float vMax = vMin + 0.0624375F;
        float origin = 0.5F;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-origin, -origin, 0.0D).tex(uMin, vMax).endVertex();
        buffer.pos(1.0F - origin, -origin, 0.0D).tex(uMax, vMax).endVertex();
        buffer.pos(1.0F - origin, 1.0F - origin, 0.0D).tex(uMax, vMin).endVertex();
        buffer.pos(-origin, 1.0F - origin, 0.0D).tex(uMin, vMin).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void addColorVertex(BufferBuilder buffer, double x, double y, double z, int color, int alpha) {
        buffer.pos(x, y, z)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                .endVertex();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return PARTICLE_TEXTURE;
    }
}
