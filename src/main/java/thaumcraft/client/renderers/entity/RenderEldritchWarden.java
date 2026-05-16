package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;

import javax.annotation.Nullable;

public class RenderEldritchWarden extends RenderBiped<EntityEldritchWarden> {

    private static final ResourceLocation WARDEN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_warden.png");

    public RenderEldritchWarden(RenderManager renderManager) {
        super(renderManager, new ModelBiped(), 0.8F);
    }

    @Override
    protected void preRenderCallback(EntityEldritchWarden entity, float partialTickTime) {
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEldritchWarden entity) {
        return WARDEN_TEXTURE;
    }
}
