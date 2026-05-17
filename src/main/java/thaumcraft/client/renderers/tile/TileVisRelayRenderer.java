package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.client.renderers.models.ModelVisRelay;
import thaumcraft.common.tiles.TileVisRelay;

public class TileVisRelayRenderer extends TileEntitySpecialRenderer<TileVisRelay> {

    private static final ResourceLocation RELAY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vis_relay.png");

    private static final int[] RELAY_COLORS = {
            0xFFFF7E,
            0xFF8844,
            0x99CCFF,
            0x80FF80,
            0xCC99FF,
            0xAAAAAA
    };
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelVisRelay model = new ModelVisRelay();

    @Override
    public void render(TileVisRelay tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        EnumFacing facing = EnumFacing.byIndex(tile.orientation);
        float pulse = 0.95F + (float) Math.sin(ticks / 2.0F) * 0.05F;
        int coreColor = relayColor(tile.color & 0xFF);
        float[] crystalColor = unpackRgb(coreColor, 1.0F / 200.0F);
        boolean parentValid = VisNetHandler.isNodeValid(tile.getParent());
        float glow = (parentValid ? 0.8F : 0.35F) + (float) Math.sin(ticks / 3.0F) * 0.08F;
        glow = Math.max(0.2F, glow);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByFace(facing);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableCull();
        bindTexture(RELAY_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75F, 0.75F, 0.75F);
        GlStateManager.translate(0.0F, 0.0F, -0.16F);
        model.renderRingBase(MODEL_SCALE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.rotate((ticks * 1.5F) % 360.0F, 0.0F, 1.0F, 0.0F);
        model.renderRingFloat(MODEL_SCALE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(pulse, pulse, pulse);
        GlStateManager.color(
                Math.min(1.0F, crystalColor[0] * glow),
                Math.min(1.0F, crystalColor[1] * glow),
                Math.min(1.0F, crystalColor[2] * glow),
                1.0F);
        model.renderCrystal(MODEL_SCALE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
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

    private static int relayColor(int colorIndex) {
        return colorIndex >= 0 && colorIndex < RELAY_COLORS.length ? RELAY_COLORS[colorIndex] : 0xFFFFFF;
    }

    private static float[] unpackRgb(int color, float normalizeScale) {
        float r = ((color >> 16) & 0xFF) * normalizeScale;
        float g = ((color >> 8) & 0xFF) * normalizeScale;
        float b = (color & 0xFF) * normalizeScale;
        return new float[]{r, g, b};
    }
}
