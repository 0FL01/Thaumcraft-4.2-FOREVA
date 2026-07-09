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
        String node = read("src/main/java/thaumcraft/common/tiles/TileNode.java");
        String utils = read("src/main/java/thaumcraft/common/lib/utils/Utils.java");
        String packet = read("src/main/java/thaumcraft/common/lib/network/misc/PacketBiomeChange.java");
        String taintFibres = read("src/main/java/thaumcraft/common/blocks/BlockTaintFibres.java");
        String taint = read("src/main/java/thaumcraft/common/blocks/BlockTaint.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEtherealBloomRenderer.java");
        String customPlant = read("src/main/java/thaumcraft/common/blocks/BlockCustomPlant.java");
        String bloomBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blockcustomplant_4.json");
        String bloomItemModel = read("src/main/resources/assets/thaumcraft/models/item/blockcustomplant_item_4.json");

        assertTrue("TileEtherealBloom should keep growth counters and tick update surface",
                tile.contains("public int counter = 0;")
                        && tile.contains("public int growthCounter = 0;")
                        && tile.contains("public void update()"));
        assertTrue("TileEtherealBloom should keep periodic biome cleanse behavior",
                tile.contains("this.counter % 20 == 0")
                        && tile.contains("isBloomTargetBiome(current, currentId)")
                        && tile.contains("getBiomes(null, tx, tz, 1, 1)")
                        && !tile.contains("getBiomesForGeneration")
                        && tile.contains("Utils.setBiomeAt(this.world, tx, tz, biome);"));
        assertTrue("TileEtherealBloom should match TC4 configured ids and 1.12 registered Thaumcraft biome instances",
                tile.contains("biomeId == Config.biomeTaintID")
                        && tile.contains("isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint)")
                        && tile.contains("isSameBiome(biome, ThaumcraftWorldGenerator.biomeEerie)")
                        && tile.contains("isSameBiome(biome, ThaumcraftWorldGenerator.biomeMagicalForest)"));
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
        assertTrue("TileEtherealBloomRenderer should animate the opaque TC4 node strip around the crystal",
                renderer.contains("int frame = tile.counter % 32;")
                        && renderer.contains("float v0 = 6.0F / 32.0F;")
                        && renderer.contains("float v1 = 7.0F / 32.0F;")
                        && renderer.contains("drawTexturedQuad(scale1, 0xFFAADDFF, u0, u1, v0, v1)"));
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
        assertTrue("TileEtherealBloomRenderer should limit disabled culling and depth writes to the node billboard",
                countOccurrences(renderer, "GlStateManager.disableCull()") == 1
                        && countOccurrences(renderer, "GlStateManager.enableCull()") == 1
                        && countOccurrences(renderer, "GlStateManager.depthMask(false)") == 1
                        && countOccurrences(renderer, "GlStateManager.depthMask(true)") == 1
                        && renderer.contains("DestFactor.ONE_MINUS_SRC_ALPHA"));
        assertTrue("Ethereal Bloom should have no static world cross while retaining its purifier seed item and particles",
                bloomBlockModel.contains("\"elements\": []")
                        && bloomBlockModel.contains("\"particle\": \"thaumcraft:blocks/purifier_seed\"")
                        && !bloomBlockModel.contains("block/cross")
                        && bloomItemModel.contains("thaumcraft:blocks/purifier_seed"));
        assertTrue("BlockCustomPlant should render cross plant models in the cutout layer",
                customPlant.contains("public BlockRenderLayer getRenderLayer()")
                        && customPlant.contains("BlockRenderLayer.CUTOUT"));

        assertTrue("TileNode should restore TC4 pure node biome cleansing cadence and radius",
                node.contains("changed = handlePureNode(changed);")
                        && node.contains("private boolean handlePureNode(boolean changed)")
                        && node.contains("ThaumcraftWorldGenerator.getDimBlacklist(dim)")
                        && node.contains("this.getNodeType() != NodeType.PURE || this.count % 50 != 0")
                        && node.contains("this.world.rand.nextInt(8) - this.world.rand.nextInt(8)")
                        && node.contains("ThaumcraftWorldGenerator.getBiomeBlacklist(biomeId)"));
        assertTrue("TileNode pure effect should cleanse taint and spread magical forest from magical log nodes",
                node.contains("Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeMagicalForest)")
                        && node.contains("isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint)")
                        && node.contains("this.world.getBlockState(this.pos).getBlock() == ConfigBlocks.blockMagicalLog"));
        assertTrue("Utils.setBiomeAt should keep TC4 client biome sync behavior",
                utils.contains("new PacketBiomeChange(x, z, (short) biomeId)")
                        && utils.contains("PacketHandler.INSTANCE.sendToAllAround")
                        && utils.contains("chunk.markDirty();")
                        && utils.contains("world.markBlockRangeForRenderUpdate")
                        && utils.contains("new NetworkRegistry.TargetPoint")
                        && utils.contains("world.provider.getDimension()"));
        assertTrue("PacketBiomeChange should serialize x/z/biome and apply biome updates on the client thread",
                packet.contains("private int x;")
                        && packet.contains("private int z;")
                        && packet.contains("private short biome;")
                        && packet.contains("buf.writeShort(this.biome)")
                        && packet.contains("this.biome = buf.readShort()")
                        && packet.contains("Thaumcraft.proxy.scheduleClientTask")
                        && packet.contains("Utils.setBiomeAt(world, this.x, this.z, biome);"));
        assertTrue("Taint blocks should random-tick so biome purification visibly kills taint back like TC4",
                taintFibres.contains("this.setTickRandomly(true);")
                        && taintFibres.contains("public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)")
                        && taintFibres.contains("!isTaintBiome(world, pos)")
                        && taintFibres.contains("world.setBlockToAir(pos)")
                        && taint.contains("this.setTickRandomly(true);")
                        && taint.contains("ConfigBlocks.blockFluxGoo.getStateFromMeta(ConfigBlocks.blockFluxGoo.getQuanta())")
                        && taint.contains("Blocks.DIRT.getDefaultState()"));
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
