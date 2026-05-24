package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;

import javax.annotation.Nullable;

public class RenderCultistPortal extends Render<EntityCultistPortal> {

    private static final ResourceLocation PORTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/cultist_portal.png");

    public RenderCultistPortal(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.1F;
        this.shadowOpaque = 0.5F;
    }

    @Override
    public void doRender(EntityCultistPortal portal, double x, double y, double z, float entityYaw, float partialTicks) {
        this.renderPortal(portal, x, y, z, partialTicks);
    }

    private void renderPortal(EntityCultistPortal portal, double x, double y, double z, float partialTicks) {
        long time = System.nanoTime() / 50000000L;
        float scaleY = 1.5F;
        int buildup = (int) Math.min(50.0F, portal.ticksExisted + partialTicks);
        if (portal.hurtTime > 0) {
            double wobble = Math.sin(portal.hurtTime * 72.0D * Math.PI / 180.0D);
            scaleY = (float) ((double) scaleY - wobble / 4.0D);
            buildup = (int) ((double) buildup + 6.0D * wobble);
        }
        if (portal.pulse > 0) {
            double pulseWave = Math.sin(portal.pulse * 36.0D * Math.PI / 180.0D);
            scaleY = (float) ((double) scaleY + pulseWave / 4.0D);
            buildup = (int) ((double) buildup + 12.0D * pulseWave);
        }
        float scale = (float) buildup / 50.0F * 1.3F;
        y += portal.height / 2.0F;
        float injuryRatio = (1.0F - portal.getHealth() / portal.getMaxHealth()) / 3.0F;
        float bob = MathHelper.sin(portal.ticksExisted / (5.0F - 12.0F * injuryRatio)) * injuryRatio + injuryRatio;
        float bob2 = MathHelper.sin(portal.ticksExisted / (6.0F - 15.0F * injuryRatio)) * injuryRatio + injuryRatio;
        float alpha = 1.0F - bob;
        scaleY -= bob / 4.0F;
        scale -= bob2 / 3.0F;

        this.bindTexture(PORTAL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        int light = 220;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1.0F : 1.0F) * -this.renderManager.playerViewX,
                1.0F, 0.0F, 0.0F);
        GlStateManager.scale(scale, scaleY, scale);

        int frame = 15 - (int) (time % 16L);
        float uMin = frame / 16.0F;
        float uMax = uMin + 0.0625F;
        float vMin = 0.0F;
        float vMax = 1.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-0.5D, -0.5D, 0.0D).tex(uMax, vMin).color(255, 255, 255, (int) (alpha * 255.0F)).endVertex();
        buffer.pos(-0.5D, 0.5D, 0.0D).tex(uMax, vMax).color(255, 255, 255, (int) (alpha * 255.0F)).endVertex();
        buffer.pos(0.5D, 0.5D, 0.0D).tex(uMin, vMax).color(255, 255, 255, (int) (alpha * 255.0F)).endVertex();
        buffer.pos(0.5D, -0.5D, 0.0D).tex(uMin, vMin).color(255, 255, 255, (int) (alpha * 255.0F)).endVertex();
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCultistPortal entity) {
        return PORTAL_TEXTURE;
    }
}
