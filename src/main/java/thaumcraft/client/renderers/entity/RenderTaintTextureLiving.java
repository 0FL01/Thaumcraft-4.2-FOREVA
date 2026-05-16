package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderTaintTextureLiving<T extends EntityLiving> extends RenderLiving<T> {

    private final ResourceLocation texture;

    public RenderTaintTextureLiving(RenderManager renderManager, ModelBase model, float shadowSize, ResourceLocation texture) {
        super(renderManager, model, shadowSize);
        this.texture = texture;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
}
