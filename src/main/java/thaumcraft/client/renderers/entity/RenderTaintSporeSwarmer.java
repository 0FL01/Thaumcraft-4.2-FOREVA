package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityTaintSporeSwarmer;

import javax.annotation.Nullable;

public class RenderTaintSporeSwarmer extends RenderLiving<EntityTaintSporeSwarmer> {

    private static final ResourceLocation SPORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taint_spore.png");

    public RenderTaintSporeSwarmer(RenderManager renderManager) {
        super(renderManager, new ModelSlime(0), 0.25F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintSporeSwarmer entity) {
        return SPORE_TEXTURE;
    }
}
