package thaumcraft.client.fx.beams;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.client.renderers.item.WandPoseMath;
import thaumcraft.client.renderers.item.WandUsePose;
import thaumcraft.client.renderers.item.WandUsePoseSampler;
import thaumcraft.common.items.wands.ItemWandCasting;

/** Resolves the animated wand tip from player-local coordinates into world space. */
@SideOnly(Side.CLIENT)
public final class WandEffectOrigin {

    private static final double VERTICAL_EPSILON = 1.0E-6D;
    private static final Vec3d WORLD_UP = new Vec3d(0.0D, 1.0D, 0.0D);

    // The wand values retain the TC4 beam baseline. Staff and sceptre have independent entries so
    // their longer/wider ModelWand geometry can be calibrated without changing ordinary wands.
    private static final TipCalibration WAND = new TipCalibration(0.066D, -0.06D, 0.30D, 0.50D);
    private static final TipCalibration STAFF = new TipCalibration(0.075D, 0.02D, 0.38D, 0.50D);
    private static final TipCalibration SCEPTRE = new TipCalibration(0.070D, -0.04D, 0.34D, 0.50D);

    private WandEffectOrigin() {
    }

    public static Vec3d resolve(EntityPlayer player, float partialTicks, double sourceYOffset) {
        Basis basis = basis(player, partialTicks);
        return resolve(player, partialTicks, sourceYOffset, basis);
    }

    static DebugFrame debugFrame(EntityPlayer player, float partialTicks, double sourceYOffset) {
        Basis basis = basis(player, partialTicks);
        return new DebugFrame(resolve(player, partialTicks, sourceYOffset, basis),
                basis.forward, basis.right, basis.up);
    }

    private static Vec3d resolve(EntityPlayer player, float partialTicks, double sourceYOffset,
                                  Basis basis) {
        Vec3d forward = basis.forward;
        Vec3d right = basis.right;
        Vec3d up = basis.up;

        double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks
                + player.getEyeHeight() + sourceYOffset;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        Vec3d eye = new Vec3d(x, y, z);

        WandContext context = resolveContext(player);
        TipCalibration calibration = calibrationFor(context.stack);
        double handSign = context.side == EnumHandSide.LEFT ? -1.0D : 1.0D;
        Vec3d localTip = new Vec3d(
                handSign * calibration.sideOffset,
                calibration.upOffset,
                calibration.forwardOffset);

        if (context.active && context.stack.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) context.stack.getItem();
            ItemFocusBasic focus = wand.getFocus(context.stack);
            ItemFocusBasic.WandFocusAnimation animation = focus == null
                    ? null : focus.getAnimation(wand.getFocusItem(context.stack));
            float elapsedTicks = player.getItemInUseMaxCount() + partialTicks;
            WandUsePose pose = WandUsePoseSampler.sample(
                    elapsedTicks, animation, context.side, isFirstPerson(player));
            // The visible tip is above the corrected-basis pivot; TC4's raw Y=+1 pivot becomes
            // Y=-1 after the model's Rx(180) correction.
            Vec3d pivot = new Vec3d(
                    handSign * calibration.sideOffset,
                    calibration.upOffset - calibration.pivotHeight,
                    calibration.forwardOffset);
            localTip = WandPoseMath.transformPoint(localTip, pivot, pose);
        }

        return eye
                .add(right.scale(localTip.x))
                .add(up.scale(localTip.y))
                .add(forward.scale(localTip.z));
    }

    private static Basis basis(EntityPlayer player, float partialTicks) {
        Vec3d forward = player.getLook(partialTicks).normalize();
        Vec3d right = forward.crossProduct(WORLD_UP);
        if (right.lengthSquared() < VERTICAL_EPSILON) {
            float yaw = player.prevRotationYaw
                    + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
            float yawRadians = yaw * 0.017453292F;
            right = new Vec3d(-MathHelper.cos(yawRadians), 0.0D, -MathHelper.sin(yawRadians));
        }
        right = right.normalize();
        Vec3d up = right.crossProduct(forward).normalize();
        return new Basis(forward, right, up);
    }

    /** Keeps the original remote third-person height without adding it on top of eye height. */
    public static double sourceYOffset(EntityPlayer player) {
        if (isFirstPerson(player)) {
            return 0.0D;
        }
        return player.height / 2.0D + 0.25D - player.getEyeHeight();
    }

    private static WandContext resolveContext(EntityPlayer player) {
        EnumHand hand = player.isHandActive() ? player.getActiveHand() : null;
        ItemStack stack = hand == null ? ItemStack.EMPTY : player.getHeldItem(hand);
        boolean active = hand != null && stack.getItem() instanceof ItemWandCasting;

        if (!(stack.getItem() instanceof ItemWandCasting)) {
            ItemStack main = player.getHeldItem(EnumHand.MAIN_HAND);
            ItemStack off = player.getHeldItem(EnumHand.OFF_HAND);
            if (main.getItem() instanceof ItemWandCasting) {
                hand = EnumHand.MAIN_HAND;
                stack = main;
            } else if (off.getItem() instanceof ItemWandCasting) {
                hand = EnumHand.OFF_HAND;
                stack = off;
            }
        }

        EnumHandSide side = player.getPrimaryHand();
        if (hand == EnumHand.OFF_HAND) {
            side = side == EnumHandSide.RIGHT ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
        }
        return new WandContext(stack, side, active);
    }

    private static boolean isFirstPerson(EntityPlayer player) {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft.player != null
                && minecraft.player.getEntityId() == player.getEntityId()
                && minecraft.gameSettings.thirdPersonView == 0;
    }

    private static TipCalibration calibrationFor(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemWandCasting)) {
            return WAND;
        }
        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        if (wand.isStaff(stack)) {
            return STAFF;
        }
        if (ItemWandCasting.isSceptre(stack)) {
            return SCEPTRE;
        }
        return WAND;
    }

    private static final class WandContext {
        private final ItemStack stack;
        private final EnumHandSide side;
        private final boolean active;

        private WandContext(ItemStack stack, EnumHandSide side, boolean active) {
            this.stack = stack;
            this.side = side;
            this.active = active;
        }
    }

    private static final class TipCalibration {
        private final double sideOffset;
        private final double upOffset;
        private final double forwardOffset;
        private final double pivotHeight;

        private TipCalibration(double sideOffset, double upOffset, double forwardOffset,
                               double pivotHeight) {
            this.sideOffset = sideOffset;
            this.upOffset = upOffset;
            this.forwardOffset = forwardOffset;
            this.pivotHeight = pivotHeight;
        }
    }

    static final class DebugFrame {
        final Vec3d origin;
        final Vec3d forward;
        final Vec3d right;
        final Vec3d up;

        private DebugFrame(Vec3d origin, Vec3d forward, Vec3d right, Vec3d up) {
            this.origin = origin;
            this.forward = forward;
            this.right = right;
            this.up = up;
        }
    }

    private static final class Basis {
        private final Vec3d forward;
        private final Vec3d right;
        private final Vec3d up;

        private Basis(Vec3d forward, Vec3d right, Vec3d up) {
            this.forward = forward;
            this.right = right;
            this.up = up;
        }
    }
}
