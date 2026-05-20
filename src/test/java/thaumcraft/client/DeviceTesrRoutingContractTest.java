package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class DeviceTesrRoutingContractTest {

    @Test
    public void woodenAndMetalDeviceRoutingShouldUseTesrWorldPathsAndBuiltinEntityItemsForTileOrientedMetas() throws IOException {
        String woodenBlock = read("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");
        String metalBlock = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String woodenItemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemWoodenDeviceRenderer.java");
        String metalItemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemMetalDeviceRenderer.java");
        String woodenTesrModel = read("src/main/resources/assets/thaumcraft/models/item/blockwoodendevice_tesr.json");
        String metalTesrModel = read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_tesr.json");

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

        assertTrue("BlockMetalDevice should route alembic through TESR-only world rendering",
                metalBlock.contains("return state.getValue(TYPE) == 1 ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;"));

        assertTrue("ClientProxy should assign block-metal variants and override tile-oriented item metas onto builtin/entity TEISR models",
                clientProxy.contains("Item metalDeviceItem = Item.getItemFromBlock(ConfigBlocks.blockMetalDevice);")
                        && clientProxy.contains("for (int meta = 0; meta <= 14; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 0, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 4, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 5, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(woodenDeviceItem, 8, \"blockwoodendevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 1, \"blockmetaldevice_tesr\");")
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
                        && metalItemRenderer.contains("GlStateManager.translate(-0.5F, 0.0F, -0.5F);"));

        assertTrue("Builtin item model stubs must exist so Forge routes the targeted metadata to TEISR",
                woodenTesrModel.contains("\"parent\": \"builtin/entity\"")
                        && metalTesrModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
