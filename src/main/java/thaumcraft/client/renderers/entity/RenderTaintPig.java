package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityTaintPig;

import javax.annotation.Nullable;

public class RenderTaintPig extends RenderLiving<EntityTaintPig> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/pig.png");

    public RenderTaintPig(RenderManager renderManager) {
        super(renderManager, new ModelPig(), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintPig entity) {
        return TEXTURE;
    }
}
