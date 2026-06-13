package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.client.renderers.models.gear.ModelWand;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.io.File;

/**
 * TEISR for {@link ItemWandCasting}. Renders rod + cap + (optional) focus from stack NBT using
 * {@link ModelWand}.
 *
 * <p>Base pose (where the item sits) is resolved from {@link WandRenderCalibration} so it can be
 * tuned without recompiling. Use animation (how it moves while a focus is active) is kept as a
 * separate step and only runs for hand contexts, matching the original 1.7.10 behaviour.
 *
 * <p>Transform application order is fixed:
 * <pre>
 *   pushMatrix
 *     translate(preTranslate)
 *     translate(translate)
 *     [if first person] scale(firstPersonScale)
 *     scale(scale)
 *     scale(scaleMultiplier)
 *     rotateX -> rotateY -> rotateZ
 *     translate(postTranslate)
 *     rotate(finalRotate)            // TC4 model basis correction (180 about X)
 *     [if hand] applyUseAnimation    // separate from base pose
 *     enableBlend
 *     model.render(stack, partialTicks, player)
 *     disableBlend
 *   popMatrix
 * </pre>
 *
 * <p>Model basis (see ModelWand): the ModelRenderer geometry is authored Y-down (cap at model
 * y~0, rod extending to y~20, bottom cap at y~20). The {@code finalRotate} of 180 about X flips
 * it so the wand points upward in world space. This mirrors the original TC4 IItemRenderer which
 * called {@code glRotatef(180, 1, 0, 0)} immediately before {@code model.render(...)}.
 */
@SideOnly(Side.CLIENT)
public class ItemWandRenderer extends TileEntityItemStackRenderer {

    private static final Logger LOGGER = LogManager.getLogger("Thaumcraft");

    private static final ThreadLocal<ItemCameraTransforms.TransformType> CURRENT_TRANSFORM =
            ThreadLocal.withInitial(() -> ItemCameraTransforms.TransformType.NONE);

    /** Directory for optional debug JSON dumps ({@code -Dthaumcraft.debugWandRender=true}). */
    private static final File DUMP_DIR = new File("run/render-dumps/wand");

    private final ModelWand model = new ModelWand();

    // last context logged by the debug path; render runs single-threaded on the client
    private static String lastDebugKey = "";

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

        String kind = resolveKind(wand, stack);
        WandRenderCalibration.Transform t = WandRenderCalibration.get(kind, transformType);
        if (t.mirrorFromRightHand) {
            t = mirrorFromRightHand(kind, t, transformType);
        }

        maybeDebugLog(wand, stack, kind, transformType, t);

