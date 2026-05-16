package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityTaintSpore;

import javax.annotation.Nullable;

public class RenderTaintSpore extends RenderLiving<EntityTaintSpore> {

    private static final ResourceLocation SPORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taint_spore.png");

    public RenderTaintSpore(RenderManager renderManager) {
        super(renderManager, new ModelSlime(0), 0.25F);
    }

    @Override
    protected void preRenderCallback(EntityTaintSpore entity, float partialTickTime) {
        float display = entity.displaySize;
        if (display < entity.getSporeSize()) {
            display += 0.02F * partialTickTime;
        }
        float flutter = 0.025F * MathHelper.sin(entity.ticksExisted * 0.075F);
        float scale = Math.max(0.05F, 0.12F * display);
        GlStateManager.scale(scale - flutter, scale + flutter, scale - flutter);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintSpore entity) {
        return SPORE_TEXTURE;
    }
}
