package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileAlchemyFurnace;

public class TileAlchemyFurnaceAdvancedRenderer extends TileEntitySpecialRenderer<TileAlchemyFurnace> {
    private static final ResourceLocation FURNACE =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace.png");
    private static final ResourceLocation FURNACE_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_on.png");
    private static final ResourceLocation TANK =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank.png");
    private static final ResourceLocation TANK_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank_on.png");

    @Override
    public void render(TileAlchemyFurnace tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float content = Math.min(1.0F, tile.vis / 50.0F);
        boolean burning = tile.isBurning();
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float pulse = burning ? (0.85F + (float) Math.sin(ticks / 6.0F) * 0.15F) : 0.65F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.55D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(burning ? FURNACE_ON : FURNACE);
        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(0.42F, 0xDDFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        bindTexture(burning ? TANK_ON : TANK);
        int tankAlpha = Math.max(60, (int) (255.0F * content * pulse));
        TileRenderHelper.drawTexturedQuad(0.30F, (tankAlpha << 24) | 0x00CCAAFF, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
