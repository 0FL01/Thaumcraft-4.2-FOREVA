package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class JarVisualShellContractTest {

    @Test
    public void jarShellLivesInBlockModelsWhileTesrKeepsDynamicContentsAndItemParity() throws IOException {
        String blockJar = read("src/main/java/thaumcraft/common/blocks/BlockJar.java");
        String jarRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemJarRenderer.java");
        String jarModel = read("src/main/java/thaumcraft/client/renderers/models/ModelJar.java");
        String brainModel = read("src/main/java/thaumcraft/client/renderers/models/ModelBrain.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockjar.json");
        String normalModel = read("src/main/resources/assets/thaumcraft/models/block/blockjar_0.json");
        String voidModel = read("src/main/resources/assets/thaumcraft/models/block/blockjar_1.json");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockjar.json");

        assertTrue("BlockJar should use baked translucent block-model rendering now that the static shell lives in block models",
                blockJar.contains("return EnumBlockRenderType.MODEL;")
                        && blockJar.contains("return BlockRenderLayer.TRANSLUCENT;"));

        assertTrue("TileJarRenderer should keep node, liquid, label, brain, and brine overlays while rendering the shell only for TEISR items or node-jar animation pulses",
                jarRenderer.contains("TileNodeRenderer.renderNodeAt((TileJarNode) tile")
                        && jarRenderer.contains("renderBrain((TileJarBrain) tile, x, y, z, partialTicks);")
                        && jarRenderer.contains("private final ModelBrain brain = new ModelBrain();")
                        && jarRenderer.contains("renderFillable((TileJarFillable) tile, x, y, z);")
                        && jarRenderer.contains("boolean renderShell = tile.getWorld() == null;")
                        && jarRenderer.contains("float shellScale = 1.0F;")
                        && jarRenderer.contains("if (renderShell) {")
                        && jarRenderer.contains("renderJarShell(tile, x, y, z, shellScale);")
                        && jarRenderer.contains("bindTexture(BRINE_TEXTURE);")
                        && jarRenderer.contains("private static final String LIQUID_TEXTURE = \"thaumcraft:blocks/animatedglow\";")
                        && jarRenderer.contains("TileRenderHelper.drawTexturedCuboid(buf, minX, minY, minZ, maxX, maxY, maxZ, liquid, color);")
                        && jarRenderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);")
                        && jarRenderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);")
                        && jarRenderer.contains("GlStateManager.translate(x + 0.5D, y + 0.01D, z + 0.5D);")
                        && jarRenderer.contains("GlStateManager.translate(0.0F, -0.8F + bob, 0.0F);")
                        && jarRenderer.contains("tile.getWorld() == null && Minecraft.getMinecraft().player != null")
                        && jarRenderer.contains("Minecraft.getMinecraft().player.ticksExisted + partialTicks")
                        && jarRenderer.contains("float delta = tile.rota - tile.rotb;")
                        && jarRenderer.contains("brain.render(MODEL_SCALE);")
                        && jarRenderer.contains("boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);")
                        && jarRenderer.contains("} finally {")
                        && !jarRenderer.contains("drawSolidHorizontalQuad")
                        && !jarRenderer.contains("renderJarShell(tile, x, y, z);"));

        assertTrue("Jar labels should preserve TC4 size, height, tinted aspect glyph, and deterministic crooked tilt",
                jarRenderer.contains("GlStateManager.translate(0.0F, -0.4F, 0.315F);")
                        && jarRenderer.contains("TileRenderHelper.drawTexturedQuad(0.25F, 0xFFFFFFFF")
                        && jarRenderer.contains("GlStateManager.translate(0.0F, -0.4F, 0.316F);")
                        && jarRenderer.contains("GlStateManager.scale(0.021F, 0.021F, 0.021F);")
                        && jarRenderer.contains("TileRenderHelper.drawTexturedQuad(8.0F,")
                        && jarRenderer.contains("0xFF000000 | (aspect.getColor() & 0x00FFFFFF)")
                        && jarRenderer.contains("aspect.getTag().hashCode() + tile.getPos().getX() + tile.facing")
                        && jarRenderer.contains("if (Config.crooked) {"));

        assertTrue("ModelJar should blend the glass core so TEISR item shells and node pulse shells stay transparent",
                jarModel.contains("renderLid(scale);")
                        && jarModel.contains("GlStateManager.enableBlend();")
                        && jarModel.contains("renderCore(scale);")
                        && jarModel.contains("GlStateManager.disableBlend();"));

        assertTrue("ModelBrain should exist so the brain jar uses model-driven geometry instead of a textured quad fallback",
                brainModel.contains("class ModelBrain extends ModelBase")
                        && brainModel.contains("shape1.addBox(0.0F, 0.0F, 0.0F, 12, 10, 16);")
                        && brainModel.contains("shape2.addBox(0.0F, 0.0F, 0.0F, 8, 3, 7);")
                        && brainModel.contains("shape3.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2);")
                        && brainModel.contains("shape3.setRotationPoint(-1.0F, 18.0F, -2.0F);"));

        assertTrue("ItemJarRenderer should keep delegating to TileJarRenderer so filled jars, node jars, and brain jars retain dynamic item visuals",
                itemRenderer.contains("private final TileJarRenderer renderer = new TileJarRenderer();")
                        && itemRenderer.contains("renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("renderer.render(tile, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);")
                        && itemRenderer.contains("Forge supplies the outer -0.5 TEISR translation")
                        && !itemRenderer.contains("DEFAULT_NODE_ASPECTS")
                        && !itemRenderer.contains("GlStateManager.translate(0.5F, 0.5F, 0.5F);"));

        assertComplete3dDisplay(itemModel);

        assertTrue("Jar blockstate should route brain and node jars to the normal shell and the void jar to the void shell",
                blockstate.contains("\"type=0\": { \"model\": \"thaumcraft:blockjar_0\" }")
                        && blockstate.contains("\"type=1\": { \"model\": \"thaumcraft:blockjar_0\" }")
                        && blockstate.contains("\"type=2\": { \"model\": \"thaumcraft:blockjar_0\" }")
                        && blockstate.contains("\"type=3\": { \"model\": \"thaumcraft:blockjar_1\" }"));

        assertTrue("Normal and void jar block models should now carry the shaped glass shell instead of the old full-cube placeholder",
                normalModel.contains("\"ambientocclusion\": false")
                        && normalModel.contains("\"from\": [3, 0, 3]")
                        && normalModel.contains("\"to\": [13, 12, 13]")
                        && normalModel.contains("\"from\": [5, 12, 5]")
                        && normalModel.contains("\"to\": [11, 14, 11]")
                        && voidModel.contains("\"side\": \"thaumcraft:blocks/jar_side_void\"")
                        && voidModel.contains("\"top\": \"thaumcraft:blocks/jar_top_void\"")
                        && voidModel.contains("\"from\": [3, 0, 3]")
                        && voidModel.contains("\"to\": [11, 14, 11]"));

        assertLidSideTextures("normal jar", normalModel);
        assertLidSideTextures("void jar", voidModel);
    }

    private static void assertLidSideTextures(String name, String model) {
        int lidStart = model.indexOf("\"from\": [5, 12, 5]");
        assertTrue(name + " should contain the raised lid cuboid", lidStart >= 0);
        String lid = model.substring(lidStart);
        assertTrue(name + " lid should use top texture only on horizontal faces and side texture on vertical faces",
                lid.contains("\"down\": { \"texture\": \"#top\" }")
                        && lid.contains("\"up\": { \"texture\": \"#top\" }")
                        && lid.contains("\"north\": { \"texture\": \"#side\" }")
                        && lid.contains("\"south\": { \"texture\": \"#side\" }")
                        && lid.contains("\"west\": { \"texture\": \"#side\" }")
                        && lid.contains("\"east\": { \"texture\": \"#side\" }"));
    }

    private static void assertComplete3dDisplay(String model) {
        assertTrue("Jar builtin item model should define every 3D display context",
                model.contains("\"gui\"")
                        && model.contains("\"ground\"")
                        && model.contains("\"fixed\"")
                        && model.contains("\"thirdperson_righthand\"")
                        && model.contains("\"thirdperson_lefthand\"")
                        && model.contains("\"firstperson_righthand\"")
                        && model.contains("\"firstperson_lefthand\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
