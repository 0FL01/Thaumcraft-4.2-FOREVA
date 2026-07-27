package thaumcraft.client.renderers.item;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import thaumcraft.common.items.wands.ItemWandCasting;

/** Bridges the previous first-person hand pass to the next world-rendered wand effect. */
@SideOnly(Side.CLIENT)
public final class FirstPersonWandTipOrigin {
    private static final double MIN_CAMERA_DISTANCE = 0.05D;
    private static final double MAX_CAMERA_DISTANCE = 2.0D;

    private static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);
    private static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer(16);
    private static final FloatBuffer RESULT = BufferUtils.createFloatBuffer(3);
    private static final float[] POINT = new float[3];

    private static long generation;
    private static Request request;
    private static Sample sample;

    private FirstPersonWandTipOrigin() {
    }

    /** Called after TESRs and before the hand pass. */
    public static void nextWorldFrame() {
        ++generation;
        if (request != null && request.generation < generation) {
            request = null;
        }
        if (sample != null && sample.generation < generation) {
            sample = null;
        }
    }

    /**
     * Requests a capture from the upcoming hand pass and resolves the immediately preceding one.
     * The returned point is absolute world space. A valid sample remains available to every
     * matching effect rendered in the current world pass.
     */
    public static Vec3d resolveAndRequest(EntityPlayer player, EnumHand hand, ItemStack activeStack,
                                          float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!isLocalFirstPerson(mc, player) || hand == null || activeStack.isEmpty()
                || !(activeStack.getItem() instanceof ItemWandCasting)
                || !player.isHandActive() || player.getItemInUseCount() <= 0
                || player.getActiveHand() != hand) {
            request = null;
            sample = null;
            return null;
        }

        request = new Request(player.world, player, hand, physicalSide(player, hand), activeStack.copy(), generation + 1L);
        Sample current = sample;
        if (current == null || current.generation != generation
                || current.world != player.world || current.player != player || current.hand != hand
                || current.side != physicalSide(player, hand)
                || !ForgeHooks.canContinueUsing(current.stack, activeStack)) {
            return null;
        }

        readMatrices();
        int viewportX = VIEWPORT.get(0);
        int viewportY = VIEWPORT.get(1);
        int viewportWidth = VIEWPORT.get(2);
        int viewportHeight = VIEWPORT.get(3);
        if (viewportX != current.viewportX || viewportY != current.viewportY
                || viewportWidth != current.viewportWidth || viewportHeight != current.viewportHeight) {
            return null;
        }

        float windowX = viewportX + current.u * viewportWidth;
        float windowY = viewportY + current.v * viewportHeight;
        if (!unprojectPoint(windowX, windowY, current.depth, MODELVIEW, PROJECTION, VIEWPORT, POINT)) {
            return null;
        }

        Entity view = mc.getRenderViewEntity();
        if (view == null) {
            return null;
        }
        double baseX = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
        double baseY = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
        double baseZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
        Vec3d source = new Vec3d(baseX + POINT[0], baseY + POINT[1], baseZ + POINT[2]);
        Vec3d eye = new Vec3d(baseX, baseY + view.getEyeHeight(), baseZ);
        double cameraDistance = source.distanceTo(eye);
        if (!isFinite(source.x) || !isFinite(source.y) || !isFinite(source.z)
                || cameraDistance <= MIN_CAMERA_DISTANCE || cameraDistance > MAX_CAMERA_DISTANCE) {
            return null;
        }
        return source;
    }

    /** Captures the visible cap under the live first-person item matrices. */
    public static void capture(EntityPlayer player, ItemStack renderedStack,
                               ItemCameraTransforms.TransformType transformType, Vec3d capPoint) {
        Request current = request;
        if (current == null || current.generation != generation || capPoint == null
                || current.world != player.world || current.player != player
                || player.getActiveHand() != current.hand || player.getItemInUseCount() <= 0
                || !isFirstPersonTransform(transformType)
                || sideForTransform(transformType) != current.side
                || !ForgeHooks.canContinueUsing(current.stack, renderedStack)) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (!isLocalFirstPerson(mc, player)) {
            return;
        }

        readMatrices();
        if (!projectPoint((float) capPoint.x, (float) capPoint.y, (float) capPoint.z,
                MODELVIEW, PROJECTION, VIEWPORT, POINT)) {
            request = null;
            return;
        }

        int viewportX = VIEWPORT.get(0);
        int viewportY = VIEWPORT.get(1);
        int viewportWidth = VIEWPORT.get(2);
        int viewportHeight = VIEWPORT.get(3);
        float u = (POINT[0] - viewportX) / viewportWidth;
        float v = (POINT[1] - viewportY) / viewportHeight;
        float depth = POINT[2];
        if (u < 0.0F || u > 1.0F || v < 0.0F || v > 1.0F || depth <= 0.0F || depth >= 1.0F) {
            request = null;
            return;
        }

        sample = new Sample(player.world, player, current.hand, current.side, renderedStack.copy(), generation,
                viewportX, viewportY, viewportWidth, viewportHeight, u, v, depth);
        request = null;
    }

    private static boolean isLocalFirstPerson(Minecraft mc, EntityPlayer player) {
        return player != null && player == mc.player && player.world == mc.world
                && mc.getRenderViewEntity() == player && mc.gameSettings.thirdPersonView == 0
                && !mc.gameSettings.anaglyph;
    }

    private static EnumHandSide physicalSide(EntityPlayer player, EnumHand hand) {
        return hand == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
    }

    private static EnumHandSide sideForTransform(ItemCameraTransforms.TransformType transformType) {
        return transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
    }

    private static boolean isFirstPersonTransform(ItemCameraTransforms.TransformType transformType) {
        return transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
    }

    private static void readMatrices() {
        MODELVIEW.clear();
        PROJECTION.clear();
        VIEWPORT.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);
        MODELVIEW.rewind();
        PROJECTION.rewind();
        VIEWPORT.rewind();
    }

    private static boolean projectPoint(float x, float y, float z,
                                        FloatBuffer modelview, FloatBuffer projection,
                                        IntBuffer viewport, float[] output) {
        if (!validMatrix(modelview) || !validMatrix(projection) || !validViewport(viewport)
                || output == null || output.length < 3
                || !isFinite(x) || !isFinite(y) || !isFinite(z)) {
            return false;
        }
        RESULT.clear();
        modelview.rewind();
        projection.rewind();
        viewport.rewind();
        if (!GLU.gluProject(x, y, z, modelview, projection, viewport, RESULT)) {
            return false;
        }
        output[0] = RESULT.get(0);
        output[1] = RESULT.get(1);
        output[2] = RESULT.get(2);
        return isFinite(output[0]) && isFinite(output[1]) && isFinite(output[2]);
    }

    private static boolean unprojectPoint(float x, float y, float z,
                                          FloatBuffer modelview, FloatBuffer projection,
                                          IntBuffer viewport, float[] output) {
        if (!validMatrix(modelview) || !validMatrix(projection) || !validViewport(viewport)
                || output == null || output.length < 3
                || !isFinite(x) || !isFinite(y) || !isFinite(z) || z <= 0.0F || z >= 1.0F) {
            return false;
        }
        RESULT.clear();
        modelview.rewind();
        projection.rewind();
        viewport.rewind();
        if (!GLU.gluUnProject(x, y, z, modelview, projection, viewport, RESULT)) {
            return false;
        }
        output[0] = RESULT.get(0);
        output[1] = RESULT.get(1);
        output[2] = RESULT.get(2);
        return isFinite(output[0]) && isFinite(output[1]) && isFinite(output[2]);
    }

    private static boolean validViewport(IntBuffer viewport) {
        return viewport != null && viewport.capacity() >= 4 && viewport.get(2) > 0 && viewport.get(3) > 0;
    }

    private static boolean validMatrix(FloatBuffer matrix) {
        return matrix != null && matrix.capacity() >= 16;
    }

    private static boolean isFinite(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value);
    }

    private static boolean isFinite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    private static final class Request {
        private final World world;
        private final EntityPlayer player;
        private final EnumHand hand;
        private final EnumHandSide side;
        private final ItemStack stack;
        private final long generation;

        private Request(World world, EntityPlayer player, EnumHand hand, EnumHandSide side,
                        ItemStack stack, long generation) {
            this.world = world;
            this.player = player;
            this.hand = hand;
            this.side = side;
            this.stack = stack;
            this.generation = generation;
        }
    }

    private static final class Sample {
        private final World world;
        private final EntityPlayer player;
        private final EnumHand hand;
        private final EnumHandSide side;
        private final ItemStack stack;
        private final long generation;
        private final int viewportX;
        private final int viewportY;
        private final int viewportWidth;
        private final int viewportHeight;
        private final float u;
        private final float v;
        private final float depth;

        private Sample(World world, EntityPlayer player, EnumHand hand, EnumHandSide side, ItemStack stack,
                       long generation, int viewportX, int viewportY, int viewportWidth, int viewportHeight,
                       float u, float v, float depth) {
            this.world = world;
            this.player = player;
            this.hand = hand;
            this.side = side;
            this.stack = stack;
            this.generation = generation;
            this.viewportX = viewportX;
            this.viewportY = viewportY;
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.u = u;
            this.v = v;
            this.depth = depth;
        }
    }
}
