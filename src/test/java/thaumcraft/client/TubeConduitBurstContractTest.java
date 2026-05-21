package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TubeConduitBurstContractTest {

    @Test
    public void stage8cTubeConduitBurstKeepsDynamicArmsAndCoreModels() throws IOException {
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/TubeConduitRenderHelper.java");
        String tubeRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeRenderer.java");
        String filterRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeFilterRenderer.java");
        String restrictRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeRestrictRenderer.java");
        String valveRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeValveRenderer.java");
        String bufferRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeBufferRenderer.java");
        String onewayRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeOnewayRenderer.java");
        String crystalizerRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEssentiaCrystalizerRenderer.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemTubeRenderer.java");
        String tubeBlock = read("src/main/java/thaumcraft/common/blocks/BlockTube.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String crystalizerItemModel = read("src/main/resources/assets/thaumcraft/models/item/blocktube_tesr.json");

        assertTrue("Tube conduit helper must drive dynamic connections through ThaumcraftApiHelper and extended-tube checks",
                helper.contains("ThaumcraftApiHelper.getConnectableTile")
                        && helper.contains("renderExtendedTube()")
                        && helper.contains("pipe_filter_core")
                        && helper.contains("renderInventoryShell(int meta)"));
        assertTrue("TileTube/Filter/Restrict renderers must delegate conduit drawing through the shared helper",
                tubeRenderer.contains("TubeConduitRenderHelper.renderConduit")
                        && filterRenderer.contains("tile.aspectFilter")
                        && restrictRenderer.contains("\"thaumcraft:blocks/pipe_restrict\""));
        assertTrue("Valve, buffer, and oneway renderers must stay item-safe while rendering conduit arms before their dedicated overlays",
                valveRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides")
                        && valveRenderer.contains("if (tile == null)")
                        && bufferRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides")
                        && bufferRenderer.contains("if (tile.getWorld() == null || tile.getPos() == null)")
                        && onewayRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides"));

        assertTrue("Tube ancillary metas plus crystalizer should route through TESR-first world and item rendering instead of baked placeholder shells",
                tubeBlock.contains("return this.getMetaFromState(state) == 2 ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 0, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 1, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 3, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 4, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 5, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 6, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 7, \"blocktube_tesr\");")
                        && clientProxy.contains("tubeItem.setTileEntityItemStackRenderer(new ItemTubeRenderer());")
                        && itemRenderer.contains("new TileEssentiaCrystalizerRenderer()")
                        && itemRenderer.contains("crystalizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileTubeValveRenderer()")
                        && itemRenderer.contains("valveRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("TubeConduitRenderHelper.renderInventoryShell(meta);")
                        && itemRenderer.contains("if (meta == 1)")
                        && itemRenderer.contains("if (meta == 7)")
                        && itemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);")
                        && crystalizerItemModel.contains("\"parent\": \"builtin/entity\""));

        assertTrue("TileEssentiaCrystalizerRenderer should keep the crystalizer.obj shell path available for worldless TEISR inventory renders",
                crystalizerRenderer.contains("if (tile == null)")
                        && crystalizerRenderer.contains("baseModel.renderBase();")
                        && crystalizerRenderer.contains("baseModel.renderTop();")
                        && !crystalizerRenderer.contains("if (tile == null || tile.getWorld() == null)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
