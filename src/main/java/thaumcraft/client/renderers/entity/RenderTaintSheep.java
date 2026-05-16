package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entities.ModelTaintSheep1;
import thaumcraft.client.renderers.models.entities.ModelTaintSheep2;
import thaumcraft.common.entities.monster.EntityTaintSheep;

import javax.annotation.Nullable;

public class RenderTaintSheep extends RenderLiving<EntityTaintSheep> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/sheep.png");
    private static final ResourceLocation FUR_TEXTURE = new ResourceLocation("thaumcraft", "textures/models/sheep_fur.png");

    public RenderTaintSheep(RenderManager renderManager) {
        super(renderManager, new ModelTaintSheep2(), 0.7F);
        this.addLayer(new SheepFurLayer(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintSheep entity) {
        return TEXTURE;
    }

    private static final class SheepFurLayer implements LayerRenderer<EntityTaintSheep> {
        private final RenderTaintSheep renderer;
        private final ModelTaintSheep1 furModel = new ModelTaintSheep1();

        private SheepFurLayer(RenderTaintSheep renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(EntityTaintSheep entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.getSheared()) {
                return;
            }
            renderer.bindTexture(FUR_TEXTURE);
            furModel.setModelAttributes(renderer.getMainModel());
            furModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            furModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

        @Override
        public boolean shouldCombineTextures() {
            return true;
        }
    }
}
