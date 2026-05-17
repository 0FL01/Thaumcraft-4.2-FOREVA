package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileEldritchLock;

public class TileEldritchLockRenderer extends TileEntitySpecialRenderer<TileEldritchLock> {
    private static final ResourceLocation CUBE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_cube.png");
    private static final ResourceLocation TUNNEL =
            new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    private static final ResourceLocation PARTICLE =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    private static final ResourceLocation PARTICLE_FALLBACK =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield32.png");

    @Override
    public void render(TileEldritchLock tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        EnumFacing facing = EnumFacing.byIndex(tile.getFacing());

        renderLockRings(tile, x, y, z, ticks);
        renderInsertKey(tile, x, y, z, ticks, facing);
        renderLockField(tile, x, y, z, ticks, facing);
    }

    private void renderLockRings(TileEldritchLock tile, double x, double y, double z, float ticks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(CUBE);

        int level = tile.count < 0 ? 0 : Math.max(1, 5 - (tile.count / 20));
        for (int u = 0; u < 4; u++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F * u, 0.0F, 1.0F, 0.0F);
            for (int a = 1; a < level; a++) {
                GlStateManager.pushMatrix();
                float wobble = (float) Math.sin((ticks + a * 10.0F + u * 20.0F) / 20.0F) * 0.1F;
                if (a == 1 || a == 4) {
                    wobble = wobble * 0.5F + 0.2F;
                }
                GlStateManager.translate(0.0D, 0.25D + 0.5D * a, 0.0D);
                GlStateManager.scale(0.5F + wobble, 0.5F, 0.5F + wobble);
                TileRenderHelper.drawTexturedQuad(0.35F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderInsertKey(TileEldritchLock tile, double x, double y, double z, float ticks, EnumFacing facing) {
        if (tile.count < 0 || ConfigItems.itemEldritchObject == null || facing == null) {
            return;
        }

        ItemStack key = new ItemStack(ConfigItems.itemEldritchObject, 1, 2);
        GlStateManager.pushMatrix();
        GlStateManager.translate(
                x + 0.5D + facing.getXOffset() * 0.525D,
                y + 0.285D,
                z + 0.5D + facing.getZOffset() * 0.525D);
        GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        TileRenderHelper.renderFloatingItem(key, ticks, 0.0F, 0.6F);
        GlStateManager.popMatrix();
    }

    private void renderLockField(TileEldritchLock tile, double x, double y, double z, float ticks, EnumFacing facing) {
        if (facing == null || facing.getAxis().isVertical()) {
            return;
        }

        float pulse = 0.4F + (float) Math.sin(ticks / 6.0F) * 0.1F;
        boolean inRange = this.rendererDispatcher != null
                && this.rendererDispatcher.entity != null
                && this.rendererDispatcher.entity.getDistanceSq(tile.getPos()) < 512.0D;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0D, 0.5D, -0.505D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        if (inRange) {
            bindTexture(TUNNEL);
            GlStateManager.blendFunc(770, 771);
            TileRenderHelper.drawTexturedQuad(2.5F, 0x88FF88FF, 0.0F, 1.0F, 0.0F, 1.0F);
            bindTexture(PARTICLE);
            GlStateManager.blendFunc(1, 1);
            GlStateManager.rotate((ticks * 3.0F) % 360.0F, 0.0F, 0.0F, 1.0F);
            TileRenderHelper.drawTexturedQuad(2.55F, (0x66 << 24) | 0x00AA88FF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.rotate((ticks * -4.0F) % 360.0F, 0.0F, 0.0F, 1.0F);
            int dynamicAlpha = Math.min(255, Math.max(0, (int) (0x55 + pulse * 40.0F)));
            TileRenderHelper.drawTexturedQuad(2.55F, (dynamicAlpha << 24) | 0x00FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        } else {
            bindTexture(PARTICLE_FALLBACK);
            GlStateManager.blendFunc(770, 771);
            TileRenderHelper.drawTexturedQuad(2.5F, 0xAA808080, 0.0F, 1.0F, 0.0F, 1.0F);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
