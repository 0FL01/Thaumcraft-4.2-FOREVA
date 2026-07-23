package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.ModelArcaneWorkbench;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class TileDeconstructionTableRenderer extends TileEntitySpecialRenderer<TileDeconstructionTable> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/decontable.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();
    private ItemStack thaumometer = ItemStack.EMPTY;

    @Override
    public void render(TileDeconstructionTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        GlStateManager.pushMatrix();
        bindTexture(TABLE_TEXTURE);
        GlStateManager.translate(x + 0.5F, y + 1.0F, z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        tableModel.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();

        float ticks = getAnimationTicks(tile, partialTicks);
        renderThaumometer(tile, x, y, z);

        ItemStack input = tile.getStackInSlot(0);
        if (!input.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.15D, z + 0.5D);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
            float hoverStart = MathHelper.sin(ticks / 14.0F) * 0.2F + 0.2F;
            renderFramedItem(tile, input, hoverStart);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        if (tile.aspect != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.081D, z + 0.5D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.024F, 0.024F, 0.024F);
            UtilsFX.drawTag(-8, -8, tile.aspect, 0.0F, 0, 0.0D, 1, 0.8F, false);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderThaumometer(TileDeconstructionTable tile, double x, double y, double z) {
        if (this.thaumometer.isEmpty() && ConfigItems.itemThaumometer != null) {
            this.thaumometer = new ItemStack(ConfigItems.itemThaumometer);
        }
        if (this.thaumometer.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.92D, z + 0.5D);
        GlStateManager.scale(0.8F, 0.8F, 0.8F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        renderFramedItem(tile, this.thaumometer, 0.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * TC4 rendered these EntityItems with RenderItem.renderInFrame enabled. The 1.12
     * equivalent is a direct ground-model render: preserve the original bob phase,
     * but do not add dropped-item camera rotation or the modern ground-height offset.
     */
    private void renderFramedItem(TileDeconstructionTable tile, ItemStack stack, float hoverStart) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        RenderItem itemRenderer = mc.getRenderItem();
        ItemStack renderStack = stack.copy();
        renderStack.setCount(1);
        IBakedModel model = itemRenderer.getItemModelWithOverrides(
                renderStack, tile == null ? null : tile.getWorld(), null);

        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, MathHelper.sin(hoverStart) * 0.1F + 0.1F, 0.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        model = ForgeHooksClient.handleCameraTransforms(
                model, ItemCameraTransforms.TransformType.GROUND, false);
        itemRenderer.renderItem(renderStack, model);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private float getAnimationTicks(TileDeconstructionTable tile, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            return mc.player.ticksExisted + partialTicks;
        }
        return TileRenderHelper.ticks(tile, partialTicks);
    }
}
