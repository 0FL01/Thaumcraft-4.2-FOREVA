package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;

import javax.annotation.Nullable;

public class RenderEldritchGolem extends RenderBiped<EntityEldritchGolem> {

    private static final ResourceLocation GOLEM_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_golem.png");

    public RenderEldritchGolem(RenderManager renderManager) {
        super(renderManager, new ModelBiped(), 0.9F);
    }

    @Override
    protected void preRenderCallback(EntityEldritchGolem entity, float partialTickTime) {
        GlStateManager.scale(2.15F, 2.15F, 2.15F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEldritchGolem entity) {
        return GOLEM_TEXTURE;
    }
}
