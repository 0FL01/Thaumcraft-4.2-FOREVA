package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;

import javax.annotation.Nullable;

public class RenderCultistPortal extends RenderBiped<EntityCultistPortal> {

    private static final ResourceLocation PORTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/cultist_portal.png");

    public RenderCultistPortal(RenderManager renderManager) {
        super(renderManager, new ModelBiped(), 0.8F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCultistPortal entity) {
        return PORTAL_TEXTURE;
    }
}
