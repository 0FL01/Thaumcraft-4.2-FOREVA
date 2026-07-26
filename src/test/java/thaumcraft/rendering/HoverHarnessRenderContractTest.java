package thaumcraft.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** CI-visible source and asset guards for the TC4 hover harness renderer. */
public class HoverHarnessRenderContractTest {
    private static final String MODEL =
            "src/main/java/thaumcraft/client/renderers/models/gear/ModelHoverHarness.java";
    private static final String OBJ =
            "src/main/resources/assets/thaumcraft/textures/models/hoverharness.obj";
    private static final String TC4_OBJ =
            "thaumcraft_src/assets/thaumcraft/textures/models/hoverharness.obj";

    @Test
    public void inactiveRendererShouldRetainTheTc4BodyAndObjContract() throws IOException {
        String model = read(MODEL);

        assertTrue(model.contains("super();")
                && model.contains("new ModelRenderer(this, 16, 16)")
                && model.contains("addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.6F)"));
        assertFalse(model.contains("textureWidth = 128") || model.contains("textureHeight = 64"));
        assertTrue(model.contains("CCModel.parseObjModels(HARNESS_MODEL)")
                && model.contains("models.get(\"Cylinder001\")")
                && model.contains("model.backfacedCopy()")
                && model.contains("normal.negate()")
                && model.contains("OBJ_UV_INSET = 0.0005D"));
        assertTrue(model.contains("GlStateManager.scale(0.1F, 0.1F, 0.1F);")
                && model.contains("GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);")
                && model.contains("GlStateManager.translate(0.0F, 0.33F, -3.7F);")
                && model.contains("textures/models/hoverharness2.png")
                && model.contains("DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL"));
        assertFalse(model.contains("super.render(") || model.contains("setRotationAngles("));
        assertFalse(model.contains("backMount") || model.contains("engineCore")
                || model.contains("engineNozzle") || model.contains("pipeL") || model.contains("pipeR"));
    }

    @Test
    public void activeRendererShouldRetainTheTc4RingGateAndTransforms() throws IOException {
        String model = read(MODEL);
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");

        assertTrue(registry.contains("LIGHTNING_RING_SPRITE")
                && registry.contains("new ResourceLocation(\"thaumcraft\", \"items/lightningring\")")
                && registry.contains("registerSprite(LIGHTNING_RING_SPRITE)"));
        String activeGate = between(model, "private static boolean isHoverActive", "private static void renderLightningRings");
        assertTrue(activeGate.contains("GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)")
                && model.contains("GL11.glGetInteger(GL11.GL_MATRIX_MODE) != GL11.GL_MODELVIEW")
                && model.contains("getItemStackFromSlot(EntityEquipmentSlot.CHEST)")
                && model.contains("getByte(\"hover\") == 1"));
        assertFalse(activeGate.contains("GL11.glIsEnabled(GL11.GL_BLEND)"));
        assertTrue(model.contains("GlStateManager.translate(0.0F, 0.075F, -0.05F);")
                && model.contains("GlStateManager.translate(0.0F, 0.2F, 0.55F);")
                && model.contains("renderRing(sprite, 2.5F, 1.0F, 1.0F, 1.0F, 1.0F)")
                && model.contains("GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);")
                && model.contains("GlStateManager.translate(0.0F, 0.0F, 0.03F);")
                && model.contains("renderRing(sprite, 1.5F, 1.0F, 0.5F, 1.0F, 1.0F)")
                && model.contains("lightmap(230, 0)"));
        assertTrue(model.contains("GlStateManager.tryBlendFuncSeparate(")
                && model.contains("GL12.GL_RESCALE_NORMAL")
                && model.contains("} finally {"));
        String ring = between(model, "private static void renderRing", "private static void ringVertex");
        assertTrue(ring.contains("buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)"));
        assertFalse(ring.contains("DefaultVertexFormats.ITEM"));
        String ringVertex = between(model, "private static void ringVertex", "private void spawnLightningBolt");
        assertTrue(ringVertex.contains(".lightmap(230, 0)"));
        assertFalse(ringVertex.contains(".normal("));
    }

