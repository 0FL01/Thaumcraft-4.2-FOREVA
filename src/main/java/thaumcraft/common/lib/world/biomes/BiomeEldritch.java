package thaumcraft.common.lib.world.biomes;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import java.util.Random;

public class BiomeEldritch extends Biome {

    public BiomeEldritch() {
        super(new BiomeProperties("Eldritch")
            .setRainDisabled()
            .setBaseHeight(0.0f)
            .setHeightVariation(0.0f));
        this.setRegistryName("thaumcraft", "biome_eldritch");
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityInhabitedZombie.class, 1, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEldritchGuardian.class, 1, 1, 1));
        this.topBlock = Blocks.DOUBLE_STONE_SLAB.getDefaultState();
        this.fillerBlock = Blocks.DOUBLE_STONE_SLAB.getDefaultState();
    }

    @Override
    public int getSkyColorByTemp(float temp) {
        return 0x000000;
    }

    @Override
    public void decorate(World world, Random rand, BlockPos pos) {
        // No decoration in Eldritch biome
    }

    @Override
    public TempCategory getTempCategory() {
        return TempCategory.MEDIUM;
    }
}
