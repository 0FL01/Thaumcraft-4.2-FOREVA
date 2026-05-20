package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class CrystalTesrRoutingContractTest {

    @Test
    public void crystalFamilyShouldUseTesrWorldRoutingAndBuiltinEntityItemRendering() throws IOException {
        String blockCrystal = read("src/main/java/thaumcraft/common/blocks/BlockCrystal.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemCrystalRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockcrystal_tesr.json");

        assertTrue("BlockCrystal should hide the baked world model so crystals render through their tile renderers like the original TESR-only path",
                blockCrystal.contains("return EnumBlockRenderType.INVISIBLE;"));

        assertTrue("ClientProxy should route every crystal metadata onto the builtin/entity TEISR model and install the crystal item renderer",
                clientProxy.contains("Item crystalItem = Item.getItemFromBlock(ConfigBlocks.blockCrystal);")
                        && clientProxy.contains("crystalItem.setTileEntityItemStackRenderer(new ItemCrystalRenderer());")
                        && clientProxy.contains("for (int meta = 0; meta <= 7; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(crystalItem, meta, \"blockcrystal_tesr\");"));

        assertTrue("ItemCrystalRenderer should preserve the original inventory transforms and delegate regular vs eldritch crystals to their dedicated tile renderers",
                itemRenderer.contains("new TileCrystalRenderer()")
                        && itemRenderer.contains("new TileEldritchCrystalRenderer()")
                        && itemRenderer.contains("meta <= 6")
                        && itemRenderer.contains("meta == 7")
                        && itemRenderer.contains("TileEntityRendererDispatcher.instance")
                        && itemRenderer.contains("BlockPos.ORIGIN")
                        && itemRenderer.contains("GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);")
                        && itemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);")
                        && itemRenderer.contains("private static final class InventoryTileCrystal extends TileCrystal")
                        && itemRenderer.contains("public int getBlockMetadata()"));

        assertTrue("The crystal TEISR item-model stub must stay builtin/entity so Forge dispatches the inventory renderer",
                itemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
