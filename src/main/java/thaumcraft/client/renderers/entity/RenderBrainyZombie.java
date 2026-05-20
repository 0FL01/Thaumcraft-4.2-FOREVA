package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;

import javax.annotation.Nullable;

public class RenderBrainyZombie extends RenderZombie {

    private static final ResourceLocation BRAINY_ZOMBIE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/bzombie.png");

    public RenderBrainyZombie(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void preRenderCallback(EntityZombie entity, float partialTickTime) {
        if (entity instanceof EntityGiantBrainyZombie) {
            float scale = ((EntityGiantBrainyZombie) entity).getAnger();
            GlStateManager.scale(scale, scale, scale);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return BRAINY_ZOMBIE_TEXTURE;
    }
}
