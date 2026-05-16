package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityEldritchCrab;

import javax.annotation.Nullable;

public class RenderEldritchCrab extends RenderLiving<EntityEldritchCrab> {

    private static final ResourceLocation CRAB_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/crab.png");
    private static final ResourceLocation CRAB_OVERLAY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/craboverlay.png");

    public RenderEldritchCrab(RenderManager renderManager) {
        super(renderManager, new ModelSpider(), 0.5F);
        this.addLayer(new CrabOverlayLayer());
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEldritchCrab entity) {
        return CRAB_TEXTURE;
    }

    private final class CrabOverlayLayer implements LayerRenderer<EntityEldritchCrab> {

        @Override
        public void doRenderLayer(EntityEldritchCrab entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(CRAB_OVERLAY_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return true;
        }
    }
}
