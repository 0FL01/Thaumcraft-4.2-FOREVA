package thaumcraft.common.lib.world.biomes;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintacle;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.WorldGenBigMagicTree;
import java.util.Random;

public class BiomeTaint extends Biome {

    public static WorldGenBlockBlob blobs = null;
    protected WorldGenBigMagicTree bigTree = new WorldGenBigMagicTree(false);

    public BiomeTaint() {
        super(new BiomeProperties("Tainted Land")
            .setRainDisabled()
            .setBaseHeight(0.0f)
            .setHeightVariation(0.0f));
        this.setRegistryName("thaumcraft", "biome_taint");
        this.decorator.flowersPerChunk = 2;
        this.decorator.treesPerChunk = -999;
        this.decorator.grassPerChunk = 2;
        this.decorator.mushroomsPerChunk = -999;
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        if (Config.spawnTaintacle) {
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityTaintacle.class, 1, 1, 1));
        }
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        if (rand.nextInt(8) == 0) {
            return this.bigTree;
        }
        return super.getRandomTreeFeature(rand);
    }

    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        return 0x7C6D87;
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        return 0x6D4189;
    }

    @Override
    public int getSkyColorByTemp(float temp) {
        return 0x7C44FF;
    }

    @Override
    public int getWaterColorMultiplier() {
        return 0xCC1188;
    }

    @Override
    public void decorate(World world, Random rand, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        super.decorate(world, rand, pos);
        this.decorateSpecial(world, rand, x, z);
    }

    private void decorateSpecial(World world, Random rand, int x, int z) {
        // Taint blobs
        if (blobs != null) {
            int count = rand.nextInt(3);
            for (int i = 0; i < count; ++i) {
                int bx = x + rand.nextInt(16) + 8;
                int bz = z + rand.nextInt(16) + 8;
                BlockPos bpos = world.getHeight(new BlockPos(bx, 0, bz));
                blobs.generate(world, rand, bpos);
            }
        }
        // Taint fibres on grass
        IBlockState taintFibres0 = ConfigBlocks.blockTaintFibres.getStateFromMeta(0);
        Block grassBlock = Blocks.GRASS;
        for (int i = 0; i < 10; ++i) {
            int fx = x + rand.nextInt(16);
            int fz = z + rand.nextInt(16);
            BlockPos surface = world.getHeight(new BlockPos(fx, 0, fz)).down();
            IBlockState state = world.getBlockState(surface);
            if (state.getBlock() == grassBlock) {
                world.setBlockState(surface.up(), taintFibres0, 2);
            } else if (state.getBlock().isReplaceable(world, surface)
                    && world.getBlockState(surface.down()).getBlock() == grassBlock) {
                BlockPos fpos = surface;
                world.setBlockState(fpos, taintFibres0, 2);
            }
        }
        // Force taint biome and place fibrous taint
        for (int i = 0; i < 8; ++i) {
            int tx = x + rand.nextInt(16);
            int tz = z + rand.nextInt(16);
            BlockPos tpos = world.getHeight(new BlockPos(tx, 0, tz));
            if (world.getBiome(tpos) != this) {
                Utils.setBiomeAt(world, tx, tz, this);
            }
            if (world.isAirBlock(tpos) && BlockUtils.isAdjacentToSolidBlock(world, tpos)) {
                world.setBlockState(tpos, ConfigBlocks.blockTaintFibres.getStateFromMeta(0), 2);
            }
        }
    }

    @Override
    public TempCategory getTempCategory() {
        return TempCategory.MEDIUM;
    }
}
