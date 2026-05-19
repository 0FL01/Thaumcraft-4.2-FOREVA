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
import thaumcraft.common.tiles.TileBrainbox;

public class TileBrainboxRenderer extends TileEntitySpecialRenderer<TileBrainbox> {

    @Override
    public void render(TileBrainbox tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }
        TextureAtlasSprite brainbox = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite("thaumcraft:blocks/brainbox");
        if (brainbox == null) {
            return;
        }

        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        int packedLight = tile.getWorld().getCombinedLight(tile.getPos().offset(tile.facing), 0);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                packedLight & 0xFFFF, (packedLight >> 16) & 0xFFFF);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        drawNozzle(buf, tile.facing, brainbox);
        tess.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawNozzle(BufferBuilder buf, EnumFacing facing, TextureAtlasSprite sprite) {
        float minX = 6.0F / 16.0F;
        float minY = 6.0F / 16.0F;
        float minZ = 6.0F / 16.0F;
        float maxX = 10.0F / 16.0F;
        float maxY = 10.0F / 16.0F;
        float maxZ = 10.0F / 16.0F;

        switch (facing == null ? EnumFacing.NORTH : facing) {
            case UP:
                minY = 13.0F / 16.0F;
                maxY = 1.0F;
                break;
            case DOWN:
                minY = 0.0F;
                maxY = 3.0F / 16.0F;
                break;
            case EAST:
                minX = 13.0F / 16.0F;
                maxX = 1.0F;
                break;
            case WEST:
                minX = 0.0F;
                maxX = 3.0F / 16.0F;
                break;
            case SOUTH:
                minZ = 13.0F / 16.0F;
                maxZ = 1.0F;
                break;
            case NORTH:
            default:
                minZ = 0.0F;
                maxZ = 3.0F / 16.0F;
                break;
        }

        TileRenderHelper.drawTexturedCuboid(buf, minX, minY, minZ, maxX, maxY, maxZ, sprite, 0xFFFFFFFF);
    }
}
