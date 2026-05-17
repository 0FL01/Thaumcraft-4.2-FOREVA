package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemElementalHoeStaticGuardTest {

    @Test
    public void elementalHoeKeepsTillingBonemealAndSaplingGrowthContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/equipment/ItemElementalHoe.java");
        String plantSource = readFile("src/main/java/thaumcraft/common/blocks/BlockCustomPlant.java");

        assertTrue("ItemElementalHoe must keep rarity, enchantability and thaumium repair contracts",
                source.contains("return 5;")
                        && source.contains("return EnumRarity.RARE;")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && source.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemElementalHoe must keep 3x3 tilling and sparkle sweep contract",
                source.contains("for (int xx = -1; xx <= 1; xx++)")
                        && source.contains("for (int zz = -1; zz <= 1; zz++)")
                        && source.contains("super.onItemUse(player, world, target, hand, facing, hitX, hitY, hitZ)")
                        && source.contains("Thaumcraft.proxy.blockSparkle(world, target.getX(), target.getY(), target.getZ(), 8401408, 2);"));
        assertTrue("ItemElementalHoe must keep bonemeal and custom-sapling growth contracts",
                source.contains("applyBonemealAtLoc(world, player, pos)")
                        && source.contains("((BlockCustomPlant) block).growGreatTree(world, pos, world.rand);")
                        && source.contains("((BlockCustomPlant) block).growSilverTree(world, pos, world.rand);")
                        && source.contains("stack.damageItem(5, player);")
                        && source.contains("stack.damageItem(25, player);")
                        && source.contains("TCSounds.WAND")
                        && source.contains("return did ? EnumActionResult.SUCCESS : EnumActionResult.PASS;"));
        assertTrue("BlockCustomPlant tree-grow helpers must remain callable from ItemElementalHoe",
                plantSource.contains("public void growGreatTree(World world, BlockPos pos, Random rand)")
                        && plantSource.contains("public void growSilverTree(World world, BlockPos pos, Random rand)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
