package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityFireBat;

import javax.annotation.Nullable;

public class RenderFireBat extends RenderLiving<EntityFireBat> {

    private static final ResourceLocation FIREBAT_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/firebat.png");
    private static final ResourceLocation VAMPIREBAT_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vampirebat.png");

    public RenderFireBat(RenderManager renderManager) {
        super(renderManager, new ModelBat(), 0.25F);
    }

    @Override
    protected void preRenderCallback(EntityFireBat entity, float partialTickTime) {
        float scale = (entity.getIsDevil() || entity.getIsVampire()) ? 0.6F : 0.35F;
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected void applyRotations(EntityFireBat entity, float ageInTicks, float rotationYaw, float partialTicks) {
        if (entity.getIsBatHanging()) {
            GlStateManager.translate(0.0F, -0.1F, 0.0F);
        } else {
            GlStateManager.translate(0.0F, MathHelper.cos(ageInTicks * 0.3F) * 0.1F, 0.0F);
        }
        super.applyRotations(entity, ageInTicks, rotationYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFireBat entity) {
        return entity.getIsVampire() ? VAMPIREBAT_TEXTURE : FIREBAT_TEXTURE;
    }
}
