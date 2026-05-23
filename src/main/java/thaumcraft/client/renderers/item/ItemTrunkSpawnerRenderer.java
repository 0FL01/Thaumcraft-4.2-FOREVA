package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemTrunkSpawnerRenderer extends TileEntityItemStackRenderer {

    private static final ResourceLocation TRUNK_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/trunk.png");

    private static final ThreadLocal<ItemCameraTransforms.TransformType> CURRENT_TRANSFORM =
            ThreadLocal.withInitial(() -> ItemCameraTransforms.TransformType.NONE);

    private final ModelChest chest = new ModelChest();

    public static void setTransformType(ItemCameraTransforms.TransformType transformType) {
        CURRENT_TRANSFORM.set(transformType == null ? ItemCameraTransforms.TransformType.NONE : transformType);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        ItemCameraTransforms.TransformType transformType = CURRENT_TRANSFORM.get();
        boolean flipCullFace = transformType == ItemCameraTransforms.TransformType.GUI;

        GlStateManager.pushMatrix();
        try {
            if (flipCullFace) {
                GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(TRUNK_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            applyHeldTransform(transformType);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            chest.chestBelow.render(0.0625F);
            chest.chestLid.render(0.0625F);
            chest.chestKnob.render(0.0625F);
        } finally {
            if (flipCullFace) {
                GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            }

            setTransformType(ItemCameraTransforms.TransformType.NONE);
            GlStateManager.popMatrix();
        }
    }

    private static void applyHeldTransform(ItemCameraTransforms.TransformType transformType) {
        if (isThirdPersonHand(transformType)) {
            GlStateManager.translate(-0.25F, -0.5F, -0.25F);
            GlStateManager.translate(1.0F, 0.0F, 0.0F);
        } else if (isFirstPersonHand(transformType)) {
            GlStateManager.translate(-0.25F, -0.5F, -0.25F);
        }
    }

    private static boolean isThirdPersonHand(ItemCameraTransforms.TransformType transformType) {
        return transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    private static boolean isFirstPersonHand(ItemCameraTransforms.TransformType transformType) {
        return transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
    }
}
