package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EtherealBloomParityStaticGuardTest {

    @Test
    public void tileAndRendererKeepEtherealBloomGrowthAndCleanseContracts() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileEtherealBloom.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEtherealBloomRenderer.java");
        String customPlant = read("src/main/java/thaumcraft/common/blocks/BlockCustomPlant.java");

        assertTrue("TileEtherealBloom should keep growth counters and tick update surface",
                tile.contains("public int counter = 0;")
                        && tile.contains("public int growthCounter = 0;")
                        && tile.contains("public void update()"));
        assertTrue("TileEtherealBloom should keep periodic biome cleanse behavior",
                tile.contains("this.counter % 20 == 0")
                        && tile.contains("Utils.setBiomeAt(this.world, tx, tz, biome);"));
        assertTrue("TileEtherealBloom should keep roots sound bootstrap on client",
                tile.contains("this.world.isRemote && this.growthCounter == 0")
                        && tile.contains("TCSounds.ROOTS"));

        assertTrue("TileEtherealBloomRenderer should keep layered bloom rendering assets",
                renderer.contains("textures/misc/nodes.png")
                        && renderer.contains("textures/models/crystalcapacitor.png")
                        && renderer.contains("textures/blocks/purifier_leaves.png")
                        && renderer.contains("textures/blocks/purifier_stalk.png"));
        assertTrue("TileEtherealBloomRenderer should keep node pulse + core + leaf/stalk layer flow",
                renderer.contains("renderNodePulse(")
                        && renderer.contains("renderCrystalCore(")
                        && renderer.contains("renderLeafLayers(")
                        && renderer.contains("renderStalkLayers(")
                        && renderer.contains("drawCenteredTexture()"));
        assertTrue("TileEtherealBloomRenderer should bind direct bloom textures instead of missing atlas sprites",
                renderer.contains("bindTexture(LEAF_TEXTURE)")
                        && renderer.contains("bindTexture(STALK_TEXTURE)")
                        && !renderer.contains("getAtlasSprite")
                        && !renderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE"));
        assertTrue("TileEtherealBloomRenderer should match TC4 quad orientation after the 180 degree X flip",
                renderer.contains("buf.pos(-half, half, 0.0D).tex(0.0D, 1.0D)")
                        && renderer.contains("buf.pos(half, -half, 0.0D).tex(1.0D, 0.0D)"));
        assertTrue("TileEtherealBloomRenderer should not draw duplicate coplanar leaf/stalk quads",
                countOccurrences(renderer, ".endVertex();") == 4);
        assertTrue("TileEtherealBloomRenderer should avoid writing bloom planes into the depth buffer",
                countOccurrences(renderer, "GlStateManager.depthMask(false)") >= 3
                        && countOccurrences(renderer, "GlStateManager.depthMask(true)") >= 3
                        && renderer.contains("DestFactor.ONE_MINUS_SRC_ALPHA"));
        assertTrue("BlockCustomPlant should render cross plant models in the cutout layer",
                customPlant.contains("public BlockRenderLayer getRenderLayer()")
                        && customPlant.contains("BlockRenderLayer.CUTOUT"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int countOccurrences(String text, String needle) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
