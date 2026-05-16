package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderInhabitedZombie extends RenderZombie {

    private static final ResourceLocation INHABITED_ZOMBIE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/czombie.png");

    public RenderInhabitedZombie(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return INHABITED_ZOMBIE_TEXTURE;
    }
}
