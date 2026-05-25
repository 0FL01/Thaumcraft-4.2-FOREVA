package thaumcraft.common.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ConfigAspects {
    private static final String[] DYES = new String[]{
            "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown",
            "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray",
            "dyeGray", "dyePink", "dyeLime", "dyeYellow",
            "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"
    };

    public static void init() {
        registerVanillaBlocks();
        registerVanillaItems();
        registerVanillaUtilityAndMechanismTags();
        registerOreDictionary();
        registerThaumcraftAlchemyBaseline();
        registerEntityAspects();
    }

    private static void registerVanillaBlocks() {
        // Stone and earth
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STONE), new AspectList().add(Aspect.EARTH, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.COBBLESTONE), new AspectList().add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GRASS), new AspectList().add(Aspect.EARTH, 1).add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DIRT), new AspectList().add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SAND), new AspectList().add(Aspect.EARTH, 1).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GRAVEL), new AspectList().add(Aspect.EARTH, 2));

        // Stone variants (metadata variants of stone block in 1.12.2)
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STONE, 1, 1), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STONE, 1, 3), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STONE, 1, 5), new AspectList().add(Aspect.EARTH, 3));

        // Wood and plants
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LOG), new AspectList().add(Aspect.TREE, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LOG2), new AspectList().add(Aspect.TREE, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PLANKS), new AspectList().add(Aspect.TREE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LEAVES), new AspectList().add(Aspect.PLANT, 2).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LEAVES2), new AspectList().add(Aspect.PLANT, 2).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SAPLING), new AspectList().add(Aspect.PLANT, 2).add(Aspect.TREE, 1));

        // Ores
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.IRON_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.METAL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GOLD_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.METAL, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DIAMOND_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 6));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.REDSTONE_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.ENERGY, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.COAL_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.ENERGY, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LAPIS_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.EMERALD_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.QUARTZ_ORE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 3));

        // Special blocks
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.OBSIDIAN), new AspectList().add(Aspect.EARTH, 4).add(Aspect.FIRE, 2).add(Aspect.DARKNESS, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GLOWSTONE), new AspectList().add(Aspect.LIGHT, 4).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.ICE), new AspectList().add(Aspect.WATER, 2).add(Aspect.COLD, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SNOW), new AspectList().add(Aspect.WATER, 1).add(Aspect.COLD, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.CLAY), new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1));

        // Water/Lava
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WATER), new AspectList().add(Aspect.WATER, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.FLOWING_WATER), new AspectList().add(Aspect.WATER, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LAVA), new AspectList().add(Aspect.FIRE, 3).add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.FLOWING_LAVA), new AspectList().add(Aspect.FIRE, 2));

        // Organic
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WOOL), new AspectList().add(Aspect.PLANT, 1).add(Aspect.TOOL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.CACTUS), new AspectList().add(Aspect.PLANT, 2).add(Aspect.WATER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PUMPKIN), new AspectList().add(Aspect.PLANT, 2).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.MELON_BLOCK), new AspectList().add(Aspect.PLANT, 2).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.VINE), new AspectList().add(Aspect.PLANT, 2).add(Aspect.MOTION, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WATERLILY), new AspectList().add(Aspect.PLANT, 2).add(Aspect.WATER, 1));

        // Nether
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.NETHERRACK), new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SOUL_SAND), new AspectList().add(Aspect.EARTH, 1).add(Aspect.SOUL, 2).add(Aspect.DEATH, 1));

        // Tall grass / fern (meta 0=dead shrub is handled by DEADBUSH; tags only for 1=grass, 2=fern)
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.TALLGRASS, 1, 1),
                new int[] { 1, 2 },
                new AspectList().add(Aspect.PLANT, 1).add(Aspect.EARTH, 1));

        // Flowers
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.YELLOW_FLOWER, 1, 0),
                new AspectList().add(Aspect.PLANT, 2).add(Aspect.LIFE, 1));

        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.RED_FLOWER, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.PLANT, 2).add(Aspect.LIFE, 1));

        // Small mushrooms
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.BROWN_MUSHROOM),
                new AspectList().add(Aspect.PLANT, 1).add(Aspect.DARKNESS, 1));
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.RED_MUSHROOM),
                new AspectList().add(Aspect.PLANT, 1).add(Aspect.POISON, 1));

        // Mushroom blocks
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK),
                new AspectList().add(Aspect.PLANT, 3).add(Aspect.DARKNESS, 2));
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.RED_MUSHROOM_BLOCK),
                new AspectList().add(Aspect.PLANT, 3).add(Aspect.POISON, 2));

        // Dead bush
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.DEADBUSH),
                new AspectList().add(Aspect.PLANT, 1).add(Aspect.DEATH, 1));

        // Double plants (sunflower, lilac, tall grass, fern, rose, peony)
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Blocks.DOUBLE_PLANT, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.PLANT, 3).add(Aspect.LIFE, 1));
    }

    private static void registerVanillaItems() {
        // Tools
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.IRON_PICKAXE), new AspectList().add(Aspect.TOOL, 3).add(Aspect.METAL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.IRON_AXE), new AspectList().add(Aspect.TOOL, 3).add(Aspect.METAL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.IRON_SHOVEL), new AspectList().add(Aspect.TOOL, 2).add(Aspect.METAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.IRON_SWORD), new AspectList().add(Aspect.TOOL, 3).add(Aspect.WEAPON, 3).add(Aspect.METAL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.IRON_HOE), new AspectList().add(Aspect.TOOL, 2).add(Aspect.METAL, 2).add(Aspect.HARVEST, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DIAMOND_PICKAXE), new AspectList().add(Aspect.TOOL, 4).add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DIAMOND_SWORD), new AspectList().add(Aspect.TOOL, 4).add(Aspect.WEAPON, 4).add(Aspect.CRYSTAL, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DIAMOND_AXE), new AspectList().add(Aspect.TOOL, 4).add(Aspect.CRYSTAL, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DIAMOND_SHOVEL), new AspectList().add(Aspect.TOOL, 3).add(Aspect.CRYSTAL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DIAMOND_HOE), new AspectList().add(Aspect.TOOL, 3).add(Aspect.CRYSTAL, 3).add(Aspect.HARVEST, 2));

        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLDEN_PICKAXE), new AspectList().add(Aspect.TOOL, 2).add(Aspect.METAL, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLDEN_SWORD), new AspectList().add(Aspect.TOOL, 2).add(Aspect.WEAPON, 2).add(Aspect.METAL, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLDEN_AXE), new AspectList().add(Aspect.TOOL, 2).add(Aspect.METAL, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLDEN_SHOVEL), new AspectList().add(Aspect.TOOL, 1).add(Aspect.METAL, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLDEN_HOE), new AspectList().add(Aspect.TOOL, 1).add(Aspect.METAL, 4).add(Aspect.HARVEST, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STONE_PICKAXE), new AspectList().add(Aspect.TOOL, 2).add(Aspect.EARTH, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STONE_SWORD), new AspectList().add(Aspect.TOOL, 2).add(Aspect.WEAPON, 2).add(Aspect.EARTH, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STONE_AXE), new AspectList().add(Aspect.TOOL, 2).add(Aspect.EARTH, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STONE_SHOVEL), new AspectList().add(Aspect.TOOL, 1).add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STONE_HOE), new AspectList().add(Aspect.TOOL, 1).add(Aspect.EARTH, 2).add(Aspect.HARVEST, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(Items.WOODEN_PICKAXE), new AspectList().add(Aspect.TOOL, 1).add(Aspect.TREE, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.WOODEN_SWORD), new AspectList().add(Aspect.TOOL, 1).add(Aspect.WEAPON, 1).add(Aspect.TREE, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.WOODEN_AXE), new AspectList().add(Aspect.TOOL, 1).add(Aspect.TREE, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.WOODEN_SHOVEL), new AspectList().add(Aspect.TOOL, 1).add(Aspect.TREE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.WOODEN_HOE), new AspectList().add(Aspect.TOOL, 1).add(Aspect.TREE, 2).add(Aspect.HARVEST, 1));

        // Food
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.APPLE), new AspectList().add(Aspect.PLANT, 2).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BREAD), new AspectList().add(Aspect.PLANT, 2).add(Aspect.LIFE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_BEEF), new AspectList().add(Aspect.BEAST, 2).add(Aspect.LIFE, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_PORKCHOP), new AspectList().add(Aspect.BEAST, 2).add(Aspect.LIFE, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_CHICKEN), new AspectList().add(Aspect.BEAST, 2).add(Aspect.LIFE, 2).add(Aspect.FLIGHT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_FISH), new AspectList().add(Aspect.BEAST, 2).add(Aspect.LIFE, 2).add(Aspect.WATER, 1));

        // Materials
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.IRON_INGOT), new AspectList().add(Aspect.METAL, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLD_INGOT), new AspectList().add(Aspect.METAL, 6));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DIAMOND), new AspectList().add(Aspect.CRYSTAL, 8));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.EMERALD), new AspectList().add(Aspect.CRYSTAL, 6).add(Aspect.EXCHANGE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.REDSTONE), new AspectList().add(Aspect.ENERGY, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COAL), new AspectList().add(Aspect.ENERGY, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STICK), new AspectList().add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STRING), new AspectList().add(Aspect.BEAST, 1).add(Aspect.TOOL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.FEATHER), new AspectList().add(Aspect.BEAST, 1).add(Aspect.FLIGHT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.LEATHER), new AspectList().add(Aspect.BEAST, 2).add(Aspect.FLESH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BONE), new AspectList().add(Aspect.UNDEAD, 2).add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GUNPOWDER), new AspectList().add(Aspect.FIRE, 2).add(Aspect.ENERGY, 2).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.ENDER_PEARL), new AspectList().add(Aspect.VOID, 4).add(Aspect.TRAVEL, 2));

        // Mob drops
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.ROTTEN_FLESH), new AspectList().add(Aspect.UNDEAD, 2).add(Aspect.FLESH, 2).add(Aspect.DEATH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SPIDER_EYE), new AspectList().add(Aspect.BEAST, 2).add(Aspect.POISON, 3).add(Aspect.SENSES, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BLAZE_ROD), new AspectList().add(Aspect.FIRE, 4).add(Aspect.ENERGY, 2).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GHAST_TEAR), new AspectList().add(Aspect.SOUL, 4).add(Aspect.FIRE, 2).add(Aspect.SENSES, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.MAGMA_CREAM), new AspectList().add(Aspect.FIRE, 3).add(Aspect.SLIME, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SLIME_BALL), new AspectList().add(Aspect.SLIME, 3).add(Aspect.LIFE, 1));

        // Potions and brewing
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GLASS_BOTTLE), new AspectList().add(Aspect.CRYSTAL, 1).add(Aspect.VOID, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.NETHER_WART), new AspectList().add(Aspect.PLANT, 2).add(Aspect.MAGIC, 2).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLDEN_CARROT), new AspectList().add(Aspect.PLANT, 2).add(Aspect.METAL, 4).add(Aspect.SENSES, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SPECKLED_MELON), new AspectList().add(Aspect.PLANT, 2).add(Aspect.METAL, 4).add(Aspect.HEAL, 2));

        // Misc
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BOOK), new AspectList().add(Aspect.TREE, 2).add(Aspect.MIND, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.PAPER), new AspectList().add(Aspect.TREE, 1).add(Aspect.MIND, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.FLINT), new AspectList().add(Aspect.EARTH, 1).add(Aspect.TOOL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BUCKET), new AspectList().add(Aspect.METAL, 3).add(Aspect.VOID, 1));

        // Fluid bucket item tags (used by placed fluid scan fallback)
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Items.WATER_BUCKET),
                new AspectList().add(Aspect.WATER, 3));
        ThaumcraftApi.registerObjectTag(
                new ItemStack(Items.LAVA_BUCKET),
                new AspectList().add(Aspect.FIRE, 3).add(Aspect.EARTH, 1));
    }

    private static void registerVanillaUtilityAndMechanismTags() {
        // Transport, timing, and redstone-adjacent utility items.
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BOW), new AspectList().add(Aspect.WEAPON, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.CAKE), new AspectList().add(Aspect.CRAFT, 2).add(Aspect.WATER, 2));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.MINECART), new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.TRAVEL, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BOAT), new AspectList().add(Aspect.WATER, 4).add(Aspect.TRAVEL, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.REPEATER), new AspectList().add(Aspect.MECHANISM, 2));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.COMPASS), new AspectList().add(Aspect.SENSES, 2));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.CLOCK), new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4));

        // Mechanism/transport blocks that are central to scan and object-tag parity.
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DISPENSER), new AspectList().add(Aspect.MECHANISM, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.RAIL, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 1).add(Aspect.TRAVEL, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DAYLIGHT_DETECTOR), new AspectList().add(Aspect.SENSES, 4).add(Aspect.MECHANISM, 2).add(Aspect.AIR, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.REDSTONE_TORCH, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.MECHANISM, 1).add(Aspect.ENERGY, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.REDSTONE_LAMP, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.SENSES, 2).merge(Aspect.LIGHT, 3).merge(Aspect.MECHANISM, 3));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.TORCH, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.LIGHT, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.FIRE, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.FIRE, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.CRAFTING_TABLE), new AspectList().add(Aspect.CRAFT, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.ENCHANTING_TABLE), new AspectList().add(Aspect.AURA, 2).add(Aspect.MAGIC, 2).add(Aspect.EXCHANGE, 2));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.ANVIL, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 64).add(Aspect.CRAFT, 2).add(Aspect.TOOL, 2));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.PISTON, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.STICKY_PISTON, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.ENDER_CHEST, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.EXCHANGE, 2).merge(Aspect.TRAVEL, 2).merge(Aspect.VOID, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.HOPPER, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.MECHANISM, 1).merge(Aspect.EXCHANGE, 1).merge(Aspect.VOID, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DROPPER, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.MECHANISM, 1).merge(Aspect.EXCHANGE, 1).merge(Aspect.VOID, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.TRAPPED_CHEST, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.MECHANISM, 1).merge(Aspect.EXCHANGE, 1).merge(Aspect.VOID, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BEACON, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.SENSES, 2).merge(Aspect.LIGHT, 3).merge(Aspect.MECHANISM, 3));
    }

    private static void registerOreDictionary() {
        // Ore dictionary entries
        if (thaumcraft.common.config.Config.foundCopperIngot) {
            ThaumcraftApi.registerObjectTag("ingotCopper", new AspectList().add(Aspect.METAL, 3));
            ThaumcraftApi.registerObjectTag("nuggetCopper", new AspectList().add(Aspect.METAL, 1));
            ThaumcraftApi.registerObjectTag("dustCopper", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1));
        }
        if (thaumcraft.common.config.Config.foundTinIngot) {
            ThaumcraftApi.registerObjectTag("ingotTin", new AspectList().add(Aspect.METAL, 3));
            ThaumcraftApi.registerObjectTag("nuggetTin", new AspectList().add(Aspect.METAL, 1));
            ThaumcraftApi.registerObjectTag("dustTin", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1));
        }
        if (thaumcraft.common.config.Config.foundSilverIngot) {
            ThaumcraftApi.registerObjectTag("ingotSilver", new AspectList().add(Aspect.METAL, 4));
            ThaumcraftApi.registerObjectTag("nuggetSilver", new AspectList().add(Aspect.METAL, 1));
            ThaumcraftApi.registerObjectTag("dustSilver", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1));
        }
        if (thaumcraft.common.config.Config.foundLeadIngot) {
            ThaumcraftApi.registerObjectTag("ingotLead", new AspectList().add(Aspect.METAL, 3));
            ThaumcraftApi.registerObjectTag("nuggetLead", new AspectList().add(Aspect.METAL, 1));
            ThaumcraftApi.registerObjectTag("dustLead", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1));
        }

        if (thaumcraft.common.config.Config.foundCopperOre) {
            ThaumcraftApi.registerObjectTag("oreCopper", new AspectList().add(Aspect.EARTH, 2).add(Aspect.METAL, 3));
            ThaumcraftApi.registerObjectTag(
                    new ItemStack(ConfigItems.itemNugget, 1, 17),
                    new AspectList().add(Aspect.ORDER, 1).add(Aspect.METAL, 5).add(Aspect.EARTH, 1).add(Aspect.EXCHANGE, 2));
        }
        if (thaumcraft.common.config.Config.foundTinOre) {
            ThaumcraftApi.registerObjectTag("oreTin", new AspectList().add(Aspect.EARTH, 2).add(Aspect.METAL, 3));
            ThaumcraftApi.registerObjectTag(
                    new ItemStack(ConfigItems.itemNugget, 1, 18),
                    new AspectList().add(Aspect.ORDER, 1).add(Aspect.METAL, 5).add(Aspect.EARTH, 1).add(Aspect.CRYSTAL, 2));
        }
        if (thaumcraft.common.config.Config.foundSilverOre) {
            ThaumcraftApi.registerObjectTag("oreSilver", new AspectList().add(Aspect.EARTH, 2).add(Aspect.METAL, 4));
            ThaumcraftApi.registerObjectTag(
                    new ItemStack(ConfigItems.itemNugget, 1, 19),
                    new AspectList().add(Aspect.ORDER, 1).add(Aspect.METAL, 5).add(Aspect.EARTH, 1).add(Aspect.GREED, 2));
        }
        if (thaumcraft.common.config.Config.foundLeadOre) {
            ThaumcraftApi.registerObjectTag("oreLead", new AspectList().add(Aspect.EARTH, 2).add(Aspect.METAL, 3));
            ThaumcraftApi.registerObjectTag(
                    new ItemStack(ConfigItems.itemNugget, 1, 20),
                    new AspectList().add(Aspect.ORDER, 1).add(Aspect.METAL, 5).add(Aspect.EARTH, 1).add(Aspect.ORDER, 2));
        }

        ThaumcraftApi.registerObjectTag("stone", new AspectList().add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag("cobblestone", new AspectList().add(Aspect.EARTH, 1).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag("nuggetIron", new AspectList().add(Aspect.METAL, 1));
        ThaumcraftApi.registerObjectTag("oreIron", new AspectList().add(Aspect.EARTH, 1).add(Aspect.METAL, 3));
        ThaumcraftApi.registerObjectTag("dustIron", new AspectList().add(Aspect.METAL, 3).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag("oreGold", new AspectList().add(Aspect.EARTH, 1).add(Aspect.METAL, 2).add(Aspect.GREED, 1));
        ThaumcraftApi.registerObjectTag("dustGold", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.GREED, 1));
        ThaumcraftApi.registerObjectTag("oreLapis", new AspectList().add(Aspect.EARTH, 1).add(Aspect.SENSES, 3));
        ThaumcraftApi.registerObjectTag("oreDiamond", new AspectList().add(Aspect.EARTH, 1).add(Aspect.GREED, 3).add(Aspect.CRYSTAL, 3));
        ThaumcraftApi.registerObjectTag("gemDiamond", new AspectList().add(Aspect.CRYSTAL, 4).add(Aspect.GREED, 4));
        ThaumcraftApi.registerObjectTag("oreRedstone", new AspectList().add(Aspect.EARTH, 1).add(Aspect.ENERGY, 2).add(Aspect.MECHANISM, 2));
        ThaumcraftApi.registerObjectTag("oreEmerald", new AspectList().add(Aspect.EARTH, 1).add(Aspect.GREED, 4).add(Aspect.CRYSTAL, 3));
        ThaumcraftApi.registerObjectTag("gemEmerald", new AspectList().add(Aspect.CRYSTAL, 4).add(Aspect.GREED, 5));
        ThaumcraftApi.registerObjectTag("oreQuartz", new AspectList().add(Aspect.EARTH, 1).add(Aspect.CRYSTAL, 3));
        ThaumcraftApi.registerObjectTag("dustRedstone", new AspectList().add(Aspect.ENERGY, 2).add(Aspect.MECHANISM, 1));
        ThaumcraftApi.registerObjectTag("dustGlowstone", new AspectList().add(Aspect.SENSES, 1).add(Aspect.LIGHT, 2));

        // Generic ore dictionary registrations
        ThaumcraftApi.registerObjectTag("treeSapling", new AspectList().add(Aspect.PLANT, 2).add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag("treeLeaves", new AspectList().add(Aspect.PLANT, 2).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag("logWood", new AspectList().add(Aspect.TREE, 4));
        ThaumcraftApi.registerObjectTag("plankWood", new AspectList().add(Aspect.TREE, 2));
        ThaumcraftApi.registerObjectTag("slabWood", new AspectList().add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag("stairWood", new AspectList().add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag("stickWood", new AspectList().add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag("blockGlass", new AspectList().add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag("paneGlass", new AspectList().add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag("blockWool", new AspectList().add(Aspect.PLANT, 2).add(Aspect.TOOL, 1));

        for (String dye : DYES) {
            ThaumcraftApi.registerObjectTag(dye, new AspectList().add(Aspect.SENSES, 1));
        }
    }

    private static void registerThaumcraftAlchemyBaseline() {
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 0), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.AIR, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 1), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.FIRE, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 2), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.WATER, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 3), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 4), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 5), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.ENTROPY, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 6),
                new AspectList().add(Aspect.AIR, 2).add(Aspect.FIRE, 2).add(Aspect.WATER, 2).add(Aspect.EARTH, 2).add(Aspect.ORDER, 2).add(Aspect.ENTROPY, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 14),
                new AspectList(new ItemStack(ConfigItems.itemShard, 1, 6)).add(Aspect.MAGIC, 2).remove(Aspect.CRYSTAL));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNugget, 1, 5), new AspectList().add(Aspect.METAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNugget, 1, 6), new AspectList().add(Aspect.METAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNugget, 1, 16), new AspectList().add(Aspect.ORDER, 1).add(Aspect.METAL, 6).add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNugget, 1, 31), new AspectList().add(Aspect.ORDER, 1).add(Aspect.METAL, 4).add(Aspect.EARTH, 1).add(Aspect.GREED, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNugget, 1, 21), new AspectList().add(Aspect.ORDER, 1).add(Aspect.METAL, 4).add(Aspect.EARTH, 1).add(Aspect.EXCHANGE, 4).add(Aspect.POISON, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNuggetEdible, 1, 0), new AspectList().add(Aspect.HUNGER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNuggetEdible, 1, 1), new AspectList().add(Aspect.HUNGER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNuggetEdible, 1, 2), new AspectList().add(Aspect.HUNGER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemNuggetEdible, 1, 3), new AspectList().add(Aspect.HUNGER, 1));
        ThaumcraftApi.registerComplexObjectTag(
                new ItemStack(ConfigItems.itemTripleMeatTreat, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.HEAL, 1).remove(Aspect.HUNGER, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 0), new AspectList().add(Aspect.EARTH, 1).add(Aspect.METAL, 2).add(Aspect.EXCHANGE, 2).add(Aspect.POISON, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 1), new AspectList().add(Aspect.EARTH, 1).add(Aspect.AIR, 3).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 2), new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 3).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 3), new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 3).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 4), new AspectList().add(Aspect.EARTH, 1).add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 5), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 3).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 6), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ENTROPY, 3).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 7), new AspectList().add(Aspect.EARTH, 1).add(Aspect.TRAP, 3).add(Aspect.CRYSTAL, 2));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTaint, 1, 0), new AspectList().add(Aspect.TREE, 1).add(Aspect.TAINT, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTaint, 1, 1), new AspectList().add(Aspect.EARTH, 1).add(Aspect.TAINT, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTaintFibres, 1, 0), new AspectList().add(Aspect.LIFE, 1).add(Aspect.TAINT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTaintFibres, 1, 1), new AspectList().add(Aspect.PLANT, 1).add(Aspect.TAINT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTaintFibres, 1, 2), new AspectList().add(Aspect.PLANT, 1).add(Aspect.TAINT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTaintFibres, 1, 3), new AspectList().add(Aspect.BEAST, 1).add(Aspect.PLANT, 1).add(Aspect.TAINT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTaintFibres, 1, 4), new AspectList().add(Aspect.BEAST, 1).add(Aspect.PLANT, 1).add(Aspect.TAINT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid), new AspectList().add(Aspect.EARTH, 4).add(Aspect.DARKNESS, 2).add(Aspect.ELDRITCH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0), new AspectList().add(Aspect.TREE, 3).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1), new AspectList().add(Aspect.TREE, 3).add(Aspect.MAGIC, 1).add(Aspect.ORDER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockMagicalLeaves, 1, 0), new AspectList().add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockMagicalLeaves, 1, 1), new AspectList().add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), new AspectList().add(Aspect.EARTH, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), new AspectList().add(Aspect.EARTH, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockMetalDevice), new AspectList().add(Aspect.METAL, 4).add(Aspect.CRAFT, 4).add(Aspect.MAGIC, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCandle), new AspectList().add(Aspect.LIGHT, 2).add(Aspect.FLESH, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockAiry, 1, 2), new AspectList().add(Aspect.LIGHT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockAiry, 1, 3), new AspectList().add(Aspect.LIGHT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockArcaneFurnace, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MAGIC, 8).add(Aspect.WATER, 8).add(Aspect.CRAFT, 8));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomPlant, 1, 0), new AspectList().add(Aspect.PLANT, 2).add(Aspect.TREE, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomPlant, 1, 1), new AspectList().add(Aspect.PLANT, 2).add(Aspect.TREE, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2), new AspectList().add(Aspect.PLANT, 2).add(Aspect.EXCHANGE, 2).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomPlant, 1, 3), new AspectList().add(Aspect.PLANT, 2).add(Aspect.FIRE, 2).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomPlant, 1, 5), new AspectList().add(Aspect.PLANT, 2).add(Aspect.POISON, 1).add(Aspect.MAGIC, 2));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemEssence, 1, 0), new AspectList().add(Aspect.VOID, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemEssence, 1, 1), new AspectList());
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemWispEssence, 1, 0), new AspectList().add(Aspect.AURA, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemCrystalEssence, 1, 0), new AspectList());
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemThaumonomicon, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.TREE, 2).add(Aspect.MIND, 4).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 3), new AspectList().add(Aspect.METAL, 3).add(Aspect.POISON, 1).add(Aspect.EXCHANGE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 6), new AspectList().add(Aspect.TRAP, 2).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 9), new AspectList().add(Aspect.MIND, 8));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 11), new AspectList().add(Aspect.TAINT, 3).add(Aspect.SLIME, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 12), new AspectList().add(Aspect.TAINT, 2).add(Aspect.GREED, 1).add(Aspect.HUNGER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 18), new AspectList().add(Aspect.GREED, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemZombieBrain), new AspectList().add(Aspect.FLESH, 2).add(Aspect.MIND, 4).add(Aspect.UNDEAD, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLootBag, 1, 0), new AspectList().add(Aspect.GREED, 8));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLootBag, 1, 1), new AspectList().add(Aspect.GREED, 16));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLootBag, 1, 2), new AspectList().add(Aspect.GREED, 32));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTable), new AspectList().add(Aspect.TREE, 4).add(Aspect.CRAFT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemInkwell), new AspectList().add(Aspect.WATER, 1).add(Aspect.DARKNESS, 1).add(Aspect.TOOL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemThaumometer), new AspectList().add(Aspect.SENSES, 3).add(Aspect.METAL, 2).add(Aspect.CRYSTAL, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.METAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.GREED, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.MAN, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBaubleBlanks, 1, 3), new AspectList().add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4), new AspectList().add(Aspect.METAL, 8).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5), new AspectList().add(Aspect.FLESH, 4).add(Aspect.LIGHT, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 11), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 12), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(ConfigItems.itemPrimalArrow), new AspectList().add(Aspect.WEAPON, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(ConfigItems.itemGoggles, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.SENSES, 4));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 1), new AspectList().add(Aspect.SENSES, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.focusPech),
                new AspectList().add(Aspect.MAGIC, 5).add(Aspect.POISON, 5).add(Aspect.ENTROPY, 5).add(Aspect.ELDRITCH, 5).add(Aspect.WEAPON, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemHelmetCultistPlate, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemCultistPlate, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLegsCultistPlate, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemHelmetCultistRobe, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 3).add(Aspect.CLOTH, 2).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemCultistRobe, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 3).add(Aspect.CLOTH, 2).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLegsCultistRobe, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 3).add(Aspect.CLOTH, 2).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemHelmetCultistLeader, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemCultistLeader, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLegsCultistLeader, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemCultistBoots, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.METAL, 4).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 8),
                new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.TREE, 2).add(Aspect.CLOTH, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemEldritchObject, 1, 0),
                new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.AURA, 3).add(Aspect.MAGIC, 3).add(Aspect.SENSES, 3).add(Aspect.SOUL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemEldritchObject, 1, 1),
                new AspectList().add(Aspect.MIND, 5).add(Aspect.MAGIC, 3).add(Aspect.ELDRITCH, 3).add(Aspect.SOUL, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemEldritchObject, 1, 2),
                new AspectList().add(Aspect.TRAP, 4).add(Aspect.MIND, 4).add(Aspect.MECHANISM, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemEldritchObject, 1, 3),
                new AspectList().add(Aspect.AIR, 16).add(Aspect.EARTH, 16).add(Aspect.FIRE, 16).add(Aspect.WATER, 16).add(Aspect.ORDER, 16).add(Aspect.ENTROPY, 16));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockEldritch, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.VOID, 8).add(Aspect.ELDRITCH, 8).add(Aspect.SENSES, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockEldritchPortal),
                new AspectList().add(Aspect.VOID, 8).add(Aspect.ELDRITCH, 8).add(Aspect.TRAVEL, 8));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockEldritch, 1, 3),
                new AspectList().add(Aspect.VOID, 4).add(Aspect.ELDRITCH, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockEldritch, 1, 4),
                new AspectList().add(Aspect.LIGHT, 1).add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockEldritch, 1, 5),
                new AspectList().add(Aspect.MIND, 2).add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockEldritch, 1, 6),
                new AspectList().add(Aspect.METAL, 2).add(Aspect.MECHANISM, 2).add(Aspect.ELDRITCH, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemHelmThaumium), new AspectList().add(Aspect.METAL, 10).add(Aspect.ARMOR, 6).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemChestThaumium), new AspectList().add(Aspect.METAL, 14).add(Aspect.ARMOR, 8).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLegsThaumium), new AspectList().add(Aspect.METAL, 12).add(Aspect.ARMOR, 7).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBootsThaumium), new AspectList().add(Aspect.METAL, 8).add(Aspect.ARMOR, 5).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemSwordThaumium), new AspectList().add(Aspect.METAL, 8).add(Aspect.WEAPON, 5).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemPickThaumium), new AspectList().add(Aspect.METAL, 8).add(Aspect.TOOL, 5).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemAxeThaumium), new AspectList().add(Aspect.METAL, 8).add(Aspect.TOOL, 5).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShovelThaumium), new AspectList().add(Aspect.METAL, 6).add(Aspect.TOOL, 4).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemHoeThaumium), new AspectList().add(Aspect.METAL, 6).add(Aspect.TOOL, 4).add(Aspect.MAGIC, 2));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemHelmVoid), new AspectList().add(Aspect.METAL, 10).add(Aspect.ARMOR, 6).add(Aspect.VOID, 3).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemChestVoid), new AspectList().add(Aspect.METAL, 14).add(Aspect.ARMOR, 8).add(Aspect.VOID, 4).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemLegsVoid), new AspectList().add(Aspect.METAL, 12).add(Aspect.ARMOR, 7).add(Aspect.VOID, 3).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBootsVoid), new AspectList().add(Aspect.METAL, 8).add(Aspect.ARMOR, 5).add(Aspect.VOID, 2).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemSwordVoid), new AspectList().add(Aspect.METAL, 8).add(Aspect.WEAPON, 5).add(Aspect.VOID, 2).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemPickVoid), new AspectList().add(Aspect.METAL, 8).add(Aspect.TOOL, 5).add(Aspect.VOID, 2).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemAxeVoid), new AspectList().add(Aspect.METAL, 8).add(Aspect.TOOL, 5).add(Aspect.VOID, 2).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShovelVoid), new AspectList().add(Aspect.METAL, 6).add(Aspect.TOOL, 4).add(Aspect.VOID, 1).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemHoeVoid), new AspectList().add(Aspect.METAL, 6).add(Aspect.TOOL, 4).add(Aspect.VOID, 1).add(Aspect.MAGIC, 2));
    }

    private static void registerEntityAspects() {
        registerEntityTag("minecraft:zombie",
                new AspectList().add(Aspect.UNDEAD, 2).add(Aspect.MAN, 1).add(Aspect.EARTH, 1));
        registerEntityTag("minecraft:giant",
                new AspectList().add(Aspect.UNDEAD, 4).add(Aspect.MAN, 3).add(Aspect.EARTH, 3));
        registerEntityTag("minecraft:skeleton",
                new AspectList().add(Aspect.UNDEAD, 3).add(Aspect.MAN, 1).add(Aspect.EARTH, 1));
        registerEntityTag("minecraft:wither_skeleton",
                new AspectList().add(Aspect.UNDEAD, 4).add(Aspect.MAN, 1).add(Aspect.FIRE, 2));
        registerEntityTag("minecraft:creeper",
                new AspectList().add(Aspect.PLANT, 2).add(Aspect.FIRE, 2));
        registerEntityTag("minecraft:creeper",
                new AspectList().add(Aspect.PLANT, 3).add(Aspect.FIRE, 3).add(Aspect.ENERGY, 3),
                new ThaumcraftApi.EntityTagsNBT("powered", (byte)1));
        registerEntityTag("minecraft:horse",
                new AspectList().add(Aspect.BEAST, 4).add(Aspect.EARTH, 1).add(Aspect.AIR, 1));
        registerEntityTag("minecraft:pig",
                new AspectList().add(Aspect.BEAST, 2).add(Aspect.EARTH, 2));
        registerEntityTag("minecraft:xp_orb",
                new AspectList().add(Aspect.MIND, 5));
        registerEntityTag("minecraft:sheep",
                new AspectList().add(Aspect.BEAST, 2).add(Aspect.EARTH, 2));
        registerEntityTag("minecraft:cow",
                new AspectList().add(Aspect.BEAST, 3).add(Aspect.EARTH, 3));
        registerEntityTag("minecraft:mooshroom",
                new AspectList().add(Aspect.BEAST, 3).add(Aspect.PLANT, 1).add(Aspect.EARTH, 2));
        registerEntityTag("minecraft:snowman",
                new AspectList().add(Aspect.COLD, 3).add(Aspect.WATER, 1));
        registerEntityTag("minecraft:ocelot",
                new AspectList().add(Aspect.BEAST, 3).add(Aspect.ENTROPY, 3));
        registerEntityTag("minecraft:chicken",
                new AspectList().add(Aspect.BEAST, 2).add(Aspect.FLIGHT, 2).add(Aspect.AIR, 1));
        registerEntityTag("minecraft:squid",
                new AspectList().add(Aspect.BEAST, 2).add(Aspect.WATER, 2));
        registerEntityTag("minecraft:wolf",
                new AspectList().add(Aspect.BEAST, 3).add(Aspect.EARTH, 3));
        registerEntityTag("minecraft:bat",
                new AspectList().add(Aspect.BEAST, 1).add(Aspect.FLIGHT, 1).add(Aspect.AIR, 1));
        registerEntityTag("minecraft:boat",
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.WATER, 2));
        registerEntityTag("minecraft:spider",
                new AspectList().add(Aspect.BEAST, 3).add(Aspect.ENTROPY, 2));
        registerEntityTag("minecraft:slime",
                new AspectList().add(Aspect.SLIME, 2).add(Aspect.WATER, 2));
        registerEntityTag("minecraft:ghast",
                new AspectList().add(Aspect.UNDEAD, 3).add(Aspect.FIRE, 2));
        registerEntityTag("minecraft:zombie_pigman",
                new AspectList().add(Aspect.UNDEAD, 4).add(Aspect.FIRE, 2));
        registerEntityTag("minecraft:enderman",
                new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.TRAVEL, 2).add(Aspect.AIR, 2));
        registerEntityTag("minecraft:cave_spider",
                new AspectList().add(Aspect.BEAST, 2).add(Aspect.POISON, 2).add(Aspect.EARTH, 1));
        registerEntityTag("minecraft:silverfish",
                new AspectList().add(Aspect.BEAST, 1).add(Aspect.EARTH, 1));
        registerEntityTag("minecraft:blaze",
                new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.FIRE, 1));
        registerEntityTag("minecraft:magma_cube",
                new AspectList().add(Aspect.SLIME, 3).add(Aspect.FIRE, 2));
        registerEntityTag("minecraft:ender_dragon",
                new AspectList().add(Aspect.ELDRITCH, 20).add(Aspect.BEAST, 20).add(Aspect.ENTROPY, 20));
        registerEntityTag("minecraft:wither",
                new AspectList().add(Aspect.UNDEAD, 20).add(Aspect.ENTROPY, 20).add(Aspect.FIRE, 15));
        registerEntityTag("minecraft:witch",
                new AspectList().add(Aspect.MAN, 3).add(Aspect.MAGIC, 2).add(Aspect.FIRE, 1));
        registerEntityTag("minecraft:villager",
                new AspectList().add(Aspect.MAN, 3).add(Aspect.AIR, 2));
        registerEntityTag("minecraft:villager_golem",
                new AspectList().add(Aspect.METAL, 4).add(Aspect.EARTH, 3));
        registerEntityTag("minecraft:minecart",
                new AspectList().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 2));
        registerEntityTag("minecraft:chest_minecart",
                new AspectList().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.VOID, 1));
        registerEntityTag("minecraft:furnace_minecart",
                new AspectList().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.FIRE, 1));
        registerEntityTag("minecraft:tnt_minecart",
                new AspectList().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.FIRE, 1));
        registerEntityTag("minecraft:hopper_minecart",
                new AspectList().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.EXCHANGE, 1));
        registerEntityTag("minecraft:spawner_minecart",
                new AspectList().add(Aspect.MECHANISM, 3).add(Aspect.AIR, 1).add(Aspect.MAGIC, 1));
        registerEntityTag("minecraft:ender_crystal",
                new AspectList().add(Aspect.ELDRITCH, 3).add(Aspect.MAGIC, 3).add(Aspect.HEAL, 3));
        registerEntityTag("minecraft:item_frame",
                new AspectList().add(Aspect.SENSES, 3).add(Aspect.CLOTH, 1));
        registerEntityTag("minecraft:painting",
                new AspectList().add(Aspect.SENSES, 5).add(Aspect.CLOTH, 3));

        registerEntityTag(tcEntity("PrimalOrb"),
                new AspectList().add(Aspect.AIR, 5).add(Aspect.ENTROPY, 10).add(Aspect.MAGIC, 10).add(Aspect.ENERGY, 10));
        registerEntityTag(tcEntity("Firebat"),
                new AspectList().add(Aspect.BEAST, 2).add(Aspect.FLIGHT, 1).add(Aspect.FIRE, 2));
        registerEntityTag(tcEntity("Pech"),
                new AspectList().add(Aspect.MAN, 2).add(Aspect.MAGIC, 2).add(Aspect.EXCHANGE, 2).add(Aspect.GREED, 2),
                new ThaumcraftApi.EntityTagsNBT("PechType", (byte)0));
        registerEntityTag(tcEntity("Pech"),
                new AspectList().add(Aspect.MAN, 2).add(Aspect.MAGIC, 2).add(Aspect.EXCHANGE, 2).add(Aspect.WEAPON, 2),
                new ThaumcraftApi.EntityTagsNBT("PechType", (byte)1));
        registerEntityTag(tcEntity("Pech"),
                new AspectList().add(Aspect.MAN, 2).add(Aspect.MAGIC, 4).add(Aspect.EXCHANGE, 2),
                new ThaumcraftApi.EntityTagsNBT("PechType", (byte)2));
        registerEntityTag(tcEntity("ThaumSlime"),
                new AspectList().add(Aspect.SLIME, 2).add(Aspect.MAGIC, 1).add(Aspect.WATER, 1));
        registerEntityTag(tcEntity("BrainyZombie"),
                new AspectList().add(Aspect.UNDEAD, 3).add(Aspect.MAN, 1).add(Aspect.MIND, 1).add(Aspect.EARTH, 1));
        registerEntityTag(tcEntity("GiantBrainyZombie"),
                new AspectList().add(Aspect.UNDEAD, 4).add(Aspect.MAN, 2).add(Aspect.MIND, 1).add(Aspect.EARTH, 2));
        registerEntityTag(tcEntity("Taintacle"),
                new AspectList().add(Aspect.TAINT, 3).add(Aspect.WATER, 2));
        registerEntityTag(tcEntity("TaintacleTiny"),
                new AspectList().add(Aspect.TAINT, 1).add(Aspect.WATER, 1));
        registerEntityTag(tcEntity("TaintSpider"),
                new AspectList().add(Aspect.TAINT, 1).add(Aspect.EARTH, 1));
        registerEntityTag(tcEntity("TaintSpore"),
                new AspectList().add(Aspect.TAINT, 2).add(Aspect.AIR, 2));
        registerEntityTag(tcEntity("TaintSwarmer"),
                new AspectList().add(Aspect.TAINT, 2).add(Aspect.AIR, 2));
        registerEntityTag(tcEntity("TaintSwarm"),
                new AspectList().add(Aspect.TAINT, 3).add(Aspect.AIR, 3));
        registerEntityTag(tcEntity("TaintedPig"),
                new AspectList().add(Aspect.TAINT, 2).add(Aspect.EARTH, 2));
        registerEntityTag(tcEntity("TaintedSheep"),
                new AspectList().add(Aspect.TAINT, 2).add(Aspect.EARTH, 2));
        registerEntityTag(tcEntity("TaintedCow"),
                new AspectList().add(Aspect.TAINT, 3).add(Aspect.EARTH, 3));
        registerEntityTag(tcEntity("TaintedChicken"),
                new AspectList().add(Aspect.TAINT, 2).add(Aspect.FLIGHT, 2).add(Aspect.AIR, 1));
        registerEntityTag(tcEntity("TaintedVillager"),
                new AspectList().add(Aspect.TAINT, 3).add(Aspect.AIR, 2));
        registerEntityTag(tcEntity("TaintedCreeper"),
                new AspectList().add(Aspect.TAINT, 2).add(Aspect.FIRE, 2));
        registerEntityTag(tcEntity("MindSpider"),
                new AspectList().add(Aspect.TAINT, 2).add(Aspect.FIRE, 2));
        registerEntityTag(tcEntity("EldritchGuardian"),
                new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.DEATH, 2).add(Aspect.UNDEAD, 4));
        registerEntityTag(tcEntity("EldritchOrb"),
                new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.DEATH, 2));
        registerEntityTag(tcEntity("CultistKnight"),
                new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.MAN, 2).add(Aspect.ENTROPY, 1));
        registerEntityTag(tcEntity("CultistCleric"),
                new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.MAN, 2).add(Aspect.ENTROPY, 1));
        for (Aspect tag : Aspect.aspects.values()) {
            if (tag != null) {
                registerEntityTag(tcEntity("Wisp"),
                        new AspectList().add(tag, 2).add(Aspect.MAGIC, 1).add(Aspect.AIR, 1),
                        new ThaumcraftApi.EntityTagsNBT("Type", tag.getTag()));
            }
        }
        registerEntityTag(tcEntity("Golem"),
                new AspectList().add(Aspect.AIR, 2).add(Aspect.EARTH, 2).add(Aspect.MAGIC, 2));
    }

    private static void registerEntityTag(String key, AspectList aspects, ThaumcraftApi.EntityTagsNBT... nbt) {
        ThaumcraftApi.registerEntityTag(key, aspects, nbt);
    }

    private static String tcEntity(String legacyToken) {
        return "thaumcraft:" + ConfigBlocks.legacyPath(legacyToken);
    }
}
