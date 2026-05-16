package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.monster.EntityTaintVillager;

import javax.annotation.Nullable;

public class RenderTaintVillager extends RenderLiving<EntityTaintVillager> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/villager.png");

    public RenderTaintVillager(RenderManager renderManager) {
        super(renderManager, new ModelVillager(0.0F), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintVillager entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityTaintVillager entitylivingbaseIn, float partialTickTime) {
        float scale = 0.9375F;
        this.shadowSize = 0.5F;
        GlStateManager.scale(scale, scale, scale);
    }
}
