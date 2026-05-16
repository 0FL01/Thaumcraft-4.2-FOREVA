package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderBrainyZombie extends RenderZombie {

    private static final ResourceLocation BRAINY_ZOMBIE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/bzombie.png");

    public RenderBrainyZombie(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return BRAINY_ZOMBIE_TEXTURE;
    }
}
