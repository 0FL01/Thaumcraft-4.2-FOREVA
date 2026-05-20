package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.equipment.ItemBowBone;

public class ItemBowBoneRenderer extends TileEntityItemStackRenderer {

    private static final ModelResourceLocation BOW =
            new ModelResourceLocation("thaumcraft:itembowbone", "inventory");
    private static final ModelResourceLocation BOW_PULLING_0 =
            new ModelResourceLocation("thaumcraft:itembowbone_pulling_0", "inventory");
    private static final ModelResourceLocation BOW_PULLING_1 =
            new ModelResourceLocation("thaumcraft:itembowbone_pulling_1", "inventory");
    private static final ModelResourceLocation BOW_PULLING_2 =
            new ModelResourceLocation("thaumcraft:itembowbone_pulling_2", "inventory");

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemBowBone)) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-60.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(2.6666667F, 2.6666667F, 2.6666667F);
        GlStateManager.translate(-0.25F, -0.1875F, 0.1875F);
        GlStateManager.translate(0.0F, 0.125F, 0.3125F);
        GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.625F, 0.625F, -0.625F);
        GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        renderIcon(stack, partialTicks, Minecraft.getMinecraft().player);
        GlStateManager.popMatrix();
    }

    private void renderIcon(ItemStack stack, float partialTicks, EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        IBakedModel model = mc.getRenderItem().getItemModelMesher().getModelManager().getModel(getBowModel(stack, player, partialTicks));
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(0.0F, -0.3F, 0.0F);
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
        GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
        mc.getRenderItem().renderItem(stack, model);
        GlStateManager.disableRescaleNormal();
    }

    private static ModelResourceLocation getBowModel(ItemStack stack, EntityPlayer player, float partialTicks) {
        if (player == null || !player.isHandActive() || !ItemStack.areItemStacksEqual(player.getActiveItemStack(), stack)) {
            return BOW;
        }

        float pull = (stack.getMaxItemUseDuration() - (player.getItemInUseCount() - partialTicks)) / 20.0F;
        if (pull > 0.9F) {
            return BOW_PULLING_2;
        }
        if (pull > 0.65F) {
            return BOW_PULLING_1;
        }
        return BOW_PULLING_0;
    }
}
