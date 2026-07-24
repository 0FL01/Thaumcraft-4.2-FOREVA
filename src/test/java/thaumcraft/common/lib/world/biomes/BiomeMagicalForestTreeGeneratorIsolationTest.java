package thaumcraft.common.lib.world.biomes;

import java.util.Random;
import net.minecraft.init.Bootstrap;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.WorldGenBigMagicTree;
import thaumcraft.common.lib.world.WorldGenGreatwoodTrees;
import thaumcraft.common.lib.world.WorldGenSilverwoodTrees;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class BiomeMagicalForestTreeGeneratorIsolationTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
        if (ConfigBlocks.blockMagicalLeaves == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void oakGeneratorsAreIsolatedWithoutChangingSpecialTreeRouting() {
        BiomeMagicalForest biome = new BiomeMagicalForest();

        assertTrue(biome.getRandomTreeFeature(new SequenceRandom(0)) instanceof WorldGenSilverwoodTrees);
        assertTrue(biome.getRandomTreeFeature(new SequenceRandom(1, 0)) instanceof WorldGenGreatwoodTrees);

        SequenceRandom fallback = new SequenceRandom(1, 1, 1, 1);
        WorldGenAbstractTree first = biome.getRandomTreeFeature(fallback);
        WorldGenAbstractTree second = biome.getRandomTreeFeature(fallback);

        assertTrue(first instanceof WorldGenBigMagicTree);
        assertTrue(second instanceof WorldGenBigMagicTree);
        assertNotSame(first, second);
    }

    private static final class SequenceRandom extends Random {
        private final int[] values;
        private int index;

        private SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            return this.values[this.index++];
        }
    }
}
