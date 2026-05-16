package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderTaintacle<T extends EntityLiving> extends RenderLiving<T> {

    private static final ResourceLocation TAINTACLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taintacle.png");

    private final float scaleMultiplier;

    public RenderTaintacle(RenderManager renderManager, float shadowSize, float scaleMultiplier) {
        super(renderManager, new ModelSpider(), shadowSize);
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    protected void preRenderCallback(T entity, float partialTickTime) {
        if (scaleMultiplier != 1.0F) {
            GlStateManager.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return TAINTACLE_TEXTURE;
    }
}
