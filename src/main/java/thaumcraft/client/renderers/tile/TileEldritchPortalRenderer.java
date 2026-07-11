package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thaumcraft.common.tiles.TileEldritchPortal;

public class TileEldritchPortalRenderer extends TileEntitySpecialRenderer<TileEldritchPortal> {
    private static final double NEAR_CAMERA_FADE_START = 0.25D;
    private static final double NEAR_CAMERA_FADE_END = 1.5D;
    private static final ResourceLocation PORTAL =
            new ResourceLocation("thaumcraft", "textures/misc/eldritch_portal.png");

    @Override
    public void render(TileEldritchPortal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        int c = (int) Math.min(30.0F, tile.opencount + partialTicks);
        int e = (int) Math.min(5.0F, tile.opencount + partialTicks);
        float scale = e / 5.0F;
        float scaleY = c / 30.0F;
        if (scale <= 0.0F || scaleY <= 0.0F) {
            return;
        }

        float cameraAlpha = nearCameraAlpha(x + 0.5D, y + 0.5D, z + 0.5D);
        if (cameraAlpha <= 0.0F) {
            return;
        }

        long frame = (System.nanoTime() / 50000000L) % 16L;
        float u0 = frame / 16.0F;
        float u1 = u0 + 0.0625F;

        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;

        GlStateManager.pushMatrix();
        try {
            bindTexture(PORTAL);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 0.0F, 1.0F, 1.0F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 0.0F);

            Entity view = Minecraft.getMinecraft().getRenderViewEntity();
            if (view != null) {
                float arX = ActiveRenderInfo.getRotationX();
                float arZ = ActiveRenderInfo.getRotationZ();
                float arYZ = ActiveRenderInfo.getRotationYZ();
                float arXY = ActiveRenderInfo.getRotationXY();
                float arXZ = ActiveRenderInfo.getRotationXZ();

                double px = x + 0.5D;
                double py = y + 0.5D;
                double pz = z + 0.5D;

                Vec3d v1 = new Vec3d(-arX - arYZ, -arXZ, -arZ - arXY);
                Vec3d v2 = new Vec3d(-arX + arYZ, arXZ, -arZ + arXY);
                Vec3d v3 = new Vec3d(arX + arYZ, arXZ, arZ + arXY);
                Vec3d v4 = new Vec3d(arX - arYZ, -arXZ, arZ - arXY);

                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buf = tess.getBuffer();
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                vertex(buf, px + v1.x * scale, py + v1.y * scaleY, pz + v1.z * scale, u0, 1.0D, cameraAlpha);
                vertex(buf, px + v2.x * scale, py + v2.y * scaleY, pz + v2.z * scale, u1, 1.0D, cameraAlpha);
                vertex(buf, px + v3.x * scale, py + v3.y * scaleY, pz + v3.z * scale, u1, 0.0D, cameraAlpha);
                vertex(buf, px + v4.x * scale, py + v4.y * scaleY, pz + v4.z * scale, u0, 0.0D, cameraAlpha);
                tess.draw();
            }
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
            GlStateManager.depthMask(depthMask);
            if (lightingEnabled) GlStateManager.enableLighting(); else GlStateManager.disableLighting();
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (blendEnabled) GlStateManager.enableBlend(); else GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static float nearCameraAlpha(double centerX, double centerY, double centerZ) {
        double distance = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ);
        if (distance <= NEAR_CAMERA_FADE_START) {
            return 0.0F;
        }
        if (distance >= NEAR_CAMERA_FADE_END) {
            return 1.0F;
        }
        return (float) ((distance - NEAR_CAMERA_FADE_START)
                / (NEAR_CAMERA_FADE_END - NEAR_CAMERA_FADE_START));
    }

    private static void vertex(BufferBuilder buf, double x, double y, double z,
                               double u, double v, float alpha) {
        buf.pos(x, y, z).tex(u, v).color(1.0F, 1.0F, 1.0F, alpha)
                .normal(0.0F, 0.0F, -1.0F).endVertex();
    }
}
