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
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileSensor;

public class TileSensorRenderer extends TileEntitySpecialRenderer<TileSensor> {
    private static final float EPSILON = 0.002F;

    @Override
    public void render(TileSensor tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null || tile.redstoneSignal <= 0) {
            return;
        }

        TextureAtlasSprite top = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite("thaumcraft:blocks/arcaneeartopon");
        TextureAtlasSprite side = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite("thaumcraft:blocks/arcaneearsideon");
        TextureAtlasSprite bottom = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite("thaumcraft:blocks/arcaneearbottom");
        if (top == null || side == null || bottom == null) {
            return;
        }

        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);

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
        TileRenderHelper.drawTexturedCuboid(buf,
                -EPSILON, -EPSILON, -EPSILON,
                1.0F + EPSILON, 3.0F / 16.0F + EPSILON, 1.0F + EPSILON,
                bottom, top, side, side, side, side, 0xFFFFFFFF);
        TileRenderHelper.drawTexturedCuboid(buf,
                4.0F / 16.0F - EPSILON, 3.0F / 16.0F - EPSILON, 4.0F / 16.0F - EPSILON,
                12.0F / 16.0F + EPSILON, 1.0F + EPSILON, 12.0F / 16.0F + EPSILON,
                bottom, top, side, side, side, side, 0xFFFFFFFF);
        tess.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
