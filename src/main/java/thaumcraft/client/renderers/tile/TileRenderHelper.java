package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.config.Config;

final class TileRenderHelper {
    private static final ResourceLocation WISPY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

    private TileRenderHelper() {}

    static float worldTicks(World world, float partialTicks) {
        return (world == null ? 0.0F : world.getTotalWorldTime()) + partialTicks;
    }

    static void renderFloatingItem(ItemStack stack, float ticks, float yOffset, float scale) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, yOffset + MathHelper.sin(ticks / 16.0F) * 0.05F, 0.0F);
        GlStateManager.rotate(ticks * 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    static void renderEntityItem(TileEntity tile, ItemStack stack, float hoverStart) {
        renderEntityItem(tile == null ? null : tile.getWorld(), stack, hoverStart);
    }

    static void renderEntityItem(World world, ItemStack stack, float hoverStart) {
        World renderWorld = resolveRenderWorld(world);
        if (renderWorld == null || stack == null || stack.isEmpty()) {
            return;
        }
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        if (renderManager == null) {
            return;
        }

        ItemStack renderStack = stack.copy();
        renderStack.setCount(1);
        EntityItem entity = new EntityItem(renderWorld, 0.0D, 0.0D, 0.0D, renderStack);
        entity.hoverStart = hoverStart;
        renderManager.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
    }

    private static World resolveRenderWorld(World world) {
        return world != null ? world : Minecraft.getMinecraft().world;
    }

    static void orientBillboardToPlayer() {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rm.playerViewX, 1.0F, 0.0F, 0.0F);
    }

    static void drawTexturedQuad(float half, int argb, float u0, float u1, float v0, float v1) {
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(-half, -half, 0.0D).tex(u0, v1).color(r, g, b, a).endVertex();
        buf.pos(half, -half, 0.0D).tex(u1, v1).color(r, g, b, a).endVertex();
        buf.pos(half, half, 0.0D).tex(u1, v0).color(r, g, b, a).endVertex();
        buf.pos(-half, half, 0.0D).tex(u0, v0).color(r, g, b, a).endVertex();
        tess.draw();
    }

    static void drawSolidHorizontalQuad(float half, int argb) {
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(-half, 0.0D, -half).color(r, g, b, a).endVertex();
        buf.pos(-half, 0.0D, half).color(r, g, b, a).endVertex();
        buf.pos(half, 0.0D, half).color(r, g, b, a).endVertex();
        buf.pos(half, 0.0D, -half).color(r, g, b, a).endVertex();
        tess.draw();
    }

    static void drawTexturedCuboid(BufferBuilder buf,
                                   float minX, float minY, float minZ,
                                   float maxX, float maxY, float maxZ,
                                   TextureAtlasSprite sprite, int argb) {
        drawTexturedCuboid(buf, minX, minY, minZ, maxX, maxY, maxZ,
                sprite, sprite, sprite, sprite, sprite, sprite, argb);
    }

    static void drawTexturedCuboid(BufferBuilder buf,
                                   float minX, float minY, float minZ,
                                   float maxX, float maxY, float maxZ,
                                   TextureAtlasSprite down,
                                   TextureAtlasSprite up,
                                   TextureAtlasSprite north,
                                   TextureAtlasSprite south,
                                   TextureAtlasSprite west,
                                   TextureAtlasSprite east,
                                   int argb) {
        addTexturedFace(buf, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, down, argb);
        addTexturedFace(buf, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, up, argb);
        addTexturedFace(buf, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, minX, minY, minZ, north, argb);
        addTexturedFace(buf, minX, maxY, maxZ, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, south, argb);
        addTexturedFace(buf, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ, minX, minY, maxZ, west, argb);
        addTexturedFace(buf, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, east, argb);
    }

    static void addTexturedFace(BufferBuilder buf,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                TextureAtlasSprite sprite, int argb) {
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        buf.pos(x1, y1, z1).tex(u0, v0).color(r, g, b, a).endVertex();
        buf.pos(x2, y2, z2).tex(u0, v1).color(r, g, b, a).endVertex();
        buf.pos(x3, y3, z3).tex(u1, v1).color(r, g, b, a).endVertex();
        buf.pos(x4, y4, z4).tex(u1, v0).color(r, g, b, a).endVertex();
    }

    static Vec3d rgb(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        return new Vec3d(r, g, b);
    }

    static void drawAdditiveLine(double sx, double sy, double sz,
                                 double ex, double ey, double ez,
                                 int color, float alphaStart, float alphaEnd, float width) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.glLineWidth(Math.max(1.0F, width));
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(sx, sy, sz).color(r, g, b, alphaStart).endVertex();
        buf.pos(ex, ey, ez).color(r, g, b, alphaEnd).endVertex();
        tess.draw();
        GlStateManager.glLineWidth(1.0F);
    }

    static void drawWispyLine(double sx, double sy, double sz,
                              double ex, double ey, double ez,
                              int color, float time, float speed, float distance, float width) {
        if (distance <= 0.0F) {
            return;
        }

        double dx = sx - ex;
        double dy = sy - ey;
        double dz = sz - ez;
        float dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        float blocks = Math.round(dist);
        float length = Math.max(1.0F, blocks * (Config.golemLinkQuality / 2.0F));
        Color tint = new Color(color);
        float red = tint.getRed() / 255.0F;
        float green = tint.getGreen() / 255.0F;
        float blue = tint.getBlue() / 255.0F;

        Minecraft.getMinecraft().getTextureManager().bindTexture(WISPY_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        drawWispyStrip(sx, sy, sz, ex, ey, ez, dist, length, time, red, green, blue, speed, distance, width, true);
        drawWispyStrip(sx, sy, sz, ex, ey, ez, dist, length, time, red, green, blue, speed, distance, width, false);

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

    private static void drawWispyStrip(double sx, double sy, double sz,
                                       double ex, double ey, double ez,
                                       float dist, float length, float time,
                                       float red, float green, float blue,
                                       float speed, float distance, float width,
                                       boolean vertical) {
        double deltaX = sx - ex;
        double deltaY = sy - ey;
        double deltaZ = sz - ez;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);

        int maxSegment = Math.max(1, (int) (length * distance));
        for (int i = 0; i <= maxSegment; i++) {
            float beamPos = i / length;
            float centerWeight = 1.0F - Math.abs(i - length / 2.0F) / (length / 2.0F);
            centerWeight = Math.max(0.0F, centerWeight);
            double waveX = MathHelper.sin((float) ((sz % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 4.0D)) * 0.5F * centerWeight;
            double waveY = MathHelper.sin((float) ((sx % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 3.0D)) * 0.5F * centerWeight;
            double waveZ = MathHelper.sin((float) ((sy % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 2.0D)) * 0.5F * centerWeight;
            double beamX = deltaX + waveX;
            double beamY = deltaY + waveY;
            double beamZ = deltaZ + waveZ;
            float texU = (1.0F - beamPos) * dist - time * speed;

            if (vertical) {
                buffer.pos(ex + beamX * beamPos, ey + beamY * beamPos - width, ez + beamZ * beamPos)
                        .tex(texU, 1.0F).color(red, green, blue, 0.8F).endVertex();
                buffer.pos(ex + beamX * beamPos, ey + beamY * beamPos + width, ez + beamZ * beamPos)
                        .tex(texU, 0.0F).color(red, green, blue, 0.8F).endVertex();
            } else {
                buffer.pos(ex + beamX * beamPos - width, ey + beamY * beamPos, ez + beamZ * beamPos)
                        .tex(texU, 1.0F).color(red, green, blue, 0.8F).endVertex();
                buffer.pos(ex + beamX * beamPos + width, ey + beamY * beamPos, ez + beamZ * beamPos)
                        .tex(texU, 0.0F).color(red, green, blue, 0.8F).endVertex();
            }
        }

        tessellator.draw();
    }

    static float clamp01(float value) {
        return MathHelper.clamp(value, 0.0F, 1.0F);
    }

    static float ticks(TileEntity tile, float partialTicks) {
        return worldTicks(tile == null ? null : tile.getWorld(), partialTicks);
    }
}
