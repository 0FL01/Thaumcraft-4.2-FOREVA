package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityTaintSpider;

import javax.annotation.Nullable;

public class RenderTaintSpider extends RenderLiving<EntityTaintSpider> {

    private static final ResourceLocation SPIDER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taint_spider.png");
    private static final ResourceLocation SPIDER_EYES_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taint_spider_eyes.png");

    public RenderTaintSpider(RenderManager renderManager) {
        super(renderManager, new ModelSpider(), 0.5F);
        this.addLayer(new SpiderEyesLayer());
    }

    @Override
    protected void preRenderCallback(EntityTaintSpider entity, float partialTickTime) {
        float scale = entity.spiderScaleAmount();
        GlStateManager.scale(scale, scale * 1.25F, scale);
    }

    @Override
    protected float getDeathMaxRotation(EntityTaintSpider entity) {
        return 180.0F;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintSpider entity) {
        return SPIDER_TEXTURE;
    }

    private final class SpiderEyesLayer implements LayerRenderer<EntityTaintSpider> {

        @Override
        public void doRenderLayer(EntityTaintSpider entity, float limbSwing, float limbSwingAmount, float partialTicks,
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
