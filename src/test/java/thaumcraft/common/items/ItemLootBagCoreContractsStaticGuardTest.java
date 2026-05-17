package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemLootBagCoreContractsStaticGuardTest {

    @Test
    public void lootBagKeepsReferenceSubtypeRarityAndConsumptionContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemLootBag.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemLootBag must keep subtype/rarity split contracts",
                source.contains("META_COMMON = 0")
                        && source.contains("META_UNCOMMON = 1")
                        && source.contains("META_RARE = 2")
                        && source.contains("return EnumRarity.UNCOMMON;")
                        && source.contains("return EnumRarity.RARE;")
                        && source.contains("return EnumRarity.COMMON;"));
        assertTrue("ItemLootBag must keep loot-roll count and generateLoot dispatch contracts",
                source.contains("int rolls = 8 + world.rand.nextInt(5);")
                        && source.contains("Utils.generateLoot(stack.getItemDamage(), world.rand)"));
        assertTrue("ItemLootBag must keep post-use stack consumption contract outside server-only branch",
                source.contains("if (!world.isRemote) {")
                        && source.contains("world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.COINS, SoundCategory.PLAYERS, 0.75F, 1.0F);")
                        && source.contains("stack.shrink(1);")
                        && source.indexOf("stack.shrink(1);") > source.indexOf("if (!world.isRemote) {"));
        assertTrue("Loot bag localization keys must exist in en_us.lang",
                lang.contains("item.thaumcraft.loot_bag.0.name=Common Treasure")
                        && lang.contains("item.thaumcraft.loot_bag.1.name=Uncommon Treasure")
                        && lang.contains("item.thaumcraft.loot_bag.2.name=Rare Treasure")
                        && lang.contains("tc.lootbag=Click to open or keep to trade"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
