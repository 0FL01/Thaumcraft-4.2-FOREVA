package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entities.ModelEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;

import javax.annotation.Nullable;

public class RenderEldritchGolem extends RenderLiving<EntityEldritchGolem> {

    private static final ResourceLocation GOLEM_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_golem.png");

    public RenderEldritchGolem(RenderManager renderManager) {
        super(renderManager, new ModelEldritchGolem(), 0.9F);
    }

    @Override
    public void doRender(EntityEldritchGolem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    @Override
    protected void preRenderCallback(EntityEldritchGolem entity, float partialTickTime) {
        GlStateManager.scale(2.15F, 2.15F, 2.15F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEldritchGolem entity) {
        return GOLEM_TEXTURE;
    }
}
