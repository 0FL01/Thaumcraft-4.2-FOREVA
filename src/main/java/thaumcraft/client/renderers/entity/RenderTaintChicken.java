package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityTaintChicken;

import javax.annotation.Nullable;

public class RenderTaintChicken extends RenderLiving<EntityTaintChicken> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/chicken.png");

    public RenderTaintChicken(RenderManager renderManager) {
        super(renderManager, new ModelChicken(), 0.3F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintChicken entity) {
        return TEXTURE;
    }

    @Override
    protected float handleRotationFloat(EntityTaintChicken livingBase, float partialTicks) {
        float wingRotation = livingBase.field_756_e + (livingBase.field_752_b - livingBase.field_756_e) * partialTicks;
        float wingSpread = livingBase.field_757_d + (livingBase.destPos - livingBase.field_757_d) * partialTicks;
        return (MathHelper.sin(wingRotation) + 1.0F) * wingSpread;
    }
}