    @Test
    public void activeRendererShouldRetainTheTc4BoltCadenceAndTypeSixPath() throws IOException {
        String model = read(MODEL);
        String bolt = read("src/main/java/thaumcraft/client/fx/bolt/FXLightningBolt.java");

        assertTrue(model.contains("currentTime + 50L + player.world.rand.nextInt(50)")
                && model.contains("player.renderYawOffset - 90.0F - player.world.rand.nextInt(180)")
                && model.contains("-80.0F + player.world.rand.nextInt(160)")
                && model.contains("Vec3d.fromPitchYaw(pitch, yaw).scale(6.0D)")
                && model.contains("rayTraceBlocks(start, end, false, true, false)")
                && model.contains("hit.typeOfHit != RayTraceResult.Type.BLOCK"));
        assertTrue(model.contains("player.renderYawOffset + 90.0F")
                && model.contains("player.posY - 0.45F - sneakOffset")
                && model.contains("player.world.rand.nextLong(), 1, 2.0F, 3")
                && model.contains("bolt.defaultFractal()")
                && model.contains("bolt.setType(6)")
                && model.contains("bolt.setWidth(0.015F)")
                && model.contains("bolt.finalizeBolt()"));
        assertTrue(bolt.contains("if (this.type == 6)")
                && bolt.contains("renderTc4Type6Bolt(entityIn, partialTicks)")
                && bolt.contains("minecraft.gameSettings.fancyGraphics ? 100 : 50")
                && bolt.contains("GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)")
                && bolt.contains("(1.0F - boltAge) * 0.4F")
                && bolt.contains("1.0F - boltAge * 0.5F")
                && bolt.contains("segment.prevdiff")
                && bolt.contains("segment.nextdiff")
                && bolt.contains("segment.next == null")
                && bolt.contains("segment.prev == null")
                && bolt.contains("color(0.75F, 1.0F, 1.0F, alpha)"));
        String typeSix = between(bolt, "private void renderTc4Type6Bolt", "private void renderTc4Type6Pass");
        assertFalse(typeSix.contains("tessellator.draw()") || typeSix.contains("buffer.begin("));
        assertTrue(typeSix.contains("Vec3d cameraView = camera.getLook(partialTicks)")
                && typeSix.contains("new WRVector3(cameraView.x, cameraView.y, cameraView.z)"));
        assertFalse(typeSix.contains("/ cosYaw") || typeSix.contains("cosSinPitch"));
        String typeSixPass = between(bolt, "private void renderTc4Type6Pass", "private static float relativeViewLength");
        assertTrue(typeSixPass.contains("buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP)")
                && typeSixPass.contains("tessellator.draw()"));
    }

    @Test
    public void packagedAssetsShouldRemainTheExactTc4Files() throws IOException {
        assertArrayEquals(Files.readAllBytes(Paths.get(TC4_OBJ)), Files.readAllBytes(Paths.get(OBJ)));
        assertArrayEquals(Files.readAllBytes(Paths.get(
                        "thaumcraft_src/assets/thaumcraft/textures/models/hoverharness.png")),
                Files.readAllBytes(Paths.get(
                        "src/main/resources/assets/thaumcraft/textures/models/hoverharness.png")));
        assertArrayEquals(Files.readAllBytes(Paths.get(
                        "thaumcraft_src/assets/thaumcraft/textures/models/hoverharness2.png")),
                Files.readAllBytes(Paths.get(
                        "src/main/resources/assets/thaumcraft/textures/models/hoverharness2.png")));

        String ring = "src/main/resources/assets/thaumcraft/textures/items/lightningring.png";
        String tc4Ring = "thaumcraft_src/assets/thaumcraft/textures/items/lightningring.png";
        String ringMeta = ring + ".mcmeta";
        String tc4RingMeta = tc4Ring + ".mcmeta";
        assertArrayEquals(Files.readAllBytes(Paths.get(tc4Ring)), Files.readAllBytes(Paths.get(ring)));
        assertArrayEquals(Files.readAllBytes(Paths.get(tc4RingMeta)), Files.readAllBytes(Paths.get(ringMeta)));
        BufferedImage image = ImageIO.read(Paths.get(ring).toFile());
        assertEquals(256, image.getWidth());
        assertEquals(4096, image.getHeight());
    }

    @Test
    public void objShouldRetainItsSingleAuthoredTriangleGroup() throws IOException {
        String obj = read(OBJ);
        assertEquals(68, countLines(obj, "v "));
        assertEquals(42, countLines(obj, "vt "));
        assertEquals(35, countLines(obj, "vn "));
        assertEquals(124, countLines(obj, "f "));
        assertEquals(1, countLines(obj, "g Cylinder001"));
    }

    private static int countLines(String source, String prefix) {
        int count = 0;
        for (String line : source.split("\\R")) {
            if (line.startsWith(prefix)) {
                ++count;
            }
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static String between(String source, String start, String end) {
        int startIndex = source.indexOf(start);
        int endIndex = source.indexOf(end, startIndex);
        assertTrue("Missing source section: " + start + " -> " + end, startIndex >= 0 && endIndex > startIndex);
        return source.substring(startIndex, endIndex);
    }
}
