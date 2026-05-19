package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelNodeStabilizer;
import thaumcraft.common.tiles.TileNodeStabilizer;

public class TileNodeStabilizerRenderer extends TileEntitySpecialRenderer<TileNodeStabilizer> {

    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_stabilizer.png");
    private static final ResourceLocation OVER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_stabilizer_over.png");
    private static final ResourceLocation BUBBLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/node_bubble.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelNodeStabilizer model = new ModelNodeStabilizer();

    @Override
    public void render(TileNodeStabilizer tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float progress = TileRenderHelper.clamp01(tile.count / 37.0F);
        int lock = resolveLock(tile);
        boolean locked = lock == 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.55D, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableCull();

        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((ticks * (1.5F + i * 0.2F)) % 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0D, 0.0D, 0.12D + progress * 0.11D);

            bindTexture(BASE_TEXTURE);
            model.renderPiston(MODEL_SCALE);

            bindTexture(OVER_TEXTURE);
            float pulse = 0.85F + (float) Math.sin((ticks + i * 5.0F) / 3.0F) * 0.1F;
            if (locked) {
                GlStateManager.color(1.0F * pulse, 0.2F * pulse, 0.2F * pulse, 1.0F);
            } else {
                GlStateManager.color(pulse, pulse, pulse, 1.0F);
            }
            model.renderPiston(MODEL_SCALE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        if (tile.count > 0) {
            float bubblePulse = 0.5F + (float) Math.sin(ticks / 8.0F) * 0.1F;
            int bubbleColor = locked ? 0xCCFF4444 : 0xCCFFFFFF;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, -0.9D, 0.0D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            TileRenderHelper.orientBillboardToPlayer();
            bindTexture(BUBBLE_TEXTURE);
            TileRenderHelper.drawTexturedQuad(0.18F * progress * bubblePulse, bubbleColor, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static int resolveLock(TileNodeStabilizer tile) {
        if (tile.lock > 0) {
            return tile.lock;
        }
        int meta = tile.getBlockType().getMetaFromState(tile.getWorld().getBlockState(tile.getPos()));
        return meta == 9 ? 1 : 2;
    }
}
