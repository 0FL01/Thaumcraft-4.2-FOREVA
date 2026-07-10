package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StoneDeviceTesrRoutingContractTest {

    @Test
    public void stoneDeviceTesrFamiliesShouldUseReferenceShapedWorldAndInventoryRouting() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemStoneDeviceRenderer.java");
        String matrixRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileRunicMatrixRenderer.java");
        String pillarRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileInfusionPillarRenderer.java");
        String stabilizerRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeStabilizerRenderer.java");
        String converterRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeConverterRenderer.java");
        String fluxRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileFluxScrubberRenderer.java");
        String tesrItemModel = read("src/main/resources/assets/thaumcraft/models/item/blockstonedevice_tesr.json");
        String matrixItemModel = read("src/main/resources/assets/thaumcraft/models/item/blockstonedevice_2_inventory.json");
        String focalItemModel = read("src/main/resources/assets/thaumcraft/models/item/blockstonedevice_13_inventory.json");

        assertTrue("BlockStoneDevice should route the matrix, pillar, node-device, focal-manipulator, and flux-scrubber TESR family through invisible world rendering instead of baked placeholder shells",
                block.contains("return meta == 2 || meta == 3 || meta == 4 || meta == 9 || meta == 10 || meta == 11 || meta == 13 || meta == 14")
                        && block.contains("? EnumBlockRenderType.INVISIBLE")
                        && block.contains(": EnumBlockRenderType.MODEL;"));

        assertTrue("ClientProxy should keep ordinary blockstate item variants, route the runic matrix and focal manipulator onto donor-style baked inventory models, and keep node-device plus flux-scrubber items on builtin/entity TEISR routing",
                clientProxy.contains("Item stoneDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockStoneDevice);")
                        && clientProxy.contains("for (int meta = 0; meta <= 14; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 2, \"blockstonedevice_2_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 9, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 10, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 11, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 13, \"blockstonedevice_13_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 14, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("stoneDeviceItem.setTileEntityItemStackRenderer(new ItemStoneDeviceRenderer());"));

        assertTrue("ItemStoneDeviceRenderer should now serve only the remaining TEISR inventory family for node stabilizers, node converter, and flux scrubber",
                itemRenderer.contains("TileEntityRendererDispatcher.instance")
                        && itemRenderer.contains("stabilizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("converterRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("fluxScrubberRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileNodeStabilizerRenderer()")
                        && itemRenderer.contains("new TileNodeConverterRenderer()")
                        && itemRenderer.contains("new TileFluxScrubberRenderer()")
                        && itemRenderer.contains("if (meta == 9 || meta == 10)")
                        && itemRenderer.contains("stabilizer.lock = meta == 9 ? 1 : 2;")
                        && itemRenderer.contains("else if (meta == 11)")
                        && itemRenderer.contains("TileNodeConverter converter = new TileNodeConverter();")
                         && itemRenderer.contains("else if (meta == 14)")
                         && itemRenderer.contains("TileFluxScrubber scrubber = new TileFluxScrubber();")
                         && !itemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);")
                        && !itemRenderer.contains("new TileRunicMatrixRenderer()")
                        && !itemRenderer.contains("new TileFocalManipulatorRenderer()")
                        && !itemRenderer.contains("if (meta == 2)")
                        && !itemRenderer.contains("else if (meta == 13)"));

        assertTrue("TileRunicMatrixRenderer should always render the shell, while keeping active overlays and crafting halo conditional",
                matrixRenderer.contains("if (tile == null)")
                        && matrixRenderer.contains("renderInfusionMatrix(tile, x, y, z, partialTicks);")
                        && matrixRenderer.contains("if (tile.getWorld() != null) {")
                        && matrixRenderer.contains("renderCubeCluster(tile, ticks, instability, startUp);")
                        && matrixRenderer.contains("if (tile.active) {")
                        && matrixRenderer.contains("if (tile.crafting) {"));
        assertFalse("TileRunicMatrixRenderer should no longer skip all rendering when the matrix is idle or when TEISR uses a worldless tile",
                matrixRenderer.contains("tile.getWorld() == null || (!tile.active && !tile.crafting && tile.startUp <= 0.0F)"));

        assertTrue("TileInfusionPillarRenderer should keep the dedicated pillar model path available without a non-null world guard",
                pillarRenderer.contains("if (tile == null)")
                        && pillarRenderer.contains("MODEL.render();")
                        && !pillarRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileNodeStabilizerRenderer should keep the original lock/piston/bubble path available for worldless TEISR inventory rendering",
                 stabilizerRenderer.contains("if (tile == null)")
                         && stabilizerRenderer.contains("int lock = resolveLock(tile);")
                         && stabilizerRenderer.contains("model.renderLock(MODEL_SCALE);")
                         && stabilizerRenderer.contains("tile.count / 100.0D")
                         && stabilizerRenderer.contains("if (tile.count > 0)")
                        && !stabilizerRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileNodeConverterRenderer should keep the original lock-plus-piston path available without a hard world guard so the converter item can render through TEISR",
                 converterRenderer.contains("if (tile == null)")
                         && converterRenderer.contains("bindTexture(BASE_TEXTURE);")
                         && converterRenderer.contains("model.renderLock(MODEL_SCALE);")
                        && converterRenderer.contains("model.renderPiston(MODEL_SCALE);")
                        && !converterRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileFluxScrubberRenderer should restore the reference cap-plus-tip model path and stay worldless-safe for inventory rendering",
                fluxRenderer.contains("if (tile == null)")
                        && fluxRenderer.contains("model.renderCap(MODEL_SCALE);")
                        && fluxRenderer.contains("model.renderTip(MODEL_SCALE);")
                        && !fluxRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("The stone-device TEISR item-model stub must stay builtin/entity for metas 9, 10, 11, and 14, while the matrix and focal manipulator item models should switch to donor-style baked geometry with block display transforms and TC4 model-texture UVs",
                tesrItemModel.contains("\"parent\": \"builtin/entity\"")
                        && matrixItemModel.contains("\"rotation\": [30, 225, 0]")
                        && matrixItemModel.contains("\"from\": [0, 0, 0]")
                        && matrixItemModel.contains("\"to\": [7, 7, 7]")
                        && matrixItemModel.contains("\"from\": [9, 9, 9]")
                        && focalItemModel.contains("\"surface\": \"thaumcraft:models/wandtable_inventory\"")
                        && focalItemModel.contains("\"up\": { \"uv\": [2, 0, 4, 4]")
                        && focalItemModel.contains("\"from\": [0, 8, 0]")
                        && focalItemModel.contains("\"to\": [16, 16, 16]")
                        && focalItemModel.contains("\"thirdperson_righthand\"")
                        && focalItemModel.contains("[75, 45, 0]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
