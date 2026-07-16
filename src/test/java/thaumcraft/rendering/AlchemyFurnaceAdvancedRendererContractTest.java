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

    @Test
    public void objFacesShouldRenderWithTheirAuthoredOutwardWinding() throws IOException {
        String renderer = read(RENDERER);
        String[] lines = new String(Files.readAllBytes(Paths.get(OBJ)), StandardCharsets.US_ASCII)
                .split("\\R");
        List<double[]> vertices = new ArrayList<>();
        List<double[]> normals = new ArrayList<>();
        String group = "";
        int baseFaces = 0;
        int tankFaces = 0;
        int outwardFaces = 0;
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.startsWith("v ")) {
                String[] values = line.split("\\s+");
                vertices.add(new double[] {
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]),
                        Double.parseDouble(values[3])
                });
            } else if (line.startsWith("vn ")) {
                String[] values = line.split("\\s+");
                normals.add(new double[] {
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]),
                        Double.parseDouble(values[3])
                });
            } else if (line.startsWith("g ")) {
                group = line.substring(2);
            } else if (("Base".equals(group) || "Tank".equals(group)) && line.startsWith("f ")) {
                String[] corners = line.split("\\s+");
                String[] first = corners[1].split("/");
                String[] second = corners[2].split("/");
                String[] third = corners[3].split("/");
                double[] a = vertices.get(Integer.parseInt(first[0]) - 1);
                double[] b = vertices.get(Integer.parseInt(second[0]) - 1);
                double[] c = vertices.get(Integer.parseInt(third[0]) - 1);
                double[] normal = normals.get(Integer.parseInt(first[2]) - 1);
                double[] ab = {b[0] - a[0], b[1] - a[1], b[2] - a[2]};
                double[] ac = {c[0] - a[0], c[1] - a[1], c[2] - a[2]};
                double[] cross = {
                        ab[1] * ac[2] - ab[2] * ac[1],
                        ab[2] * ac[0] - ab[0] * ac[2],
                        ab[0] * ac[1] - ab[1] * ac[0]
                };
                if (cross[0] * normal[0] + cross[1] * normal[1] + cross[2] * normal[2] > 0.0D) {
                    ++outwardFaces;
                }
                if ("Base".equals(group)) {
                    ++baseFaces;
                } else {
                    ++tankFaces;
                }
            }
        }

        assertEquals(116, baseFaces);
        assertEquals(64, tankFaces);
        assertEquals("Every source OBJ face must agree with its authored outward normal",
                baseFaces + tankFaces, outwardFaces);
        assertTrue(renderer.contains("this.base = restoreObjFaceOrder(parsedBase);")
                && renderer.contains("this.tank = restoreObjFaceOrder(parsedTank);")
                && renderer.contains("CCModel corrected = model.backfacedCopy();")
                && renderer.contains("normal.negate();")
                && renderer.contains("boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE)")
                && renderer.contains("GlStateManager.enableCull();")
                && renderer.contains("GlStateManager.disableCull();")
                && !renderer.contains("BASE_FLOOR_Z_OFFSET")
                && !renderer.contains("renderBase("));
    }

    @Test
    public void workingFurnaceShouldRetainTc4HeatAndAmbientEffects() throws IOException {
        String renderer = read(RENDERER);
        String block = read("src/main/java/thaumcraft/common/blocks/BlockAlchemyFurnace.java");
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue(renderer.contains("bindTexture(tile.heat > 100 ? FURNACE_ON : FURNACE);")
                && renderer.contains("if (tile.heat > 100)")
                && renderer.contains("(float) tile.heat / (float) tile.maxPower")
                && renderer.contains("renderQuadCenteredFromIcon(fire, 220, fill);"));
        assertTrue(block.contains("public void randomDisplayTick(")
                && block.contains("this.getMetaFromState(state) == CENTER")
                && block.contains("((TileAlchemyFurnaceAdvanced) tile).vis > 0")
                && occurrences(block, "Thaumcraft.proxy.slimyBubble(") == 2
                && block.contains("0.06F + rand.nextFloat() * 0.06F")
                && block.contains("0.6F - rand.nextFloat() * 0.2F")
                && block.contains("0.6F + rand.nextFloat() * 0.2F")
                && block.contains("rand.nextInt(50) == 0")
                && block.contains("SoundEvents.BLOCK_LAVA_POP")
                && block.contains("super.randomDisplayTick(state, world, pos, rand);"));
        assertFalse("Common furnace blocks must not directly load client particle classes",
                block.contains("thaumcraft.client.fx"));
        assertTrue(commonProxy.contains("public void slimyBubble("));
        assertTrue(clientProxy.contains("public void slimyBubble(")
                && clientProxy.contains("FXSlimyBubble bubble = new FXSlimyBubble(")
                && clientProxy.contains("bubble.setRBGColorF(red, green, blue);")
                && clientProxy.contains("bubble.setAlphaF(alpha);")
                && clientProxy.contains("ParticleEngine.addEffect(world, bubble);"));
    }

    private static int occurrences(String source, String needle) {
        int count = 0;
        int offset = 0;
        while ((offset = source.indexOf(needle, offset)) >= 0) {
            ++count;
            offset += needle.length();
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
