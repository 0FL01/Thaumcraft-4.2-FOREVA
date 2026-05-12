package thaumcraft.common.lib.world.biomes;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityWisp;

public class BiomeEerie extends Biome {

    public BiomeEerie() {
        super(new BiomeProperties("Eerie")
            .setRainDisabled()
            .setBaseHeight(0.0f)
            .setHeightVariation(0.0f)
            .setTemperature(0.5f)
            .setRainfall(0.5f));
        this.setRegistryName("thaumcraft", "biome_eerie");
        this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityBat.class, 3, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWitch.class, 8, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEnderman.class, 4, 1, 1));
        if (Config.spawnAngryZombie) {
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityBrainyZombie.class, 32, 1, 1));
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityGiantBrainyZombie.class, 8, 1, 1));
        }
        if (Config.spawnWisp) {
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWisp.class, 3, 1, 1));
        }
        if (Config.spawnElder) {
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEldritchGuardian.class, 1, 1, 1));
        }
        this.decorator.flowersPerChunk = 2;
        this.decorator.treesPerChunk = 1;
        this.decorator.grassPerChunk = 2;
    }

    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        return 0x404840;
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        return 0x405040;
    }

    @Override
    public int getSkyColorByTemp(float temp) {
        return 0x222299;
    }

    @Override
    public int getWaterColorMultiplier() {
        return 0x2E52DF;
    }

    @Override
    public TempCategory getTempCategory() {
        return TempCategory.MEDIUM;
    }
}
