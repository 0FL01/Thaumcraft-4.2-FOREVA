package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileLifterParityStaticTest {

    @Test
    public void tileLifterKeepsTc4235EntityMotionParity() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileLifter.java");

        assertTrue("Arcane Levitator must stop range on opaque blocks like TC4 4.2.3.5",
                source.contains(".isOpaqueCube()"));
        assertTrue("Arcane Levitator must lift collidable entities/items/horse-family entities",
                source.contains("e.canBeCollidedWith()")
                        && source.contains("e instanceof EntityItem")
                        && source.contains("e instanceof AbstractHorse"));
        assertTrue("Arcane Levitator descent must use the TC4 shift proxy surface, not per-entity sneaking",
                source.contains("Thaumcraft.proxy.isShiftKeyDown()"));
        assertFalse("Arcane Levitator must not gate target selection/descent on Entity.isSneaking()",
                source.contains(".isSneaking()"));
        assertTrue("Arcane Levitator motion constants must match TC4",
                source.contains("e.motionY *= 0.9D")
                        && source.contains("e.motionY < 0.35D")
                        && source.contains("e.motionY += 0.1D")
                        && source.contains("e.fallDistance = 0.0F"));
    }

    @Test
    public void shiftProxySurfaceKeepsClientOnlyKeyStateParity() throws IOException {
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("CommonProxy must expose default false shift state for server/common code",
                commonProxy.contains("public boolean isShiftKeyDown()")
                        && commonProxy.contains("return false;"));
        assertTrue("ClientProxy must route shift state through GuiScreen like TC4",
                clientProxy.contains("public boolean isShiftKeyDown()")
                        && clientProxy.contains("return GuiScreen.isShiftKeyDown();"));
    }

    @Test
    public void lifterPresentationKeepsTc4NameAndGlowPalette() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockLifter.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileLifterRenderer.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("Lifter base model must render as cutout so TC4 transparent glow windows expose the active overlay",
                block.contains("BlockRenderLayer.CUTOUT"));
        assertTrue("Lifter renderer must keep TC4 animated glow overlay colors",
                renderer.contains("TOP_GLOW_COLOR = 0xD000A000")
                        && renderer.contains("SIDE_GLOW_COLOR = 0xD0DD11FF")
                        && renderer.contains("getAtlasSprite(\"thaumcraft:blocks/animatedglow\")"));
        assertTrue("English display name must match TC4 Arcane Levitator",
                lang.contains("tile.thaumcraft.lifter.name=Arcane Levitator")
                        && lang.contains("tile.blockLifter.name=Arcane Levitator"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
