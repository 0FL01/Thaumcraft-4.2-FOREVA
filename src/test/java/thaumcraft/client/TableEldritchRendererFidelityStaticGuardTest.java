package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TableEldritchRendererFidelityStaticGuardTest {

    @Test
    public void researchAndEldritchRenderersKeepReferenceShapedItemDisplayContracts() throws IOException {
        String research = read("src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java");
        String cap = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchCapRenderer.java");
        String lock = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchLockRenderer.java");

        assertTrue(research.contains("textures/misc/quill.png"));
        assertTrue(research.contains("TileRenderHelper.drawTexturedQuad(0.5F"));
        assertFalse(research.contains("renderFloatingItem(new ItemStack(Items.FEATHER)"));

        assertTrue(cap.contains("textures/models/obelisk_cap_altar.png"));
        assertTrue(cap.contains("public TileEldritchCapRenderer(ResourceLocation capTexture)"));
        assertTrue(cap.contains("tile.getWorld().provider.getDimension() == Config.dimensionOuterId"));
        assertTrue(cap.contains("renderItem(eye.copy(), ItemCameraTransforms.TransformType.GROUND)"));
        assertFalse(cap.contains("TileRenderHelper.renderFloatingItem("));

        assertTrue(lock.contains("renderItem(key, ItemCameraTransforms.TransformType.GROUND)"));
        assertFalse(lock.contains("TileRenderHelper.renderFloatingItem(key"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
