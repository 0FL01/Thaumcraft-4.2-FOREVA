package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelEldritchCap;
import thaumcraft.common.config.Config;
import thaumcraft.common.tiles.TileEldritchObelisk;

public class TileEldritchObeliskRenderer extends TileEntitySpecialRenderer<TileEldritchObelisk> {
    private static final ResourceLocation SIDE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_side.png");
    private static final ResourceLocation CAP_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap.png");
    private static final ResourceLocation SIDE_TEXTURE_OUTER =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_side_2.png");
    private static final ResourceLocation CAP_TEXTURE_OUTER =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap_2.png");
    private static final ModelEldritchCap CAP_MODEL = new ModelEldritchCap();

    @Override
    public void render(TileEldritchObelisk tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        boolean outer = tile.getWorld() != null && tile.getWorld().provider.getDimension() == Config.dimensionOuterId;
        ResourceLocation sideTexture = outer ? SIDE_TEXTURE_OUTER : SIDE_TEXTURE;
        ResourceLocation capTexture = outer ? CAP_TEXTURE_OUTER : CAP_TEXTURE;
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float bob = (float) Math.sin(ticks / 10.0F) * 0.1F + 0.1F;
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        // LOD: full rendering (sides + caps + fields) within 96 blocks, field-only fallback beyond.
        boolean inRange = tile.getWorld() != null
                && viewer != null
                && tile.getPos().distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 9216.0D;
        double viewX = 0.0D;
        double viewY = 0.0D;
        double viewZ = 0.0D;
        if (viewer != null) {
            viewX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
            viewY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
            viewZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        }

        renderSideFields(inRange, outer, x, y + bob + 1.0D, z, viewX, viewY, viewZ);

        if (inRange) {
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);

            float previousLightX = OpenGlHelper.lastBrightnessX;
            float previousLightY = OpenGlHelper.lastBrightnessY;
            if (tile.getWorld() != null) {
                int packedLight = tile.getWorld().getCombinedLight(tile.getPos().up(5), 0);
                int low = packedLight % 65536;
                int high = packedLight / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, low, high);
            }

            GlStateManager.translate(x + 0.5D, y + bob + 1.0D, z + 0.5D);
            GlStateManager.disableCull();
            bindTexture(sideTexture);
            renderSides(0.5F, 3.0F);

            bindTexture(capTexture);
            renderObeliskCapPair();

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }

    private static void renderObeliskCapPair() {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        CAP_MODEL.renderCap();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 3.0D, 0.0D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        CAP_MODEL.renderCap();
        GlStateManager.popMatrix();
    }

    private void renderSideFields(boolean inRange, boolean outer,
                                  double x, double y, double z,
                                  double viewX, double viewY, double viewZ) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            renderFieldLayersForFacing(facing, inRange, outer, x, y, z, viewX, viewY, viewZ);
        }
    }

    private void renderFieldLayersForFacing(
            EnumFacing facing, boolean inRange, boolean outer,
            double x, double y, double z, double viewX, double viewY, double viewZ) {
        float offset = facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 0.499F : -0.499F;
        LayeredFieldPlaneHelper.renderLayeredFaceRect(
                facing,
                x + 0.5D,
                y,
                z + 0.5D,
                offset,
                inRange,
                outer ? 0.55F : 1.0F,
                viewX,
                viewY,
                viewZ,
                -0.5F,
                0.5F,
                0.0F,
                3.0F);
    }

    private static void renderSides(float half, float height) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        face(buf, -half, 0.0F, -half, half, height, -half);
        face(buf, -half, 0.0F, half, half, height, half);
        faceZ(buf, -half, 0.0F, -half, -half, height, half);
        faceZ(buf, half, 0.0F, -half, half, height, half);

        tess.draw();
    }

    private static void face(BufferBuilder buf, float x0, float y0, float z, float x1, float y1, float z1) {
        buf.pos(x0, y1, z).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x0, y0, z).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }

    private static void faceZ(BufferBuilder buf, float x, float y0, float z0, float x1, float y1, float z1) {
        buf.pos(x, y1, z0).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x, y0, z0).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }

    private static void v(BufferBuilder buf, float x, float y, float z,
                          float u, float v, float r, float g, float b, float a) {
        buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }
}
