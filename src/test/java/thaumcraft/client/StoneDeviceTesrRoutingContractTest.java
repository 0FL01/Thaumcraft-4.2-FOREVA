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
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockstonedevice_tesr.json");

        assertTrue("BlockStoneDevice should route the matrix, pillar, node-device, focal-manipulator, and flux-scrubber TESR family through invisible world rendering instead of baked placeholder shells",
                block.contains("return meta == 2 || meta == 3 || meta == 4 || meta == 9 || meta == 10 || meta == 11 || meta == 13 || meta == 14")
                        && block.contains("? EnumBlockRenderType.INVISIBLE")
                        && block.contains(": EnumBlockRenderType.MODEL;"));

        assertTrue("ClientProxy should keep ordinary blockstate item variants but override the matrix and reference TESR-inventory stone-device family onto builtin/entity item routing",
                clientProxy.contains("Item stoneDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockStoneDevice);")
                        && clientProxy.contains("for (int meta = 0; meta <= 14; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 2, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 9, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 10, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 11, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 13, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 14, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("stoneDeviceItem.setTileEntityItemStackRenderer(new ItemStoneDeviceRenderer());"));

        assertTrue("ItemStoneDeviceRenderer should route the matrix and the remaining reference TESR inventory family through their dedicated renderers",
                itemRenderer.contains("new TileRunicMatrixRenderer()")
                        && itemRenderer.contains("TileEntityRendererDispatcher.instance")
                        && itemRenderer.contains("matrixRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("stabilizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("converterRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("focalManipulatorRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("fluxScrubberRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileNodeStabilizerRenderer()")
                        && itemRenderer.contains("new TileNodeConverterRenderer()")
                        && itemRenderer.contains("new TileFocalManipulatorRenderer()")
                        && itemRenderer.contains("new TileFluxScrubberRenderer()")
                        && itemRenderer.contains("if (meta == 2)")
                        && itemRenderer.contains("TileInfusionMatrix matrix = new TileInfusionMatrix();")
                        && itemRenderer.contains("else if (meta == 9 || meta == 10)")
                        && itemRenderer.contains("stabilizer.lock = meta == 9 ? 1 : 2;")
                        && itemRenderer.contains("else if (meta == 11)")
                        && itemRenderer.contains("TileNodeConverter converter = new TileNodeConverter();")
                        && itemRenderer.contains("else if (meta == 13)")
                        && itemRenderer.contains("focalManipulatorRenderer.render(new TileFocalManipulator()")
                        && itemRenderer.contains("else if (meta == 14)")
                        && itemRenderer.contains("TileFluxScrubber scrubber = new TileFluxScrubber();")
                        && itemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);")
                        && itemRenderer.contains("matrixRenderer.render(matrix, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);"));

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
                        && stabilizerRenderer.contains("if (tile.count > 0)")
                        && !stabilizerRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileNodeConverterRenderer should keep the original lock-plus-piston path available without a hard world guard so the converter item can render through TEISR",
                converterRenderer.contains("if (tile == null)")
                        && converterRenderer.contains("model.renderLock(MODEL_SCALE);")
                        && converterRenderer.contains("model.renderPiston(MODEL_SCALE);")
                        && !converterRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileFluxScrubberRenderer should restore the reference cap-plus-tip model path and stay worldless-safe for inventory rendering",
                fluxRenderer.contains("if (tile == null)")
                        && fluxRenderer.contains("model.renderCap(MODEL_SCALE);")
                        && fluxRenderer.contains("model.renderTip(MODEL_SCALE);")
                        && !fluxRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("The stone-device TEISR item-model stub must stay builtin/entity so Forge dispatches the custom renderer",
                itemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
