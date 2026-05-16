package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.projectile.EntityDart;

import javax.annotation.Nullable;

public class RenderDart extends RenderArrow<EntityDart> {

    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation("textures/entity/arrow.png");

    public RenderDart(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityDart entity) {
        return ARROW_TEXTURE;
    }
}
