package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;

import javax.annotation.Nullable;

public class RenderEldritchGuardian extends RenderBiped<EntityEldritchGuardian> {

    private static final ResourceLocation GUARDIAN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_guardian.png");

    public RenderEldritchGuardian(RenderManager renderManager) {
        super(renderManager, new ModelBiped(), 0.6F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEldritchGuardian entity) {
        return GUARDIAN_TEXTURE;
    }
}
