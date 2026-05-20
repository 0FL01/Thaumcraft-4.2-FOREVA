package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityMindSpider;

import javax.annotation.Nullable;

public class RenderMindSpider extends RenderLiving<EntityMindSpider> {

    private static final ResourceLocation SPIDER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taint_spider.png");
    private static final ResourceLocation SPIDER_EYES_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taint_spider_eyes.png");

    public RenderMindSpider(RenderManager renderManager) {
        super(renderManager, new ModelSpider(), 0.0F);
        this.addLayer(new SpiderEyesLayer());
    }

    @Override
    public void doRender(EntityMindSpider entity, double x, double y, double z, float entityYaw, float partialTicks) {
        String viewer = entity.getViewer();
        if (viewer.length() == 0) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            return;
        }
        if (Minecraft.getMinecraft().player != null && viewer.equals(Minecraft.getMinecraft().player.getName())) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    protected void preRenderCallback(EntityMindSpider entity, float partialTickTime) {
        float scale = entity.spiderScaleAmount();
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected float getDeathMaxRotation(EntityMindSpider entity) {
        return 180.0F;
    }

    @Override
    protected void renderModel(EntityMindSpider entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                               float netHeadYaw, float headPitch, float scaleFactor) {
        bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, Math.min(0.1F, entity.ticksExisted / 100.0F));
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityMindSpider entity) {
        return SPIDER_TEXTURE;
    }

    private final class SpiderEyesLayer implements LayerRenderer<EntityMindSpider> {

        @Override
        public void doRenderLayer(EntityMindSpider entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(SPIDER_EYES_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.depthMask(!entity.isInvisible());
            int i = 61680;
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
