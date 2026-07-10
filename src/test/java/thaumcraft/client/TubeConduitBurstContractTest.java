package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TubeConduitBurstContractTest {

    @Test
    public void tubeConduitShellsKeepTc4GeometryAndBakedItemParity() throws IOException {
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
        String tubeTesrItemModel = read("src/main/resources/assets/thaumcraft/models/item/blocktube_tesr.json");
        String tubeItemModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_0.json");
        String valveItemModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_1.json");
        String filterItemModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_3.json");
        String bufferItemModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_4.json");
        String restrictedItemModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_5.json");
        String directionalItemModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_6.json");

        assertTrue("Tube conduit helper must use TC4 connection, axis, and tube-specific UV rules",
                helper.contains("ThaumcraftApiHelper.getConnectableTile")
                        && helper.contains("renderExtendedTube()")
                        && helper.contains("drawnAxes")
                        && helper.contains("hasExternalNeighbour")
                        && helper.contains("instanceof TileBellows")
                        && helper.contains("POSITION_TEX_COLOR_NORMAL")
                        && helper.contains("getInterpolatedU")
                        && helper.contains("getInterpolatedV")
                        && helper.contains(".normal(normalX, normalY, normalZ)")
                        && helper.contains("pipe_filter_core")
                        && helper.contains("filterAspect == null")
                        && helper.contains("? 0xFFFFFFFF")
                        && !helper.contains("TileRenderHelper.drawTexturedCuboid")
                        && !helper.contains("renderInventoryShell"));
        assertTrue("Every specialized renderer must select an explicit TC4 shell type",
                tubeRenderer.contains("TubeType.ORDINARY")
                        && filterRenderer.contains("TubeType.FILTER")
                        && filterRenderer.contains("tile.aspectFilter")
                        && restrictRenderer.contains("TubeType.RESTRICTED")
                        && valveRenderer.contains("TubeType.VALVE")
                        && bufferRenderer.contains("TubeType.BUFFER")
                        && onewayRenderer.contains("TubeType.DIRECTIONAL"));
        assertTrue("Valve, buffer, and oneway renderers must stay item-safe while drawing their dedicated overlays",
                valveRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides")
                        && valveRenderer.contains("if (tile == null)")
                        && bufferRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides")
                        && bufferRenderer.contains("if (tile.getWorld() == null || tile.getPos() == null)")
                        && onewayRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides"));

        assertTrue("Static tube shells and the dynamic valve/crystalizer routes must remain intact",
                tubeBlock.contains("return this.getMetaFromState(state) == 2 ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;")
                        && !clientProxy.contains("registerBuiltinItemModel(tubeItem, 0, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 1, \"blocktube_tesr\");")
                        && !clientProxy.contains("registerBuiltinItemModel(tubeItem, 3, \"blocktube_tesr\");")
                        && !clientProxy.contains("registerBuiltinItemModel(tubeItem, 4, \"blocktube_tesr\");")
                        && !clientProxy.contains("registerBuiltinItemModel(tubeItem, 5, \"blocktube_tesr\");")
                        && !clientProxy.contains("registerBuiltinItemModel(tubeItem, 6, \"blocktube_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 7, \"blocktube_tesr\");")
                        && clientProxy.contains("tubeItem.setTileEntityItemStackRenderer(new ItemTubeRenderer());")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 2, \"blocktube_2_inventory\");")
                        && itemRenderer.contains("new TileEssentiaCrystalizerRenderer()")
                        && itemRenderer.contains("crystalizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileTubeValveRenderer()")
                        && itemRenderer.contains("valveRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("else if (meta == 1)")
                        && itemRenderer.contains("renderBakedShell(meta);")
                        && itemRenderer.contains("renderModelBrightnessColor(")
                        && !itemRenderer.contains("renderInventoryShell")
                        && itemRenderer.contains("if (meta == 7)")
                        && itemRenderer.contains("valve.facing = EnumFacing.EAST;")
                        && itemRenderer.contains("try {")
                        && itemRenderer.contains("} finally {")
                        && tubeTesrItemModel.contains("\"parent\": \"builtin/entity\"")
                        && tubeTesrItemModel.contains("\"thirdperson_righthand\"")
                        && tubeTesrItemModel.contains("\"firstperson_lefthand\"")
                        && tubeItemModel.contains("\"parent\": \"block/block\"")
                        && tubeItemModel.contains("\"from\": [6.5, 6.5, 6.5]")
                        && bufferItemModel.contains("\"parent\": \"block/block\"")
                        && bufferItemModel.contains("\"from\": [4, 4, 4]"));
        assertTrue("Valve item shell must bake the TC4 pipe_1 arm and pipe_2 fallback before its dynamic overlay",
                valveItemModel.contains("\"from\": [7, 0, 7]")
                        && valveItemModel.contains("\"from\": [6, 6, 6]")
                        && valveItemModel.contains("\"joint\": \"thaumcraft:blocks/pipe_2\"")
                        && valveItemModel.contains("\"uv\": [7, 0, 9, 16]"));
        assertTrue("Filter item shell must include the arm plus co-spatial default-white filter and core layers",
                filterItemModel.contains("\"from\": [7, 0, 7]")
                        && occurrences(filterItemModel, "\"from\": [5.5, 5.5, 5.5]") == 2
                        && filterItemModel.contains("\"texture\": \"#filter\"")
                        && filterItemModel.contains("\"texture\": \"#core\"")
                        && filterItemModel.contains("\"uv\": [5.5, 5.5, 10.5, 10.5]"));
        assertTrue("Restricted and directional items must retain their exact TC4 bounds and textures",
                restrictedItemModel.contains("\"from\": [6.5, 6.5, 6.5]")
                        && restrictedItemModel.contains("\"uv\": [6.5, 6.5, 9.5, 9.5]")
                        && directionalItemModel.contains("\"from\": [6.984, 1.6, 6.984]")
                        && directionalItemModel.contains("\"to\": [9.016, 14.4, 9.016]")
                        && directionalItemModel.contains("\"direction\": \"thaumcraft:blocks/pipe_oneway\"")
                        && directionalItemModel.contains("\"joint\": \"thaumcraft:blocks/pipe_3\"")
                        && directionalItemModel.contains("\"uv\": [6.984, 1.6, 9.016, 14.4]"));
        assertFalse("Forge item display transforms must not receive a duplicate legacy -0.5 origin shift",
                itemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);"));

        assertTrue("TileEssentiaCrystalizerRenderer should keep the crystalizer.obj shell path available for worldless TEISR inventory renders",
                crystalizerRenderer.contains("if (tile == null)")
                        && crystalizerRenderer.contains("baseModel.renderBase();")
                        && crystalizerRenderer.contains("baseModel.renderTop();")
                        && !crystalizerRenderer.contains("if (tile == null || tile.getWorld() == null)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int occurrences(String value, String needle) {
        int count = 0;
        int offset = 0;
        while ((offset = value.indexOf(needle, offset)) >= 0) {
            ++count;
            offset += needle.length();
        }
        return count;
    }
}
