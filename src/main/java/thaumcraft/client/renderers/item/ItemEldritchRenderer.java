package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import thaumcraft.client.renderers.tile.TileEldritchCapRenderer;
import thaumcraft.client.renderers.tile.TileEldritchCrabSpawnerRenderer;
import thaumcraft.client.renderers.tile.TileEldritchLockRenderer;
import thaumcraft.client.renderers.tile.TileEldritchObeliskRenderer;
import thaumcraft.common.tiles.TileEldritchAltar;
import thaumcraft.common.tiles.TileEldritchCap;
import thaumcraft.common.tiles.TileEldritchCrabSpawner;
import thaumcraft.common.tiles.TileEldritchLock;
import thaumcraft.common.tiles.TileEldritchObelisk;

public class ItemEldritchRenderer extends TileEntityItemStackRenderer {

    private final TileEldritchCapRenderer capRenderer = new TileEldritchCapRenderer();
    private final TileEldritchCapRenderer altarRenderer =
            new TileEldritchCapRenderer(TileEldritchCapRenderer.altarTexture());
    private final TileEldritchObeliskRenderer obeliskRenderer = new TileEldritchObeliskRenderer();
    private final TileEldritchLockRenderer lockRenderer = new TileEldritchLockRenderer();
    private final TileEldritchCrabSpawnerRenderer crabSpawnerRenderer = new TileEldritchCrabSpawnerRenderer();

    public ItemEldritchRenderer() {
        capRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        altarRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        obeliskRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        lockRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        crabSpawnerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        int meta = stack.getMetadata();
        if (meta == 0) {
            TileEldritchAltar altar = new TileEldritchAltar();
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            altarRenderer.render(altar, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
            return;
        }
        if (meta == 1) {
            TileEldritchObelisk obelisk = new TileEldritchObelisk();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.33F, 0.33F, 0.33F);
            GlStateManager.translate(0.0F, -0.1F, 0.0F);
            obeliskRenderer.render(obelisk, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
            return;
        }
        if (meta == 3) {
            TileEldritchCap cap = new TileEldritchCap();
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            capRenderer.render(cap, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
            return;
        }
        if (meta == 8) {
            TileEldritchLock lock = new TileEldritchLock();
            lock.setFacing((byte) EnumFacing.SOUTH.getIndex());
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.2F, 0.2F, 0.2F);
            GlStateManager.translate(-2.5F, -2.4F, -2.5F);
            lockRenderer.render(lock, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
            return;
        }
        if (meta == 9) {
            TileEldritchCrabSpawner crabSpawner = new TileEldritchCrabSpawner();
            crabSpawner.setFacing((byte) EnumFacing.SOUTH.getIndex());
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            crabSpawnerRenderer.render(crabSpawner, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}
