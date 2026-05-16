package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderCultist<T extends EntityLiving> extends RenderBiped<T> {

    private static final ResourceLocation CULTIST_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/cultist.png");

    public RenderCultist(RenderManager renderManager, float shadowSize) {
        super(renderManager, new ModelBiped(), shadowSize);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return CULTIST_TEXTURE;
    }
}
