package thaumcraft.rendering;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.minecraft.util.math.Vec3d;
import org.junit.Test;
import org.lwjgl.BufferUtils;
import thaumcraft.client.renderers.item.FirstPersonWandTipOrigin;
import thaumcraft.client.renderers.models.gear.ModelWand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** CI-visible numerical and wiring contract for local first-person node drain attachment. */
public class WandDrainFirstPersonProjectionTest {
    @Test
    public void primaryCapAnchorShouldFollowActualKindTransforms() {
        assertPoint(ModelWand.getPrimaryCapTip(false, false), -0.0625D);
        assertPoint(ModelWand.getPrimaryCapTip(false, true), -0.08125D);
        assertPoint(ModelWand.getPrimaryCapTip(true, false), 0.13125D);
        assertPoint(ModelWand.getPrimaryCapTip(true, true), 0.110625D);
    }

    @Test
    public void projectedTipShouldReprojectToSameViewportPositionInWorldSpace() throws Exception {
        Method project = privateMethod("projectPoint", float.class, float.class, float.class,
                FloatBuffer.class, FloatBuffer.class, IntBuffer.class, float[].class);
        Method unproject = privateMethod("unprojectPoint", float.class, float.class, float.class,
                FloatBuffer.class, FloatBuffer.class, IntBuffer.class, float[].class);

        FloatBuffer handModelview = matrix(
                0.9396926F, 0.3420201F, 0.0F, 0.0F,
                -0.3420201F, 0.9396926F, 0.0F, 0.0F,
                0.0F, 0.0F, 1.0F, 0.0F,
                0.18F, -0.12F, -0.8F, 1.0F);
        FloatBuffer handProjection = perspective(70.0F, 16.0F / 9.0F, 0.05F, 512.0F);
        IntBuffer handViewport = viewport(31, 47, 1600, 900);
        float[] handWindow = new float[3];
        assertTrue(invoke(project, 0.0F, -0.0625F, 0.0F,
                handModelview, handProjection, handViewport, handWindow));

        float u = (handWindow[0] - 31.0F) / 1600.0F;
        float v = (handWindow[1] - 47.0F) / 900.0F;

        FloatBuffer worldModelview = matrix(
                0.9659258F, 0.0F, -0.2588190F, 0.0F,
                0.0F, 1.0F, 0.0F, 0.0F,
                0.2588190F, 0.0F, 0.9659258F, 0.0F,
                0.0F, -1.62F, 0.0F, 1.0F);
        FloatBuffer worldProjection = perspective(105.0F, 21.0F / 9.0F, 0.05F, 1024.0F);
        IntBuffer worldViewport = viewport(5, 9, 1920, 823);
        float worldWindowX = 5.0F + u * 1920.0F;
        float worldWindowY = 9.0F + v * 823.0F;
        float[] worldPoint = new float[3];
        assertTrue(invoke(unproject, worldWindowX, worldWindowY, handWindow[2],
                worldModelview, worldProjection, worldViewport, worldPoint));

        float[] reprojected = new float[3];
        assertTrue(invoke(project, worldPoint[0], worldPoint[1], worldPoint[2],
                worldModelview, worldProjection, worldViewport, reprojected));
        assertEquals(worldWindowX, reprojected[0], 0.001F);
        assertEquals(worldWindowY, reprojected[1], 0.001F);
        assertEquals(handWindow[2], reprojected[2], 0.00001F);

        assertFalse(invoke(project, Float.NaN, 0.0F, 0.0F,
                handModelview, handProjection, handViewport, new float[3]));
        assertFalse(invoke(unproject, worldWindowX, worldWindowY, 0.0F,
                worldModelview, worldProjection, worldViewport, new float[3]));
        assertFalse(invoke(unproject, worldWindowX, worldWindowY, handWindow[2],
                worldModelview, matrix(new float[16]), worldViewport, new float[3]));
    }

    @Test
    public void rendererWiringShouldCaptureConsumeAndPinOnlyTheLocalFirstPersonBeam() throws Exception {
        String item = read("src/main/java/thaumcraft/client/renderers/item/ItemWandRenderer.java");
        String origin = read("src/main/java/thaumcraft/client/renderers/item/FirstPersonWandTipOrigin.java");
        String model = read("src/main/java/thaumcraft/client/renderers/models/gear/ModelWand.java");
        String tile = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeRenderer.java");
        String events = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");

        assertInOrder(item,
                "applyModelBasisCorrection(t);",
                "FirstPersonWandTipOrigin.capture(",
                "ModelWand.getPrimaryCapTip(",
                "model.render(stack, partialTicks, player);");
        assertTrue(model.contains("rootY + CAP_FACE_Y * capScaleY * sceptreScale"));
        assertInOrder(events,
                "public void renderLast(RenderWorldLastEvent event)",
                "FirstPersonWandTipOrigin.nextWorldFrame();",
                "Minecraft mc = Minecraft.getMinecraft();");

        assertTrue(origin.contains("generation + 1L")
                && origin.contains("current.generation != generation")
                && origin.contains("ForgeHooks.canContinueUsing(current.stack, activeStack)")
                && origin.contains("baseX + POINT[0]")
                && origin.contains("baseY + POINT[1]")
                && origin.contains("baseZ + POINT[2]"));
        assertTrue(tile.contains("boolean localFirstPerson = player == mc.player")
                && tile.contains("FirstPersonWandTipOrigin.resolveAndRequest(")
                && tile.contains("if (source == null) {")
                && tile.contains("source = WandEffectOrigin.resolve("));
        assertInOrder(tile,
                "UtilsFX.drawFloatyLine(",
                "if (reveal >= 1.0F && lineLength > 0.0F)",
                "drawDrainContact(deltaX, deltaY, deltaZ, color);");
        assertTrue(tile.contains("GlStateManager.disableTexture2D();")
                && tile.contains("DefaultVertexFormats.POSITION_COLOR")
                && tile.contains("GlStateManager.depthMask(false);"));
    }

    private static void assertPoint(Vec3d point, double expectedY) {
        assertEquals(0.0D, point.x, 0.0D);
        assertEquals(expectedY, point.y, 0.0000001D);
        assertEquals(0.0D, point.z, 0.0D);
    }

    private static Method privateMethod(String name, Class<?>... parameterTypes) throws Exception {
        Method method = FirstPersonWandTipOrigin.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    private static boolean invoke(Method method, Object... arguments) throws Exception {
        return (Boolean) method.invoke(null, arguments);
    }

    private static FloatBuffer perspective(float fovDegrees, float aspect, float near, float far) {
        float f = 1.0F / (float) Math.tan(Math.toRadians(fovDegrees) * 0.5D);
        return matrix(
                f / aspect, 0.0F, 0.0F, 0.0F,
                0.0F, f, 0.0F, 0.0F,
                0.0F, 0.0F, (far + near) / (near - far), -1.0F,
                0.0F, 0.0F, (2.0F * far * near) / (near - far), 0.0F);
    }

    private static FloatBuffer matrix(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }

    private static IntBuffer viewport(int x, int y, int width, int height) {
        IntBuffer buffer = BufferUtils.createIntBuffer(4);
        buffer.put(x).put(y).put(width).put(height);
        buffer.flip();
        return buffer;
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static void assertInOrder(String source, String... markers) {
        int index = -1;
        for (String marker : markers) {
            int next = source.indexOf(marker, index + 1);
            assertTrue("Missing or out-of-order source marker: " + marker, next > index);
            index = next;
        }
    }
}
