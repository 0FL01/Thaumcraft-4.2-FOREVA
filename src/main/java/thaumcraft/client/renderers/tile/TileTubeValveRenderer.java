package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelTubeValve;
import thaumcraft.common.tiles.TileTubeValve;

public class TileTubeValveRenderer extends TileEntitySpecialRenderer<TileTubeValve> {
    private static final ResourceLocation VALVE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/valve.png");
    private static final float MODEL_SCALE = 0.0625F;
    private static final float VALVE_THICKNESS = 0.1F;

    private final ModelTubeValve model = new ModelTubeValve();

    @Override
    public void render(TileTubeValve tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides,
                TubeConduitRenderHelper.TubeType.VALVE, null, x, y, z);

        bindTexture(VALVE_TEXTURE);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            orientByFace(tile.facing);
            GlStateManager.rotate(-tile.rotation * 1.5F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0D, -(tile.rotation / 360.0F) * 0.12D, 0.0D);
            model.render(MODEL_SCALE);
            renderValveOverlay();
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private void renderValveOverlay() {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(-0.25F, -0.25F, -0.25F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
                    .getAtlasSprite("thaumcraft:blocks/pipe_valve");
            renderExtrudedSprite(sprite, VALVE_THICKNESS);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static void renderExtrudedSprite(TextureAtlasSprite sprite, float thickness) {
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

    private static void orientByFace(EnumFacing face) {
        if (face.getYOffset() == 0) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else {
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(90.0F, face.getYOffset(), 0.0F, 0.0F);
        }
        GlStateManager.rotate(90.0F, face.getXOffset(), face.getYOffset(), face.getZOffset());
    }
}
