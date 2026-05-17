package thaumcraft.common.config.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

class ConfigRecipesInfusionEquipmentSlice {

    static void initializeInfusionEquipmentArmorRecipeBaseline() {
        ConfigRecipesInfusionSlice.registerInfusionRecipe("HoverHarness", "HOVERHARNESS", new ItemStack(ConfigItems.itemHoverHarness), 6,
                new AspectList().add(Aspect.FLIGHT, 32).add(Aspect.ENERGY, 32).add(Aspect.MECHANISM, 32).add(Aspect.TRAVEL, 16),
                new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6), new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                new ItemStack(Items.COMPARATOR), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_INGOT));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("HoverGirdle", "HOVERGIRDLE", new ItemStack(ConfigItems.itemGirdleHover), 8,
                new AspectList().add(Aspect.FLIGHT, 16).add(Aspect.ENERGY, 32).add(Aspect.AIR, 32).add(Aspect.TRAVEL, 16),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(Items.FEATHER),
                new ItemStack(Items.GOLD_INGOT), new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(Items.FEATHER), new ItemStack(Items.GOLD_INGOT));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("VisAmulet", "VISAMULET", new ItemStack(ConfigItems.itemAmuletVis, 1, 1), 6,
                new AspectList().add(Aspect.AURA, 24).add(Aspect.ENERGY, 64).add(Aspect.MAGIC, 64).add(Aspect.VOID, 24),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0), new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigBlocks.blockCrystal, 1, 6),
                new ItemStack(ConfigBlocks.blockCrystal, 1, 6), new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigBlocks.blockCrystal, 1, 6),
                new ItemStack(ConfigBlocks.blockCrystal, 1, 6));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicAmulet", "RUNICARMOR", new ItemStack(ConfigItems.itemAmuletRunic, 1, 0), 4,
                new AspectList().add(Aspect.ARMOR, 20).add(Aspect.MAGIC, 35).add(Aspect.ENERGY, 35),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0), new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigItems.itemResource, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemInkwell));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicAmuletEmergency", "RUNICEMERGENCY", new ItemStack(ConfigItems.itemAmuletRunic, 1, 1), 7,
                new AspectList().add(Aspect.ARMOR, 20).add(Aspect.MAGIC, 35).add(Aspect.EARTH, 32).add(Aspect.VOID, 32),
                new ItemStack(ConfigItems.itemAmuletRunic, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(Items.POTIONITEM, 1, 8233), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(ConfigItems.itemShard, 1, 3));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicRing", "RUNICARMOR", new ItemStack(ConfigItems.itemRingRunic, 1, 1), 3,
                new AspectList().add(Aspect.ARMOR, 10).add(Aspect.MAGIC, 25).add(Aspect.ENERGY, 25),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1), new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigItems.itemResource, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemInkwell));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicRingCharged", "RUNICCHARGED", new ItemStack(ConfigItems.itemRingRunic, 1, 2), 6,
                new AspectList().add(Aspect.ARMOR, 16).add(Aspect.MAGIC, 16).add(Aspect.ENERGY, 64),
                new ItemStack(ConfigItems.itemRingRunic, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(Items.POTIONITEM, 1, 8226), new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 1));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicRingHealing", "RUNICHEALING", new ItemStack(ConfigItems.itemRingRunic, 1, 3), 6,
                new AspectList().add(Aspect.ARMOR, 16).add(Aspect.MAGIC, 16).add(Aspect.WATER, 32).add(Aspect.HEAL, 32),
                new ItemStack(ConfigItems.itemRingRunic, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 2), new ItemStack(Items.POTIONITEM, 1, 8257), new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 2));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicGirdle", "RUNICARMOR", new ItemStack(ConfigItems.itemGirdleRunic, 1, 0), 4,
                new AspectList().add(Aspect.ARMOR, 30).add(Aspect.MAGIC, 50).add(Aspect.ENERGY, 50),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigItems.itemResource, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemInkwell));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicGirdleKinetic", "RUNICKINETIC", new ItemStack(ConfigItems.itemGirdleRunic, 1, 1), 7,
                new AspectList().add(Aspect.ARMOR, 33).add(Aspect.MAGIC, 55).add(Aspect.AIR, 64),
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(Items.POTIONITEM, 1, 16428), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 0));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("RunicGirdleKinetic_2", "RUNICKINETIC", new ItemStack(ConfigItems.itemGirdleRunic, 1, 1), 7,
                new AspectList().add(Aspect.ARMOR, 33).add(Aspect.MAGIC, 55).add(Aspect.AIR, 64),
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(Items.POTIONITEM, 1, 24620), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 0));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ElementalAxe", "ELEMENTALAXE", new ItemStack(ConfigItems.itemAxeElemental), 1,
                new AspectList().add(Aspect.WATER, 16).add(Aspect.TREE, 8), new ItemStack(ConfigItems.itemAxeThaumium), new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 2), new ItemStack(Items.DIAMOND), new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ElementalPick", "ELEMENTALPICK", new ItemStack(ConfigItems.itemPickElemental), 1,
                new AspectList().add(Aspect.FIRE, 8).add(Aspect.MINE, 8).add(Aspect.SENSES, 8), new ItemStack(ConfigItems.itemPickThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(Items.DIAMOND),
                new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ElementalSword", "ELEMENTALSWORD", new ItemStack(ConfigItems.itemSwordElemental), 1,
                new AspectList().add(Aspect.AIR, 8).add(Aspect.MOTION, 8).add(Aspect.ENERGY, 8), new ItemStack(ConfigItems.itemSwordThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(Items.DIAMOND),
                new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ElementalShovel", "ELEMENTALSHOVEL", new ItemStack(ConfigItems.itemShovelElemental), 1,
                new AspectList().add(Aspect.EARTH, 16).add(Aspect.CRAFT, 8), new ItemStack(ConfigItems.itemShovelThaumium), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(Items.DIAMOND), new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ElementalHoe", "ELEMENTALHOE", new ItemStack(ConfigItems.itemHoeElemental), 1,
                new AspectList().add(Aspect.HARVEST, 8).add(Aspect.PLANT, 8).add(Aspect.EARTH, 8), new ItemStack(ConfigItems.itemHoeThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 4), new ItemStack(ConfigItems.itemShard, 1, 5), new ItemStack(Items.DIAMOND),
                new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("BootsTraveller", "BOOTSTRAVELLER", new ItemStack(ConfigItems.itemBootsTraveller), 1,
                new AspectList().add(Aspect.FLIGHT, 25).add(Aspect.TRAVEL, 25), new ItemStack(Items.LEATHER_BOOTS), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 7),
                new ItemStack(Items.FEATHER), new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("TravelTrunk", "TRAVELTRUNK", new ItemStack(ConfigItems.itemTrunkSpawner), 3,
                new AspectList().add(Aspect.MOTION, 4).add(Aspect.SOUL, 4).add(Aspect.TRAVEL, 4).add(Aspect.VOID, 16), new ItemStack(ConfigBlocks.blockChestHungry),
                new ItemStack(Items.IRON_INGOT), new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6), new ItemStack(ConfigItems.itemGolemPlacer, 1, 1),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ThaumiumFortressHelm", "ARMORFORTRESS", new ItemStack(ConfigItems.itemHelmFortress), 3,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 16).add(Aspect.MAGIC, 16), new ItemStack(ConfigItems.itemHelmThaumium),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ThaumiumFortressChest", "ARMORFORTRESS", new ItemStack(ConfigItems.itemChestFortress), 3,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 24).add(Aspect.MAGIC, 16), new ItemStack(ConfigItems.itemChestThaumium),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.LEATHER));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ThaumiumFortressLegs", "ARMORFORTRESS", new ItemStack(ConfigItems.itemLegsFortress), 3,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 20).add(Aspect.MAGIC, 16), new ItemStack(ConfigItems.itemLegsThaumium),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.LEATHER));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("VoidRobeHelm", "ARMORVOIDFORTRESS", new ItemStack(ConfigItems.itemHelmVoidRobe), 6,
                new AspectList().add(Aspect.METAL, 16).add(Aspect.SENSES, 16).add(Aspect.ARMOR, 16).add(Aspect.CLOTH, 16).add(Aspect.MAGIC, 16)
                        .add(Aspect.ELDRITCH, 16).add(Aspect.VOID, 16),
                new ItemStack(ConfigItems.itemHelmVoid), new ItemStack(ConfigItems.itemGoggles), new ItemStack(ConfigItems.itemResource, 1, 7),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 14), new ItemStack(ConfigItems.itemResource, 1, 7),
                new ItemStack(ConfigItems.itemResource, 1, 7));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("VoidRobeChest", "ARMORVOIDFORTRESS", new ItemStack(ConfigItems.itemChestVoidRobe), 6,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 24).add(Aspect.CLOTH, 24).add(Aspect.MAGIC, 16).add(Aspect.ELDRITCH, 16)
                        .add(Aspect.VOID, 24),
                new ItemStack(ConfigItems.itemChestVoid), new ItemStack(ConfigItems.itemChestRobe), new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 14), new ItemStack(ConfigItems.itemResource, 1, 7),
                new ItemStack(Items.LEATHER));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("VoidRobeLegs", "ARMORVOIDFORTRESS", new ItemStack(ConfigItems.itemLegsVoidRobe), 6,
                new AspectList().add(Aspect.METAL, 20).add(Aspect.ARMOR, 20).add(Aspect.CLOTH, 20).add(Aspect.MAGIC, 16).add(Aspect.ELDRITCH, 16)
                        .add(Aspect.VOID, 20),
                new ItemStack(ConfigItems.itemLegsVoid), new ItemStack(ConfigItems.itemLegsRobe), new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 14), new ItemStack(ConfigItems.itemResource, 1, 7),
                new ItemStack(Items.LEATHER));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("HelmGoggles", "HELMGOGGLES", new Object[]{"goggles", new NBTTagByte((byte) 1)}, 5,
                new AspectList().add(Aspect.SENSES, 32).add(Aspect.AURA, 16).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.SLIME_BALL),
                new ItemStack(ConfigItems.itemGoggles, 1, OreDictionary.WILDCARD_VALUE));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("MaskGrinningDevil", "MASKGRINNINGDEVIL", new Object[]{"mask", new NBTTagInt(0)}, 8,
                new AspectList().add(Aspect.MIND, 64).add(Aspect.HEAL, 64).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE, 1, 0), new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.LEATHER), new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2), new ItemStack(ConfigItems.itemZombieBrain),
                new ItemStack(Items.IRON_INGOT));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("MaskAngryGhost", "MASKANGRYGHOST", new Object[]{"mask", new NBTTagInt(1)}, 8,
                new AspectList().add(Aspect.ENTROPY, 64).add(Aspect.DEATH, 64).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.LEATHER), new ItemStack(Items.POISONOUS_POTATO), new ItemStack(Items.SKULL, 1, 1), new ItemStack(Items.IRON_INGOT));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("MaskSippingFiend", "MASKSIPPINGFIEND", new Object[]{"mask", new NBTTagInt(2)}, 8,
                new AspectList().add(Aspect.UNDEAD, 64).add(Aspect.LIFE, 64).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.DYE, 1, 1), new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.LEATHER), new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.IRON_INGOT));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("SanityCheck", "SANITYCHECK", new ItemStack(ConfigItems.itemSanityChecker), 4,
                new AspectList().add(Aspect.MIND, 24).add(Aspect.SENSES, 24).add(Aspect.ELDRITCH, 8), new ItemStack(ConfigItems.itemThaumometer),
                new ItemStack(ConfigItems.itemResource, 1, 10), new ItemStack(ConfigItems.itemZombieBrain), new ItemStack(Items.DIAMOND));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("SinStone", "SINSTONE", new ItemStack(ConfigItems.itemCompassStone), 5,
                new AspectList().add(Aspect.SENSES, 8).add(Aspect.DARKNESS, 8).add(Aspect.ELDRITCH, 8).add(Aspect.AURA, 8), new ItemStack(Items.FLINT),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 4), new ItemStack(ConfigItems.itemResource, 1, 9),
                new ItemStack(ConfigItems.itemShard, 1, 5));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("PrimalCrusher", "PRIMALCRUSHER", new ItemStack(ConfigItems.itemPrimalCrusher), 6,
                new AspectList().add(Aspect.MINE, 24).add(Aspect.TOOL, 24).add(Aspect.ENTROPY, 16).add(Aspect.VOID, 16)
                        .add(Aspect.WEAPON, 16).add(Aspect.ELDRITCH, 16).add(Aspect.GREED, 16),
                new ItemStack(ConfigItems.itemEldritchObject, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigItems.itemPickVoid, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(ConfigItems.itemShovelVoid, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigItems.itemPickElemental, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(ConfigItems.itemShovelElemental, 1, OreDictionary.WILDCARD_VALUE));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("EldritchEye", "OCULUS", new ItemStack(ConfigItems.itemEldritchObject), 5,
                new AspectList().add(Aspect.ELDRITCH, 64).add(Aspect.VOID, 16).add(Aspect.DARKNESS, 16).add(Aspect.TRAVEL, 16),
                new ItemStack(Items.ENDER_EYE), new ItemStack(ConfigItems.itemResource, 1, 17), new ItemStack(Items.GOLD_INGOT));
    }
}
