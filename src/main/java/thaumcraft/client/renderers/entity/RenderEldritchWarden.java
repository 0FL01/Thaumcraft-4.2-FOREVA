package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entities.ModelEldritchGuardian;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;

import javax.annotation.Nullable;

public class RenderEldritchWarden extends RenderLiving<EntityEldritchWarden> {

    private static final ResourceLocation WARDEN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_warden.png");

    public RenderEldritchWarden(RenderManager renderManager) {
        super(renderManager, new ModelEldritchGuardian(), 0.8F);
    }

    @Override
    public void doRender(EntityEldritchWarden entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        double adjustedY = y;
        if (entity.getSpawnTimer() > 0) {
            adjustedY -= entity.height * (entity.getSpawnTimer() / 150.0F);
        }
        super.doRender(entity, x, adjustedY, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    @Override
    protected void preRenderCallback(EntityEldritchWarden entity, float partialTickTime) {
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEldritchWarden entity) {
        return WARDEN_TEXTURE;
    }
}
