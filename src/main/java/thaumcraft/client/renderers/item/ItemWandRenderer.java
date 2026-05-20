package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.client.renderers.models.gear.ModelWand;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ItemWandRenderer extends TileEntityItemStackRenderer {

    private final ModelWand model = new ModelWand();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemWandCasting)) {
            return;
        }

        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        EntityPlayer player = Minecraft.getMinecraft().player;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        if (wand.isStaff(stack)) {
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
        }

        if (player != null && player.isHandActive() && ItemStack.areItemStacksEqual(player.getActiveItemStack(), stack)) {
            float useTicks = player.getItemInUseCount() + partialTicks;
            float t = Math.min(useTicks, 3.0F);
            GlStateManager.translate(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(33.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(60.0F * (t / 3.0F), -1.0F, 0.0F, 0.0F);

            ItemFocusBasic focus = wand.getFocus(stack);
            if (focus == null || focus.getAnimation(wand.getFocusItem(stack)) == ItemFocusBasic.WandFocusAnimation.WAVE) {
                float wave = MathHelper.sin(useTicks / 10.0F) * 10.0F;
                GlStateManager.rotate(wave, 0.0F, 0.0F, 1.0F);
                wave = MathHelper.sin(useTicks / 15.0F) * 10.0F;
                GlStateManager.rotate(wave, 1.0F, 0.0F, 0.0F);
            } else if (focus.getAnimation(wand.getFocusItem(stack)) == ItemFocusBasic.WandFocusAnimation.CHARGE) {
                float wave = MathHelper.sin(useTicks / 0.8F);
                GlStateManager.rotate(wave, 0.0F, 0.0F, 1.0F);
                wave = MathHelper.sin(useTicks / 0.7F);
                GlStateManager.rotate(wave, 1.0F, 0.0F, 0.0F);
            }
            GlStateManager.translate(0.0F, -1.0F, 0.0F);
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        model.render(stack, partialTicks, player);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
