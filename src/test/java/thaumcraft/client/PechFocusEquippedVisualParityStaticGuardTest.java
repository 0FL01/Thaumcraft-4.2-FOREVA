package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class PechFocusEquippedVisualParityStaticGuardTest {

    @Test
    public void pechFocusShouldExposeReferenceDepthLayerForEquippedWandRender() throws IOException {
        String focusPech = read("src/main/java/thaumcraft/common/items/wands/foci/FocusPech.java");
        String clientModelRegistry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String modelWand = read("src/main/java/thaumcraft/client/renderers/models/gear/ModelWand.java");

        assertTrue("FocusPech must expose the original animated depth icon used by ModelWand instead of rendering as a flat green stub cube",
                focusPech.contains("getFocusDepthLayerIcon")
                        && focusPech.contains("thaumcraft:items/focus_pech_depth")
                        && focusPech.contains("getTextureMapBlocks().getAtlasSprite(DEPTH_SPRITE)"));

        assertTrue("The Pech depth icon is not referenced by the generated item model, so it must be stitched explicitly for equipped wand rendering",
                clientModelRegistry.contains("TextureStitchEvent.Pre")
                        && clientModelRegistry.contains("items/focus_pech_depth")
                        && clientModelRegistry.contains("event.getMap().registerSprite(FOCUS_PECH_DEPTH_SPRITE)"));

        assertTrue("ModelWand must keep the TC4 focus render path: depth cube first, then translucent focus-colour shell",
                modelWand.contains("focusItem.getFocusDepthLayerIcon(focusStack)")
                        && modelWand.contains("renderDepthCube(depthSprite)")
                        && modelWand.contains("float alpha = depthSprite != null ? 0.6F : 0.95F"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
