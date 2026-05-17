package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemEldritchObjectCoreContractsStaticGuardTest {

    @Test
    public void eldritchObjectKeepsReferenceTooltipRarityAndCrimsonUnlockContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemEldritchObject.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemEldritchObject must keep subtype/rarity split contracts",
                source.contains("META_CRIMSON_RITES = 1")
                        && source.contains("META_ELDRITCH_OBJECT_2 = 2")
                        && source.contains("META_ELDRITCH_OBJECT_3 = 3")
                        && source.contains("META_OB_PLACER = 4")
                        && source.contains("return EnumRarity.UNCOMMON;")
                        && source.contains("return EnumRarity.RARE;")
                        && source.contains("return EnumRarity.EPIC;"));
        assertTrue("ItemEldritchObject must keep crimson-rites unlock on right click",
                source.contains("stack.getItemDamage() == META_CRIMSON_RITES")
                        && source.contains("ResearchManager.addResearch(player, \"CRIMSON\")")
                        && source.contains("TCSounds.LEARN"));
        assertTrue("ItemEldritchObject must keep metadata tooltip branches",
                source.contains("item.ItemEldritchObject.text.1")
                        && source.contains("item.ItemEldritchObject.text.2")
                        && source.contains("item.ItemEldritchObject.text.3")
                        && source.contains("item.ItemEldritchObject.text.4")
                        && source.contains("item.ItemEldritchObject.text.5")
                        && source.contains("item.ItemEldritchObject.text.6")
                        && source.contains("Creative Mode Only"));
        assertTrue("Eldritch object tooltip localization keys must exist in en_us.lang",
                lang.contains("item.ItemEldritchObject.text.1=It seems to be watching you.")
                        && lang.contains("item.ItemEldritchObject.text.2=The book is filled with strange symbols.")
                        && lang.contains("item.ItemEldritchObject.text.3=Click to study it.")
                        && lang.contains("item.ItemEldritchObject.text.4=It seems to be part of something larger.")
                        && lang.contains("item.ItemEldritchObject.text.5=The very fabric of magic seems")
                        && lang.contains("item.ItemEldritchObject.text.6=to buckle each time it pulses."));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
