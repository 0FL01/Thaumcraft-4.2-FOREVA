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
    public void stoneDeviceInfusionFamilyShouldUseTesrWorldPathAndBuiltinEntityMatrixItem() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemStoneDeviceRenderer.java");
        String matrixRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileRunicMatrixRenderer.java");
        String pillarRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileInfusionPillarRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockstonedevice_tesr.json");

        assertTrue("BlockStoneDevice should route the infusion matrix and pillar halves through TESR-only world rendering instead of the baked arcane-stone cube placeholder",
                block.contains("return meta == 2 || meta == 3 || meta == 4 ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;"));

        assertTrue("ClientProxy should keep ordinary blockstate item variants but override the infusion matrix item onto a builtin/entity TEISR model and install the dedicated item renderer",
                clientProxy.contains("Item stoneDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockStoneDevice);")
                        && clientProxy.contains("for (int meta = 0; meta <= 14; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(stoneDeviceItem, 2, \"blockstonedevice_tesr\");")
                        && clientProxy.contains("stoneDeviceItem.setTileEntityItemStackRenderer(new ItemStoneDeviceRenderer());"));

        assertTrue("ItemStoneDeviceRenderer should delegate the matrix item to TileRunicMatrixRenderer with the original inventory translation",
                itemRenderer.contains("new TileRunicMatrixRenderer()")
                        && itemRenderer.contains("if (stack.getMetadata() != 2)")
                        && itemRenderer.contains("TileInfusionMatrix matrix = new TileInfusionMatrix();")
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

        assertTrue("The stone-device TEISR item-model stub must stay builtin/entity so Forge dispatches the custom renderer",
                itemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
