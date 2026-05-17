package thaumcraft.common.items.baubles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemRunicBaublesRarityStaticGuardTest {

    @Test
    public void runicBaublesKeepReferenceRarityContracts() throws IOException {
        String amulet = readFile("src/main/java/thaumcraft/common/items/baubles/ItemAmuletRunic.java");
        String ring = readFile("src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java");
        String girdle = readFile("src/main/java/thaumcraft/common/items/baubles/ItemGirdleRunic.java");

        assertTrue("ItemAmuletRunic must keep rare rarity baseline",
                amulet.contains("public EnumRarity getRarity(ItemStack stack)")
                        && amulet.contains("return EnumRarity.RARE;"));
        assertTrue("ItemGirdleRunic must keep rare rarity baseline",
                girdle.contains("public EnumRarity getRarity(ItemStack stack)")
                        && girdle.contains("return EnumRarity.RARE;"));
        assertTrue("ItemRingRunic must keep uncommon-lesser / rare-others rarity split",
                ring.contains("return stack.getItemDamage() == META_LESSER ? EnumRarity.UNCOMMON : EnumRarity.RARE;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
