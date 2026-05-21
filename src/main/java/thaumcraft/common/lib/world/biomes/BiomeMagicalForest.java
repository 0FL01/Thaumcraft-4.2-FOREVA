package thaumcraft.common.lib.world.biomes;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.lib.world.WorldGenBigMagicTree;
import thaumcraft.common.lib.world.WorldGenGreatwoodTrees;
import thaumcraft.common.lib.world.WorldGenManaPods;
import thaumcraft.common.lib.world.WorldGenSilverwoodTrees;
import java.util.Random;

public class BiomeMagicalForest extends Biome {

    protected WorldGenBigMagicTree bigTree = new WorldGenBigMagicTree(false);
    private static final WorldGenBlockBlob blobs = new WorldGenBlockBlob(Blocks.STONE, 0);

    public BiomeMagicalForest() {
        super(new BiomeProperties("Magical Forest")
            .setBaseHeight(0.2f)
            .setHeightVariation(0.2f)
            .setTemperature(0.6f)
            .setRainfall(0.7f)
            .setWaterColor(0x0077EE));
        this.setRegistryName("thaumcraft", "biome_magical_forest");
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityWolf.class, 2, 1, 3));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityHorse.class, 2, 1, 3));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWitch.class, 3, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEnderman.class, 3, 1, 1));
        if (Config.spawnPech) {
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityPech.class, 10, 1, 2));
        }
        if (Config.spawnWisp) {
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWisp.class, 10, 1, 2));
        }
        this.decorator.flowersPerChunk = 2;
        this.decorator.treesPerChunk = 10;
        this.decorator.grassPerChunk = 12;
        this.decorator.mushroomsPerChunk = 6;
        this.decorator.reedsPerChunk = 6;
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        if (rand.nextInt(28) == 0) {
            return new WorldGenSilverwoodTrees(false, 8, 5);
        }
        if (rand.nextInt(20) == 0) {
            return new WorldGenGreatwoodTrees(false);
        }
        return this.bigTree;
    }

    public WorldGenerator getRandomWorldGenForGrass(Random rand) {
        if (rand.nextInt(4) == 0) {
            return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
        }
        return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }

    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        return Config.blueBiome ? 0x66AACC : 0x55FF11;
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        return Config.blueBiome ? 0x77CCEE : 0x66FF05;
    }

    @Override
    public void decorate(World world, Random rand, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        // Stone blobs
        for (int i = 0; i < 3; ++i) {
            int bx = x + rand.nextInt(16);
            int bz = z + rand.nextInt(16);
            blobs.generate(world, rand, world.getHeight(new BlockPos(bx, 0, bz)));
        }
        // Big mushrooms
        for (int i = 0; i < 16; ++i) {
            int mx = x + rand.nextInt(16);
            int mz = z + rand.nextInt(16);
            BlockPos mpos = world.getHeight(new BlockPos(mx, 0, mz));
            if (rand.nextInt(40) == 0) {
                (new WorldGenBigMushroom()).generate(world, rand, mpos);
            }
        }
        super.decorate(world, rand, pos);
        // Mana pods
        for (int i = 0; i < 10; ++i) {
            int px = x + rand.nextInt(16);
            int pz = z + rand.nextInt(16);
            BlockPos ppos = new BlockPos(px, 64, pz);
            (new WorldGenManaPods()).generate(world, rand, ppos);
        }
        // Shimmerleaf adjacent to wood
        for (int i = 0; i < 8; ++i) {
            int sx = x + rand.nextInt(16);
            int sz = z + rand.nextInt(16);
            BlockPos spos = world.getHeight(new BlockPos(sx, 0, sz));
            if (isBlockAdjacentToWood(world, spos)) {
                world.setBlockState(spos, ConfigBlocks.blockCustomPlant.getStateFromMeta(2), 2);
            }
        }
    }

    private boolean isBlockAdjacentToWood(World world, BlockPos pos) {
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    BlockPos check = pos.add(dx, dy, dz);
                    if (world.isBlockLoaded(check, false) && world.getBlockState(check).getBlock().isWood(world, check)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public TempCategory getTempCategory() {
        return TempCategory.MEDIUM;
    }
}
