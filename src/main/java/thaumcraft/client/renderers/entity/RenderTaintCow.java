package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityTaintCow;

import javax.annotation.Nullable;

public class RenderTaintCow extends RenderLiving<EntityTaintCow> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/cow.png");

    public RenderTaintCow(RenderManager renderManager) {
        super(renderManager, new ModelCow(), 0.7F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintCow entity) {
        return TEXTURE;
    }
}
