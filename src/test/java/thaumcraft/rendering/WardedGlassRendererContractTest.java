package thaumcraft.rendering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class WardedGlassRendererContractTest {
    private static final Path PORT_TEXTURES = Paths.get("src/main/resources/assets/thaumcraft/textures/blocks");
    private static final Path TC4_TEXTURES = Paths.get("thaumcraft_src/assets/thaumcraft/textures/blocks");

    @Test
    public void hardenedGlassShouldUseTheTranslucentConnectedBakedModelForBlocksAndItems() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockCosmeticOpaque.java");
        String model = read("src/main/java/thaumcraft/client/renderers/block/WardedGlassBakedModel.java");
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String json = read("src/main/resources/assets/thaumcraft/models/block/blockcosmeticopaque_2.json");

        assertTrue(block.contains("state.getValue(TYPE) == 2")
                && block.contains("layer == BlockRenderLayer.TRANSLUCENT")
                && block.contains("new ExtendedBlockState(this")
                && block.contains("ConnectedTextureUtils.getTextureIndex")
                && block.contains("neighbor.getBlock() == this && neighbor.getValue(TYPE) == 2"));
        assertTrue(model.contains("class WardedGlassBakedModel implements IBakedModel")
                && model.contains("state == null ? 0")
                && model.contains("warded_glass_")
                && model.contains("BlockCosmeticOpaque.GLASS_DOWN")
                && model.contains("BlockCosmeticOpaque.GLASS_EAST")
                && model.contains("if (side == null)"));
        assertTrue(registry.contains("WARDED_GLASS_MODEL")
                && registry.contains("texture <= 47")
                && registry.contains("registerSprite(new ResourceLocation(\"thaumcraft\", \"blocks/warded_glass_\" + texture))")
                && registry.contains("new WardedGlassBakedModel(delegate)"));
        assertTrue(clientProxy.contains("Item cosmeticOpaqueItem = Item.getItemFromBlock(ConfigBlocks.blockCosmeticOpaque)")
                && clientProxy.contains("registerBlockItemModel(cosmeticOpaqueItem, meta, \"type=\" + meta)"));
        assertTrue(json.contains("\"all\": \"thaumcraft:blocks/warded_glass_1\""));
    }

    @Test
    public void allConnectedTextureFramesShouldRemainExactTc4Assets() throws IOException {
        for (int frame = 1; frame <= 47; frame++) {
            String name = "warded_glass_" + frame + ".png";
            assertArrayEquals(name,
                    Files.readAllBytes(TC4_TEXTURES.resolve(name)),
                    Files.readAllBytes(PORT_TEXTURES.resolve(name)));
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
