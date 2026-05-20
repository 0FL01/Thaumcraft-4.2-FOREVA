package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entities.ModelTaintacle;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;

import javax.annotation.Nullable;

public class RenderTaintacle<T extends EntityLiving> extends RenderLiving<T> {

    private static final ResourceLocation TAINTACLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/taintacle.png");

    public RenderTaintacle(RenderManager renderManager, float shadowSize, int length) {
        super(renderManager, new ModelTaintacle(length), shadowSize);
    }

    @Override
    protected void preRenderCallback(T entity, float partialTickTime) {
        if (entity instanceof EntityTaintacleGiant) {
            GlStateManager.scale(1.33F, 1.33F, 1.33F);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return TAINTACLE_TEXTURE;
    }
}
