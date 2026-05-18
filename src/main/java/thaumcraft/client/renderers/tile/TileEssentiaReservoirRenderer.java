package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.renderers.models.ModelEssentiaReservoir;
import thaumcraft.common.tiles.TileEssentiaReservoir;

public class TileEssentiaReservoirRenderer extends TileEntitySpecialRenderer<TileEssentiaReservoir> {
    private static final ResourceLocation RESERVOIR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/reservoir.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelEssentiaReservoir model = new ModelEssentiaReservoir();

    @Override
    public void render(TileEssentiaReservoir tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByFace(tile.facing);
        bindTexture(RESERVOIR_TEXTURE);
        model.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();

        renderLiquid(tile, x, y, z);
    }

    private void renderLiquid(TileEssentiaReservoir tile, double x, double y, double z) {
        if (tile.essentia == null || tile.essentia.visSize() <= 0) {
            return;
        }

        float fill = (float) tile.essentia.visSize() / (float) Math.max(1, tile.maxAmount);
        fill = TileRenderHelper.clamp01(fill);
        float minX = (float) x + 0.25F;
        float maxX = (float) x + 0.75F;
        float minZ = (float) z + 0.25F;
        float maxZ = (float) z + 0.75F;
        float minY = (float) y + 0.10F;
        float maxY = minY + 0.45F * fill;

        int color = colorFromReservoir(tile);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Top
        addVertex(buf, minX, maxY, minZ, r, g, b, a);
        addVertex(buf, minX, maxY, maxZ, r, g, b, a);
        addVertex(buf, maxX, maxY, maxZ, r, g, b, a);
        addVertex(buf, maxX, maxY, minZ, r, g, b, a);

        // Bottom
        addVertex(buf, maxX, minY, minZ, r, g, b, a * 0.9F);
        addVertex(buf, maxX, minY, maxZ, r, g, b, a * 0.9F);
        addVertex(buf, minX, minY, maxZ, r, g, b, a * 0.9F);
        addVertex(buf, minX, minY, minZ, r, g, b, a * 0.9F);

        // Sides
        addVertex(buf, minX, minY, minZ, r, g, b, a);
        addVertex(buf, minX, maxY, minZ, r, g, b, a);
        addVertex(buf, maxX, maxY, minZ, r, g, b, a);
        addVertex(buf, maxX, minY, minZ, r, g, b, a);

        addVertex(buf, maxX, minY, maxZ, r, g, b, a);
        addVertex(buf, maxX, maxY, maxZ, r, g, b, a);
        addVertex(buf, minX, maxY, maxZ, r, g, b, a);
        addVertex(buf, minX, minY, maxZ, r, g, b, a);

        addVertex(buf, minX, minY, maxZ, r, g, b, a);
        addVertex(buf, minX, maxY, maxZ, r, g, b, a);
        addVertex(buf, minX, maxY, minZ, r, g, b, a);
        addVertex(buf, minX, minY, minZ, r, g, b, a);

        addVertex(buf, maxX, minY, minZ, r, g, b, a);
        addVertex(buf, maxX, maxY, minZ, r, g, b, a);
        addVertex(buf, maxX, maxY, maxZ, r, g, b, a);
        addVertex(buf, maxX, minY, maxZ, r, g, b, a);

        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void addVertex(BufferBuilder buf, float x, float y, float z, float r, float g, float b, float a) {
        buf.pos(x, y, z).color(r, g, b, a).endVertex();
    }

    private static int colorFromReservoir(TileEssentiaReservoir tile) {
        int rgb = reservoirRgb(tile);
        int alpha = computeLiquidAlpha(tile);
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    private static int reservoirRgb(TileEssentiaReservoir tile) {
        Aspect aspect = tile.displayAspect;
        if (aspect == null && tile.essentia != null) {
            Aspect[] aspects = tile.essentia.getAspects();
            if (aspects.length > 0) {
                aspect = aspects[0];
            }
        }
        if (aspect != null) {
            return aspect.getColor() & 0x00FFFFFF;
        }
        int r = Math.max(0, Math.min(255, (int) (tile.colorR * 255.0F)));
        int g = Math.max(0, Math.min(255, (int) (tile.colorG * 255.0F)));
        int b = Math.max(0, Math.min(255, (int) (tile.colorB * 255.0F)));
        return (r << 16) | (g << 8) | b;
    }

    private static int computeLiquidAlpha(TileEssentiaReservoir tile) {
        float fullness = tile.essentia == null ? 0.0F : (float) tile.essentia.visSize() / Math.max(1.0F, tile.maxAmount);
        return Math.max(0, Math.min(255, (int) (140.0F + 80.0F * TileRenderHelper.clamp01(fullness))));
    }

    private static void orientByFace(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case NORTH:
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            default:
                break;
        }
    }
}
