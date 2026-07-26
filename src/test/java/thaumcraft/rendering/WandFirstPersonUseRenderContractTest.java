package thaumcraft.rendering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** CI-visible guard for the TC4 active first-person wand transform. */
public class WandFirstPersonUseRenderContractTest {
    private static final String HANDLER =
            "src/main/java/thaumcraft/client/lib/RenderEventHandler.java";

    @Test
    public void activeWandUseShouldReplaceOnlyTheMatchingVanillaBowHand() throws IOException {
        String source = read(HANDLER);
        String event = between(source, "public void renderActiveWandHand", "private static void renderLegacyWandFirstPerson");

        assertTrue(event.contains("player.isHandActive()")
                && event.contains("player.getItemInUseCount() <= 0")
                && event.contains("player.getActiveHand() != event.getHand()")
                && event.contains("activeStack.getItem() instanceof ItemWandCasting")
                && event.contains("renderedStack.getItem() instanceof ItemWandCasting")
                && event.contains("renderedStack.getItemUseAction() != EnumAction.BOW")
                && event.contains("activeStack != renderedStack")
                && event.contains("ForgeHooks.canContinueUsing(activeStack, renderedStack)"));
        assertTrue(event.contains("event.getHand() == EnumHand.MAIN_HAND")
                && event.contains("player.getPrimaryHand().opposite()"));
        assertInOrder(event,
                "renderLegacyWandFirstPerson(",
                "event.setCanceled(true);");
    }

    @Test
    public void legacyTransformShouldRetainOrderMirroringAndKindCompensation() throws IOException {
        String source = read(HANDLER);
        String legacy = between(source, "private static void renderLegacyWandFirstPerson", "public void renderThaumometerHands");

        assertTrue(legacy.contains("float handedness = heldSide == EnumHandSide.RIGHT ? 1.0F : -1.0F;")
                && legacy.contains("heldSide == EnumHandSide.LEFT"));
        assertFalse(legacy.contains("-swingRoot * 0.4F * handedness")
                || legacy.contains("renderItemInFirstPerson("));
        assertInOrder(legacy,
                "GlStateManager.pushMatrix();",
                "GlStateManager.translate(0.56F * handedness, -0.52F - equipProgress * 0.6F, -0.72F);",
                "GlStateManager.rotate(45.0F * handedness, 0.0F, 1.0F, 0.0F);",
                "GlStateManager.rotate(-20.0F * swingSquared * handedness, 0.0F, 1.0F, 0.0F);",
                "GlStateManager.rotate(-20.0F * swingRoot * handedness, 0.0F, 0.0F, 1.0F);",
                "GlStateManager.rotate(-80.0F * swingRoot, 1.0F, 0.0F, 0.0F);",
                "GlStateManager.scale(0.4F, 0.4F, 0.4F);",
                "GlStateManager.rotate(-18.0F * handedness, 0.0F, 0.0F, 1.0F);",
                "GlStateManager.rotate(-12.0F * handedness, 0.0F, 1.0F, 0.0F);",
                "GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);",
                "GlStateManager.translate(-0.9F * handedness, 0.2F, 0.0F);",
                "stack.getMaxItemUseDuration()",
                "player.getItemInUseCount() - partialTicks + 1.0F",
                "draw = (draw * draw + draw * 2.0F) / 3.0F;",
                "GlStateManager.translate(0.0F, 0.0F, draw * 0.1F);",
                "GlStateManager.rotate(-335.0F * handedness, 0.0F, 0.0F, 1.0F);",
                "GlStateManager.rotate(-50.0F * handedness, 0.0F, 1.0F, 0.0F);",
                "GlStateManager.translate(0.0F, 0.5F, 0.0F);",
                "GlStateManager.scale(1.0F, 1.0F, 1.0F + draw * 0.2F);",
                "GlStateManager.translate(0.0F, -0.5F, 0.0F);",
                "GlStateManager.rotate(50.0F * handedness, 0.0F, 1.0F, 0.0F);",
                "GlStateManager.rotate(335.0F * handedness, 0.0F, 0.0F, 1.0F);",
                "if (wand.isStaff(stack)) {",
                "GlStateManager.translate(0.0F, -0.5F, 0.0F);",
                "GlStateManager.scale(2.0F, 2.0F, 2.0F);",
                "mc.getItemRenderer().renderItemSide(",
                "} finally {",
                "GlStateManager.popMatrix();");

        assertEquivalent(defaultLegacyBase(false), compensatedCurrentBase(false));
        assertEquivalent(defaultLegacyBase(true), compensatedCurrentBase(true));
    }

    private static Affine defaultLegacyBase(boolean staff) {
        Affine transform = new Affine();
        transform.translate(-0.5F, -0.5F, -0.5F);
        if (staff) {
            transform.translate(0.0F, 0.5F, 0.0F);
        }
        transform.translate(0.5F, 1.5F, 0.5F);
        transform.scale(1.0F, 1.1F, 1.0F);
        return transform;
    }

    private static Affine compensatedCurrentBase(boolean staff) {
        Affine transform = new Affine();
        if (staff) {
            transform.translate(0.0F, -0.5F, 0.0F);
        }
        transform.scale(2.0F, 2.0F, 2.0F);
        transform.translate(-0.5F, -0.5F, -0.5F);
        if (staff) {
            transform.translate(0.0F, 0.5F, 0.0F);
        }
        transform.translate(0.5F, 1.0F, 0.5F);
        transform.scale(1.0F, 1.1F, 1.0F);
        transform.scale(0.5F, 0.5F, 0.5F);
        return transform;
    }

    private static void assertEquivalent(Affine expected, Affine actual) {
        assertEquals(expected.scaleX, actual.scaleX, 0.000001F);
        assertEquals(expected.scaleY, actual.scaleY, 0.000001F);
        assertEquals(expected.scaleZ, actual.scaleZ, 0.000001F);
        assertEquals(expected.translateX, actual.translateX, 0.000001F);
        assertEquals(expected.translateY, actual.translateY, 0.000001F);
        assertEquals(expected.translateZ, actual.translateZ, 0.000001F);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static String between(String source, String start, String end) {
        int startIndex = source.indexOf(start);
        int endIndex = source.indexOf(end, startIndex);
        assertTrue("Missing source section: " + start + " -> " + end,
                startIndex >= 0 && endIndex > startIndex);
        return source.substring(startIndex, endIndex);
    }

    private static void assertInOrder(String source, String... markers) {
        int index = -1;
        for (String marker : markers) {
            int next = source.indexOf(marker, index + 1);
            assertTrue("Missing or out-of-order source marker: " + marker, next > index);
            index = next;
        }
    }

    private static final class Affine {
        private float scaleX = 1.0F;
        private float scaleY = 1.0F;
        private float scaleZ = 1.0F;
        private float translateX;
        private float translateY;
        private float translateZ;

        private void translate(float x, float y, float z) {
            translateX += scaleX * x;
            translateY += scaleY * y;
            translateZ += scaleZ * z;
        }

        private void scale(float x, float y, float z) {
            scaleX *= x;
            scaleY *= y;
            scaleZ *= z;
        }
    }
}
