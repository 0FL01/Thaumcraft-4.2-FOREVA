package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PedestalRendererFidelityStaticGuardTest {

    @Test
    public void pedestalFamilyRenderersKeepReferenceItemDisplayContracts() throws IOException {
        String pedestal = read("src/main/java/thaumcraft/client/renderers/tile/TilePedestalRenderer.java");
        String wandPedestal = read("src/main/java/thaumcraft/client/renderers/tile/TileWandPedestalRenderer.java");
        String renderHelper = read("src/main/java/thaumcraft/client/renderers/tile/TileRenderHelper.java");

        assertTrue("TilePedestalRenderer should keep explicit reference bob/rotate/scale item path",
                pedestal.contains("MathHelper.sin((ticks % 32767.0F) / 16.0F) * 0.05F")
                        && pedestal.contains("GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);")
                        && pedestal.contains("stack.getItem() instanceof ItemBlock ? 2.0F : 1.0F")
                        && pedestal.contains("TileRenderHelper.renderEntityItem(tile, renderStack, 0.0F);")
                        && pedestal.contains("if (!Minecraft.isFancyGraphicsEnabled())"));
        assertTrue("TilePedestalRenderer should cancel only Forge 1.12's model-dependent EntityItem ground lift",
                pedestal.contains("renderStack.setCount(1);")
                        && pedestal.contains("getItemModelWithOverrides(renderStack, renderWorld, null)")
                        && pedestal.contains("getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y")
                        && pedestal.contains("float groundLift = 0.25F *")
                        && pedestal.contains("GlStateManager.translate(0.0F, -groundLift, 0.0F);"));
        int scale = pedestal.indexOf("GlStateManager.scale(scale, scale, scale);");
        int compensation = pedestal.indexOf("GlStateManager.translate(0.0F, -groundLift, 0.0F);");
        int render = pedestal.indexOf("TileRenderHelper.renderEntityItem(tile, renderStack, 0.0F);");
        assertTrue("Ground-lift cancellation must occur inside the TC4 ItemBlock scale and before both draws",
                scale >= 0 && scale < compensation && compensation < render);
        assertFalse("TilePedestalRenderer should not regress to generic floating helper path",
                pedestal.contains("TileRenderHelper.renderFloatingItem("));
        assertFalse("Arcane Pedestal height compensation must not alter shared EntityItem helper callers",
                renderHelper.contains("groundLift"));

        assertTrue("TileWandPedestalRenderer should keep explicit reference bob/rotate item path",
                wandPedestal.contains("MathHelper.sin((ticks % 32767.0F) / 16.0F) * 0.05F")
                        && wandPedestal.contains("GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);")
                        && wandPedestal.contains("TileRenderHelper.renderEntityItem(tile, stack, 0.0F);")
                        && wandPedestal.contains("TileRenderHelper.drawWispyLine("));
        assertFalse("TileWandPedestalRenderer should not use block-only item scaling",
                wandPedestal.contains("instanceof ItemBlock"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
