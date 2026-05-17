package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
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
    private static final ResourceLocation FIELD_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");

    @Override
    public void render(TileEldritchObelisk tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        boolean outer = tile.getWorld().provider.getDimension() == Config.dimensionOuterId;
        ResourceLocation sideTexture = outer ? SIDE_TEXTURE_OUTER : SIDE_TEXTURE;
        ResourceLocation capTexture = outer ? CAP_TEXTURE_OUTER : CAP_TEXTURE;
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float bob = (float) Math.sin(ticks / 10.0F) * 0.1F + 0.1F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + bob + 1.0D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(sideTexture);
        renderSides(0.48F, 3.0F);

        bindTexture(capTexture);
        TileRenderHelper.drawTexturedQuad(0.52F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0D, 3.0D, 0.0D);
        TileRenderHelper.drawTexturedQuad(0.52F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0D, -3.0D, 0.0D);

        bindTexture(FIELD_TEXTURE);
        float glowScale = 0.26F + (float) Math.sin(ticks / 5.0F) * 0.05F;
        int glowColor = outer ? 0xBBAA66FF : 0xBBCC99FF;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0D, 1.5D, -0.50D);
            TileRenderHelper.drawTexturedQuad(glowScale, glowColor, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void renderSides(float half, float height) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        face(buf, -half, 0.0F, -half, half, height, -half, 0.0F, 1.0F);
        face(buf, -half, 0.0F, half, half, height, half, 0.0F, 1.0F);
        faceZ(buf, -half, 0.0F, -half, -half, height, half, 0.0F, 1.0F);
        faceZ(buf, half, 0.0F, -half, half, height, half, 0.0F, 1.0F);

        tess.draw();
    }

    private static void face(BufferBuilder buf, float x0, float y0, float z, float x1, float y1, float z1, float v0, float v1) {
        buf.pos(x0, y1, z).tex(0.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x0, y0, z).tex(0.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }

    private static void faceZ(BufferBuilder buf, float x, float y0, float z0, float x1, float y1, float z1, float v0, float v1) {
        buf.pos(x, y1, z0).tex(0.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x, y0, z0).tex(0.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }
}
