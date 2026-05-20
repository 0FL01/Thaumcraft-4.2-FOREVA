package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceTesrRoutingContractTest {

    @Test
    public void woodenAndMetalDeviceRoutingShouldUseTesrWorldPathsAndBuiltinEntityItemsForTileOrientedMetas() throws IOException {
        String woodenBlock = read("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");
        String metalBlock = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String woodenItemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemWoodenDeviceRenderer.java");
        String metalItemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemMetalDeviceRenderer.java");
        String thaumatoriumRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java");
        String woodenTesrModel = read("src/main/resources/assets/thaumcraft/models/item/blockwoodendevice_tesr.json");
        String metalTesrModel = read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_tesr.json");
        String thaumatoriumBaseModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_10.json");
        String thaumatoriumTopModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_11.json");

        assertTrue("BlockWoodenDevice should route bellows, bore base, bore, and banner through TESR-only world rendering while keeping sensor/plates/planks on baked models",
                woodenBlock.contains("return meta == 0 || meta == 4 || meta == 5 || meta == 8")
                        && woodenBlock.contains("? EnumBlockRenderType.INVISIBLE")
                        && woodenBlock.contains(": EnumBlockRenderType.MODEL;"));

        assertTrue("BlockWoodenDevice should restore reference-shaped bellows, bore, banner, and pressure-plate bounds/collision behavior",
                woodenBlock.contains("private static final AxisAlignedBB BELLOWS_AABB")
                        && woodenBlock.contains("private static final AxisAlignedBB PRESSURE_PLATE_AABB")
                        && woodenBlock.contains("private static final AxisAlignedBB PRESSED_PLATE_AABB")
                        && woodenBlock.contains("private static final AxisAlignedBB BANNER_STANDING_AABB")
                        && woodenBlock.contains("return BELLOWS_AABB;")
                        && woodenBlock.contains("return getBoreBounds(((TileArcaneBore) tile).orientation);")
                        && woodenBlock.contains("return getBannerBounds((TileBanner) tile);")
                        && woodenBlock.contains("if (meta == 2 || meta == 3 || meta == 8) {")
                        && woodenBlock.contains("return;"));

        assertTrue("BlockMetalDevice should route alembic, charger, lamp family, vis relay, and thaumatorium halves through TESR-only world rendering",
                metalBlock.contains("return meta == 1 || meta == 2 || meta == 7 || meta == 8 || meta == 10 || meta == 11 || meta == 13 || meta == 14")
                        && metalBlock.contains("? EnumBlockRenderType.INVISIBLE")
                        && metalBlock.contains(": EnumBlockRenderType.MODEL;"));

        assertTrue("ClientProxy should assign block-metal variants and override tile-oriented item metas onto builtin/entity TEISR models",
                clientProxy.contains("Item metalDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockMetalDevice);")
                        && clientProxy.contains("for (int meta = 0; meta <= 14; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 0, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 4, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 5, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 8, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 1, \"blockmetaldevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 2, \"blockmetaldevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 10, \"blockmetaldevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 11, \"blockmetaldevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 14, \"blockmetaldevice_tesr\");")
                        && clientProxy.contains("woodenDeviceItem.setTileEntityItemStackRenderer(new ItemWoodenDeviceRenderer());")
                        && clientProxy.contains("metalDeviceItem.setTileEntityItemStackRenderer(new ItemMetalDeviceRenderer());"));

        assertTrue("Wooden and metal device item renderers should keep the original inventory transforms and delegate to dedicated tile renderers",
                woodenItemRenderer.contains("new TileBellowsRenderer()")
                        && woodenItemRenderer.contains("new TileArcaneBoreBaseRenderer()")
                        && woodenItemRenderer.contains("new TileArcaneBoreRenderer()")
                        && woodenItemRenderer.contains("new TileBannerRenderer()")
                        && woodenItemRenderer.contains("GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);")
                        && woodenItemRenderer.contains("GlStateManager.translate(-0.5F, -0.75F, -0.5F);")
                        && woodenItemRenderer.contains("banner.setFacing((byte) 8);")
                        && metalItemRenderer.contains("new TileAlembicRenderer()")
                        && metalItemRenderer.contains("new TileMagicWorkbenchChargerRenderer()")
                        && metalItemRenderer.contains("new TileThaumatoriumRenderer()")
                        && metalItemRenderer.contains("new TileVisRelayRenderer()")
                        && metalItemRenderer.contains("GlStateManager.translate(-0.5F, 0.0F, -0.5F);")
                        && metalItemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);")
                        && metalItemRenderer.contains("if (meta == 10 || meta == 11)")
                        && metalItemRenderer.contains("GlStateManager.scale(0.65F, 0.65F, 0.65F);")
                        && metalItemRenderer.contains("GlStateManager.scale(1.5F, 1.5F, 1.5F);")
                        && metalItemRenderer.contains("GlStateManager.translate(-0.5F, -0.25F, -0.5F);"));

        assertTrue("Thaumatorium renderer should keep the shell path available for worldless TEISR renders while gating only the output-item layer on world presence",
                thaumatoriumRenderer.contains("if (tile == null)")
                        && thaumatoriumRenderer.contains("renderThaumatoriumModel(tile, x, y, z);")
                        && thaumatoriumRenderer.contains("if (tile.getWorld() == null) {")
                        && thaumatoriumRenderer.contains("renderOutputItem(tile, x, y, z, partialTicks);"));
        assertFalse("Thaumatorium renderer should no longer skip the shell outright when item TEISR uses a worldless tile",
                thaumatoriumRenderer.contains("if (tile == null || tile.getWorld() == null) {"));

        assertTrue("Builtin item model stubs must exist so Forge routes the targeted metadata to TEISR",
                woodenTesrModel.contains("\"parent\": \"builtin/entity\"")
                        && metalTesrModel.contains("\"parent\": \"builtin/entity\""));

        assertTrue("Thaumatorium fallback models should at least retain the reference alchemyblock texture instead of the old metalbase placeholder",
                thaumatoriumBaseModel.contains("\"thaumcraft:blocks/alchemyblock\"")
                        && thaumatoriumTopModel.contains("\"thaumcraft:blocks/alchemyblock\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
