package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class MirrorRendererFidelityStaticGuardTest {

    @Test
    public void tileMirrorRendererKeepsLayeredPortalPaneAndFrameContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java");
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/LayeredFieldPlaneHelper.java");
        String extruded = read("src/main/java/thaumcraft/client/renderers/tile/ExtrudedSpriteRenderHelper.java");
        String valve = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeValveRenderer.java");
        String block = read("src/main/java/thaumcraft/common/blocks/BlockMirror.java");

        assertTrue("TileMirrorRenderer should keep mirror portal textures and pane overlays",
                source.contains("textures/blocks/mirrorpane.png")
                        && source.contains("textures/blocks/mirrorpanetrans.png"));

        assertTrue("TileMirrorRenderer should keep linked-vs-unlinked pane flow",
                source.contains("if (linked && isVisible(tile))")
                        && source.contains("renderPortalLayers(")
                        && source.contains("renderPane(facing, x, y, z, MIRROR_PANE_TRANS")
                        && source.contains("renderPane(facing, x, y, z, MIRROR_PANE"));

        assertTrue("Mirror panes should keep the original visible winding, normals, and alpha blend",
                source.contains("GlStateManager.enableBlend();")
                        && source.contains("DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL")
                        && source.contains(".tex(u1, v0).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F)")
                        && source.contains("GlStateManager.disableBlend();"));

        assertTrue("TileMirrorRenderer should keep mirror frame atlas pass and orientation transform",
                source.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && source.contains("blocks/mirrorframe")
                        && source.contains("blocks/mirrorframe2")
                        && source.contains("transformFromOrientation("));

        assertTrue("Mirror world rendering should remain TESR-only",
                block.contains("return EnumBlockRenderType.INVISIBLE;"));

        assertTrue("TESR-only mirrors should use their frame sprites for destroy particles",
                block.contains("public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)")
                        && block.contains("blocks/mirrorframe2")
                        && block.contains("particle.setParticleTexture(frame);")
                        && block.contains("return true;"));

        assertTrue("TileMirrorRenderer should render the stable extruded frame after the pane",
                source.contains("renderFrame(facing, x, y, z, tile instanceof TileMirrorEssentia);")
                        && source.contains("private static final float FRAME_THICKNESS = 0.0625F;")
                        && source.contains("transformFromOrientation(x, y, z, facing.getIndex(), 0.0F);")
                        && source.contains("ExtrudedSpriteRenderHelper.render(sprite, FRAME_THICKNESS);"));

        assertTrue("Mirror and valve renderers should share the existing extruded-sprite geometry",
                valve.contains("ExtrudedSpriteRenderHelper.render(sprite, VALVE_THICKNESS);")
                        && extruded.contains("DefaultVertexFormats.POSITION_TEX_NORMAL")
                        && extruded.contains("for (int i = 0; i < width; ++i)")
                        && extruded.contains("for (int i = 0; i < height; ++i)"));

        assertTrue("TileMirrorRenderer should route linked portal fields through the shared layered-field helper with inset bounds",
                source.contains("LayeredFieldPlaneHelper.renderLayeredFaceRect(")
                        && source.contains("INSET, 1.0F - INSET, INSET, 1.0F - INSET")
                        && source.contains("view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks"));

        assertTrue("LayeredFieldPlaneHelper should keep the tunnel/particle texgen and matrix flow used by mirror portals",
                helper.contains("textures/misc/tunnel.png")
                        && helper.contains("textures/misc/particlefield.png")
                        && helper.contains("FIELD_COLOR_SEED = 31100L")
                        && helper.contains("GL11.glTexGeni")
                        && helper.contains("GlStateManager.matrixMode(5890)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
