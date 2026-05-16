package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemThaumiumToolsStaticGuardTest {

    @Test
    public void thaumiumToolsKeepRarityRepairAndToolClassContracts() throws IOException {
        String sword = readFile("src/main/java/thaumcraft/common/items/equipment/ItemThaumiumSword.java");
        String axe = readFile("src/main/java/thaumcraft/common/items/equipment/ItemThaumiumAxe.java");
        String pickaxe = readFile("src/main/java/thaumcraft/common/items/equipment/ItemThaumiumPickaxe.java");
        String shovel = readFile("src/main/java/thaumcraft/common/items/equipment/ItemThaumiumShovel.java");
        String hoe = readFile("src/main/java/thaumcraft/common/items/equipment/ItemThaumiumHoe.java");

        assertTrue("ItemThaumiumSword must keep uncommon rarity and thaumium repair contract",
                sword.contains("return EnumRarity.UNCOMMON;")
                        && sword.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && sword.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemThaumiumAxe must keep axe toolclass, uncommon rarity and thaumium repair contract",
                axe.contains("ImmutableSet.of(\"axe\")")
                        && axe.contains("return EnumRarity.UNCOMMON;")
                        && axe.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && axe.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemThaumiumPickaxe must keep pickaxe toolclass, uncommon rarity and thaumium repair contract",
                pickaxe.contains("ImmutableSet.of(\"pickaxe\")")
                        && pickaxe.contains("return EnumRarity.UNCOMMON;")
                        && pickaxe.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && pickaxe.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemThaumiumShovel must keep shovel toolclass, uncommon rarity and thaumium repair contract",
                shovel.contains("ImmutableSet.of(\"shovel\")")
                        && shovel.contains("return EnumRarity.UNCOMMON;")
                        && shovel.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && shovel.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemThaumiumHoe must keep enchantability, uncommon rarity and thaumium repair contract",
                hoe.contains("return 5;")
                        && hoe.contains("return EnumRarity.UNCOMMON;")
                        && hoe.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && hoe.contains("repair.isItemEqual(thaumiumIngot)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
