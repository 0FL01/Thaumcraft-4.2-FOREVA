package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemTrunkSpawnerRenderer extends TileEntityItemStackRenderer {

    private static final ResourceLocation TRUNK_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/trunk.png");

    private final ModelChest model = new ModelChest();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(TRUNK_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        model.chestLid.rotateAngleX = 0.0F;
        model.chestKnob.rotateAngleX = 0.0F;
        model.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
