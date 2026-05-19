package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

final class TileRenderHelper {

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

    static float clamp01(float value) {
        return MathHelper.clamp(value, 0.0F, 1.0F);
    }

    static float ticks(TileEntity tile, float partialTicks) {
        return worldTicks(tile == null ? null : tile.getWorld(), partialTicks);
    }
}
