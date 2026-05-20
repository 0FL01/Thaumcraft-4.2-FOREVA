package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entities.ModelTrunk;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;

import javax.annotation.Nullable;

public class RenderTravelingTrunk extends RenderLiving<EntityTravelingTrunk> {

    private static final ResourceLocation TRUNK_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/trunk.png");
    private static final ResourceLocation TRUNK_ANGRY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/trunkangry.png");

    private final ModelTrunk trunkModel;

    public RenderTravelingTrunk(RenderManager renderManager) {
        super(renderManager, new ModelTrunk(), 0.6F);
        this.trunkModel = (ModelTrunk) this.mainModel;
    }

    @Override
    protected void preRenderCallback(EntityTravelingTrunk entity, float partialTickTime) {
        this.adjustTrunk(entity, partialTickTime);
    }

    private void adjustTrunk(EntityTravelingTrunk entity, float partialTickTime) {
        int baseScale = 2;
        float squish = (entity.field_767_b + (entity.field_768_a - entity.field_767_b) * partialTickTime)
                / (baseScale * 0.5F + 1.0F);
        float inverseSquish = 1.0F / (squish + 1.0F);
        float scale = baseScale;
        squish /= 1.5F;
        inverseSquish /= 1.4F;
        scale = entity.getUpgrade() == 1 ? scale / 1.33F : scale / 1.5F;
        GlStateManager.scale(inverseSquish * scale, 0.5F / inverseSquish * scale, inverseSquish * scale);
        GlStateManager.translate(-0.5F, 0.5F, -0.5F);

        float lidAngle = 1.0F - entity.lidrot;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        this.trunkModel.chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2.0F));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTravelingTrunk entity) {
        return entity.getAnger() > 0 ? TRUNK_ANGRY_TEXTURE : TRUNK_TEXTURE;
    }
}
