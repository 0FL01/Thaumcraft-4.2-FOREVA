package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockMagicalLeavesRenderContractTest {

    @Test
    public void magicalLeavesRenderLayerAndMetadataContractsStayWired() throws IOException {
        String blockSource = readFile("src/main/java/thaumcraft/common/blocks/BlockMagicalLeaves.java");
        String itemSource = readFile("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockMagicalLeavesItem.java");
        String clientSource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("BlockMagicalLeaves must render in CUTOUT_MIPPED layer",
                blockSource.contains("public BlockRenderLayer getRenderLayer()")
                        && blockSource.contains("return BlockRenderLayer.CUTOUT_MIPPED;"));
        assertTrue("BlockMagicalLeavesItem metadata contract must preserve only the type bit",
                itemSource.contains("return damage & 1;"));
        assertTrue("ClientProxy must register a biome foliage tint handler for greatwood leaves",
                clientSource.contains("BiomeColorHelper.getFoliageColorAtPos(world, pos)")
                        && clientSource.contains("ColorizerFoliage.getFoliageColorBasic()")
                        && clientSource.contains("ConfigBlocks.blockMagicalLeaves")
                        && clientSource.contains("ConfigBlocks.blockMagicalLeavesItem"));
    }

    @Test
    public void magicalLeavesModelAssetsExist() {
        assertExists("src/main/resources/assets/thaumcraft/blockstates/blockmagicalleaves.json");
        assertExists("src/main/resources/assets/thaumcraft/models/block/blockmagicalleaves_0.json");
        assertExists("src/main/resources/assets/thaumcraft/models/block/blockmagicalleaves_1.json");
        assertExists("src/main/resources/assets/thaumcraft/textures/blocks/greatwoodleaves.png");
        assertExists("src/main/resources/assets/thaumcraft/textures/blocks/greatwoodleaveslow.png");
        assertExists("src/main/resources/assets/thaumcraft/textures/blocks/silverwoodleaves.png");
        assertExists("src/main/resources/assets/thaumcraft/textures/blocks/silverwoodleaveslow.png");
    }

    private static void assertExists(String relativePath) {
        Path path = Paths.get(relativePath);
        assertTrue("Missing magical leaves render asset: " + relativePath, Files.exists(path));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
