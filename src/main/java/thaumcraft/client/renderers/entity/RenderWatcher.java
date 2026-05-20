package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thaumcraft.client.renderers.models.entities.ModelWatcher;
import thaumcraft.common.entities.monster.EntityWatcher;

import javax.annotation.Nullable;

public class RenderWatcher extends RenderLiving<EntityWatcher> {

    private static final ResourceLocation WATCHER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/watcher.png");
    private static final ResourceLocation WATCHER_BEAM_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/watcher_beam.png");

    private int eyeTextureOffset;

    public RenderWatcher(RenderManager renderManager) {
        super(renderManager, new ModelWatcher(), 0.5F);
        this.eyeTextureOffset = ((ModelWatcher) this.mainModel).getEyeTextureOffset();
    }

    @Override
    public boolean shouldRender(EntityWatcher livingEntity, ICamera camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
            return true;
        }
        if (livingEntity.hasTargetedEntity()) {
            EntityLivingBase target = livingEntity.getTargetedEntity();
            if (target != null) {
                Vec3d targetPos = getPosition(target, target.height * 0.5D, 1.0F);
                Vec3d sourcePos = getPosition(livingEntity, livingEntity.getEyeHeight(), 1.0F);
                return camera.isBoundingBoxInFrustum(new AxisAlignedBB(
                        sourcePos.x, sourcePos.y, sourcePos.z, targetPos.x, targetPos.y, targetPos.z));
            }
        }
        return false;
    }

    private Vec3d getPosition(EntityLivingBase entity, double yOffset, float partialTicks) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double y = yOffset + entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        return new Vec3d(x, y, z);
    }

    @Override
    public void doRender(EntityWatcher entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (eyeTextureOffset != ((ModelWatcher) this.mainModel).getEyeTextureOffset()) {
            this.mainModel = new ModelWatcher();
            this.eyeTextureOffset = ((ModelWatcher) this.mainModel).getEyeTextureOffset();
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        EntityLivingBase target = entity.getTargetedEntity();
        if (target == null) {
            return;
        }

        float progress = entity.getGazeProgress(partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        bindTexture(WATCHER_BEAM_TEXTURE);
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        float beamTime = entity.world.getTotalWorldTime() + partialTicks;
        float beamOffset = beamTime * 0.5F % 1.0F;
        float eyeHeight = entity.getEyeHeight();

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + eyeHeight, (float) z);

        Vec3d targetPos = getPosition(target, target.height * 0.5D, partialTicks);
        Vec3d sourcePos = getPosition(entity, eyeHeight, partialTicks);
        Vec3d beamVec = targetPos.subtract(sourcePos);
        double beamLength = beamVec.length() + 1.0D;
        beamVec = beamVec.normalize();
        float beamPitch = (float) Math.acos(beamVec.y);
        float beamYaw = (float) Math.atan2(beamVec.z, beamVec.x);
        GlStateManager.rotate((((float) Math.PI / 2F) + -beamYaw) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(beamPitch * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);

        double angle = beamTime * 0.05D * -1.5D;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        float beamScale = progress * progress;
        int red = 64 + (int) (beamScale * 191.0F);
        int green = 32 + (int) (beamScale * 191.0F);
        int blue = 128 - (int) (beamScale * 64.0F);
        double inner = 0.2D;
        double outer = 0.282D;
        double d4 = Math.cos(angle + 2.356194490192345D) * outer;
        double d5 = Math.sin(angle + 2.356194490192345D) * outer;
        double d6 = Math.cos(angle + (Math.PI / 4D)) * outer;
        double d7 = Math.sin(angle + (Math.PI / 4D)) * outer;
        double d8 = Math.cos(angle + 3.9269908169872414D) * outer;
        double d9 = Math.sin(angle + 3.9269908169872414D) * outer;
        double d10 = Math.cos(angle + 5.497787143782138D) * outer;
        double d11 = Math.sin(angle + 5.497787143782138D) * outer;
        double d12 = Math.cos(angle + Math.PI) * inner;
        double d13 = Math.sin(angle + Math.PI) * inner;
        double d14 = Math.cos(angle) * inner;
        double d15 = Math.sin(angle) * inner;
        double d16 = Math.cos(angle + (Math.PI / 2D)) * inner;
        double d17 = Math.sin(angle + (Math.PI / 2D)) * inner;
        double d18 = Math.cos(angle + (Math.PI * 3D / 2D)) * inner;
        double d19 = Math.sin(angle + (Math.PI * 3D / 2D)) * inner;
        double u0 = 0.0D;
        double u1 = 0.4999D;
        double v0 = -1.0F + beamOffset;
        double v1 = beamLength * 2.5D + v0;

        buffer.pos(d12, beamLength, d13).tex(u1, v1).color(red, green, blue, 255).endVertex();
        buffer.pos(d12, 0.0D, d13).tex(u1, v0).color(red, green, blue, 255).endVertex();
        buffer.pos(d14, 0.0D, d15).tex(u0, v0).color(red, green, blue, 255).endVertex();
        buffer.pos(d14, beamLength, d15).tex(u0, v1).color(red, green, blue, 255).endVertex();
        buffer.pos(d16, beamLength, d17).tex(u1, v1).color(red, green, blue, 255).endVertex();
        buffer.pos(d16, 0.0D, d17).tex(u1, v0).color(red, green, blue, 255).endVertex();
        buffer.pos(d18, 0.0D, d19).tex(u0, v0).color(red, green, blue, 255).endVertex();
        buffer.pos(d18, beamLength, d19).tex(u0, v1).color(red, green, blue, 255).endVertex();

        double altV = entity.ticksExisted % 2 == 0 ? 0.5D : 0.0D;
        buffer.pos(d4, beamLength, d5).tex(0.5D, altV + 0.5D).color(red, green, blue, 255).endVertex();
        buffer.pos(d6, beamLength, d7).tex(1.0D, altV + 0.5D).color(red, green, blue, 255).endVertex();
        buffer.pos(d10, beamLength, d11).tex(1.0D, altV).color(red, green, blue, 255).endVertex();
        buffer.pos(d8, beamLength, d9).tex(0.5D, altV).color(red, green, blue, 255).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityWatcher entity) {
        return WATCHER_TEXTURE;
    }
}
