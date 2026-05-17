package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemElementalShovelStaticGuardTest {

    @Test
    public void elementalShovelKeepsArchitectPlacementAndBurstMiningContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/equipment/ItemElementalShovel.java");

        assertTrue("ItemElementalShovel must keep IArchitect + rarity/repair contracts",
                source.contains("implements IRepairable, IArchitect")
                        && source.contains("return ImmutableSet.of(\"shovel\")")
                        && source.contains("return EnumRarity.RARE;")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && source.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemElementalShovel must keep placement-copy sweep contracts",
                source.contains("for (int aa = -1; aa <= 1; aa++)")
                        && source.contains("for (int bb = -1; bb <= 1; bb++)")
                        && source.contains("if (world.isRemote) {")
                        && source.contains("return EnumActionResult.PASS;")
                        && source.contains("getPlaneOffset(aa, bb, facing.getIndex(), getOrientation(stack), player)")
                        && source.contains("InventoryUtils.consumeInventoryItem(player, source, meta)")
                        && source.contains("Thaumcraft.proxy.blockSparkle(world, target.getX(), target.getY(), target.getZ(), 8401408, 4);"));
        assertTrue("ItemElementalShovel must keep burst-mining and side-capture contracts",
                source.contains("onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)")
                        && source.contains("this.side = hit.sideHit.getIndex();")
                        && source.contains("onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity)")
                        && source.contains("ForgeHooks.isToolEffective(world, pos, stack)")
                        && source.contains("world.destroyBlock(target, true)")
                        && source.contains("stack.damageItem(1, entity);"));
        assertTrue("ItemElementalShovel must keep architect preview + orientation NBT contracts",
                source.contains("getArchitectBlocks(ItemStack stack, World world, int x, int y, int z, int side, EntityPlayer player)")
                        && source.contains("showAxis(ItemStack stack, World world, EntityPlayer player, int side, IArchitect.EnumAxis axis)")
                        && source.contains("Math.floorMod(orientation, 3)")
                        && source.contains("getTagCompound().setByte(\"or\""));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
