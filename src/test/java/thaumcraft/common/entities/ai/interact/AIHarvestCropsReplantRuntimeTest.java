package thaumcraft.common.entities.ai.interact;

import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AIHarvestCropsReplantRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void beetrootHarvestRejectsNearbyWheatSeeds() {
        assertTrue(AIHarvestCrops.isSeedForHarvestedCrop(null, POS,
                Blocks.BEETROOTS.getStateFromMeta(3), new ItemStack(Items.BEETROOT_SEEDS)));
        assertFalse(AIHarvestCrops.isSeedForHarvestedCrop(null, POS,
                Blocks.BEETROOTS.getStateFromMeta(3), new ItemStack(Items.WHEAT_SEEDS)));
    }

    @Test
    public void vanillaPlantingItemsMatchOnlyTheirOwnCropBlocks() {
        assertTrue(AIHarvestCrops.isSeedForHarvestedCrop(null, POS,
                Blocks.WHEAT.getStateFromMeta(7), new ItemStack(Items.WHEAT_SEEDS)));
        assertTrue(AIHarvestCrops.isSeedForHarvestedCrop(null, POS,
                Blocks.CARROTS.getStateFromMeta(7), new ItemStack(Items.CARROT)));
        assertTrue(AIHarvestCrops.isSeedForHarvestedCrop(null, POS,
                Blocks.POTATOES.getStateFromMeta(7), new ItemStack(Items.POTATO)));
        assertFalse(AIHarvestCrops.isSeedForHarvestedCrop(null, POS,
                Blocks.CARROTS.getStateFromMeta(7), ItemStack.EMPTY));
    }
}
