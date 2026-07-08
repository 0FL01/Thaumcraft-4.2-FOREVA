package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileLifter;

public class TileLifterRenderer extends TileEntitySpecialRenderer<TileLifter> {
    private static final int TOP_GLOW_COLOR = 0xD000A000;
    private static final int SIDE_GLOW_COLOR = 0xD0DD11FF;

    @Override
    public void render(TileLifter tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null || tile.gettingPower() || tile.rangeAbove <= 0) {
            return;
        }
        TextureAtlasSprite glow = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite("thaumcraft:blocks/animatedglow");
        if (glow == null) {
            return;
        }

        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.disableCull();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 180.0F, 180.0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        if (shouldRenderFace(tile, EnumFacing.UP)) {
            TileRenderHelper.addTexturedFace(buf,
                    0.01F, 0.99F, 0.01F,
                    0.01F, 0.99F, 0.99F,
                    0.99F, 0.99F, 0.99F,
                    0.99F, 0.99F, 0.01F,
                    glow, TOP_GLOW_COLOR);
        }
        if (shouldRenderFace(tile, EnumFacing.WEST)) {
            TileRenderHelper.addTexturedFace(buf,
                    0.01F, 0.90F, 0.99F,
                    0.01F, 0.10F, 0.99F,
                    0.01F, 0.10F, 0.01F,
                    0.01F, 0.90F, 0.01F,
                    glow, SIDE_GLOW_COLOR);
        }
        if (shouldRenderFace(tile, EnumFacing.EAST)) {
            TileRenderHelper.addTexturedFace(buf,
                    0.99F, 0.90F, 0.01F,
                    0.99F, 0.10F, 0.01F,
                    0.99F, 0.10F, 0.99F,
                    0.99F, 0.90F, 0.99F,
                    glow, SIDE_GLOW_COLOR);
        }
        if (shouldRenderFace(tile, EnumFacing.NORTH)) {
            TileRenderHelper.addTexturedFace(buf,
                    0.99F, 0.90F, 0.01F,
                    0.99F, 0.10F, 0.01F,
                    0.01F, 0.10F, 0.01F,
                    0.01F, 0.90F, 0.01F,
                    glow, SIDE_GLOW_COLOR);
        }
        if (shouldRenderFace(tile, EnumFacing.SOUTH)) {
            TileRenderHelper.addTexturedFace(buf,
                    0.01F, 0.90F, 0.99F,
                    0.01F, 0.10F, 0.99F,
                    0.99F, 0.10F, 0.99F,
                    0.99F, 0.90F, 0.99F,
                    glow, SIDE_GLOW_COLOR);
        }
        tess.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static boolean shouldRenderFace(TileLifter tile, EnumFacing face) {
        return tile.getWorld() != null
                && tile.getWorld().getBlockState(tile.getPos().offset(face))
                .doesSideBlockRendering(tile.getWorld(), tile.getPos().offset(face), face.getOpposite()) == false;
    }
}
