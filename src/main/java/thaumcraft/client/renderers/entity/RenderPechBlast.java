package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.projectile.EntityPechBlast;

import javax.annotation.Nullable;

public class RenderPechBlast extends Render<EntityPechBlast> {

    public RenderPechBlast(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.1F;
    }

    @Override
    public void doRender(EntityPechBlast entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Reference renderer is an intentional no-op draw path.
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPechBlast entity) {
        return null;
    }
}
