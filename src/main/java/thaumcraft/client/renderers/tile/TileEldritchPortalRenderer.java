package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileEldritchPortal;

public class TileEldritchPortalRenderer extends TileEntitySpecialRenderer<TileEldritchPortal> {
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

        long frame = (System.nanoTime() / 50000000L) % 16L;
        float u0 = frame / 16.0F;
        float u1 = u0 + 0.0625F;

        GlStateManager.pushMatrix();
        bindTexture(PORTAL);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 0.0F, 1.0F, 1.0F);

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
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buf.pos(px + v1.x * scale, py + v1.y * scaleY, pz + v1.z * scale).tex(u0, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buf.pos(px + v2.x * scale, py + v2.y * scaleY, pz + v2.z * scale).tex(u1, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buf.pos(px + v3.x * scale, py + v3.y * scaleY, pz + v3.z * scale).tex(u1, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buf.pos(px + v4.x * scale, py + v4.y * scaleY, pz + v4.z * scale).tex(u0, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            tess.draw();
        }

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
