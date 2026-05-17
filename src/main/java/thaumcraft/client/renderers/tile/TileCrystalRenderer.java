package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.tiles.TileCrystal;

public class TileCrystalRenderer extends TileEntitySpecialRenderer<TileCrystal> {
    private static final ResourceLocation CRYSTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/crystal.png");

    @Override
    public void render(TileCrystal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        int md = tile.getBlockMetadata();
        int baseColor = md == 6 ? ItemShard.colors[1 + (int) (tile.getWorld().getTotalWorldTime() % 6L)] : colorForMeta(md);
        Random rand = new Random(tile.getPos().toLong() ^ 0x5B96A5D1L);

        bindTexture(CRYSTAL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByAttachment(tile.orientation);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        renderShard(rand, baseColor, 1.1F, 0.0F, 0.0F);
        for (int i = 1; i < 6; i++) {
            int color = md == 6 ? ItemShard.colors[i == 5 ? 6 : i] : baseColor;
            renderShard(rand, color, 0.8F, i * 72.0F + rand.nextInt(36), 14.0F + rand.nextInt(16));
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderShard(Random rand, int color, float size, float yaw, float pitch) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
        float sx = (0.15F + rand.nextFloat() * 0.075F) * size;
        float sy = (0.50F + rand.nextFloat() * 0.10F) * size;
        float sz = (0.15F + rand.nextFloat() * 0.05F) * size;
        GlStateManager.scale(sx, sy, sz);
        TileRenderHelper.drawTexturedQuad(0.5F, 0xFF000000 | (color & 0x00FFFFFF), 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.5F, 0xFF000000 | (color & 0x00FFFFFF), 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static int colorForMeta(int md) {
        int idx = Math.max(0, Math.min(6, md + 1));
        return ItemShard.colors[idx];
    }

    private static void orientByAttachment(short orientation) {
        EnumFacing face = EnumFacing.byIndex(orientation);
        if (face == null) {
            return;
        }
        switch (face) {
            case DOWN:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case NORTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
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
