package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thaumcraft.client.renderers.tile.TileArcaneWorkbenchRenderer;
import thaumcraft.client.renderers.tile.TileDeconstructionTableRenderer;
import thaumcraft.client.renderers.tile.TileResearchTableRenderer;
import thaumcraft.client.renderers.tile.TileTableRenderer;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileDeconstructionTable;
import thaumcraft.common.tiles.TileResearchTable;
import thaumcraft.common.tiles.TileTable;

public class ItemTableRenderer extends TileEntityItemStackRenderer {
    private final TileTableRenderer tableRenderer = new TileTableRenderer();
    private final TileResearchTableRenderer researchTableRenderer = new TileResearchTableRenderer();
    private final TileDeconstructionTableRenderer deconstructionRenderer = new TileDeconstructionTableRenderer();
    private final TileArcaneWorkbenchRenderer arcaneWorkbenchRenderer = new TileArcaneWorkbenchRenderer();
    private final TileResearchTable researchTable = new TileResearchTable();

    public ItemTableRenderer() {
        tableRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        researchTableRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        deconstructionRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        arcaneWorkbenchRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        researchTable.setInventorySlotContents(0, new ItemStack(ConfigItems.itemInkwell));
        researchTable.setInventorySlotContents(1, new ItemStack(ConfigItems.itemResearchNotes));
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int meta = stack.getMetadata();
        boolean rescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
        GlStateManager.pushMatrix();
        try {
            restoreLegacyInventoryOrigin();
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            if (meta == 0) {
                tableRenderer.render(new TileTable(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            } else if (meta == 1) {
                GlStateManager.translate(-0.5F, 0.0F, 0.0F);
                researchTableRenderer.render(researchTable, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            } else if (meta == 14) {
                deconstructionRenderer.render(new TileDeconstructionTable(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            } else if (meta == 15) {
                arcaneWorkbenchRenderer.render(new TileArcaneWorkbench(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            }
        } finally {
            if (rescaleNormalEnabled) {
                GlStateManager.enableRescaleNormal();
            } else {
                GlStateManager.disableRescaleNormal();
            }
            GlStateManager.popMatrix();
        }
    }

    private static void restoreLegacyInventoryOrigin() {
        // Forge 1.12 enters TEISR at -0.5 on every axis; TC4's inventory renderer did not.
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
    }
}
