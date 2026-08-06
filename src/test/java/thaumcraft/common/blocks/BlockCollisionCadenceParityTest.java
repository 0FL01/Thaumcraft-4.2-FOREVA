package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockCollisionCadenceParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void manaPodUsesExactBoundsForEveryGrowthStage() {
        BlockManaPod pod = new BlockManaPod();
        double[] minY = {0.75D, 0.625D, 0.5D, 0.375D, 0.3125D, 0.25D, 0.1875D, 0.125D};
        for (int meta = 0; meta < minY.length; meta++) {
            assertEquals(new AxisAlignedBB(0.25D, minY[meta], 0.25D, 0.75D, 1.0D, 0.75D),
                    pod.getBoundingBox(pod.getStateFromMeta(meta), null, BlockPos.ORIGIN));
        }
    }

    @Test
    public void lootKeepsShapedSelectionAndFullGameplayCollision() {
        BlockLoot urn = new BlockLoot(Material.ROCK, 1);
        BlockLoot crate = new BlockLoot(Material.WOOD, 0);

        assertEquals(new AxisAlignedBB(0.125D, 0.0625D, 0.125D, 0.875D, 0.8125D, 0.875D),
                urn.getBoundingBox(urn.getDefaultState(), null, BlockPos.ORIGIN));
        assertEquals(new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D),
                crate.getBoundingBox(crate.getDefaultState(), null, BlockPos.ORIGIN));
        assertEquals(BlockLoot.FULL_BLOCK_AABB,
                urn.getCollisionBoundingBox(urn.getDefaultState(), null, BlockPos.ORIGIN));
        assertEquals(BlockLoot.FULL_BLOCK_AABB,
                crate.getCollisionBoundingBox(crate.getDefaultState(), null, BlockPos.ORIGIN));
    }
}
