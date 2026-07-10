package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.renderers.tile.TileEssentiaReservoirRenderer;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileEssentiaReservoir;

public class ItemEssentiaReservoirRenderer extends TileEntityItemStackRenderer {

    private final TileEssentiaReservoirRenderer reservoirRenderer = new TileEssentiaReservoirRenderer();

    public ItemEssentiaReservoirRenderer() {
        reservoirRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        TileEssentiaReservoir reservoir = createTile(stack);
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();
        try {
            renderReservoirCore(mc);
            // Forge's builtin/entity path already supplies the outer -0.5 block transform.
            reservoirRenderer.render(reservoir, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static void renderReservoirCore(Minecraft mc) {
        if (ConfigBlocks.blockEssentiaReservoir == null) {
            return;
        }
        GlStateManager.pushMatrix();
        try {
            // BlockModelRenderer adds +90 Y for inventory brightness rendering.
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            mc.getBlockRendererDispatcher().renderBlockBrightness(
                    ConfigBlocks.blockEssentiaReservoir.getDefaultState(), 1.0F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static TileEssentiaReservoir createTile(ItemStack stack) {
        TileEssentiaReservoir reservoir = new TileEssentiaReservoir();
        if (!stack.hasTagCompound()) {
            return reservoir;
        }

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return reservoir;
        }

        AspectList essentia = new AspectList();
        essentia.readFromNBT(tag);
        if (essentia.visSize() > 0) {
            reservoir.essentia = essentia;
        }
        if (tag.hasKey("maxAmount")) {
            reservoir.maxAmount = tag.getInteger("maxAmount");
        }
        if (tag.hasKey("face")) {
            reservoir.facing = EnumFacing.byIndex(tag.getByte("face"));
        } else if (tag.hasKey("facing")) {
            reservoir.facing = EnumFacing.byIndex(tag.getByte("facing"));
        }
        if (reservoir.facing == null) {
            reservoir.facing = EnumFacing.DOWN;
        }
        if (tag.hasKey("displayAspect")) {
            reservoir.displayAspect = Aspect.getAspect(tag.getString("displayAspect"));
        }
        if (reservoir.displayAspect == null && reservoir.essentia.visSize() > 0) {
            Aspect[] aspects = reservoir.essentia.getAspects();
            if (aspects.length > 0) {
                reservoir.displayAspect = aspects[0];
            }
        }
        if (reservoir.displayAspect != null) {
            int color = reservoir.displayAspect.getColor();
            reservoir.colorR = ((color >> 16) & 0xFF) / 255.0F;
            reservoir.colorG = ((color >> 8) & 0xFF) / 255.0F;
            reservoir.colorB = (color & 0xFF) / 255.0F;
        }
        if (tag.hasKey("colorR")) {
            reservoir.colorR = tag.getFloat("colorR");
        }
        if (tag.hasKey("colorG")) {
            reservoir.colorG = tag.getFloat("colorG");
        }
        if (tag.hasKey("colorB")) {
            reservoir.colorB = tag.getFloat("colorB");
        }
        return reservoir;
    }
}
