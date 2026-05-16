package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityGolemBobber;

import javax.annotation.Nullable;

public class RenderGolemBobber extends Render<EntityGolemBobber> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/particle/particles.png");

    public RenderGolemBobber(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityGolemBobber entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        bindTexture(TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float uMin = 8.0F / 128.0F;
        float uMax = 16.0F / 128.0F;
        float vMin = 16.0F / 128.0F;
        float vMax = 24.0F / 128.0F;
        float halfWidth = 0.5F;
        float halfHeight = 0.5F;

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(0.0F - halfWidth, 0.0F - halfHeight, 0.0D).tex(uMin, vMax).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(1.0F - halfWidth, 0.0F - halfHeight, 0.0D).tex(uMax, vMax).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(1.0F - halfWidth, 1.0F - halfHeight, 0.0D).tex(uMax, vMin).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.0F - halfWidth, 1.0F - halfHeight, 0.0D).tex(uMin, vMin).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        if (entity.fisher != null) {
            renderTetherLine(entity, x, y, z, partialTicks);
        }
    }

    private void renderTetherLine(EntityGolemBobber bobber, double x, double y, double z, float partialTicks) {
        EntityGolemBase fisher = bobber.fisher;
        float armSwing = fisher.rightArm / 3.0F;
        float armCurve = MathHelper.sin(MathHelper.sqrt(armSwing) * (float) Math.PI);
        Vec3d handOffset = new Vec3d(-0.5D, 0.03D, 0.8D)
                .rotatePitch(-(fisher.prevRotationPitch + (fisher.rotationPitch - fisher.prevRotationPitch) * partialTicks) * 0.017453292F)
                .rotateYaw(-(fisher.prevRotationYaw + (fisher.rotationYaw - fisher.prevRotationYaw) * partialTicks) * 0.017453292F)
                .rotateYaw(armCurve * 0.5F)
                .rotatePitch(-armCurve * 0.7F);

        double fisherX = fisher.prevPosX + (fisher.posX - fisher.prevPosX) * partialTicks + handOffset.x;
        double fisherY = fisher.prevPosY + (fisher.posY - fisher.prevPosY) * partialTicks + handOffset.y;
        double fisherZ = fisher.prevPosZ + (fisher.posZ - fisher.prevPosZ) * partialTicks + handOffset.z;

        double eyeHeight = fisher.getEyeHeight();
        float bodyYaw = fisher.prevRenderYawOffset + (fisher.renderYawOffset - fisher.prevRenderYawOffset) * partialTicks;
        float yawRadians = bodyYaw * 0.017453292F;
        double sinYaw = MathHelper.sin(yawRadians);
        double cosYaw = MathHelper.cos(yawRadians);
        fisherX = fisher.prevPosX + (fisher.posX - fisher.prevPosX) * partialTicks - cosYaw * 0.25D - sinYaw * 0.7D;
        fisherY = fisher.prevPosY + eyeHeight + (fisher.posY - fisher.prevPosY) * partialTicks - 0.4D;
        fisherZ = fisher.prevPosZ + (fisher.posZ - fisher.prevPosZ) * partialTicks - sinYaw * 0.25D + cosYaw * 0.7D;

        double bobberX = bobber.prevPosX + (bobber.posX - bobber.prevPosX) * partialTicks;
        double bobberY = bobber.prevPosY + (bobber.posY - bobber.prevPosY) * partialTicks + 0.25D;
        double bobberZ = bobber.prevPosZ + (bobber.posZ - bobber.prevPosZ) * partialTicks;

        double dx = fisherX - bobberX;
        double dy = fisherY - bobberY;
        double dz = fisherZ - bobberZ;

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        int segments = 16;
        for (int i = 0; i <= segments; i++) {
            float segment = (float) i / (float) segments;
            buffer.pos(
                    x + dx * segment,
                    y + dy * (segment * segment + segment) * 0.5D + 0.25D,
                    z + dz * segment
            ).color(0, 0, 0, 255).endVertex();
        }
        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGolemBobber entity) {
        return TEXTURE;
    }
}
