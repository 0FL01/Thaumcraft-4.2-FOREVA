package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.renderers.tile.TileArcaneBoreBaseRenderer;
import thaumcraft.client.renderers.tile.TileArcaneBoreRenderer;
import thaumcraft.client.renderers.tile.TileBannerRenderer;
import thaumcraft.client.renderers.tile.TileBellowsRenderer;
import thaumcraft.common.tiles.TileArcaneBore;
import thaumcraft.common.tiles.TileArcaneBoreBase;
import thaumcraft.common.tiles.TileBanner;
import thaumcraft.common.tiles.TileBellows;

public class ItemWoodenDeviceRenderer extends TileEntityItemStackRenderer {

    private static final ThreadLocal<ItemCameraTransforms.TransformType> CURRENT_TRANSFORM =
            ThreadLocal.withInitial(() -> ItemCameraTransforms.TransformType.NONE);

    private final TileBellowsRenderer bellowsRenderer = new TileBellowsRenderer();
    private final TileArcaneBoreBaseRenderer boreBaseRenderer = new TileArcaneBoreBaseRenderer();
    private final TileArcaneBoreRenderer boreRenderer = new TileArcaneBoreRenderer();
    private final TileBannerRenderer bannerRenderer = new TileBannerRenderer();

    public ItemWoodenDeviceRenderer() {
        bellowsRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        boreBaseRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        boreRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        bannerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    public static void setTransformType(ItemCameraTransforms.TransformType transformType) {
        CURRENT_TRANSFORM.set(transformType == null ? ItemCameraTransforms.TransformType.NONE : transformType);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        try {
            if (stack == null || stack.isEmpty()) {
                return;
            }
            int meta = stack.getMetadata();
            if (meta == 0) {
                TileBellows bellows = new TileBellows();
                GlStateManager.pushMatrix();
                try {
                    restoreLegacyInventoryOrigin();
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.5F, -0.5F, -0.5F);
                    bellowsRenderer.render(bellows, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
                } finally {
                    GlStateManager.popMatrix();
                }
                return;
            }
            if (meta == 4) {
                TileArcaneBoreBase boreBase = new TileArcaneBoreBase();
                GlStateManager.pushMatrix();
                try {
                    restoreLegacyInventoryOrigin();
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.5F, -0.5F, -0.5F);
                    boreBaseRenderer.render(boreBase, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
                } finally {
                    GlStateManager.popMatrix();
                }
                return;
            }
            if (meta == 5) {
                TileArcaneBore bore = new TileArcaneBore();
                GlStateManager.pushMatrix();
                try {
                    restoreLegacyInventoryOrigin();
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.5F, -0.75F, -0.5F);
                    boreRenderer.render(bore, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
                } finally {
                    GlStateManager.popMatrix();
                }
                return;
            }
            if (meta == 8) {
                TileBanner banner = new TileBanner();
                banner.setFacing((byte) 8);
                applyBannerData(stack, banner);
                GlStateManager.pushMatrix();
                try {
                    // NBT changes only the banner skin; every variant must expose the same normalized item origin.
                    restoreLegacyInventoryOrigin();
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.5F, -1.0F, -0.5F);
                    bannerRenderer.render(banner, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
                } finally {
                    GlStateManager.popMatrix();
                }
            }
        } finally {
            setTransformType(ItemCameraTransforms.TransformType.NONE);
        }
    }

    private static void restoreLegacyInventoryOrigin() {
        // Forge 1.12 enters TEISR at -0.5 on every axis; TC4's custom inventory renderers did not.
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
    }

    private static void applyBannerData(ItemStack stack, TileBanner banner) {
        if (!stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return;
        }
        if (tag.hasKey("aspect")) {
            String aspectTag = tag.getString("aspect");
            if (aspectTag != null && !aspectTag.isEmpty()) {
                banner.setAspect(Aspect.getAspect(aspectTag));
            }
        }
        if (tag.hasKey("color")) {
            banner.setColor(tag.getByte("color"));
        }
    }
}
