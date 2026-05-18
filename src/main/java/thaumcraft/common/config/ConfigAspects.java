package thaumcraft.common.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ConfigAspects {

    public static void init() {
        registerVanillaBlocks();
        registerVanillaItems();
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

        ThaumcraftApi.registerObjectTag("nuggetIron", new AspectList().add(Aspect.METAL, 1));
        ThaumcraftApi.registerObjectTag("oreIron", new AspectList().add(Aspect.EARTH, 1).add(Aspect.METAL, 3));
        ThaumcraftApi.registerObjectTag("dustIron", new AspectList().add(Aspect.METAL, 3).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag("oreGold", new AspectList().add(Aspect.EARTH, 1).add(Aspect.METAL, 2).add(Aspect.GREED, 1));
        ThaumcraftApi.registerObjectTag("dustGold", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.GREED, 1));
        ThaumcraftApi.registerObjectTag("dustGlowstone", new AspectList().add(Aspect.SENSES, 1).add(Aspect.LIGHT, 2));

        // Generic ore dictionary registrations
        ThaumcraftApi.registerObjectTag("treeSapling", new AspectList().add(Aspect.PLANT, 2).add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag("treeLeaves", new AspectList().add(Aspect.PLANT, 2).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag("logWood", new AspectList().add(Aspect.TREE, 4));
        ThaumcraftApi.registerObjectTag("plankWood", new AspectList().add(Aspect.TREE, 2));
        ThaumcraftApi.registerObjectTag("slabWood", new AspectList().add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag("stickWood", new AspectList().add(Aspect.TREE, 1));
        ThaumcraftApi.registerObjectTag("blockGlass", new AspectList().add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag("paneGlass", new AspectList().add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag("blockWool", new AspectList().add(Aspect.PLANT, 2).add(Aspect.TOOL, 1));
    }

    private static void registerThaumcraftAlchemyBaseline() {
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 0), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.AIR, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 1), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.FIRE, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 2), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.WATER, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 3), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 4), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 5), new AspectList().add(Aspect.MAGIC, 1).add(Aspect.ENTROPY, 2).add(Aspect.CRYSTAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemResource, 1, 14), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.AIR, 2).add(Aspect.FIRE, 2).add(Aspect.WATER, 2).add(Aspect.EARTH, 2).add(Aspect.ORDER, 2).add(Aspect.ENTROPY, 2));

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

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockTable), new AspectList().add(Aspect.TREE, 4).add(Aspect.CRAFT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemInkwell), new AspectList().add(Aspect.WATER, 1).add(Aspect.DARKNESS, 1).add(Aspect.TOOL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemThaumometer), new AspectList().add(Aspect.SENSES, 3).add(Aspect.METAL, 2).add(Aspect.CRYSTAL, 1).add(Aspect.MAGIC, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.METAL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.GREED, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.MAN, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4), new AspectList().add(Aspect.METAL, 8).add(Aspect.MAGIC, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5), new AspectList().add(Aspect.FLESH, 4).add(Aspect.LIGHT, 1).add(Aspect.MAGIC, 1));

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
        // Minimal 1.7.10 parity baseline for research entity triggers in ConfigResearch.
        ThaumcraftApi.registerEntityTag("minecraft:enderman",
                new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.TRAVEL, 2).add(Aspect.AIR, 2),
                new ThaumcraftApi.EntityTagsNBT[0]);
        ThaumcraftApi.registerEntityTag("thaumcraft:brainyzombie",
                new AspectList().add(Aspect.UNDEAD, 3).add(Aspect.MAN, 1).add(Aspect.MIND, 1).add(Aspect.EARTH, 1),
                new ThaumcraftApi.EntityTagsNBT[0]);
        ThaumcraftApi.registerEntityTag("thaumcraft:giantbrainyzombie",
                new AspectList().add(Aspect.UNDEAD, 4).add(Aspect.MAN, 2).add(Aspect.MIND, 1).add(Aspect.EARTH, 2),
                new ThaumcraftApi.EntityTagsNBT[0]);
        ThaumcraftApi.registerEntityTag("thaumcraft:firebat",
                new AspectList().add(Aspect.BEAST, 2).add(Aspect.FLIGHT, 1).add(Aspect.FIRE, 2),
                new ThaumcraftApi.EntityTagsNBT[0]);
        ThaumcraftApi.registerEntityTag("thaumcraft:primalorb",
                new AspectList().add(Aspect.AIR, 5).add(Aspect.ENTROPY, 10).add(Aspect.MAGIC, 10).add(Aspect.ENERGY, 10),
                new ThaumcraftApi.EntityTagsNBT[0]);
    }
}