        GlStateManager.pushMatrix();
        try {
            applyBasePose(t, transformType);
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

    /**
     * Applies the resolved base pose. The order here is the single source of truth referenced by
     * {@link WandRenderCalibration}. Do not reorder casually: the defaults were chosen so this
     * exact sequence reproduces the original hardcoded matrices.
     */
    private static void applyBasePose(WandRenderCalibration.Transform t,
                                      ItemCameraTransforms.TransformType transformType) {
        GlStateManager.translate(t.preTranslateX, t.preTranslateY, t.preTranslateZ);
        GlStateManager.translate(t.translateX, t.translateY, t.translateZ);
        if (isFirstPerson(transformType)) {
            GlStateManager.scale(t.firstPersonScaleX, t.firstPersonScaleY, t.firstPersonScaleZ);
        }
        GlStateManager.scale(t.scaleX, t.scaleY, t.scaleZ);
        GlStateManager.scale(t.scaleMulX, t.scaleMulY, t.scaleMulZ);
        GlStateManager.rotate(t.rotateX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(t.rotateY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(t.rotateZ, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(t.postTranslateX, t.postTranslateY, t.postTranslateZ);
        // TC4 model basis correction (see class javadoc).
        GlStateManager.rotate(t.finalRotateX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(t.finalRotateY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(t.finalRotateZ, 0.0F, 0.0F, 1.0F);
    }

    /** staff > sceptre > wand, matching existing isStaff/isSceptre logic. */
    private static String resolveKind(ItemWandCasting wand, ItemStack stack) {
        if (wand.isStaff(stack)) {
            return WandRenderCalibration.KIND_STAFF;
        }
        if (ItemWandCasting.isSceptre(stack)) {
            return WandRenderCalibration.KIND_SCEPTRE;
        }
        return WandRenderCalibration.KIND_WAND;
    }

    /**
     * Derives a left-hand pose from the matching right-hand context by mirroring about the local
     * X axis (negate translate X, flip Y/Z rotations). Only used when the calibration entry sets
     * {@code mirrorFromRightHand: true}. Defaults leave this off so left == right (current
     * behaviour).
     */
    private static WandRenderCalibration.Transform mirrorFromRightHand(String kind,
                                                                       WandRenderCalibration.Transform left,
                                                                       ItemCameraTransforms.TransformType leftType) {
        ItemCameraTransforms.TransformType rightType;
        if (leftType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            rightType = ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
        } else if (leftType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            rightType = ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
        } else {
            return left;
        }
        WandRenderCalibration.Transform right = WandRenderCalibration.get(kind, rightType);
        WandRenderCalibration.Transform m = new WandRenderCalibration.Transform();
        m.preTranslateX = right.preTranslateX; m.preTranslateY = right.preTranslateY; m.preTranslateZ = right.preTranslateZ;
        m.translateX = -right.translateX; m.translateY = right.translateY; m.translateZ = right.translateZ;
        m.firstPersonScaleX = right.firstPersonScaleX; m.firstPersonScaleY = right.firstPersonScaleY; m.firstPersonScaleZ = right.firstPersonScaleZ;
        m.scaleX = right.scaleX; m.scaleY = right.scaleY; m.scaleZ = right.scaleZ;
        m.scaleMulX = right.scaleMulX; m.scaleMulY = right.scaleMulY; m.scaleMulZ = right.scaleMulZ;
        m.rotateX = right.rotateX; m.rotateY = -right.rotateY; m.rotateZ = -right.rotateZ;
        m.postTranslateX = -right.postTranslateX; m.postTranslateY = right.postTranslateY; m.postTranslateZ = right.postTranslateZ;
        m.finalRotateX = right.finalRotateX; m.finalRotateY = right.finalRotateY; m.finalRotateZ = right.finalRotateZ;
        m.mirrorFromRightHand = true;
        return m;
    }

    // ------------------------------------------------------------------ Debug observability

    private static void maybeDebugLog(ItemWandCasting wand, ItemStack stack, String kind,
                                      ItemCameraTransforms.TransformType type, WandRenderCalibration.Transform t) {
        if (!WandRenderCalibration.isDebug()) {
            return;
        }
        String key = kind + "|" + type.name();
        if (key.equals(lastDebugKey)) {
            return; // one compact line per context change, not per frame
        }
        lastDebugKey = key;

        String rod = safeRodTag(wand, stack);
        String cap = safeCapTag(wand, stack);
        ItemFocusBasic focus = wand.getFocus(stack);
        ItemStack focusStack = wand.getFocusItem(stack);
        boolean hasFocus = focus != null;
        String focusId = hasFocus ? focusStack.getItem().getRegistryName().toString() : "-";

        LOGGER.info("[TC4F/WandRender] item=thaumcraft:itemWandCasting kind={} transform={} rod={} cap={} focus={} {}",
                kind, type.name(), rod, cap, hasFocus, focusId);
        LOGGER.info("[TC4F/WandRender]   {}", t.toString());

        WandRenderCalibration.dumpTransform(DUMP_DIR, kind, type, t, rod, cap, hasFocus,
                hasFocus ? focusId : null);
    }

    private static String safeRodTag(ItemWandCasting wand, ItemStack stack) {
        try {
            return wand.getRod(stack).getTag();
        } catch (Throwable t) {
            return "?";
        }
    }

    private static String safeCapTag(ItemWandCasting wand, ItemStack stack) {
        try {
            return wand.getCap(stack).getTag();
        } catch (Throwable t) {
            return "?";
        }
    }

    // ------------------------------------------------------------------ Use animation (unchanged)

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
