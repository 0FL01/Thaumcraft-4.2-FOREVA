package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class GolemBellWorldOverlayParityStaticGuardTest {

    @Test
    public void renderWorldLastRestoresTc4BellMarkerHomeAndLinkEffects() throws IOException {
        String source = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");

        int markerRender = source.indexOf("renderMarkedBlocks(event.getPartialTicks(), player);");
        int scanEarlyExit = source.indexOf("if (scanExpireAtMs <= 0L || now >= scanExpireAtMs)");
        assertTrue("Bell overlays must render independently of the thaumometer scan lifetime",
                markerRender >= 0 && scanEarlyExit > markerRender);

        assertTrue("TC4 renders stored markers for both a linked bell and a golem placer",
                source.contains("held.getItem() instanceof ItemGolemBell")
                        && source.contains("held.getItem() instanceof ItemGolemPlacer"));
        assertTrue("A bell must keep the TC4 loaded-golem gate and home marker route",
                source.contains("player.world.getEntityByID(ItemGolemBell.getGolemId(held))")
                        && source.contains("ItemGolemBell.getGolemHomeCoords(held)")
                        && source.contains("ItemGolemBell.getGolemHomeFace(held)"));
        assertTrue("Marker visibility must remain dimension- and 64-block-limited",
                source.contains("marker.dim != (byte) dimension")
                        && source.contains("player.getDistanceSq(markerX, markerY, markerZ) >= 4096.0D"));

        assertTrue("TC4 marker, home, empty-block, and script textures must all be routed",
                source.contains("textures/misc/mark.png")
                        && source.contains("textures/misc/home.png")
                        && source.contains("textures/blocks/empty.png")
                        && source.contains("textures/misc/script.png"));
        assertTrue("Removed marked blocks must retain the six-face empty-block overlay",
                source.contains("player.world.isAirBlock(markedPos)")
                        && source.contains("for (EnumFacing airFace : EnumFacing.values())")
                        && source.contains("GOLEM_EMPTY_TEX"));
        assertTrue("UP and DOWN marker quads must be translated onto their matching block faces",
                source.contains("case DOWN:\n                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);")
                        && source.contains("case UP:\n                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);"));
        assertTrue("The golem-to-marker trail must honor the original quality cutoff and additive strip rendering",
                source.contains("golem != null && Config.golemLinkQuality > 3")
                        && source.contains("buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR)")
                        && source.contains("GlStateManager.DestFactor.ONE"));
        assertTrue("TC4 marker colors must preserve dye ordering and the animated any-color fallback",
                source.contains("ItemDye.DYE_COLORS[ItemDye.DYE_COLORS.length - 1 - color]")
                        && source.contains("MathHelper.sin(time / 12.0F + side + segment)")
                        && source.contains("MathHelper.sin(time / 16.0F + side + segment)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
