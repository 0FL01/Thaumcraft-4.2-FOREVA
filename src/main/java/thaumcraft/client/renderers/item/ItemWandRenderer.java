package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.client.renderers.models.gear.ModelWand;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ItemWandRenderer extends TileEntityItemStackRenderer {

    private static final ThreadLocal<ItemCameraTransforms.TransformType> CURRENT_TRANSFORM =
            ThreadLocal.withInitial(() -> ItemCameraTransforms.TransformType.NONE);
    private static final float HAND_SCALE = 0.5F;
    private static final float HAND_Y_OFFSET = -0.5F;
    private static final float INVENTORY_X_OFFSET = 0.5F;
    private static final float INVENTORY_Y_OFFSET = 0.5F;
    private static final float INVENTORY_SCALE = 0.6F;
    private static final float INVENTORY_X_ROTATION = 20.0F;
    private static final float INVENTORY_Y_ROTATION = -45.0F;
    private static final float INVENTORY_Z_ROTATION = 45.0F;

    private final ModelWand model = new ModelWand();

    public static void setTransformType(ItemCameraTransforms.TransformType transformType) {
        CURRENT_TRANSFORM.set(transformType == null ? ItemCameraTransforms.TransformType.NONE : transformType);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemWandCasting)) {
            return;
        }

        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        EntityPlayer player = Minecraft.getMinecraft().player;
        ItemCameraTransforms.TransformType transformType = CURRENT_TRANSFORM.get();

        GlStateManager.pushMatrix();
        try {
            applyBasePose(wand, stack, transformType);
            if (isHandTransform(transformType)) {
                applyUseAnimation(wand, stack, player, partialTicks, isFirstPerson(transformType));
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            model.render(stack, partialTicks, player);
        } finally {
            GlStateManager.disableBlend();
            setTransformType(ItemCameraTransforms.TransformType.NONE);
            GlStateManager.popMatrix();
        }
    }

    private static void applyBasePose(ItemWandCasting wand, ItemStack stack,
                                      ItemCameraTransforms.TransformType transformType) {
        if (wand.isStaff(stack)) {
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
        }
        if (transformType == ItemCameraTransforms.TransformType.GUI) {
            applyInventoryPose(wand, stack);
        } else if (transformType == ItemCameraTransforms.TransformType.GROUND
                || transformType == ItemCameraTransforms.TransformType.FIXED) {
            applyEntityPose(wand, stack);
        } else {
            if (isHandTransform(transformType)) {
                GlStateManager.translate(0.5F, 1.5F + HAND_Y_OFFSET, 0.5F);
            } else {
                GlStateManager.translate(0.5F, 1.5F, 0.5F);
            }
            if (isFirstPerson(transformType)) {
                GlStateManager.scale(1.0F, 1.1F, 1.0F);
            }
            if (isHandTransform(transformType)) {
                GlStateManager.scale(HAND_SCALE, HAND_SCALE, HAND_SCALE);
            }
        }
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
    }

    private static void applyInventoryPose(ItemWandCasting wand, ItemStack stack) {
        GlStateManager.translate(INVENTORY_X_OFFSET, INVENTORY_Y_OFFSET, 0.0F);
        GlStateManager.scale(INVENTORY_SCALE, INVENTORY_SCALE, INVENTORY_SCALE);
        if (wand.isStaff(stack)) {
            GlStateManager.scale(0.8F, 0.8F, 0.8F);
        }
        GlStateManager.rotate(INVENTORY_X_ROTATION, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(INVENTORY_Y_ROTATION, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(INVENTORY_Z_ROTATION, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, 0.6F, 0.0F);
        if (wand.isStaff(stack)) {
            GlStateManager.translate(-0.7F, 0.6F, 0.0F);
        }
    }

    private static void applyEntityPose(ItemWandCasting wand, ItemStack stack) {
        if (wand.isStaff(stack)) {
            GlStateManager.translate(0.0F, 1.5F, 0.0F);
            GlStateManager.scale(0.9F, 0.9F, 0.9F);
        } else {
            GlStateManager.translate(0.0F, 1.0F, 0.0F);
        }
    }

    private static void applyUseAnimation(ItemWandCasting wand, ItemStack stack, EntityPlayer player, float partialTicks,
                                          boolean firstPerson) {
        if (player == null || !player.isHandActive() || !ItemStack.areItemStacksEqual(player.getActiveItemStack(), stack)) {
            return;
        }

        float useTicks = player.getItemInUseCount() + partialTicks;
        float t = Math.min(useTicks, 3.0F);
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        if (firstPerson) {
            GlStateManager.rotate(10.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        } else {
            GlStateManager.rotate(33.0F, 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.rotate(60.0F * (t / 3.0F), -1.0F, 0.0F, 0.0F);

        ItemFocusBasic focus = wand.getFocus(stack);
        ItemStack focusStack = wand.getFocusItem(stack);
        if (focus == null || focus.getAnimation(focusStack) == ItemFocusBasic.WandFocusAnimation.WAVE) {
            float wave = MathHelper.sin(useTicks / 10.0F) * 10.0F;
            GlStateManager.rotate(wave, 0.0F, 0.0F, 1.0F);
            wave = MathHelper.sin(useTicks / 15.0F) * 10.0F;
            GlStateManager.rotate(wave, 1.0F, 0.0F, 0.0F);
        } else if (focus.getAnimation(focusStack) == ItemFocusBasic.WandFocusAnimation.CHARGE) {
            float wave = MathHelper.sin(useTicks / 0.8F);
            GlStateManager.rotate(wave, 0.0F, 0.0F, 1.0F);
            wave = MathHelper.sin(useTicks / 0.7F);
            GlStateManager.rotate(wave, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
    }

    private static boolean isHandTransform(ItemCameraTransforms.TransformType transformType) {
        return isFirstPerson(transformType)
                || transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    private static boolean isFirstPerson(ItemCameraTransforms.TransformType transformType) {
        return transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
    }
}
