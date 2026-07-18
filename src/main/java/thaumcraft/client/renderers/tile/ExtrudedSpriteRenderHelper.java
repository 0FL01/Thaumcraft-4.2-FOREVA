package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

final class ExtrudedSpriteRenderHelper {
    private ExtrudedSpriteRenderHelper() {
    }

    static void render(TextureAtlasSprite sprite, float thickness) {
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        vertex(buffer, 0.0F, 0.0F, 0.0F, maxU, maxV, 0.0F, 0.0F, 1.0F);
        vertex(buffer, 1.0F, 0.0F, 0.0F, minU, maxV, 0.0F, 0.0F, 1.0F);
        vertex(buffer, 1.0F, 1.0F, 0.0F, minU, minV, 0.0F, 0.0F, 1.0F);
        vertex(buffer, 0.0F, 1.0F, 0.0F, maxU, minV, 0.0F, 0.0F, 1.0F);

        vertex(buffer, 0.0F, 1.0F, -thickness, maxU, minV, 0.0F, 0.0F, -1.0F);
        vertex(buffer, 1.0F, 1.0F, -thickness, minU, minV, 0.0F, 0.0F, -1.0F);
        vertex(buffer, 1.0F, 0.0F, -thickness, minU, maxV, 0.0F, 0.0F, -1.0F);
        vertex(buffer, 0.0F, 0.0F, -thickness, maxU, maxV, 0.0F, 0.0F, -1.0F);

        int width = sprite.getIconWidth();
        int height = sprite.getIconHeight();
        float halfU = (maxU - minU) * 0.5F / width;
        float halfV = (maxV - minV) * 0.5F / height;
        for (int i = 0; i < width; ++i) {
            float left = (float) i / width;
            float right = (float) (i + 1) / width;
            float u = maxU + (minU - maxU) * left - halfU;
            vertex(buffer, left, 0.0F, -thickness, u, maxV, -1.0F, 0.0F, 0.0F);
            vertex(buffer, left, 0.0F, 0.0F, u, maxV, -1.0F, 0.0F, 0.0F);
            vertex(buffer, left, 1.0F, 0.0F, u, minV, -1.0F, 0.0F, 0.0F);
            vertex(buffer, left, 1.0F, -thickness, u, minV, -1.0F, 0.0F, 0.0F);

            vertex(buffer, right, 1.0F, -thickness, u, minV, 1.0F, 0.0F, 0.0F);
            vertex(buffer, right, 1.0F, 0.0F, u, minV, 1.0F, 0.0F, 0.0F);
            vertex(buffer, right, 0.0F, 0.0F, u, maxV, 1.0F, 0.0F, 0.0F);
            vertex(buffer, right, 0.0F, -thickness, u, maxV, 1.0F, 0.0F, 0.0F);
        }
        for (int i = 0; i < height; ++i) {
            float bottom = (float) i / height;
            float top = (float) (i + 1) / height;
            float v = maxV + (minV - maxV) * bottom - halfV;
            vertex(buffer, 0.0F, bottom, -thickness, maxU, v, 0.0F, -1.0F, 0.0F);
            vertex(buffer, 1.0F, bottom, -thickness, minU, v, 0.0F, -1.0F, 0.0F);
            vertex(buffer, 1.0F, bottom, 0.0F, minU, v, 0.0F, -1.0F, 0.0F);
            vertex(buffer, 0.0F, bottom, 0.0F, maxU, v, 0.0F, -1.0F, 0.0F);

            vertex(buffer, 0.0F, top, 0.0F, maxU, v, 0.0F, 1.0F, 0.0F);
            vertex(buffer, 1.0F, top, 0.0F, minU, v, 0.0F, 1.0F, 0.0F);
            vertex(buffer, 1.0F, top, -thickness, minU, v, 0.0F, 1.0F, 0.0F);
            vertex(buffer, 0.0F, top, -thickness, maxU, v, 0.0F, 1.0F, 0.0F);
        }

        tessellator.draw();
    }

    private static void vertex(BufferBuilder buffer,
                               float x, float y, float z, float u, float v,
                               float normalX, float normalY, float normalZ) {
        buffer.pos(x, y, z).tex(u, v).normal(normalX, normalY, normalZ).endVertex();
    }
}
