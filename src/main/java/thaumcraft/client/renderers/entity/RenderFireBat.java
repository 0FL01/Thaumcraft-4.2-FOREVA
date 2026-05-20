package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.entities.ModelFireBat;
import thaumcraft.common.entities.monster.EntityFireBat;

import javax.annotation.Nullable;

public class RenderFireBat extends RenderLiving<EntityFireBat> {

    private static final ResourceLocation FIREBAT_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/firebat.png");
    private static final ResourceLocation VAMPIREBAT_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vampirebat.png");

    private int renderedBatSize;

    public RenderFireBat(RenderManager renderManager) {
        super(renderManager, new ModelFireBat(), 0.25F);
        this.renderedBatSize = ((ModelFireBat) this.mainModel).getBatSize();
    }

    @Override
    public void doRender(EntityFireBat entity, double x, double y, double z, float entityYaw, float partialTicks) {
        int currentSize = ((ModelFireBat) this.mainModel).getBatSize();
        if (currentSize != this.renderedBatSize) {
            this.renderedBatSize = currentSize;
            this.mainModel = new ModelFireBat();
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
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
