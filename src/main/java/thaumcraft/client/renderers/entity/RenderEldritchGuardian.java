package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entities.ModelEldritchGuardian;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;

import javax.annotation.Nullable;

public class RenderEldritchGuardian extends RenderLiving<EntityEldritchGuardian> {

    private static final ResourceLocation GUARDIAN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_guardian.png");

    public RenderEldritchGuardian(RenderManager renderManager) {
        super(renderManager, new ModelEldritchGuardian(), 0.6F);
    }

    @Override
    public void doRender(EntityEldritchGuardian entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, getDistanceFadeAlpha(entity));
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    private float getDistanceFadeAlpha(EntityEldritchGuardian entity) {
        EntityLivingBase viewer = Minecraft.getMinecraft().player;
        if (viewer == null || entity.world == null) {
            return 1.0F;
        }
        if (entity.world.provider.getDimension() == Config.dimensionOuterId) {
            return 1.0F;
        }
        float maxDistance = viewer.world.getDifficulty() == EnumDifficulty.HARD ? 576.0F : 1024.0F;
        float fullAlphaDistance = 256.0F;
        double distSq = entity.getDistanceSq(viewer.posX, viewer.posY, viewer.posZ);
        if (distSq < fullAlphaDistance) {
            return 0.6F;
        }
        double fade = 1.0D - Math.min(maxDistance - fullAlphaDistance, distSq - fullAlphaDistance) / (double) (maxDistance - fullAlphaDistance);
        return (float) fade * 0.6F;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEldritchGuardian entity) {
        return GUARDIAN_TEXTURE;
    }
}
