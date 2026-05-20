package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entities.ModelPech;
import thaumcraft.common.entities.monster.EntityPech;

import javax.annotation.Nullable;

public class RenderPech extends RenderLiving<EntityPech> {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            new ResourceLocation("thaumcraft", "textures/models/pech_forage.png"),
            new ResourceLocation("thaumcraft", "textures/models/pech_thaum.png"),
            new ResourceLocation("thaumcraft", "textures/models/pech_stalker.png")
    };

    public RenderPech(RenderManager manager) {
        super(manager, new ModelPech(), 0.5F);
        this.addLayer(new PechHeldItemLayer(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPech entity) {
        int type = entity.getPechType();
        if (type < 0 || type >= TEXTURES.length) {
            type = 0;
        }
        return TEXTURES[type];
    }

    private static final class PechHeldItemLayer implements LayerRenderer<EntityPech> {
        private final RenderPech renderer;

        private PechHeldItemLayer(RenderPech renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(EntityPech entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            ItemStack held = entity.getHeldItemMainhand();
            if (held.isEmpty()) {
                return;
            }
            GlStateManager.pushMatrix();
            ((ModelPech) this.renderer.getMainModel()).rightArm.postRender(0.0625F);
            GlStateManager.translate(-0.0625F, 0.3375F, 0.0625F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(
                    entity,
                    held,
                    ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    false);
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
