package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.entities.ModelTaintSpore;
import thaumcraft.common.entities.monster.EntityTaintSpore;

import javax.annotation.Nullable;

public class RenderTaintSpore extends RenderLiving<EntityTaintSpore> {

    private static final ResourceLocation SPORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taint_spore.png");

    public RenderTaintSpore(RenderManager renderManager) {
        super(renderManager, new ModelTaintSpore(), 0.25F);
    }

    @Override
    protected void preRenderCallback(EntityTaintSpore entity, float partialTickTime) {
        float displaySize = entity.displaySize;
        if (entity.displaySize < entity.getSporeSize()) {
            displaySize += 0.02F * partialTickTime;
        }
        float flutter = 0.025F * MathHelper.sin(entity.ticksExisted * 0.075F);
        float baseScale = -0.12F;
        GlStateManager.scale(baseScale * displaySize - flutter, baseScale * displaySize + flutter, baseScale * displaySize - flutter);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintSpore entity) {
        return SPORE_TEXTURE;
    }

    @Override
    protected float handleRotationFloat(EntityTaintSpore livingBase, float partialTicks) {
        return 0.0F;
    }
}
