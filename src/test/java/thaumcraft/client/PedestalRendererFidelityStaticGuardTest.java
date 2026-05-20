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

        assertTrue("TilePedestalRenderer should keep explicit reference bob/rotate/scale item path",
                pedestal.contains("MathHelper.sin((ticks % 32767.0F) / 16.0F) * 0.05F")
                        && pedestal.contains("GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);")
                        && pedestal.contains("stack.getItem() instanceof ItemBlock ? 2.0F : 1.0F")
                        && pedestal.contains("TileRenderHelper.renderEntityItem(tile, stack, 0.0F);")
                        && pedestal.contains("if (!Minecraft.isFancyGraphicsEnabled())"));
        assertFalse("TilePedestalRenderer should not regress to generic floating helper path",
                pedestal.contains("TileRenderHelper.renderFloatingItem("));

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
