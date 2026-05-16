package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityTaintCreeper;

import javax.annotation.Nullable;

public class RenderTaintCreeper extends RenderLiving<EntityTaintCreeper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/creeper.png");

    public RenderTaintCreeper(RenderManager renderManager) {
        super(renderManager, new ModelCreeper(), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintCreeper entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityTaintCreeper entity, float partialTickTime) {
        float flash = entity.getCreeperFlashIntensity(partialTickTime);
        float wobble = 1.0F + MathHelper.sin(flash * 100.0F) * flash * 0.01F;
        flash = MathHelper.clamp(flash, 0.0F, 1.0F);
        flash *= flash;
        flash *= flash;
        float scaleXZ = (1.0F + flash * 0.4F) * wobble;
        float scaleY = (1.0F + flash * 0.1F) / wobble;
        GlStateManager.scale(scaleXZ, scaleY, scaleXZ);
    }

    @Override
    protected int getColorMultiplier(EntityTaintCreeper entity, float lightBrightness, float partialTickTime) {
        float flash = entity.getCreeperFlashIntensity(partialTickTime);
        if (((int) (flash * 10.0F)) % 2 == 0) {
            return 0;
        }
        int alpha = (int) (flash * 0.2F * 255.0F);
        alpha = MathHelper.clamp(alpha, 0, 255);
        return alpha << 24 | 0xFFFFFF;
    }
}
