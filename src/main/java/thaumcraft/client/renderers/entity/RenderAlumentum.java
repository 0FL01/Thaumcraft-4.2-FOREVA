package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.projectile.EntityAlumentum;

import javax.annotation.Nullable;

public class RenderAlumentum extends Render<EntityAlumentum> {

    public RenderAlumentum(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.1F;
    }

    @Override
    public void doRender(EntityAlumentum entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Reference renderer is an intentional no-op draw path.
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityAlumentum entity) {
        return null;
    }
}
