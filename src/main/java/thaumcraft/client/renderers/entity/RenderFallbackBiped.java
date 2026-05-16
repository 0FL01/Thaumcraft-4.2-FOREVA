package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderFallbackBiped<T extends EntityLiving> extends RenderBiped<T> {

    private final ResourceLocation texture;

    public RenderFallbackBiped(RenderManager renderManager, ModelBiped model, float shadowSize, ResourceLocation texture) {
        super(renderManager, model, shadowSize);
        this.texture = texture;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
}
