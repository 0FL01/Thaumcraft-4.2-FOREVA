package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.projectile.EntityGolemOrb;

import javax.annotation.Nullable;

public class RenderElectricOrb extends Render<Entity> {

    private static final ResourceLocation PARTICLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particles.png");

    public RenderElectricOrb(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        bindTexture(PARTICLE_TEXTURE);

        float uMin = (1.0F + (float) (entity.ticksExisted % 6)) / 8.0F;
        float uMax = uMin + 0.125F;
        float vMin = 0.875F;
        if (entity instanceof EntityGolemOrb && ((EntityGolemOrb) entity).red) {
            vMin = 0.75F;
        }
        float vMax = vMin + 0.125F;
        float origin = 0.5F;
        float bob = MathHelper.sin(entity.ticksExisted / 5.0F) * 0.2F + 0.2F;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(1.0F + bob, 1.0F + bob, 1.0F + bob);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 220.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-origin, -origin, 0.0D).tex(uMin, vMax).endVertex();
        buffer.pos(1.0F - origin, -origin, 0.0D).tex(uMax, vMax).endVertex();
        buffer.pos(1.0F - origin, 1.0F - origin, 0.0D).tex(uMax, vMin).endVertex();
        buffer.pos(-origin, 1.0F - origin, 0.0D).tex(uMin, vMin).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return PARTICLE_TEXTURE;
    }
}
