package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileAlembicRenderer;
import thaumcraft.client.renderers.tile.TileMagicWorkbenchChargerRenderer;
import thaumcraft.client.renderers.tile.TileThaumatoriumRenderer;
import thaumcraft.client.renderers.tile.TileVisRelayRenderer;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileMagicWorkbenchCharger;
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileVisRelay;

public class ItemMetalDeviceRenderer extends TileEntityItemStackRenderer {

    private final TileAlembicRenderer alembicRenderer = new TileAlembicRenderer();
    private final TileMagicWorkbenchChargerRenderer chargerRenderer = new TileMagicWorkbenchChargerRenderer();
    private final TileThaumatoriumRenderer thaumatoriumRenderer = new TileThaumatoriumRenderer();
    private final TileVisRelayRenderer relayRenderer = new TileVisRelayRenderer();

    public ItemMetalDeviceRenderer() {
        alembicRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        chargerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        thaumatoriumRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        relayRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        int meta = stack.getMetadata();
        if (meta == 1) {
            TileAlembic alembic = new TileAlembic();
            GlStateManager.pushMatrix();
            try {
                restoreLegacyInventoryOrigin();
                GlStateManager.translate(-0.5F, 0.0F, -0.5F);
                alembicRenderer.render(alembic, 0.0D, 0.0D, 0.0D, 0.0F, 0, 1.0F);
            } finally {
                GlStateManager.popMatrix();
            }
            return;
        }
        if (meta == 2) {
            TileMagicWorkbenchCharger charger = new TileMagicWorkbenchCharger();
            GlStateManager.pushMatrix();
            try {
                restoreLegacyInventoryOrigin();
                GlStateManager.translate(-0.5F, -0.5F, -0.5F);
                chargerRenderer.render(charger, 0.0D, 0.0D, 0.0D, 0.0F, 0, 1.0F);
            } finally {
                GlStateManager.popMatrix();
            }
            return;
        }
        if (meta == 10 || meta == 11) {
            TileThaumatorium thaumatorium = new TileThaumatorium();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.65F, 0.65F, 0.65F);
            GlStateManager.translate(-0.75F, -0.15F, -0.75F);
            thaumatoriumRenderer.render(thaumatorium, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
            return;
        }
        if (meta == 14) {
            TileVisRelay relay = new TileVisRelay();
            GlStateManager.pushMatrix();
            try {
                restoreLegacyInventoryOrigin();
                GlStateManager.scale(1.5F, 1.5F, 1.5F);
                GlStateManager.translate(-0.5F, -0.25F, -0.5F);
                relayRenderer.render(relay, 0.0D, 0.0D, 0.0D, 0.0F, 0, 1.0F);
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    private static void restoreLegacyInventoryOrigin() {
        // Forge 1.12 enters TEISR at -0.5 on every axis; TC4's custom inventory renderer did not.
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
    }
}
