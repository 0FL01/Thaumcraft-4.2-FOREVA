package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderNoop<T extends Entity> extends Render<T> {

    private static final ResourceLocation NOOP_TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public RenderNoop(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return NOOP_TEXTURE;
    }
}
