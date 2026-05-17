package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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

    @Override
    public void render(TileVisRelay tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        EnumFacing facing = EnumFacing.byIndex(tile.orientation);
        float pulse = 0.85F + (float) Math.sin(ticks / 2.0F) * 0.12F;
        int coreColor = relayColor(tile.color);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByFace(facing);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(RELAY_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.rotate((ticks * 1.25F) % 360.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(0.26F, 0xD8FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.02D, 0.0D);
        GlStateManager.rotate((-ticks * 2.0F) % 360.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.orientBillboardToPlayer();
        int pulseAlpha = Math.min(255, Math.max(0, (int) (220.0F * pulse)));
        int pulseColor = (pulseAlpha << 24) | (coreColor & 0x00FFFFFF);
        TileRenderHelper.drawTexturedQuad(0.16F, pulseColor, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();

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
}
