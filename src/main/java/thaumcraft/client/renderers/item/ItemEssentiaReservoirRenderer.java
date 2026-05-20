package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.renderers.tile.TileEssentiaReservoirRenderer;
import thaumcraft.common.tiles.TileEssentiaReservoir;

public class ItemEssentiaReservoirRenderer extends TileEntityItemStackRenderer {

    private static final ModelResourceLocation RESERVOIR_SHELL =
            new ModelResourceLocation("thaumcraft:blockessentiareservoir", "inventory");

    private final TileEssentiaReservoirRenderer reservoirRenderer = new TileEssentiaReservoirRenderer();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        TileEssentiaReservoir reservoir = createTile(stack);
        Minecraft mc = Minecraft.getMinecraft();
        IBakedModel model = mc.getRenderItem().getItemModelMesher().getModelManager().getModel(RESERVOIR_SHELL);

        GlStateManager.pushMatrix();
        mc.getRenderItem().renderItem(stack, model);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        reservoirRenderer.render(reservoir, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        GlStateManager.popMatrix();
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
