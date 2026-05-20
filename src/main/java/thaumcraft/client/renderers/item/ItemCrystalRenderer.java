package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thaumcraft.client.renderers.tile.TileCrystalRenderer;
import thaumcraft.client.renderers.tile.TileEldritchCrystalRenderer;
import thaumcraft.common.tiles.TileCrystal;
import thaumcraft.common.tiles.TileEldritchCrystal;

public class ItemCrystalRenderer extends TileEntityItemStackRenderer {

    private final TileCrystalRenderer crystalRenderer = new TileCrystalRenderer();
    private final TileEldritchCrystalRenderer eldritchCrystalRenderer = new TileEldritchCrystalRenderer();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        int meta = stack.getMetadata();
        if (meta <= 6) {
            TileCrystal crystal = new InventoryTileCrystal(meta);
            crystalRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            crystalRenderer.render(crystal, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
            return;
        }
        if (meta == 7) {
            TileEldritchCrystal crystal = new TileEldritchCrystal();
            crystal.setPos(BlockPos.ORIGIN);
            eldritchCrystalRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            eldritchCrystalRenderer.render(crystal, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static final class InventoryTileCrystal extends TileCrystal {
        private final int metadata;

        private InventoryTileCrystal(int metadata) {
            this.metadata = metadata;
            this.setPos(BlockPos.ORIGIN);
        }

        @Override
        public int getBlockMetadata() {
            return metadata;
        }
    }
}
