package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileEssentiaCrystalizerRenderer;
import thaumcraft.common.tiles.TileEssentiaCrystalizer;

public class ItemTubeRenderer extends TileEntityItemStackRenderer {

    private final TileEssentiaCrystalizerRenderer crystalizerRenderer = new TileEssentiaCrystalizerRenderer();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (stack.getMetadata() != 7) {
            return;
        }

        TileEssentiaCrystalizer crystalizer = new TileEssentiaCrystalizer();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        crystalizerRenderer.render(crystalizer, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        GlStateManager.popMatrix();
    }
}
