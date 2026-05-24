package thaumcraft.client.renderers.item;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemChestHungryRenderer extends TileEntityItemStackRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/chesthungry.png");
    private final ModelChest model = new ModelChest();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        net.minecraft.client.Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        this.model.chestLid.rotateAngleX = 0.0F;
        this.model.chestKnob.rotateAngleX = 0.0F;
        this.model.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
