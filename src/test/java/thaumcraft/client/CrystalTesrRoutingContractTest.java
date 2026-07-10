package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CrystalTesrRoutingContractTest {

    @Test
    public void crystalFamilyShouldUseTesrWorldRoutingAndBuiltinEntityItemRendering() throws IOException {
        String blockCrystal = read("src/main/java/thaumcraft/common/blocks/BlockCrystal.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String modelRegistry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemCrystalRenderer.java");
        String tileRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileCrystalRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockcrystal_tesr.json");

        assertTrue("BlockCrystal should hide the baked world model so crystals render through their tile renderers like the original TESR-only path",
                blockCrystal.contains("return EnumBlockRenderType.INVISIBLE;"));

        assertTrue("ClientProxy should route every crystal metadata onto the builtin/entity TEISR model and install the crystal item renderer",
                clientProxy.contains("Item crystalItem = Item.getItemFromBlock(ConfigBlocks.blockCrystal);")
                        && clientProxy.contains("crystalItem.setTileEntityItemStackRenderer(new ItemCrystalRenderer());")
                        && clientProxy.contains("for (int meta = 0; meta <= 7; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(crystalItem, meta, \"blockcrystal_tesr\");"));

        assertTrue("ClientModelRegistry should wrap the crystal builtin/entity model so the item renderer can branch by transform type",
                modelRegistry.contains("BLOCKCRYSTAL_MODEL")
                        && modelRegistry.contains("new ModelResourceLocation(\"thaumcraft:blockcrystal_tesr\", \"inventory\")")
                        && modelRegistry.contains("new CrystalPerspectiveModel(model)"));

        assertTrue("ItemCrystalRenderer should route every context through the original TC4 inventory TESR path",
                itemRenderer.contains("new TileCrystalRenderer()")
                        && itemRenderer.contains("new TileEldritchCrystalRenderer()")
                        && itemRenderer.contains("CURRENT_TRANSFORM")
                        && itemRenderer.contains("setTransformType(ItemCameraTransforms.TransformType transformType)")
                        && itemRenderer.contains("meta <= 6")
                        && itemRenderer.contains("meta == 7")
                        && itemRenderer.contains("crystalRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("crystalRenderer.render(crystal, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);")
                        && itemRenderer.contains("TileEntityRendererDispatcher.instance")
                        && itemRenderer.contains("BlockPos.ORIGIN")
                        && itemRenderer.contains("GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);")
                        && itemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);")
                        && itemRenderer.contains("private static final class InventoryTileCrystal extends TileCrystal")
                        && itemRenderer.contains("public int getBlockMetadata()"));

        assertFalse("TileCrystalRenderer must not bypass world orientation for item contexts",
                tileRenderer.contains("renderItemCluster") || tileRenderer.contains("drawItemCrystal"));

        assertTrue("The crystal TEISR item-model must keep every display matrix neutral because the TC4 renderer owns its pose",
                itemModel.contains("\"parent\": \"builtin/entity\"")
                        && itemModel.contains("\"gui\"")
                        && itemModel.contains("\"ground\"")
                        && itemModel.contains("\"fixed\"")
                        && itemModel.contains("\"thirdperson_righthand\"")
                        && itemModel.contains("\"thirdperson_lefthand\"")
                        && itemModel.contains("\"firstperson_righthand\"")
                        && itemModel.contains("\"firstperson_lefthand\"")
                        && itemModel.contains("\"rotation\": [0, 0, 0]")
                        && itemModel.contains("\"translation\": [0, 0, 0]")
                        && itemModel.contains("\"scale\": [1, 1, 1]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
