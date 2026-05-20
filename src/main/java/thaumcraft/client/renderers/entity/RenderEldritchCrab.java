package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entities.ModelEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchCrab;

import javax.annotation.Nullable;

public class RenderEldritchCrab extends RenderLiving<EntityEldritchCrab> {

    private static final ResourceLocation CRAB_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/crab.png");
    private static final ResourceLocation CRAB_OVERLAY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/craboverlay.png");

    public RenderEldritchCrab(RenderManager renderManager) {
        super(renderManager, new ModelEldritchCrab(), 1.0F);
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
            if (entity.isInvisible()) {
                GlStateManager.depthMask(false);
            } else {
                GlStateManager.depthMask(true);
            }
            int packedLight = 200;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, packedLight, 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
