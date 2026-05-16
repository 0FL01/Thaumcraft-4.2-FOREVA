package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityPech;

import javax.annotation.Nullable;

public class RenderPech extends RenderBiped<EntityPech> {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            new ResourceLocation("thaumcraft", "textures/models/pech_forage.png"),
            new ResourceLocation("thaumcraft", "textures/models/pech_thaum.png"),
            new ResourceLocation("thaumcraft", "textures/models/pech_stalker.png")
    };

    public RenderPech(RenderManager manager) {
        super(manager, new ModelBiped(), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPech entity) {
        int type = entity.getPechType();
        if (type < 0 || type >= TEXTURES.length) {
            type = 0;
        }
        return TEXTURES[type];
    }
}
