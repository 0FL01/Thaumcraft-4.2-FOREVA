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

    private final ModelChest chest = new ModelChest();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TRUNK_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        chest.chestBelow.render(0.0625F);
        chest.chestLid.render(0.0625F);
        chest.chestKnob.render(0.0625F);
        GlStateManager.popMatrix();
    }
}
