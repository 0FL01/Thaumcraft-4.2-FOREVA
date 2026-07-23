package thaumcraft.client;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeconstructionTableVisualParityStaticGuardTest {

    @Test
    public void guiUsesTintedAspectAndOriginalTooltipContract() throws IOException {
        String gui = read("src/main/java/thaumcraft/client/gui/GuiDeconstructionTable.java");

        assertTrue(gui.contains("new ResourceLocation(\"thaumcraft\", \"textures/gui/gui_decontable.png\")"));
        assertTrue(gui.contains("this.guiLeft + 93, this.guiTop + 15 + 46 - progress"));
        assertTrue(gui.contains("176, 46 - progress, 9, progress"));
        assertTrue(gui.contains("UtilsFX.drawTag(this.guiLeft + 64, this.guiTop + 48, this.table.aspect,"));
        assertTrue(gui.contains("0.0F, 0, this.zLevel, 771, 1.0F, false);"));
        assertTrue(gui.contains("GlStateManager.alphaFunc(516, 0.1F);"));
        assertTrue(gui.contains("UtilsFX.drawCustomTooltip(this, this.itemRender, this.fontRenderer,"));
        assertTrue(gui.contains("mouseX, mouseY - 8, 11);"));
        assertFalse(gui.contains("drawModalRectWithCustomSizedTexture"));
        assertFalse(gui.contains("drawHoveringText("));
    }

    @Test
    public void tesrUsesOriginalOverlayGeometryAndFramedItemAnimation() throws IOException {
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileDeconstructionTableRenderer.java");
        String thaumometerRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java");
        String scannerPath = section(renderer, "private void renderTableThaumometer", "private void renderDeconstructionInput");
        String inputPath = section(renderer, "private void renderDeconstructionInput", "private float getAnimationTicks");

        assertTrue(renderer.contains("return mc.player.ticksExisted + partialTicks;"));
        assertTrue(renderer.contains("GlStateManager.translate(x + 0.5D, y + 1.15D, z + 0.5D);"));
        assertTrue(renderer.contains("GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);"));
        assertTrue(renderer.contains("GlStateManager.DestFactor.ONE"));
        assertTrue(renderer.contains("GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);"));
        assertTrue(renderer.contains("MathHelper.sin(ticks / 14.0F) * 0.2F + 0.2F"));
        assertTrue(inputPath.contains("MathHelper.sin(hoverStart) * 0.1F + 0.1F"));
        assertTrue(inputPath.contains("renderStack.setCount(1);"));
        assertTrue(inputPath.contains("ItemCameraTransforms.TransformType.GROUND, false"));
        assertTrue(renderer.contains("GlStateManager.translate(x + 0.5D, y + 0.92D, z + 0.5D);"));
        assertTrue(renderer.contains("GlStateManager.scale(0.8F, 0.8F, 0.8F);"));
        assertTrue(renderer.contains("private static final float FRAME_SCALE = 1.25F;"));
        assertTrue(renderer.contains("private static final float FRAME_Y_OFFSET = 0.05F;"));
        assertTrue(renderer.contains("private static final float ENTITY_MODEL_SCALE = 0.5F;"));
        assertTrue(renderer.contains("private static final float THAUMOMETER_ENTITY_SCALE = 0.5F;"));
        assertTrue(renderer.contains("private static final float ENTITY_ITEM_BASE_BOB = 0.1F;"));
        assertTrue(scannerPath.contains("GlStateManager.translate(0.0F, ENTITY_ITEM_BASE_BOB, 0.0F);"));
        assertTrue(scannerPath.contains("GlStateManager.scale(FRAME_SCALE, FRAME_SCALE, FRAME_SCALE);"));
        assertTrue(scannerPath.contains("GlStateManager.translate(0.0F, FRAME_Y_OFFSET, 0.0F);"));
        assertTrue(scannerPath.contains("GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);"));
        assertTrue(scannerPath.contains("GlStateManager.scale(ENTITY_MODEL_SCALE, ENTITY_MODEL_SCALE, ENTITY_MODEL_SCALE);"));
        assertTrue(scannerPath.contains("GlStateManager.scale(THAUMOMETER_ENTITY_SCALE, THAUMOMETER_ENTITY_SCALE,"));
        assertTrue(scannerPath.contains("((ItemThaumometerRenderer) renderer).renderTableDisplay(stack);"));
        assertTrue(thaumometerRenderer.contains("renderScanner(stack, ItemCameraTransforms.TransformType.NONE, false);"));
        assertFalse(scannerPath.contains("TransformType.GROUND"));
        assertFalse(scannerPath.contains("ForgeHooksClient.handleCameraTransforms"));
        assertTrue(renderer.contains("GlStateManager.translate(x + 0.5D, y + 1.081D, z + 0.5D);"));
        assertTrue(renderer.contains("GlStateManager.scale(0.024F, 0.024F, 0.024F);"));
        assertTrue(renderer.contains("UtilsFX.drawTag(-8, -8, tile.aspect, 0.0F, 0, 0.0D, 1, 0.8F, false);"));
        assertTrue(renderer.contains("GlStateManager.alphaFunc(516, 0.1F);"));
        assertFalse(renderer.contains("GlStateManager.scale(0.65F"));
        assertFalse(renderer.contains("Math.sin(ticks / 14.0F)"));
        assertFalse(renderer.contains("drawTexturedQuad(0.5F"));
    }

    @Test
    public void tableThaumometerTransformMatchesTc4Bounds() {
        float bottom = 0.92F + 0.8F * (0.1F + 1.25F * 0.05F);
        float effectiveScale = 0.8F * 1.25F * 0.5F * 0.5F;
        float top = bottom + 0.2F * effectiveScale;
        float width = 2.8F * effectiveScale;

        assertEquals(1.05F, bottom, 0.0001F);
        assertEquals(1.10F, top, 0.0001F);
        assertEquals(0.70F, width, 0.0001F);
    }

    @Test
    public void metaFourteenUsesTableTesrItemRoute() throws IOException {
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String model = read("src/main/resources/assets/thaumcraft/models/item/blocktable_tesr.json");
        String renderer = read("src/main/java/thaumcraft/client/renderers/item/ItemTableRenderer.java");

        assertTrue(proxy.contains("registerBuiltinItemModel(tableItem, 14, \"blocktable_tesr\");"));
        assertFalse(proxy.contains("registerBuiltinItemModel(tableItem, 14, \"blocktable_14_inventory\");"));
        assertTrue(model.contains("\"parent\": \"builtin/entity\""));
        assertTrue(renderer.contains("meta == 14"));
        assertTrue(renderer.contains("deconstructionRenderer.render(new TileDeconstructionTable()"));
    }

    @Test
    public void originalVisualAssetsRemainByteExact() throws IOException, NoSuchAlgorithmException {
        assertImage(
                "src/main/resources/assets/thaumcraft/textures/gui/gui_decontable.png",
                256,
                256,
                "6c26e086c30eab750571a42933b95d6a83adf315161df8f0388ad61594224d40");
        assertImage(
                "src/main/resources/assets/thaumcraft/textures/models/decontable.png",
                128,
                64,
                "0a916f79cf770d0502ad42a13200babe8b37a236c6c42c63c6056b1085962305");
        assertEquals(
                "56b008ca784d5a6704f12177ca57ff977118c90b775db22e665e7ad1584e1259",
                sha256(Paths.get("src/main/resources/assets/thaumcraft/textures/models/scanner.obj")));
    }

    private static void assertImage(String path, int width, int height, String sha256)
            throws IOException, NoSuchAlgorithmException {
        Path imagePath = Paths.get(path);
        BufferedImage image = ImageIO.read(imagePath.toFile());
        assertEquals(width, image.getWidth());
        assertEquals(height, image.getHeight());
        assertEquals(sha256, sha256(imagePath));
    }

    private static String sha256(Path path) throws IOException, NoSuchAlgorithmException {
        return hex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path)));
    }

    private static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format("%02x", value & 0xff));
        }
        return result.toString();
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static String section(String source, String start, String end) {
        int startIndex = source.indexOf(start);
        int endIndex = source.indexOf(end, startIndex + start.length());
        assertTrue("Missing source section start: " + start, startIndex >= 0);
        assertTrue("Missing source section end: " + end, endIndex > startIndex);
        return source.substring(startIndex, endIndex);
    }
}
