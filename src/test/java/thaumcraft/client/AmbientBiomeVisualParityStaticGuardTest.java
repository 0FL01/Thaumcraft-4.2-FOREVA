package thaumcraft.client;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.world.biomes.BiomeEerie;
import thaumcraft.common.lib.world.biomes.BiomeMagicalForest;
import thaumcraft.common.lib.world.biomes.BiomeTaint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AmbientBiomeVisualParityStaticGuardTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void nodeBiomeGrassAndFoliageColorsMatchTC4() {
        BiomeTaint taint = new BiomeTaint();
        BiomeEerie eerie = new BiomeEerie();
        BiomeMagicalForest magical = new BiomeMagicalForest();

        assertEquals(0x7C6D87, taint.getFoliageColorAtPos(BlockPos.ORIGIN));
        assertEquals(0x6D4189, taint.getGrassColorAtPos(BlockPos.ORIGIN));
        assertEquals(0x405340, eerie.getFoliageColorAtPos(BlockPos.ORIGIN));
        assertEquals(0x404840, eerie.getGrassColorAtPos(BlockPos.ORIGIN));

        boolean blueBiome = Config.blueBiome;
        try {
            Config.blueBiome = true;
            assertEquals(0x77CCEE, magical.getFoliageColorAtPos(BlockPos.ORIGIN));
            assertEquals(0x66AACC, magical.getGrassColorAtPos(BlockPos.ORIGIN));
            Config.blueBiome = false;
            assertEquals(0x66FFC5, magical.getFoliageColorAtPos(BlockPos.ORIGIN));
            assertEquals(0x55FF81, magical.getGrassColorAtPos(BlockPos.ORIGIN));
        } finally {
            Config.blueBiome = blueBiome;
        }
    }

    @Test
    public void nodeBiomeColorsRemainBiomeDrivenAndMatchTC4() throws IOException {
        String taintBiome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java");
        String eerieBiome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeEerie.java");
        String magicalBiome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java");
        String renderHandler = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String tickHandler = read("src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java");

        assertTrue(taintBiome.contains("public int getSkyColorByTemp(float temp)"));
        assertTrue(taintBiome.contains("return 0x7C44FF;"));
        assertTrue(eerieBiome.contains("public int getSkyColorByTemp(float temp)"));
        assertTrue(eerieBiome.contains("return 0x222299;"));
        assertTrue(magicalBiome.contains("public int getFoliageColorAtPos(BlockPos pos)"));
        assertTrue(magicalBiome.contains("public int getGrassColorAtPos(BlockPos pos)"));

        assertTrue(renderHandler.contains("event.getType() == RenderGameOverlayEvent.ElementType.PORTAL"));
        assertTrue(renderHandler.contains("renderVignette(targetBrightness"));
        assertTrue(tickHandler.contains("if (warpVignette > 0)"));
        assertFalse(renderHandler.contains("biomeTaint"));
        assertFalse(renderHandler.contains("biomeEerie"));
        assertFalse(tickHandler.contains("biomeTaint"));
        assertFalse(tickHandler.contains("biomeEerie"));
    }

    @Test
    public void darkTaintedAndPureNodesMutateBiomesLikeTC4() throws IOException {
        String node = read("src/main/java/thaumcraft/common/tiles/TileNode.java");
        String utils = read("src/main/java/thaumcraft/common/lib/utils/Utils.java");

        assertTrue(node.contains("changed = handleTaintNode(changed);"));
        assertTrue(node.contains("changed = handleDarkNode(changed);"));
        assertTrue(node.contains("changed = handlePureNode(changed);"));

        assertTrue(node.contains("private boolean handleTaintNode(boolean changed)"));
        assertTrue(node.contains("this.getNodeType() == NodeType.TAINTED && this.count % 50 == 0"));
        assertTrue(node.contains("this.world.rand.nextInt(8) - this.world.rand.nextInt(8)"));
        assertTrue(node.contains("Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeTaint);"));
        assertTrue(node.contains("Config.hardNode && this.world.rand.nextBoolean()"));
        assertTrue(node.contains("BlockTaintFibres.spreadFibres(this.world, new BlockPos(x, y, z));"));
        assertTrue(node.contains("this.world.rand.nextInt(500) != 0"));
        assertTrue(node.contains("this.setNodeType(NodeType.TAINTED);"));

        assertTrue(node.contains("private boolean handleDarkNode(boolean changed)"));
        assertTrue(node.contains("this.getNodeType() != NodeType.DARK || this.count % 50 != 0"));
        assertTrue(node.contains("this.world.rand.nextInt(12) - this.world.rand.nextInt(12)"));
        assertTrue(node.contains("Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeEerie);"));
        assertTrue(node.contains("new EntityGiantBrainyZombie(this.world)"));
        assertTrue(node.contains("this.world.getClosestPlayer("));
        assertTrue(node.contains("new AxisAlignedBB(this.pos).grow(10.0D, 6.0D, 10.0D)"));
        assertTrue(node.contains("this.world.playEvent(2004, this.pos, 0);"));
        assertTrue(node.contains("entity.spawnExplosionParticle();"));

        assertTrue(node.contains("private boolean handlePureNode(boolean changed)"));
        assertTrue(node.contains("this.getNodeType() != NodeType.PURE || this.count % 50 != 0"));
        assertTrue(node.contains("this.world.rand.nextInt(8) - this.world.rand.nextInt(8)"));
        assertTrue(node.contains("isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint)"));
        assertTrue(node.contains("this.world.getBlockState(this.pos).getBlock() == ConfigBlocks.blockMagicalLog"));
        assertTrue(node.contains("Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeMagicalForest);"));

        assertTrue(utils.contains("new PacketBiomeChange(x, z, (short) biomeId)"));
    }

    @Test
    public void airyFireAndEerieAmbientSparksMatchTC4Colors() throws IOException {
        String airy = read("src/main/java/thaumcraft/common/blocks/BlockAiry.java");

        assertTrue(airy.contains("public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)"));
        assertTrue(airy.contains("if (meta != 10 && meta != 11)"));
        assertTrue(airy.contains("float h = rand.nextFloat() * 0.33F;"));
        assertTrue(airy.contains("0.1515F + h / 2.0F"));
        assertTrue(airy.contains("0.33F + h"));
        assertTrue(airy.contains("0.65F + rand.nextFloat() * 0.1F, 1.0F, 1.0F, 0.8F"));
        assertTrue(airy.contains("0.3F - rand.nextFloat() * 0.1F, 0.0F"));
        assertTrue(airy.contains("0.5F + rand.nextFloat() * 0.2F, 1.0F"));
        assertTrue(airy.contains("Thaumcraft.proxy.spark("));
        assertTrue(airy.contains("if (rand.nextInt(50) == 0)"));
        assertTrue(airy.contains("TCSounds.JACOBS, SoundCategory.MASTER"));
        assertTrue(airy.contains("0.5F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F, false"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
