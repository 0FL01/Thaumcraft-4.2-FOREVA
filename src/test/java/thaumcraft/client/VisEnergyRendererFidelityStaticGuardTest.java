package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class VisEnergyRendererFidelityStaticGuardTest {

    @Test
    public void energizedNodeAndWorkbenchChargerRenderersUseReferenceShapedPaths() throws IOException {
        String nodeCoreRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeRenderer.java");
        String nodeRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeEnergizedRenderer.java");
        String stabilizerRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeStabilizerRenderer.java");
        String converterRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeConverterRenderer.java");
        String chargerRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileMagicWorkbenchChargerRenderer.java");
        String relayRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileVisRelayRenderer.java");
        String crystalizerRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEssentiaCrystalizerRenderer.java");
        String crystalizerModel = read("src/main/java/thaumcraft/client/renderers/models/ModelCrystalizer.java");
        String relayModel = read("src/main/java/thaumcraft/client/renderers/models/ModelVisRelay.java");
        String stabilizerModel = read("src/main/java/thaumcraft/client/renderers/models/ModelNodeStabilizer.java");
        String stoneDeviceBlockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockstonedevice.json");
        String stabilizerBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_9.json");
        String converterBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_11.json");

        assertTrue("TileNodeRenderer should keep the original node-type strip/blend mapping for NORMAL/UNSTABLE/DARK/TAINTED/PURE/HUNGRY",
                nodeCoreRenderer.contains("case UNSTABLE:")
                        && nodeCoreRenderer.contains("strip = 6;")
                        && nodeCoreRenderer.contains("case DARK:")
                        && nodeCoreRenderer.contains("strip = 2;")
                        && nodeCoreRenderer.contains("case TAINTED:")
                        && nodeCoreRenderer.contains("strip = 5;")
                        && nodeCoreRenderer.contains("case PURE:")
                        && nodeCoreRenderer.contains("strip = 4;")
                        && nodeCoreRenderer.contains("case HUNGRY:")
                        && nodeCoreRenderer.contains("centerScale *= 0.75F;")
                        && nodeCoreRenderer.contains("0xFFFFFF")
                        && !nodeCoreRenderer.contains("0xFF00FF")
                        && nodeCoreRenderer.contains("DEFAULT_NODE_ASPECTS")
                        && nodeCoreRenderer.contains("node.getAspectsBase()")
                        && nodeCoreRenderer.contains("pos.getX()")
                        && nodeCoreRenderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 220.0F);")
                        && nodeCoreRenderer.contains("renderFacingStrip(renderX, renderY, renderZ, 0.0F, 0.5F, 0.1F, FRAMES, 1, frame, 0xFFFFFF)")
                        && nodeCoreRenderer.contains("renderNodeSeeded("));

        assertTrue("TileNodeEnergizedRenderer should keep node-core rendering and animated lightning-ring overlay",
                nodeRenderer.contains("TileNodeRenderer.renderNode(")
                        && nodeRenderer.contains("tile.getAuraBase()")
                        && nodeRenderer.contains("tile.getNodeType()")
                        && nodeRenderer.contains("tile.getNodeModifier()")
                        && nodeRenderer.contains("tile.getPos().getX()")
                        && nodeRenderer.contains("textures/items/lightningringv.png")
                        && nodeRenderer.contains("RING_FRAMES = 16")
                        && nodeRenderer.contains("drawTexturedQuad(0.33F, u0, u1, v0, v1)")
                        && !nodeRenderer.contains("textures/misc/node_bubble.png"));

        assertTrue("TileNodeStabilizerRenderer should render the complete TC4 lock, count-driven pistons, and bubble overlay through TESR",
                 stabilizerRenderer.contains("new ModelNodeStabilizer()")
                         && stabilizerRenderer.contains("bindTexture(BASE_TEXTURE);")
                         && stabilizerRenderer.contains("model.renderLock(MODEL_SCALE);")
                         && stabilizerRenderer.contains("model.renderPiston(MODEL_SCALE);")
                         && stabilizerRenderer.contains("textures/misc/node_bubble.png")
                         && stabilizerRenderer.contains("tile.count / 100.0D")
                         && stabilizerRenderer.contains("drawTexturedQuad(0.9F, bubbleColor")
                         && stabilizerRenderer.contains("OpenGlHelper.setLightmapTextureCoords"));

        assertTrue("TileNodeConverterRenderer should render both the base and colored overlay lock plus count-driven pistons",
                 converterRenderer.contains("new ModelNodeStabilizer()")
                         && converterRenderer.contains("bindTexture(BASE_TEXTURE);\n            model.renderLock(MODEL_SCALE);")
                         && converterRenderer.contains("bindTexture(OVER_TEXTURE);")
                         && converterRenderer.contains("model.renderLock(MODEL_SCALE);")
                         && converterRenderer.contains("model.renderPiston(MODEL_SCALE);")
                         && converterRenderer.contains("OpenGlHelper.setLightmapTextureCoords")
                         && !converterRenderer.contains("ticks * (1.8F"));

        assertTrue("TileMagicWorkbenchChargerRenderer should keep the reference ring/support/crystal TESR path with lightmap pulse contract",
                chargerRenderer.contains("new ModelVisRelay()")
                        && chargerRenderer.contains("model.renderRingFloat(MODEL_SCALE)")
                        && chargerRenderer.contains("model.renderSupport(MODEL_SCALE)")
                        && chargerRenderer.contains("for (int i = 0; i < 4; i++)")
                        && chargerRenderer.contains("GlStateManager.translate(0.0F, 0.0F, 0.5F);")
                        && chargerRenderer.contains("model.renderCrystal(MODEL_SCALE)")
                        && chargerRenderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && chargerRenderer.contains("VisNetHandler.isNodeValid(tile.getParent())")
                        && chargerRenderer.contains("new Color(ItemShard.colors[tile.color])")
                        && chargerRenderer.contains("float previousLightX = OpenGlHelper.lastBrightnessX;")
                        && chargerRenderer.contains("GlStateManager.tryBlendFuncSeparate(")
                        && chargerRenderer.indexOf("model.renderSupport(MODEL_SCALE)")
                                < chargerRenderer.indexOf("GlStateManager.enableBlend();")
                        && !chargerRenderer.contains("ModelMagicWorkbenchCharger")
                        && !chargerRenderer.contains("GlStateManager.disableLighting()")
                        && !chargerRenderer.contains("GlStateManager.disableCull()")
                        && !chargerRenderer.contains("TileRenderHelper.drawTexturedQuad("));

        assertTrue("TileVisRelayRenderer should keep ring/crystal model path with lightmap pulse contract",
                relayRenderer.contains("model.renderRingBase(MODEL_SCALE)")
                        && relayRenderer.contains("model.renderRingFloat(MODEL_SCALE)")
                        && relayRenderer.contains("model.renderCrystal(MODEL_SCALE)")
                        && relayRenderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && relayRenderer.contains("VisNetHandler.isNodeValid(tile.getParent())")
                        && relayRenderer.contains("new Color(ItemShard.colors[tile.color])")
                        && relayRenderer.contains("float previousLightX = OpenGlHelper.lastBrightnessX;")
                        && relayRenderer.contains("GlStateManager.tryBlendFuncSeparate(")
                        && relayRenderer.indexOf("model.renderRingFloat(MODEL_SCALE)")
                                < relayRenderer.indexOf("GlStateManager.enableBlend();")
                        && !relayRenderer.contains("GlStateManager.disableLighting()")
                        && !relayRenderer.contains("GlStateManager.disableCull()"));

        assertTrue("TileEssentiaCrystalizerRenderer should keep the crystalizer.obj shell plus crystal loop and lightmap-driven glow contract",
                crystalizerRenderer.contains("new ModelCrystalizer()")
                        && crystalizerRenderer.contains("baseModel.renderBase();")
                        && crystalizerRenderer.contains("baseModel.renderTop();")
                        && crystalizerRenderer.contains("model.renderCrystal(MODEL_SCALE)")
                        && crystalizerRenderer.contains("for (int i = 0; i < 4; i++)")
                        && crystalizerRenderer.contains("OpenGlHelper.setLightmapTextureCoords("));

        assertTrue("ModelCrystalizer should keep grouped crystalizer.obj surfaces instead of the old vis-relay ring fallback",
                crystalizerModel.contains("Wavefront crystalizer.obj groups")
                        && crystalizerModel.contains("BASE_TRIANGLES")
                        && crystalizerModel.contains("TOP_TRIANGLES")
                        && crystalizerModel.contains("renderBase()")
                        && crystalizerModel.contains("renderTop()")
                        && crystalizerModel.contains(".tex(uv[0], 1.0F - uv[1])")
                        && crystalizerModel.contains("DefaultVertexFormats.POSITION_TEX_NORMAL"));

        assertTrue("ModelVisRelay should keep grouped vis_relay.obj surfaces instead of the old ModelRenderer cuboids",
                relayModel.contains("Wavefront vis_relay.obj groups")
                        && relayModel.contains("CRYSTAL_TRIANGLES")
                        && relayModel.contains("RING_FLOAT_TRIANGLES")
                        && relayModel.contains("RING_BASE_TRIANGLES")
                        && relayModel.contains("SUPPORT_TRIANGLES")
                        && relayModel.contains("renderSupport(float scale)")
                        && relayModel.contains(".tex(uv[0], 1.0F - uv[1])")
                        && relayModel.contains("DefaultVertexFormats.POSITION_TEX_NORMAL")
                        && !relayModel.contains("extends ModelBase")
                        && !relayModel.contains("new ModelRenderer("));

        assertTrue("ModelNodeStabilizer should keep grouped node_stabilizer.obj surfaces instead of the old ModelRenderer cuboids",
                stabilizerModel.contains("Wavefront node_stabilizer.obj groups")
                        && stabilizerModel.contains("LOCK_TRIANGLES")
                        && stabilizerModel.contains("PISTON_TRIANGLES")
                        && stabilizerModel.contains("renderLock(float scale)")
                         && stabilizerModel.contains("renderPiston(float scale)")
                         && stabilizerModel.contains(".tex(uv[0], 1.0F - uv[1])")
                        && stabilizerModel.contains("DefaultVertexFormats.POSITION_TEX_NORMAL")
                        && !stabilizerModel.contains("extends ModelBase")
                        && !stabilizerModel.contains("new ModelRenderer("));

        assertTrue("Stone-device fallback models should remain metadata-specific even though TESR owns the live shell",
                stoneDeviceBlockstate.contains("\"type=9\": { \"model\": \"thaumcraft:blockstonedevice_9\" }")
                        && stoneDeviceBlockstate.contains("\"type=10\": { \"model\": \"thaumcraft:blockstonedevice_9\" }")
                        && stoneDeviceBlockstate.contains("\"type=11\": { \"model\": \"thaumcraft:blockstonedevice_11\" }"));

        assertTrue("Node-device fallback assets should retain representative source textures and bounds",
                stabilizerBlockModel.contains("\"ambientocclusion\": false")
                        && stabilizerBlockModel.contains("\"surface\": \"thaumcraft:models/node_stabilizer\"")
                        && stabilizerBlockModel.contains("\"from\": [4, 4, 7]")
                        && stabilizerBlockModel.contains("\"to\": [12, 12, 9]")
                        && stabilizerBlockModel.contains("\"from\": [6.5, 3.5, 6.5]")
                        && converterBlockModel.contains("\"surface\": \"thaumcraft:models/node_converter\"")
                        && converterBlockModel.contains("\"from\": [4, 4, 7]")
                        && converterBlockModel.contains("\"to\": [9.5, 8.5, 9.5]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
