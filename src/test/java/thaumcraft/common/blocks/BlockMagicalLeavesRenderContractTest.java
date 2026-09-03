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

        assertTrue("BlockMagicalLeaves must follow vanilla fancy/fast layer parity",
                blockSource.contains("public BlockRenderLayer getRenderLayer()")
                        && blockSource.contains("BlockRenderLayer.CUTOUT_MIPPED")
                        && blockSource.contains("BlockRenderLayer.SOLID")
                        && blockSource.contains("this.leavesFancy"));
        assertTrue("BlockMagicalLeaves must keep the TC4 leaf lighting and full-cube geometry contracts",
                blockSource.contains("this.setLightOpacity(1);")
                        && blockSource.contains("return !this.leavesFancy;")
                        && blockSource.contains("public boolean isFullCube(IBlockState state) {\n        return true;"));
        assertTrue("BlockMagicalLeaves must expose its graphics mode for the client resync ticker",
                blockSource.contains("public boolean isFancy()"));
        assertTrue("BlockMagicalLeavesItem metadata contract must mark player-placed leaves",
                itemSource.contains("return damage | 4;"));
        assertTrue("Client model routing must ignore runtime-only decay properties",
                clientSource.contains("ignore(BlockMagicalLeaves.DECAYABLE, BlockMagicalLeaves.CHECK_DECAY)"));
        assertTrue("ClientProxy must register a biome foliage tint handler for greatwood leaves",
                clientSource.contains("BiomeColorHelper.getFoliageColorAtPos(world, pos)")
                        && clientSource.contains("ColorizerFoliage.getFoliageColorBasic()")
                        && clientSource.contains("ConfigBlocks.blockMagicalLeaves")
                        && clientSource.contains("ConfigBlocks.blockMagicalLeavesItem"));
        assertTrue("ClientProxy must seed magical leaves graphics level (vanilla only syncs its own)",
                clientSource.contains("blockMagicalLeaves.setGraphicsLevel"));
        String tickSource = readFile("src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java");
        assertTrue("Client tick must resync magical leaves when fancy/fast changes",
                tickSource.contains("syncLeafGraphics")
                        && tickSource.contains("gameSettings.fancyGraphics")
                        && tickSource.contains("loadRenderers()"));
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
