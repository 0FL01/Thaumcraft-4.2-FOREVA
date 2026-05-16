package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityThaumicSlime;

import javax.annotation.Nullable;

public class RenderThaumicSlime extends RenderLiving<EntityThaumicSlime> {

    private static final ResourceLocation SLIME_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/tslime.png");
    private final ModelSlime gelModel = new ModelSlime(0);

    public RenderThaumicSlime(RenderManager renderManager) {
        super(renderManager, new ModelSlime(16), 0.5F);
        this.addLayer(new SlimeGelLayer());
    }

    @Override
    protected void preRenderCallback(EntityThaumicSlime entity, float partialTickTime) {
        float slimeSize = (float) Math.sqrt(entity.getSlimeSize());
        float squish = entity.field_70812_c + (entity.field_70811_b - entity.field_70812_c) * partialTickTime;
        float f4 = squish / (slimeSize * 0.25F + 1.0F);
        float f5 = 1.0F / (f4 + 1.0F);
        GlStateManager.scale(f5 * slimeSize + 0.1F, 1.0F / f5 * slimeSize + 0.1F, f5 * slimeSize + 0.1F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityThaumicSlime entity) {
        return SLIME_TEXTURE;
    }

    private final class SlimeGelLayer implements LayerRenderer<EntityThaumicSlime> {

        @Override
        public void doRenderLayer(EntityThaumicSlime entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.isInvisible()) {
                return;
            }
            bindTexture(SLIME_TEXTURE);
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            gelModel.setModelAttributes(getMainModel());
            gelModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }

        @Override
        public boolean shouldCombineTextures() {
            return true;
        }
    }
}
