package thaumcraft.common.entities.ai.interact;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class AIHarvestCropsParityStaticGuardTest {

    @Test
    public void orderUpgradeReplantsOnlyAfterSuccessfulPhysicalHarvest() throws IOException {
        String source = readSource();
        int harvest = source.indexOf("harvested = BlockUtils.harvestBlock");
        int orderUpgrade = source.indexOf("theGolem.getUpgradeAmount(4) > 0", harvest);
        int replant = source.indexOf("this.replant(state);", orderUpgrade);

        assertTrue("Order replanting must remain gated behind a successful Forge-safe block harvest",
                harvest >= 0 && orderUpgrade > harvest && replant > orderUpgrade);
    }

    @Test
    public void orderUpgradeConsumesDroppedPlantingItemsThroughFakePlayer() throws IOException {
        String source = readSource();

        assertTrue(source.contains("EntityUtils.getEntitiesInRange")
                && source.contains("EntityItem.class, 6.0D")
                && source.contains("isSeedForHarvestedCrop(theGolem.world, this.targetPos, harvestedState, stack)"));
        assertTrue("Nearby seeds for a different crop must not replace the harvested crop",
                source.contains("stack.getItem() instanceof IPlantable")
                        && source.contains("((IPlantable) stack.getItem()).getPlant(world, pos)")
                        && source.contains("plantedState.getBlock() == harvestedState.getBlock()"));
        assertTrue(source.contains("held.onItemUse(this.player, theGolem.world")
                && source.contains("finally {")
                && source.contains("this.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);"));
        assertTrue(source.contains("stack.shrink(1)")
                && source.contains("if (stack.isEmpty()) drop.setDead();")
                && source.contains("else drop.setItem(stack);"));
        assertTrue("TC4 harvest golems should retain their 3x3 replant attempt pattern",
                source.contains("private static final int[] REPLANT_X = {0, 0, 1, 1, -1, 0, -1, -1, 1};")
                        && source.contains("private static final int[] REPLANT_Z = {0, 1, 0, 1, 0, -1, -1, 1, -1};"));
    }

    @Test
    public void orderUpgradeKeepsTc4SpecialCropCases() throws IOException {
        String source = readSource();

        assertTrue(source.contains("harvestedState.getBlock() == Blocks.COCOA")
                && source.contains("EnumDyeColor.BROWN.getDyeDamage()")
                && source.contains("harvestedState.getValue(BlockHorizontal.FACING)"));
        assertTrue(source.contains("harvestedState.getBlock() == ConfigBlocks.blockManaPod")
                && source.contains("stack.getItem() == ConfigItems.itemManaBean")
                && source.contains("this.targetPos.up(), EnumFacing.DOWN"));
        assertTrue(source.contains("drop.getAge() >= 2")
                && source.contains("drop.motionY = 0.075D;"));
    }

    private static String readSource() throws IOException {
        return new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/entities/ai/interact/AIHarvestCrops.java")),
                StandardCharsets.UTF_8);
    }
}
