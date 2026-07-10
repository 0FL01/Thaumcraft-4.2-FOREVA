package thaumcraft.rendering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** CI-visible source and asset guards for the TC4 advanced furnace TESR. */
public class AlchemyFurnaceAdvancedRendererContractTest {
    private static final String RENDERER =
            "src/main/java/thaumcraft/client/renderers/tile/TileAlchemyFurnaceAdvancedRenderer.java";
    private static final String OBJ =
            "src/main/resources/assets/thaumcraft/textures/models/adv_alch_furnace.obj";
    private static final String TC4_OBJ =
            "thaumcraft_src/assets/thaumcraft/textures/models/adv_alch_furnace.obj";

    @Test
    public void rendererShouldRetainTheTc4ObjTransformsTexturesAndStateContract() throws IOException {
        String renderer = read(RENDERER);
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");

        assertTrue(renderer.contains("TileEntitySpecialRenderer<TileAlchemyFurnaceAdvanced>")
                && renderer.contains("CCModel.parseObjModels(FURNACE_MODEL)")
                && renderer.contains("models.get(\"Base\")")
                && renderer.contains("models.get(\"Tank\")")
                && renderer.contains("DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL"));
        assertTrue(renderer.contains("GlStateManager.translate(x + 0.5D, y, z + 0.5D);")
                && renderer.contains("GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);")
                && renderer.contains("bindTexture(tile.heat > 100 ? FURNACE_ON : FURNACE);")
                && renderer.contains("bindTexture(tile.vis > 0 ? TANK_ON : TANK);")
                && renderer.contains("GlStateManager.rotate(90.0F * side, 0.0F, 0.0F, 1.0F);"));
        assertTrue(renderer.contains("GlStateManager.translate(0.5F, -0.5F, 1.1F);")
                && renderer.contains("atlas(\"thaumcraft:blocks/fluxgoo\")")
                && renderer.contains("atlas(\"thaumcraft:blocks/metalbase\")")
                && renderer.contains("GlStateManager.translate(0.85F, -1.8F, -1.4F);")
                && renderer.contains("GlStateManager.translate(1.15F, 1.8F, -1.4F);")
                && renderer.contains("GlStateManager.scale(-0.3D, -0.6D, -1.0D);"));
        assertTrue(renderer.contains("Blocks.FIRE.getDefaultState()")
                && renderer.contains("GlStateManager.rotate(135.0F, 1.0F, 0.0F, 0.0F);")
                && renderer.contains("renderQuadCenteredFromIcon(fire, 220, fill);")
                && !renderer.contains("Blocks.LAVA"));
        assertTrue("TC4 stretches the complete source icon over the visible gauge height",
                renderer.contains("buffer.pos(1.0D, fill, 0.0D).tex(maxU, minV)")
                        && renderer.contains("buffer.pos(0.0D, fill, 0.0D).tex(minU, minV)")
                        && !renderer.contains("vFill"));
        assertTrue(renderer.contains("renderQuadCenteredFromIcon(metalbase, 150, 0.0F)")
                && renderer.contains("renderQuadCenteredFromIcon(fluxgoo, 190, fill)")
                && renderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness, 0.0F);"));
        assertTrue("All mutable 1.12 render state must be restored even if rendering fails",
                renderer.contains("boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)")
                        && renderer.contains("boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING)")
                        && renderer.contains("boolean rescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL)")
                        && renderer.contains("GlStateManager.tryBlendFuncSeparate(")
                        && renderer.contains("previousLightX, previousLightY")
                        && renderer.contains("} finally {"));
        assertFalse(renderer.contains("ModelAlchemyFurnaceAdvanced"));
        assertTrue(registry.contains("ADVANCED_FURNACE_FLUXGOO_SPRITE")
                && registry.contains("ADVANCED_FURNACE_METALBASE_SPRITE")
                && registry.contains("registerSprite(ADVANCED_FURNACE_FLUXGOO_SPRITE)")
                && registry.contains("registerSprite(ADVANCED_FURNACE_METALBASE_SPRITE)"));
        assertFalse(Files.exists(Paths.get(
                "src/main/java/thaumcraft/client/renderers/models/ModelAlchemyFurnaceAdvanced.java")));
    }

    @Test
    public void packagedObjShouldRemainTheExactTc4BaseAndTankAsset() throws IOException {
        byte[] packaged = Files.readAllBytes(Paths.get(OBJ));
        assertArrayEquals(Files.readAllBytes(Paths.get(TC4_OBJ)), packaged);

        Matcher matcher = Pattern.compile("(?m)^g (\\S+)\\s*$")
                .matcher(new String(packaged, StandardCharsets.US_ASCII));
        List<String> groups = new ArrayList<>();
        while (matcher.find()) {
            groups.add(matcher.group(1));
        }
        assertEquals(Arrays.asList("Base", "Tank"), groups);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
