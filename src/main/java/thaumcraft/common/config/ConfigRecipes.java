package thaumcraft.common.config;

import java.util.Arrays;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;
import thaumcraft.common.items.armor.RecipesRobeArmorDyes;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.items.armor.RecipesVoidRobeArmorDyes;

public class ConfigRecipes {
    private static boolean specialRecipesRegistered = false;
    private static IRecipe recipeArcaneStone2;
    private static IRecipe recipeArcaneStone3;
    private static IRecipe recipeArcaneStone4;
    private static IRecipe recipeKnowFrag;
    private static IRecipe recipePlankGreatwood;
    private static IRecipe recipePlankSilverwood;
    private static IRecipe recipeGrate;
    private static IRecipe recipePhial;
    private static IRecipe recipeTable;
    private static IRecipe recipeScribe1;
    private static IRecipe recipeScribe2;
    private static IRecipe recipeScribe3;
    private static IRecipe recipeThaumometer;
    private static IRecipe recipeWandCapIron;
    private static IRecipe recipeWandBasic;
    private static CrucibleRecipe recipeNitor;
    private static CrucibleRecipe recipeAlumentum;
    private static CrucibleRecipe recipeThaumium;
    private static CrucibleRecipe recipeVoidMetal;
    private static CrucibleRecipe recipeVoidSeed;

    public static void init() {
        ConfigResearch.recipes.clear();
        initializeArcaneRecipeBaseline();
        initializeInfusionWandRecipeBaseline();
        initializeInfusionEnchantmentRecipeBaseline();
        initializeInfusionFocusDeviceRecipeBaseline();
        initializeInfusionGolemDeviceRecipeBaseline();
        initializeInfusionEquipmentArmorRecipeBaseline();
        initializeCrucibleRecipeBaseline();
        if (recipeArcaneStone2 != null) {
            ConfigResearch.recipes.put("ArcaneStone2", recipeArcaneStone2);
        }
        if (recipeArcaneStone3 != null) {
            ConfigResearch.recipes.put("ArcaneStone3", recipeArcaneStone3);
        }
        if (recipeArcaneStone4 != null) {
            ConfigResearch.recipes.put("ArcaneStone4", recipeArcaneStone4);
        }
        if (recipeKnowFrag != null) {
            ConfigResearch.recipes.put("KnowFrag", recipeKnowFrag);
        }
        if (recipePlankGreatwood != null) {
            ConfigResearch.recipes.put("PlankGreatwood", recipePlankGreatwood);
        }
        if (recipePlankSilverwood != null) {
            ConfigResearch.recipes.put("PlankSilverwood", recipePlankSilverwood);
        }
        if (recipeGrate != null) {
            ConfigResearch.recipes.put("Grate", recipeGrate);
        }
        if (recipePhial != null) {
            ConfigResearch.recipes.put("Phial", recipePhial);
        }
        if (recipeTable != null) {
            ConfigResearch.recipes.put("Table", recipeTable);
        }
        if (recipeScribe1 != null) {
            ConfigResearch.recipes.put("Scribe1", recipeScribe1);
        }
        if (recipeScribe2 != null) {
            ConfigResearch.recipes.put("Scribe2", recipeScribe2);
        }
        if (recipeScribe3 != null) {
            ConfigResearch.recipes.put("Scribe3", recipeScribe3);
        }
        if (recipeThaumometer != null) {
            ConfigResearch.recipes.put("Thaumometer", recipeThaumometer);
        }
        if (recipeWandCapIron != null) {
            ConfigResearch.recipes.put("WandCapIron", recipeWandCapIron);
        }
        if (recipeWandBasic != null) {
            ConfigResearch.recipes.put("WandBasic", recipeWandBasic);
        }
        if (recipeNitor != null) {
            ConfigResearch.recipes.put("Nitor", recipeNitor);
        }
        if (recipeAlumentum != null) {
            ConfigResearch.recipes.put("Alumentum", recipeAlumentum);
        }
        if (recipeThaumium != null) {
            ConfigResearch.recipes.put("Thaumium", recipeThaumium);
        }
        if (recipeVoidMetal != null) {
            ConfigResearch.recipes.put("VoidMetal", recipeVoidMetal);
        }
        if (recipeVoidSeed != null) {
            ConfigResearch.recipes.put("VoidSeed", recipeVoidSeed);
        }
        ItemStack basicWand = new ItemStack(ConfigItems.itemWandCasting, 1, 0);
        ConfigResearch.recipes.put("Thaumonomicon",
                Arrays.asList(new AspectList(), 1, 2, 1,
                        Arrays.asList(basicWand, new ItemStack(Blocks.BOOKSHELF))));
        ConfigResearch.recipes.put("ArcTable",
                Arrays.asList(new AspectList(), 1, 2, 1,
                        Arrays.asList(basicWand, new ItemStack(ConfigBlocks.blockTable))));
        ConfigResearch.recipes.put("ResTable",
                Arrays.asList(new AspectList(), 1, 2, 2,
                        Arrays.asList(null, new ItemStack(ConfigItems.itemInkwell),
                                new ItemStack(ConfigBlocks.blockTable), new ItemStack(ConfigBlocks.blockTable))));
        ConfigResearch.recipes.put("Crucible",
                Arrays.asList(new AspectList(), 1, 2, 1,
                        Arrays.asList(basicWand, new ItemStack(Items.CAULDRON))));

        boolean hasArcaneWand = false;
        boolean hasArcaneSceptre = false;
        boolean hasRunicAugment = false;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (recipe instanceof ArcaneWandRecipe) {
                hasArcaneWand = true;
            } else if (recipe instanceof ArcaneSceptreRecipe) {
                hasArcaneSceptre = true;
            }
            if (recipe instanceof InfusionRunicAugmentRecipe) {
                hasRunicAugment = true;
            }
        }
        if (!hasArcaneWand) {
            ThaumcraftApi.getCraftingRecipes().add(new ArcaneWandRecipe());
        }
        if (!hasArcaneSceptre) {
            ThaumcraftApi.getCraftingRecipes().add(new ArcaneSceptreRecipe());
        }
        if (!hasRunicAugment) {
            ThaumcraftApi.getCraftingRecipes().add(new InfusionRunicAugmentRecipe());
        }
    }

    private static void initializeCrucibleRecipeBaseline() {
        recipeAlumentum = ThaumcraftApi.addCrucibleRecipe(
                "ALUMENTUM",
                new ItemStack(ConfigItems.itemResource, 1, 0),
                new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.ENERGY, 3).merge(Aspect.FIRE, 3).merge(Aspect.ENTROPY, 3));

        recipeNitor = ThaumcraftApi.addCrucibleRecipe(
                "NITOR",
                new ItemStack(ConfigItems.itemResource, 1, 1),
                "dustGlowstone",
                new AspectList().merge(Aspect.ENERGY, 3).merge(Aspect.FIRE, 3).merge(Aspect.LIGHT, 3));

        recipeThaumium = ThaumcraftApi.addCrucibleRecipe(
                "THAUMIUM",
                new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(Items.IRON_INGOT),
                new AspectList().merge(Aspect.MAGIC, 4));

        recipeVoidMetal = ThaumcraftApi.addCrucibleRecipe(
                "VOIDMETAL",
                new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigItems.itemResource, 1, 17),
                new AspectList().merge(Aspect.METAL, 8));

        recipeVoidSeed = ThaumcraftApi.addCrucibleRecipe(
                "VOIDMETAL",
                new ItemStack(ConfigItems.itemResource, 1, 17),
                new ItemStack(Items.ENDER_PEARL),
                new AspectList().merge(Aspect.DARKNESS, 8).merge(Aspect.VOID, 8).merge(Aspect.ELDRITCH, 2));
    }

    private static void initializeArcaneRecipeBaseline() {
        for (int color = 0; color < 16; color++) {
            ItemStack banner = new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 8);
            NBTTagCompound bannerTag = new NBTTagCompound();
            bannerTag.setByte("color", (byte) color);
            banner.setTagCompound(bannerTag);
            registerArcaneRecipe("Banner_" + color, "BANNERS",
                    banner,
                    new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5),
                    "WS", "WS", "WB",
                    'W', new ItemStack(Blocks.WOOL, 1, color),
                    'S', "stickWood",
                    'B', "slabWood");
        }

        registerArcaneRecipe("PrimalCharm", "BASICARTIFACE",
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new AspectList().add(Aspect.EARTH, 25).add(Aspect.FIRE, 25).add(Aspect.AIR, 25)
                        .add(Aspect.WATER, 25).add(Aspect.ORDER, 25).add(Aspect.ENTROPY, 25),
                "123", "ISI", "456",
                'S', new ItemStack(ConfigItems.itemShard, 1, 6),
                'I', Items.GOLD_INGOT,
                '1', new ItemStack(ConfigItems.itemShard, 1, 0),
                '2', new ItemStack(ConfigItems.itemShard, 1, 1),
                '3', new ItemStack(ConfigItems.itemShard, 1, 2),
                '4', new ItemStack(ConfigItems.itemShard, 1, 3),
                '5', new ItemStack(ConfigItems.itemShard, 1, 4),
                '6', new ItemStack(ConfigItems.itemShard, 1, 5));

        registerArcaneRecipe("IronKey", "WARDEDARCANA",
                new ItemStack(ConfigItems.itemKey, 2, 0),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                "NNI", "N  ",
                'I', Items.IRON_INGOT,
                'N', "nuggetIron");

        registerArcaneRecipe("ArcaneDoor", "WARDEDARCANA",
                new ItemStack(ConfigItems.itemArcaneDoor),
                new AspectList().add(Aspect.WATER, 20).add(Aspect.ORDER, 10).add(Aspect.EARTH, 10).add(Aspect.FIRE, 5),
                "TDT", "DBD", "TDT",
                'T', "ingotThaumium",
                'B', new ItemStack(ConfigItems.itemZombieBrain),
                'D', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6));

        registerArcaneRecipe("WardedGlass", "WARDEDARCANA",
                new ItemStack(ConfigBlocks.blockCosmeticOpaque, 8, 2),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 10).add(Aspect.EARTH, 5).add(Aspect.FIRE, 5),
                "GGG", "WBW", "GGG",
                'B', new ItemStack(ConfigItems.itemZombieBrain),
                'G', new ItemStack(Blocks.GLASS),
                'W', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6));

        registerArcaneRecipe("FluxScrubber", "FLUXSCRUB",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 14),
                new AspectList().add(Aspect.WATER, 16).add(Aspect.ORDER, 16).add(Aspect.AIR, 8),
                " B ", "GOG", "STS",
                'B', new ItemStack(ConfigBlocks.blockWoodenDevice),
                'G', new ItemStack(Blocks.IRON_BARS),
                'T', new ItemStack(ConfigBlocks.blockTube),
                'O', new ItemStack(ConfigItems.itemResource, 1, 8),
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7));

        if (Config.wardedStone) {
            registerArcaneRecipe("GoldKey", "WARDEDARCANA",
                    new ItemStack(ConfigItems.itemKey, 2, 1),
                    new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                    "NNI", "N  ",
                    'I', Items.GOLD_INGOT,
                    'N', Items.GOLD_NUGGET);

            registerArcaneRecipe("ArcanePressurePlate", "WARDEDARCANA",
                    new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 2),
                    new AspectList().add(Aspect.WATER, 20).add(Aspect.ORDER, 10).add(Aspect.FIRE, 5).add(Aspect.EARTH, 10),
                    " B ", "TDT",
                    'T', new ItemStack(ConfigItems.itemResource, 1, 2),
                    'B', new ItemStack(ConfigItems.itemZombieBrain),
                    'D', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6));
        }

        registerArcaneRecipe("ArcaneStone1", "ARCANESTONE",
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6),
                new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1),
                "SSS", "SCS", "SSS",
                'S', "stone",
                'C', new ItemStack(ConfigItems.itemShard, 1, OreDictionary.WILDCARD_VALUE));

        registerArcaneRecipe("NodeStabilizer", "NODESTABILIZER",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9),
                new AspectList().add(Aspect.WATER, 32).add(Aspect.EARTH, 32).add(Aspect.ORDER, 32),
                " G ", "QPQ", "SNS",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7),
                'G', new ItemStack(Items.GOLD_INGOT),
                'P', new ItemStack(Blocks.PISTON),
                'Q', new ItemStack(Blocks.QUARTZ_BLOCK),
                'N', new ItemStack(ConfigItems.itemResource, 1, 1));

        registerArcaneRecipe("NodeTransducer", "VISPOWER",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 11),
                new AspectList().add(Aspect.FIRE, 32).add(Aspect.AIR, 32).add(Aspect.ENTROPY, 32),
                "RCR", "ISI", "RAR",
                'S', new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9),
                'C', new ItemStack(Items.COMPARATOR),
                'I', new ItemStack(Items.IRON_INGOT),
                'R', new ItemStack(Blocks.REDSTONE_BLOCK),
                'A', new ItemStack(ConfigItems.itemResource, 1, 1));

        registerArcaneRecipe("NodeRelay", "VISPOWER",
                new ItemStack(ConfigBlocks.blockMetalDevice, 2, 14),
                new AspectList().add(Aspect.FIRE, 8).add(Aspect.ORDER, 8),
                " I ", "ISI", " I ",
                'I', new ItemStack(Items.IRON_INGOT),
                'S', new ItemStack(ConfigItems.itemShard, 1, 6));

        registerArcaneRecipe("NodeChargeRelay", "VISCHARGERELAY",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 2),
                new AspectList().add(Aspect.FIRE, 16).add(Aspect.ORDER, 16).add(Aspect.AIR, 16),
                " R ", "W W", "I I",
                'I', new ItemStack(Items.IRON_INGOT),
                'R', new ItemStack(ConfigBlocks.blockMetalDevice, 1, 14),
                'W', new ItemStack(ConfigItems.itemWandRod, 1, 0));

        registerArcaneRecipe("FocalManipulator", "FOCALMANIPULATION",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 13),
                new AspectList().add(Aspect.FIRE, 32).add(Aspect.AIR, 32).add(Aspect.ENTROPY, 32)
                        .add(Aspect.EARTH, 32).add(Aspect.WATER, 32).add(Aspect.ORDER, 32),
                "IQI", "SPS", "GTG",
                'Q', new ItemStack(ConfigBlocks.blockSlabStone, 1, 0),
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
                'T', new ItemStack(ConfigBlocks.blockTable),
                'I', new ItemStack(Items.IRON_INGOT),
                'G', new ItemStack(Items.GOLD_INGOT),
                'P', new ItemStack(ConfigItems.itemResource, 1, 15));

        registerArcaneRecipe("GolemFetter", "GOLEMFETTER",
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 9),
                new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 5),
                "SSS", "IRI", "BBB",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
                'I', new ItemStack(Items.IRON_INGOT),
                'B', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7),
                'R', new ItemStack(Blocks.REDSTONE_BLOCK));

        registerArcaneRecipe("HungryChest", "HUNGRYCHEST",
                new ItemStack(ConfigBlocks.blockChestHungry),
                new AspectList().add(Aspect.AIR, 5).add(Aspect.ORDER, 3).add(Aspect.ENTROPY, 3),
                "WTW", "W W", "WWW",
                'W', "plankWood",
                'T', new ItemStack(Blocks.TRAPDOOR));

        registerArcaneRecipe("WardedJar", "DISTILESSENTIA",
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new AspectList().add(Aspect.WATER, 1),
                "GWG", "G G", "GGG",
                'W', "slabWood",
                'G', Blocks.GLASS);

        registerArcaneRecipe("JarVoid", "JARVOID",
                new ItemStack(ConfigBlocks.blockJar, 1, 3),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ENTROPY, 15),
                "O", "J", "P",
                'O', Blocks.OBSIDIAN,
                'P', Items.BLAZE_POWDER,
                'J', new ItemStack(ConfigBlocks.blockJar, 1, 0));

        registerArcaneRecipe("WandCapGold", "CAP_gold",
                new ItemStack(ConfigItems.itemWandCap, 1, 1),
                new AspectList().add(Aspect.ORDER, getWandCapCost("gold"))
                        .add(Aspect.FIRE, getWandCapCost("gold"))
                        .add(Aspect.AIR, getWandCapCost("gold")),
                "NNN", "N N",
                'N', Items.GOLD_NUGGET);

        if (Config.foundCopperIngot) {
            registerArcaneRecipe("WandCapCopper", "CAP_copper",
                    new ItemStack(ConfigItems.itemWandCap, 1, 3),
                    new AspectList().add(Aspect.ORDER, getWandCapCost("copper"))
                            .add(Aspect.FIRE, getWandCapCost("copper"))
                            .add(Aspect.AIR, getWandCapCost("copper")),
                    "NNN", "N N",
                    'N', "nuggetCopper");
        }

        if (Config.foundSilverIngot) {
            registerArcaneRecipe("WandCapSilverInert", "CAP_silver",
                    new ItemStack(ConfigItems.itemWandCap, 1, 5),
                    new AspectList().add(Aspect.ORDER, getWandCapCost("silver"))
                            .add(Aspect.FIRE, getWandCapCost("silver"))
                            .add(Aspect.AIR, getWandCapCost("silver")),
                    "NNN", "N N",
                    'N', "nuggetSilver");
        }

        registerArcaneRecipe("WandCapThaumiumInert", "CAP_thaumium",
                new ItemStack(ConfigItems.itemWandCap, 1, 6),
                new AspectList().add(Aspect.ORDER, getWandCapCost("thaumium"))
                        .add(Aspect.FIRE, getWandCapCost("thaumium"))
                        .add(Aspect.AIR, getWandCapCost("thaumium")),
                "NNN", "N N",
                'N', "nuggetThaumium");

        registerArcaneRecipe("WandCapVoidInert", "CAP_void",
                new ItemStack(ConfigItems.itemWandCap, 1, 8),
                new AspectList().add(Aspect.ENTROPY, getWandCapCost("void") * 3)
                        .add(Aspect.ORDER, getWandCapCost("void") * 3)
                        .add(Aspect.FIRE, getWandCapCost("void") * 2)
                        .add(Aspect.AIR, getWandCapCost("void") * 2),
                "NNN", "N N",
                'N', "nuggetVoid");

        registerArcaneRecipe("WandRodGreatwood", "ROD_greatwood",
                new ItemStack(ConfigItems.itemWandRod, 1, 0),
                new AspectList().add(Aspect.ENTROPY, getWandRodCost("greatwood")),
                " G", "G ",
                'G', new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        registerArcaneRecipe("WandRodGreatwoodStaff", "ROD_greatwood_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 50),
                new AspectList().add(Aspect.ORDER, getWandRodCost("greatwood_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 0));

        registerArcaneRecipe("WandRodObsidianStaff", "ROD_obsidian_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 51),
                new AspectList().add(Aspect.ORDER, getWandRodCost("obsidian_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 1));

        registerArcaneRecipe("WandRodSilverwoodStaff", "ROD_silverwood_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 52),
                new AspectList().add(Aspect.ORDER, getWandRodCost("silverwood_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 2));

        registerArcaneRecipe("WandRodIceStaff", "ROD_ice_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 53),
                new AspectList().add(Aspect.ORDER, getWandRodCost("ice_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 3));

        registerArcaneRecipe("WandRodQuartzStaff", "ROD_quartz_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 54),
                new AspectList().add(Aspect.ORDER, getWandRodCost("quartz_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 4));

        registerArcaneRecipe("WandRodReedStaff", "ROD_reed_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 55),
                new AspectList().add(Aspect.ORDER, getWandRodCost("reed_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 5));

        registerArcaneRecipe("WandRodBlazeStaff", "ROD_blaze_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 56),
                new AspectList().add(Aspect.ORDER, getWandRodCost("blaze_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 6));

        registerArcaneRecipe("WandRodBoneStaff", "ROD_bone_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 57),
                new AspectList().add(Aspect.ORDER, getWandRodCost("bone_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 7));

        registerArcaneRecipe("FocusFire", "FOCUSFIRE",
                new ItemStack(ConfigItems.focusFire),
                new AspectList().add(Aspect.FIRE, 20).add(Aspect.ENTROPY, 10),
                "CQC", "Q#Q", "CQC",
                '#', Items.FIRE_CHARGE,
                'Q', Items.QUARTZ,
                'C', new ItemStack(ConfigItems.itemShard, 1, 1));

        registerArcaneRecipe("FocusFrost", "FOCUSFROST",
                new ItemStack(ConfigItems.focusFrost),
                new AspectList().add(Aspect.WATER, 10).add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10),
                "CQC", "Q#Q", "CQC",
                '#', Items.DIAMOND,
                'Q', Items.QUARTZ,
                'C', new ItemStack(ConfigItems.itemShard, 1, 2));

        registerArcaneRecipe("RobeChest", "ENCHFABRIC",
                new ItemStack(ConfigItems.itemChestRobe, 1),
                new AspectList().add(Aspect.AIR, 5),
                "I I", "III", "III",
                'I', new ItemStack(ConfigItems.itemResource, 1, 7));

        registerArcaneRecipe("RobeLegs", "ENCHFABRIC",
                new ItemStack(ConfigItems.itemLegsRobe, 1),
                new AspectList().add(Aspect.WATER, 5),
                "III", "I I", "I I",
                'I', new ItemStack(ConfigItems.itemResource, 1, 7));

        registerArcaneRecipe("RobeBoots", "ENCHFABRIC",
                new ItemStack(ConfigItems.itemBootsRobe, 1),
                new AspectList().add(Aspect.EARTH, 3),
                "I I", "I I",
                'I', new ItemStack(ConfigItems.itemResource, 1, 7));

        registerArcaneRecipe("Goggles", "GOGGLES",
                new ItemStack(ConfigItems.itemGoggles),
                new AspectList().add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.WATER, 5)
                        .add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 3).add(Aspect.ORDER, 3),
                "LGL", "L L", "TGT",
                'T', ConfigItems.itemThaumometer,
                'G', Items.GOLD_INGOT,
                'L', Items.LEATHER);

        registerShapelessArcaneRecipe("MirrorGlass", "BASICARTIFACE",
                new ItemStack(ConfigItems.itemResource, 1, 10),
                new AspectList().add(Aspect.FIRE, 10).add(Aspect.EARTH, 10),
                new ItemStack(ConfigItems.itemResource, 1, 3), Blocks.GLASS_PANE);

        registerArcaneRecipe("BoneBow", "BONEBOW",
                new ItemStack(ConfigItems.itemBowBone),
                new AspectList().add(Aspect.AIR, 16).add(Aspect.ENTROPY, 32),
                "SB ", "SEB", "SB ",
                'E', new ItemStack(ConfigItems.itemShard, 1, 5),
                'B', Items.BONE,
                'S', Items.STRING);

        Aspect[] primalAspects = new Aspect[]{Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY};
        for (int i = 0; i < primalAspects.length; i++) {
            registerArcaneRecipe("PrimalArrow_" + i, "PRIMALARROW",
                    new ItemStack(ConfigItems.itemPrimalArrow, 8, i),
                    new AspectList().add(primalAspects[i], 8),
                    "AAA", "ASA", "AAA",
                    'A', Items.ARROW,
                    'S', new ItemStack(ConfigItems.itemShard, 1, i));
        }

        registerArcaneRecipe("InfusionMatrix", "INFUSION",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2),
                new AspectList().add(Aspect.ORDER, 40),
                "SBS", "BEB", "SBS",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
                'E', Items.ENDER_PEARL,
                'B', new ItemStack(ConfigItems.itemShard, 1, OreDictionary.WILDCARD_VALUE));

        registerArcaneRecipe("ArcanePedestal", "INFUSION",
                new ItemStack(ConfigBlocks.blockStoneDevice, 2, 1),
                new AspectList().add(Aspect.AIR, 5),
                "SSS", " S ", "SSS",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6));

        registerArcaneRecipe("PaveTravel", "PAVETRAVEL",
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 2),
                new AspectList().add(Aspect.EARTH, 10).add(Aspect.AIR, 10),
                "SAS", "SBS",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7),
                'A', new ItemStack(ConfigItems.itemShard, 1, 0),
                'B', new ItemStack(ConfigItems.itemShard, 1, 3));

        registerArcaneRecipe("ArcaneLamp", "ARCANELAMP",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 7),
                new AspectList().add(Aspect.FIRE, 8).add(Aspect.AIR, 8).add(Aspect.WATER, 4).add(Aspect.ENTROPY, 4),
                " S ", "IAI", " N ",
                'A', new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 0),
                'S', new ItemStack(Blocks.DAYLIGHT_DETECTOR),
                'N', new ItemStack(ConfigItems.itemResource, 1, 1),
                'I', new ItemStack(Items.IRON_INGOT));

        registerArcaneRecipe("ArcaneSpa", "ARCANESPA",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 12),
                new AspectList().add(Aspect.WATER, 16).add(Aspect.ORDER, 8).add(Aspect.EARTH, 4),
                "QIQ", "SJS", "SPS",
                'P', new ItemStack(Blocks.PISTON),
                'J', new ItemStack(ConfigBlocks.blockJar),
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
                'Q', new ItemStack(Blocks.QUARTZ_BLOCK),
                'I', new ItemStack(Blocks.IRON_BARS));

        registerArcaneRecipe("PaveWard", "PAVEWARD",
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 3),
                new AspectList().add(Aspect.FIRE, 10).add(Aspect.ORDER, 10),
                "SAS", "SBS",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7),
                'A', new ItemStack(ConfigItems.itemShard, 1, 1),
                'B', new ItemStack(ConfigItems.itemShard, 1, 4));

        registerArcaneRecipe("Levitator", "LEVITATOR",
                new ItemStack(ConfigBlocks.blockLifter),
                new AspectList().add(Aspect.AIR, 10).add(Aspect.EARTH, 5),
                "WEW", "BNB", "WAW",
                'W', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                'E', new ItemStack(ConfigItems.itemShard, 1, 3),
                'A', new ItemStack(ConfigItems.itemShard, 1, 0),
                'N', new ItemStack(ConfigItems.itemResource, 1, 1),
                'B', new ItemStack(Items.IRON_INGOT));

        registerArcaneRecipe("ArcaneEar", "ARCANEEAR",
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 1),
                new AspectList().add(Aspect.AIR, 10).add(Aspect.ORDER, 10),
                "GIG", "GBG", "WRW",
                'W', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                'R', Items.REDSTONE,
                'I', Items.IRON_INGOT,
                'G', Items.GOLD_INGOT,
                'B', new ItemStack(ConfigItems.itemZombieBrain));

        registerArcaneRecipe("FocusShock", "FOCUSSHOCK",
                new ItemStack(ConfigItems.focusShock),
                new AspectList().add(Aspect.AIR, 10).add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10),
                "CQC", "Q#Q", "CQC",
                '#', Items.POTATO,
                'Q', Items.QUARTZ,
                'C', new ItemStack(ConfigItems.itemShard, 1, 0));

        registerArcaneRecipe("FocusTrade", "FOCUSTRADE",
                new ItemStack(ConfigItems.focusTrade),
                new AspectList().add(Aspect.ORDER, 15).add(Aspect.ENTROPY, 15).add(Aspect.EARTH, 10),
                "CQE", "Q#Q", "CQE",
                '#', new ItemStack(ConfigItems.itemResource, 1, 3),
                'Q', Items.QUARTZ,
                'C', new ItemStack(ConfigItems.itemShard, 1, 6),
                'E', new ItemStack(ConfigItems.itemShard, 1, 6));

        registerArcaneRecipe("FocusExcavation", "FOCUSEXCAVATION",
                new ItemStack(ConfigItems.focusExcavation),
                new AspectList().add(Aspect.EARTH, 20).add(Aspect.ENTROPY, 5).add(Aspect.ORDER, 5),
                "CQC", "Q#Q", "CQC",
                '#', "gemEmerald",
                'Q', Items.QUARTZ,
                'C', new ItemStack(ConfigItems.itemShard, 1, 3));

        registerArcaneRecipe("FocusPrimal", "FOCUSPRIMAL",
                new ItemStack(ConfigItems.focusPrimal),
                new AspectList().add(Aspect.EARTH, 25).add(Aspect.ENTROPY, 25).add(Aspect.ORDER, 25)
                        .add(Aspect.AIR, 25).add(Aspect.FIRE, 25).add(Aspect.WATER, 25),
                "CQC", "Q#Q", "CQC",
                '#', new ItemStack(ConfigItems.itemResource, 1, 15),
                'Q', Items.QUARTZ,
                'C', Items.DIAMOND);

        registerArcaneRecipe("FocusPouch", "FOCUSPOUCH",
                new ItemStack(ConfigItems.itemFocusPouch),
                new AspectList().add(Aspect.EARTH, 10).add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10),
                "LGL", "LBL", "LLL",
                'B', new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2),
                'L', Items.LEATHER,
                'G', Items.GOLD_INGOT);

        registerArcaneRecipe("Deconstructor", "DECONSTRUCTOR",
                new ItemStack(ConfigBlocks.blockTable, 1, 14),
                new AspectList().add(Aspect.ENTROPY, 20),
                " S ", "ATP",
                'T', new ItemStack(ConfigBlocks.blockTable, 1, 0),
                'S', new ItemStack(ConfigItems.itemThaumometer),
                'P', new ItemStack(Items.GOLDEN_PICKAXE),
                'A', new ItemStack(Items.GOLDEN_AXE));

        registerArcaneRecipe("ArcaneBoreBase", "ARCANEBORE",
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 4),
                new AspectList().add(Aspect.AIR, 10).add(Aspect.ORDER, 10),
                "WIW", "IDI", "WIW",
                'W', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                'I', Items.IRON_INGOT,
                'D', Blocks.DISPENSER);

        registerArcaneRecipe("EnchantedFabric", "ENCHFABRIC",
                new ItemStack(ConfigItems.itemResource, 1, 7),
                new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.FIRE, 1)
                        .add(Aspect.WATER, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
                " S ", "SCS", " S ",
                'S', new ItemStack(Items.STRING, 1, OreDictionary.WILDCARD_VALUE),
                'C', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));

        registerArcaneRecipe("GolemBell", "GOLEMBELL",
                new ItemStack(ConfigItems.itemGolemBell),
                new AspectList().add(Aspect.ORDER, 5),
                " QQ", " QQ", "S  ",
                'S', "stickWood",
                'Q', Items.QUARTZ);

        registerArcaneRecipe("CoreBlank", "COREGATHER",
                new ItemStack(ConfigItems.itemGolemCore, 1, 100),
                new AspectList().add(Aspect.ORDER, 5).add(Aspect.FIRE, 5),
                " C ", "CNC", " C ",
                'C', Items.BRICK,
                'N', new ItemStack(ConfigItems.itemResource, 1, 1));

        registerArcaneRecipe("UpgradeAir", "UPGRADEAIR",
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 0),
                new AspectList().add(Aspect.AIR, 10),
                "NNN", "NCN", "NNN",
                'N', Items.GOLD_NUGGET,
                'C', new ItemStack(ConfigItems.itemShard, 1, 0));

        registerArcaneRecipe("UpgradeEarth", "UPGRADEEARTH",
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 1),
                new AspectList().add(Aspect.EARTH, 10),
                "NNN", "NCN", "NNN",
                'N', Items.GOLD_NUGGET,
                'C', new ItemStack(ConfigItems.itemShard, 1, 3));

        registerArcaneRecipe("UpgradeFire", "UPGRADEFIRE",
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 2),
                new AspectList().add(Aspect.FIRE, 10),
                "NNN", "NCN", "NNN",
                'N', Items.GOLD_NUGGET,
                'C', new ItemStack(ConfigItems.itemShard, 1, 1));

        registerArcaneRecipe("UpgradeWater", "UPGRADEWATER",
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 3),
                new AspectList().add(Aspect.WATER, 10),
                "NNN", "NCN", "NNN",
                'N', Items.GOLD_NUGGET,
                'C', new ItemStack(ConfigItems.itemShard, 1, 2));

        registerArcaneRecipe("UpgradeOrder", "UPGRADEORDER",
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 4),
                new AspectList().add(Aspect.ORDER, 10),
                "NNN", "NCN", "NNN",
                'N', Items.GOLD_NUGGET,
                'C', new ItemStack(ConfigItems.itemShard, 1, 4));

        registerArcaneRecipe("UpgradeEntropy", "UPGRADEENTROPY",
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 5),
                new AspectList().add(Aspect.ENTROPY, 10),
                "NNN", "NCN", "NNN",
                'N', Items.GOLD_NUGGET,
                'C', new ItemStack(ConfigItems.itemShard, 1, 5));

        registerArcaneRecipe("TinyHat", "TINYHAT",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 0),
                new AspectList().add(Aspect.ORDER, 8).add(Aspect.FIRE, 8),
                " C ", " G ", "CCC",
                'C', new ItemStack(Blocks.WOOL, 1, 15),
                'G', Items.GOLD_INGOT);

        registerArcaneRecipe("TinyFez", "TINYFEZ",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 3),
                new AspectList().add(Aspect.WATER, 4).add(Aspect.EARTH, 4),
                "CCS", "CCS", "  S",
                'C', new ItemStack(Blocks.WOOL, 1, 14),
                'S', Items.STRING);

        registerArcaneRecipe("TinyBowtie", "TINYBOWTIE",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 2),
                new AspectList().add(Aspect.AIR, 4).add(Aspect.ORDER, 4),
                "CSC", "C C",
                'C', new ItemStack(Blocks.WOOL, 1, 15),
                'S', Items.STRING);

        registerArcaneRecipe("TinyGlasses", "TINYGLASSES",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 1),
                new AspectList().add(Aspect.AIR, 4).add(Aspect.WATER, 4),
                "GIG",
                'G', Blocks.GLASS,
                'I', Items.IRON_INGOT);

        registerArcaneRecipe("TinyDart", "TINYDART",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 4),
                new AspectList().add(Aspect.AIR, 4).add(Aspect.FIRE, 4),
                "AIA", "ADA", "AIA",
                'I', Items.IRON_INGOT,
                'D', Blocks.DISPENSER,
                'A', Items.ARROW);

        registerArcaneRecipe("TinyVisor", "TINYVISOR",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 5),
                new AspectList().add(Aspect.EARTH, 4).add(Aspect.WATER, 4),
                "IHI",
                'I', Items.IRON_INGOT,
                'H', new ItemStack(Items.IRON_HELMET, 1, OreDictionary.WILDCARD_VALUE));

        registerArcaneRecipe("TinyArmor", "TINYARMOR",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 6),
                new AspectList().add(Aspect.EARTH, 8),
                "I I", "IAI",
                'I', Items.IRON_INGOT,
                'A', new ItemStack(Items.IRON_CHESTPLATE, 1, OreDictionary.WILDCARD_VALUE));

        registerArcaneRecipe("TinyHammer", "TINYHAMMER",
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 7),
                new AspectList().add(Aspect.EARTH, 4).add(Aspect.FIRE, 4),
                "III", "III", " I ",
                'I', Items.IRON_INGOT);

        registerArcaneRecipe("Filter", "DISTILESSENTIA",
                new ItemStack(ConfigItems.itemResource, 2, 8),
                new AspectList().add(Aspect.ORDER, 5).add(Aspect.WATER, 5),
                "GWG",
                'G', Items.GOLD_INGOT,
                'W', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7));

        registerArcaneRecipe("AlchemyFurnace", "DISTILESSENTIA",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 0),
                new AspectList().add(Aspect.FIRE, 5).add(Aspect.WATER, 5),
                "SCS", "SFS", "SSS",
                'C', new ItemStack(ConfigBlocks.blockMetalDevice, 1, 0),
                'F', Blocks.FURNACE,
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6));

        registerArcaneRecipe("Alembic", "DISTILESSENTIA",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1),
                new AspectList().add(Aspect.AIR, 5).add(Aspect.WATER, 5),
                "FIG", "IBI", "I I",
                'I', Items.IRON_INGOT,
                'B', Items.BUCKET,
                'G', Items.GOLD_INGOT,
                'F', new ItemStack(ConfigItems.itemResource, 1, 8),
                'L', new ItemStack(ConfigBlocks.blockMagicalLeaves, 1, 1));

        registerArcaneRecipe("Bellows", "BELLOWS",
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 0),
                new AspectList().add(Aspect.AIR, 10).add(Aspect.ORDER, 5),
                "WW ", "LCI", "WW ",
                'W', "plankWood",
                'C', new ItemStack(ConfigItems.itemShard, 1, 0),
                'I', Items.IRON_INGOT,
                'L', Items.LEATHER);

        registerArcaneRecipe("Tube", "TUBES",
                new ItemStack(ConfigBlocks.blockTube, 8, 0),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                " Q ", "IGI", " B ",
                'I', Items.IRON_INGOT,
                'B', Items.GOLD_NUGGET,
                'G', Blocks.GLASS,
                'Q', new ItemStack(ConfigItems.itemNugget, 1, 5));

        registerArcaneRecipe("Resonator", "TUBES",
                new ItemStack(ConfigItems.itemResonator),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.AIR, 5),
                "I I", "INI", " S ",
                'I', Items.IRON_INGOT,
                'N', Items.QUARTZ,
                'S', "stickWood");

        registerShapelessArcaneRecipe("TubeValve", "TUBES",
                new ItemStack(ConfigBlocks.blockTube, 1, 1),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                new ItemStack(ConfigBlocks.blockTube, 1, 0), new ItemStack(Blocks.LEVER));

        registerShapelessArcaneRecipe("TubeFilter", "TUBEFILTER",
                new ItemStack(ConfigBlocks.blockTube, 1, 3),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 16),
                new ItemStack(ConfigBlocks.blockTube, 1, 0), new ItemStack(ConfigItems.itemResource, 1, 8));

        registerShapelessArcaneRecipe("TubeRestrict", "TUBEFILTER",
                new ItemStack(ConfigBlocks.blockTube, 1, 5),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 16),
                new ItemStack(ConfigBlocks.blockTube, 1, 0), "stone");

        registerShapelessArcaneRecipe("TubeOneway", "TUBEFILTER",
                new ItemStack(ConfigBlocks.blockTube, 1, 6),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 8).add(Aspect.ENTROPY, 8),
                new ItemStack(ConfigBlocks.blockTube, 1, 0), "dyeBlue");

        registerArcaneRecipe("TubeBuffer", "CENTRIFUGE",
                new ItemStack(ConfigBlocks.blockTube, 1, 4),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                "PVP", "T T", "PRP",
                'T', new ItemStack(ConfigBlocks.blockTube, 1, 0),
                'V', new ItemStack(ConfigBlocks.blockTube, 1, 1),
                'R', new ItemStack(ConfigBlocks.blockTube, 1, 5),
                'P', new ItemStack(ConfigItems.itemEssence, 1, 0));

        registerArcaneRecipe("AlchemicalConstruct", "DISTILESSENTIA",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                "VTF", "TWT", "FTV",
                'W', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                'V', new ItemStack(ConfigBlocks.blockTube, 1, 1),
                'T', new ItemStack(ConfigBlocks.blockTube, 1, 0),
                'F', new ItemStack(ConfigItems.itemResource, 1, 8));

        registerArcaneRecipe("AdvAlchemyConstruct", "ADVALCHEMYFURNACE",
                new ItemStack(ConfigBlocks.blockMetalDevice, 4, 3),
                new AspectList().add(Aspect.WATER, 10).add(Aspect.ORDER, 30).add(Aspect.EARTH, 10),
                "VAV", "APA", "VAV",
                'A', new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
                'V', new ItemStack(ConfigItems.itemResource, 1, 16),
                'P', new ItemStack(ConfigItems.itemEldritchObject, 1, 3));

        registerArcaneRecipe("Centrifuge", "CENTRIFUGE",
                new ItemStack(ConfigBlocks.blockTube, 1, 2),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5),
                " T ", "ACP", " T ",
                'T', new ItemStack(ConfigBlocks.blockTube, 1, 0),
                'P', new ItemStack(Blocks.PISTON),
                'A', new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1),
                'C', new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9));

        registerArcaneRecipe("EssentiaCrystalizer", "ESSENTIACRYSTAL",
                new ItemStack(ConfigBlocks.blockTube, 1, 7),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 15).add(Aspect.ORDER, 5),
                "IDI", "QCQ", "WTW",
                'T', new ItemStack(ConfigBlocks.blockTube, 1, 0),
                'D', new ItemStack(Blocks.DISPENSER),
                'Q', new ItemStack(ConfigItems.itemShard, 1, 6),
                'I', "ingotIron",
                'W', "plankWood",
                'C', new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9));

        registerArcaneRecipe("MnemonicMatrix", "THAUMATORIUM",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 12),
                new AspectList().add(Aspect.FIRE, 5).add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                "IAI", "ABA", "IAI",
                'B', new ItemStack(ConfigItems.itemZombieBrain),
                'A', new ItemStack(ConfigItems.itemResource, 1, 6),
                'I', new ItemStack(Items.IRON_INGOT));
    }

    private static void registerArcaneRecipe(String key, String research, ItemStack output, AspectList aspects, Object... recipe) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addArcaneCraftingRecipe(research, output, aspects, recipe));
    }

    private static void registerShapelessArcaneRecipe(String key, String research, ItemStack output, AspectList aspects, Object... recipe) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addShapelessArcaneCraftingRecipe(research, output, aspects, recipe));
    }

    private static void initializeInfusionWandRecipeBaseline() {
        if (Config.foundSilverIngot) {
            registerInfusionRecipe("WandCapSilver", "CAP_silver",
                    new ItemStack(ConfigItems.itemWandCap, 1, 4),
                    4,
                    new AspectList().add(Aspect.ENERGY, getWandCapCost("silver") * 2)
                            .add(Aspect.AURA, getWandCapCost("silver")),
                    new ItemStack(ConfigItems.itemWandCap, 1, 5),
                    new ItemStack(ConfigItems.itemResource, 1, 14),
                    new ItemStack(ConfigItems.itemResource, 1, 14));
        }

        registerInfusionRecipe("WandCapThaumium", "CAP_thaumium",
                new ItemStack(ConfigItems.itemWandCap, 1, 2),
                5,
                new AspectList().add(Aspect.ENERGY, getWandCapCost("thaumium") * 2)
                        .add(Aspect.AURA, getWandCapCost("thaumium")),
                new ItemStack(ConfigItems.itemWandCap, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionRecipe("WandCapVoid", "CAP_void",
                new ItemStack(ConfigItems.itemWandCap, 1, 7),
                8,
                new AspectList().add(Aspect.ENERGY, getWandCapCost("void") * 2)
                        .add(Aspect.VOID, getWandCapCost("void") * 2)
                        .add(Aspect.ELDRITCH, getWandCapCost("void") * 2)
                        .add(Aspect.AURA, getWandCapCost("void") * 2),
                new ItemStack(ConfigItems.itemWandCap, 1, 8),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionRecipe("WandRodObsidian", "ROD_obsidian",
                new ItemStack(ConfigItems.itemWandRod, 1, 1),
                3,
                new AspectList().add(Aspect.EARTH, getWandRodCost("obsidian") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("obsidian"))
                        .add(Aspect.DARKNESS, getWandRodCost("blaze")),
                new ItemStack(Blocks.OBSIDIAN),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 3));

        registerInfusionRecipe("WandRodIce", "ROD_ice",
                new ItemStack(ConfigItems.itemWandRod, 1, 3),
                3,
                new AspectList().add(Aspect.WATER, getWandRodCost("ice") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("ice"))
                        .add(Aspect.COLD, getWandRodCost("blaze")),
                new ItemStack(Blocks.PACKED_ICE),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 2));

        registerInfusionRecipe("WandRodQuartz", "ROD_quartz",
                new ItemStack(ConfigItems.itemWandRod, 1, 4),
                3,
                new AspectList().add(Aspect.ORDER, getWandRodCost("quartz") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("quartz"))
                        .add(Aspect.CRYSTAL, getWandRodCost("blaze")),
                new ItemStack(Blocks.QUARTZ_BLOCK),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 4));

        registerInfusionRecipe("WandRodReed", "ROD_reed",
                new ItemStack(ConfigItems.itemWandRod, 1, 5),
                3,
                new AspectList().add(Aspect.AIR, getWandRodCost("reed") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("reed"))
                        .add(Aspect.MOTION, getWandRodCost("blaze")),
                new ItemStack(Items.REEDS),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 0));

        registerInfusionRecipe("WandRodBlaze", "ROD_blaze",
                new ItemStack(ConfigItems.itemWandRod, 1, 6),
                3,
                new AspectList().add(Aspect.FIRE, getWandRodCost("blaze") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("blaze"))
                        .add(Aspect.BEAST, getWandRodCost("blaze")),
                new ItemStack(Items.BLAZE_ROD),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 1));

        registerInfusionRecipe("WandRodBone", "ROD_bone",
                new ItemStack(ConfigItems.itemWandRod, 1, 7),
                3,
                new AspectList().add(Aspect.ENTROPY, getWandRodCost("bone") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("bone"))
                        .add(Aspect.UNDEAD, getWandRodCost("blaze")),
                new ItemStack(Items.BONE),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 5));

        registerInfusionRecipe("WandRodSilverwood", "ROD_silverwood",
                new ItemStack(ConfigItems.itemWandRod, 1, 2),
                5,
                new AspectList().add(Aspect.AIR, getWandRodCost("silverwood"))
                        .add(Aspect.FIRE, getWandRodCost("silverwood"))
                        .add(Aspect.WATER, getWandRodCost("silverwood"))
                        .add(Aspect.EARTH, getWandRodCost("silverwood"))
                        .add(Aspect.ORDER, getWandRodCost("silverwood"))
                        .add(Aspect.ENTROPY, getWandRodCost("silverwood"))
                        .add(Aspect.MAGIC, getWandRodCost("silverwood")),
                new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(ConfigItems.itemShard, 1, 4),
                new ItemStack(ConfigItems.itemShard, 1, 5));

        registerInfusionRecipe("WandRodPrimalStaff", "ROD_primal_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 100),
                8,
                new AspectList().add(Aspect.AIR, getWandRodCost("primal_staff"))
                        .add(Aspect.FIRE, getWandRodCost("primal_staff"))
                        .add(Aspect.WATER, getWandRodCost("primal_staff"))
                        .add(Aspect.EARTH, getWandRodCost("primal_staff"))
                        .add(Aspect.ORDER, getWandRodCost("primal_staff"))
                        .add(Aspect.ENTROPY, getWandRodCost("primal_staff"))
                        .add(Aspect.MAGIC, getWandRodCost("primal_staff") * 2),
                new ItemStack(ConfigItems.itemWandRod, 1, 2),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigItems.itemWandRod, 1, 1),
                new ItemStack(ConfigItems.itemWandRod, 1, 3),
                new ItemStack(ConfigItems.itemWandRod, 1, 4),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigItems.itemWandRod, 1, 5),
                new ItemStack(ConfigItems.itemWandRod, 1, 6),
                new ItemStack(ConfigItems.itemWandRod, 1, 7));
    }

    private static void initializeInfusionEnchantmentRecipeBaseline() {
        registerInfusionEnchantmentRecipe("InfEnchRepair", "INFUSIONENCHANTMENT",
                Config.enchRepair,
                4,
                new AspectList().add(Aspect.MAGIC, 8).add(Aspect.CRAFT, 10).add(Aspect.ORDER, 10),
                new ItemStack(Blocks.ANVIL),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnchHaste", "INFUSIONENCHANTMENT",
                Config.enchHaste,
                3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TRAVEL, 8).add(Aspect.FLIGHT, 8),
                new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch0", "INFUSIONENCHANTMENT",
                Enchantments.PROTECTION,
                1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 8),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch1", "INFUSIONENCHANTMENT",
                Enchantments.FIRE_PROTECTION,
                1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 4),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.MAGMA_CREAM),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch2", "INFUSIONENCHANTMENT",
                Enchantments.FEATHER_FALLING,
                1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 4).add(Aspect.ENTROPY, 4),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.GUNPOWDER),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch3", "INFUSIONENCHANTMENT",
                Enchantments.BLAST_PROTECTION,
                1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 4).add(Aspect.FLIGHT, 4),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.ARROW),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch4", "INFUSIONENCHANTMENT",
                Enchantments.PROJECTILE_PROTECTION,
                1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.AIR, 4).add(Aspect.FLIGHT, 4),
                new ItemStack(Items.FEATHER),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch5", "INFUSIONENCHANTMENT",
                Enchantments.RESPIRATION,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.AIR, 8).add(Aspect.WATER, 8),
                new ItemStack(Items.REEDS),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch6", "INFUSIONENCHANTMENT",
                Enchantments.AQUA_AFFINITY,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.MOTION, 8).add(Aspect.WATER, 8),
                new ItemStack(Items.REEDS),
                new ItemStack(Items.SLIME_BALL),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch7", "INFUSIONENCHANTMENT",
                Enchantments.THORNS,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 8).add(Aspect.PLANT, 8),
                new ItemStack(Blocks.DEADBUSH),
                new ItemStack(Items.QUARTZ),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch8", "INFUSIONENCHANTMENT",
                Enchantments.SHARPNESS,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 8),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch9", "INFUSIONENCHANTMENT",
                Enchantments.SMITE,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.UNDEAD, 4),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch10", "INFUSIONENCHANTMENT",
                Enchantments.BANE_OF_ARTHROPODS,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.BEAST, 4),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(ConfigItems.itemResource, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch11", "INFUSIONENCHANTMENT",
                Enchantments.KNOCKBACK,
                1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 3).add(Aspect.MOTION, 3),
                new ItemStack(Blocks.PISTON),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch12", "INFUSIONENCHANTMENT",
                Enchantments.FIRE_ASPECT,
                3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.FIRE, 8),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch13", "INFUSIONENCHANTMENT",
                Enchantments.LOOTING,
                3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.GREED, 8),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch14", "INFUSIONENCHANTMENT",
                Enchantments.EFFICIENCY,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TOOL, 4).add(Aspect.ORDER, 4),
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch15", "INFUSIONENCHANTMENT",
                Enchantments.SILK_TOUCH,
                5,
                new AspectList().add(Aspect.MAGIC, 16).add(Aspect.TOOL, 16).add(Aspect.ORDER, 16)
                        .add(Aspect.HARVEST, 16).add(Aspect.MINE, 16),
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Blocks.WEB),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch16", "INFUSIONENCHANTMENT",
                Enchantments.UNBREAKING,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TOOL, 4).add(Aspect.ORDER, 8),
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Blocks.OBSIDIAN),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch17", "INFUSIONENCHANTMENT",
                Enchantments.FORTUNE,
                3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TOOL, 4).add(Aspect.GREED, 8),
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch18", "INFUSIONENCHANTMENT",
                Enchantments.POWER,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 8),
                new ItemStack(Items.BOW),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch19", "INFUSIONENCHANTMENT",
                Enchantments.PUNCH,
                2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 3).add(Aspect.MOTION, 3),
                new ItemStack(Blocks.PISTON),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch20", "INFUSIONENCHANTMENT",
                Enchantments.FLAME,
                3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.FIRE, 8),
                new ItemStack(Items.BOW),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch21", "INFUSIONENCHANTMENT",
                Enchantments.INFINITY,
                5,
                new AspectList().add(Aspect.MAGIC, 8).add(Aspect.WEAPON, 16).add(Aspect.VOID, 16).add(Aspect.EXCHANGE, 16),
                new ItemStack(Items.BOW),
                new ItemStack(Items.ARROW),
                new ItemStack(ConfigItems.itemResource, 1, 14));
    }

    private static void initializeInfusionFocusDeviceRecipeBaseline() {
        registerInfusionRecipe("FocusHellbat", "FOCUSHELLBAT",
                new ItemStack(ConfigItems.focusHellbat),
                3,
                new AspectList().add(Aspect.FIRE, 25).add(Aspect.AIR, 15).add(Aspect.BEAST, 15).add(Aspect.ENTROPY, 25),
                new ItemStack(Items.MAGMA_CREAM),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 5));

        registerInfusionRecipe("FocusPortableHole", "FOCUSPORTABLEHOLE",
                new ItemStack(ConfigItems.focusPortableHole),
                3,
                new AspectList().add(Aspect.TRAVEL, 25).add(Aspect.ELDRITCH, 10).add(Aspect.EXCHANGE, 10).add(Aspect.ENTROPY, 25),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 5));

        registerInfusionRecipe("FocusWarding", "FOCUSWARDING",
                new ItemStack(ConfigItems.focusWarding),
                4,
                new AspectList().add(Aspect.EARTH, 25).add(Aspect.ARMOR, 25).add(Aspect.ORDER, 25).add(Aspect.MIND, 10),
                new ItemStack(Items.NETHER_STAR),
                new ItemStack(ConfigItems.itemResource, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 4),
                new ItemStack(ConfigItems.itemResource, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 4));

        registerInfusionRecipe("WandPed", "WANDPED",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 5),
                3,
                new AspectList().add(Aspect.AURA, 10).add(Aspect.MAGIC, 15).add(Aspect.EXCHANGE, 15),
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 1),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(Items.DIAMOND));

        registerInfusionRecipe("WandPedFocus", "WANDPEDFOC",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 8),
                4,
                new AspectList().add(Aspect.ORDER, 10).add(Aspect.MAGIC, 15).add(Aspect.EXCHANGE, 10),
                new ItemStack(Items.COMPARATOR),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8));

        registerInfusionRecipe("NodeStabilizerAdv", "NODESTABILIZERADV",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 10),
                10,
                new AspectList().add(Aspect.AURA, 32).add(Aspect.MAGIC, 16).add(Aspect.ORDER, 16).add(Aspect.ENERGY, 16),
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(Blocks.REDSTONE_BLOCK),
                new ItemStack(ConfigItems.itemResource, 1, 0), new ItemStack(Blocks.REDSTONE_BLOCK),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(Blocks.REDSTONE_BLOCK),
                new ItemStack(ConfigItems.itemResource, 1, 0), new ItemStack(Blocks.REDSTONE_BLOCK));

        registerInfusionRecipe("JarBrain", "JARBRAIN",
                new ItemStack(ConfigBlocks.blockJar, 1, 1),
                4,
                new AspectList().add(Aspect.MIND, 10).add(Aspect.SENSES, 10).add(Aspect.UNDEAD, 20),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigItems.itemZombieBrain),
                new ItemStack(Items.SPIDER_EYE),
                new ItemStack(Items.WATER_BUCKET),
                new ItemStack(Items.SPIDER_EYE));

        if (Config.allowMirrors) {
            registerInfusionRecipe("Mirror", "MIRROR",
                    new ItemStack(ConfigBlocks.blockMirror, 1, 0),
                    1,
                    new AspectList().add(Aspect.TRAVEL, 8).add(Aspect.DARKNESS, 8).add(Aspect.EXCHANGE, 8),
                    new ItemStack(ConfigItems.itemResource, 1, 10),
                    new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(Items.ENDER_PEARL));

            registerInfusionRecipe("MirrorHand", "MIRRORHAND",
                    new ItemStack(ConfigItems.itemHandMirror),
                    5,
                    new AspectList().add(Aspect.TOOL, 16).add(Aspect.TRAVEL, 16),
                    new ItemStack(ConfigBlocks.blockMirror, 1, 0),
                    new ItemStack(Items.STICK),
                    new ItemStack(Items.COMPASS),
                    new ItemStack(Items.MAP));

            registerInfusionRecipe("MirrorEssentia", "MIRRORESSENTIA",
                    new ItemStack(ConfigBlocks.blockMirror, 1, 6),
                    2,
                    new AspectList().add(Aspect.TRAVEL, 8).add(Aspect.WATER, 8).add(Aspect.EXCHANGE, 8),
                    new ItemStack(ConfigItems.itemResource, 1, 10),
                    new ItemStack(Items.IRON_INGOT),
                    new ItemStack(Items.IRON_INGOT),
                    new ItemStack(Items.IRON_INGOT),
                    new ItemStack(Items.ENDER_PEARL));
        }
    }

    private static void initializeInfusionGolemDeviceRecipeBaseline() {
        registerInfusionRecipe("AdvancedGolem", "ADVANCEDGOLEM",
                new Object[]{"advanced", new NBTTagByte((byte) 1)},
                3,
                new AspectList().add(Aspect.MIND, 8).add(Aspect.SENSES, 8).add(Aspect.LIFE, 8),
                new ItemStack(ConfigItems.itemGolemPlacer, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(Items.REDSTONE),
                new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(Items.GUNPOWDER),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigItems.itemZombieBrain));

        registerInfusionRecipe("CoreAlchemy", "COREALCHEMY",
                new ItemStack(ConfigItems.itemGolemCore, 1, 6),
                2,
                new AspectList().add(Aspect.MAGIC, 15).add(Aspect.WATER, 15).add(Aspect.MOTION, 15),
                new ItemStack(ConfigItems.itemGolemCore, 1, 5),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(Items.POTIONITEM),
                new ItemStack(Items.POTIONITEM),
                new ItemStack(Items.POTIONITEM));

        registerInfusionRecipe("CoreSorting", "CORESORTING",
                new ItemStack(ConfigItems.itemGolemCore, 1, 10),
                3,
                new AspectList().add(Aspect.VOID, 16).add(Aspect.EXCHANGE, 16).add(Aspect.HUNGER, 16).add(Aspect.GREED, 16),
                new ItemStack(ConfigItems.itemZombieBrain),
                new ItemStack(ConfigItems.itemGolemCore, 1, 0),
                new ItemStack(Items.COMPARATOR),
                new ItemStack(ConfigItems.itemGolemCore, 1, 1),
                new ItemStack(Items.PAPER));

        registerInfusionRecipe("CoreLumber", "CORELUMBER",
                new ItemStack(ConfigItems.itemGolemCore, 1, 7),
                2,
                new AspectList().add(Aspect.TOOL, 16).add(Aspect.TREE, 16).add(Aspect.HARVEST, 16),
                new ItemStack(ConfigItems.itemGolemCore, 1, 3),
                new ItemStack(ConfigItems.itemAxeElemental),
                new ItemStack(Items.IRON_AXE),
                new ItemStack(Items.IRON_AXE),
                new ItemStack(Items.IRON_AXE));

        registerInfusionRecipe("CoreFishing", "COREFISHING",
                new ItemStack(ConfigItems.itemGolemCore, 1, 11),
                3,
                new AspectList().add(Aspect.WATER, 16).add(Aspect.HARVEST, 16).add(Aspect.BEAST, 16),
                new ItemStack(ConfigItems.itemGolemCore, 1, 3),
                new ItemStack(Items.FISHING_ROD),
                new ItemStack(Items.FISH, 1, 0),
                new ItemStack(Items.FISH, 1, 3),
                new ItemStack(Items.FISH, 1, 1));

        registerInfusionRecipe("CoreUse", "COREUSE",
                new ItemStack(ConfigItems.itemGolemCore, 1, 8),
                3,
                new AspectList().add(Aspect.TOOL, 20).add(Aspect.MECHANISM, 20).add(Aspect.MAN, 20),
                new ItemStack(ConfigItems.itemGolemCore, 1, 1),
                new ItemStack(Items.COMPARATOR),
                new ItemStack(Items.FLINT_AND_STEEL),
                new ItemStack(Items.SHEARS),
                new ItemStack(Blocks.LEVER));

        registerInfusionRecipe("ArcaneBore", "ARCANEBORE",
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 5),
                4,
                new AspectList().add(Aspect.ENERGY, 16).add(Aspect.MINE, 32).add(Aspect.MECHANISM, 32)
                        .add(Aspect.VOID, 16).add(Aspect.MOTION, 16),
                new ItemStack(Blocks.PISTON),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DIAMOND_PICKAXE),
                new ItemStack(Items.DIAMOND_SHOVEL),
                new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 3));

        registerInfusionRecipe("LampGrowth", "LAMPGROWTH",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 8),
                4,
                new AspectList().add(Aspect.PLANT, 16).add(Aspect.LIGHT, 8).add(Aspect.LIFE, 16),
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 7),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DYE, 1, 15),
                new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DYE, 1, 15),
                new ItemStack(ConfigItems.itemShard, 1, 3));

        registerInfusionRecipe("LampFertility", "LAMPFERTILITY",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 13),
                4,
                new AspectList().add(Aspect.BEAST, 16).add(Aspect.LIFE, 16).add(Aspect.LIGHT, 8),
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 7),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.WHEAT),
                new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.CARROT),
                new ItemStack(ConfigItems.itemShard, 1, 1));

        registerInfusionRecipe("EssentiaReservoir", "ESSENTIARESERVOIR",
                new ItemStack(ConfigBlocks.blockEssentiaReservoir),
                6,
                new AspectList().add(Aspect.WATER, 8).add(Aspect.VOID, 8).add(Aspect.MAGIC, 8).add(Aspect.EXCHANGE, 8),
                new ItemStack(ConfigBlocks.blockTube, 1, 4),
                new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigBlocks.blockJar, 1, 0));
    }

    private static void initializeInfusionEquipmentArmorRecipeBaseline() {
        registerInfusionRecipe("HoverHarness", "HOVERHARNESS",
                new ItemStack(ConfigItems.itemHoverHarness),
                6,
                new AspectList().add(Aspect.FLIGHT, 32).add(Aspect.ENERGY, 32).add(Aspect.MECHANISM, 32).add(Aspect.TRAVEL, 16),
                new ItemStack(Items.LEATHER_CHESTPLATE),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6), new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                new ItemStack(Items.COMPARATOR), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_INGOT));

        registerInfusionRecipe("HoverGirdle", "HOVERGIRDLE",
                new ItemStack(ConfigItems.itemGirdleHover),
                8,
                new AspectList().add(Aspect.FLIGHT, 16).add(Aspect.ENERGY, 32).add(Aspect.AIR, 32).add(Aspect.TRAVEL, 16),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(Items.FEATHER), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(Items.FEATHER), new ItemStack(Items.GOLD_INGOT));

        registerInfusionRecipe("VisAmulet", "VISAMULET",
                new ItemStack(ConfigItems.itemAmuletVis, 1, 1),
                6,
                new AspectList().add(Aspect.AURA, 24).add(Aspect.ENERGY, 64).add(Aspect.MAGIC, 64).add(Aspect.VOID, 24),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigBlocks.blockCrystal, 1, 6),
                new ItemStack(ConfigBlocks.blockCrystal, 1, 6), new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigBlocks.blockCrystal, 1, 6), new ItemStack(ConfigBlocks.blockCrystal, 1, 6));

        registerInfusionRecipe("RunicAmulet", "RUNICARMOR",
                new ItemStack(ConfigItems.itemAmuletRunic, 1, 0),
                4,
                new AspectList().add(Aspect.ARMOR, 20).add(Aspect.MAGIC, 35).add(Aspect.ENERGY, 35),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigItems.itemResource, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemInkwell));

        registerInfusionRecipe("RunicAmuletEmergency", "RUNICEMERGENCY",
                new ItemStack(ConfigItems.itemAmuletRunic, 1, 1),
                7,
                new AspectList().add(Aspect.ARMOR, 20).add(Aspect.MAGIC, 35).add(Aspect.EARTH, 32).add(Aspect.VOID, 32),
                new ItemStack(ConfigItems.itemAmuletRunic, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(Items.POTIONITEM, 1, 8233),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 3));

        registerInfusionRecipe("RunicRing", "RUNICARMOR",
                new ItemStack(ConfigItems.itemRingRunic, 1, 1),
                3,
                new AspectList().add(Aspect.ARMOR, 10).add(Aspect.MAGIC, 25).add(Aspect.ENERGY, 25),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1),
                new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigItems.itemResource, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemInkwell));

        registerInfusionRecipe("RunicRingCharged", "RUNICCHARGED",
                new ItemStack(ConfigItems.itemRingRunic, 1, 2),
                6,
                new AspectList().add(Aspect.ARMOR, 16).add(Aspect.MAGIC, 16).add(Aspect.ENERGY, 64),
                new ItemStack(ConfigItems.itemRingRunic, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(Items.POTIONITEM, 1, 8226),
                new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 1));

        registerInfusionRecipe("RunicRingHealing", "RUNICHEALING",
                new ItemStack(ConfigItems.itemRingRunic, 1, 3),
                6,
                new AspectList().add(Aspect.ARMOR, 16).add(Aspect.MAGIC, 16).add(Aspect.WATER, 32).add(Aspect.HEAL, 32),
                new ItemStack(ConfigItems.itemRingRunic, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 2), new ItemStack(Items.POTIONITEM, 1, 8257),
                new ItemStack(ConfigItems.itemShard, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 2));

        registerInfusionRecipe("RunicGirdle", "RUNICARMOR",
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 0),
                4,
                new AspectList().add(Aspect.ARMOR, 30).add(Aspect.MAGIC, 50).add(Aspect.ENERGY, 50),
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2),
                new ItemStack(ConfigItems.itemResource, 1, 15), new ItemStack(ConfigItems.itemResource, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemInkwell));

        registerInfusionRecipe("RunicGirdleKinetic", "RUNICKINETIC",
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 1),
                7,
                new AspectList().add(Aspect.ARMOR, 33).add(Aspect.MAGIC, 55).add(Aspect.AIR, 64),
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(Items.POTIONITEM, 1, 16428),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 0));

        registerInfusionRecipe("RunicGirdleKinetic_2", "RUNICKINETIC",
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 1),
                7,
                new AspectList().add(Aspect.ARMOR, 33).add(Aspect.MAGIC, 55).add(Aspect.AIR, 64),
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 6), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(Items.POTIONITEM, 1, 24620),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 0));

        registerInfusionRecipe("ElementalAxe", "ELEMENTALAXE",
                new ItemStack(ConfigItems.itemAxeElemental),
                1,
                new AspectList().add(Aspect.WATER, 16).add(Aspect.TREE, 8),
                new ItemStack(ConfigItems.itemAxeThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(Items.DIAMOND), new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        registerInfusionRecipe("ElementalPick", "ELEMENTALPICK",
                new ItemStack(ConfigItems.itemPickElemental),
                1,
                new AspectList().add(Aspect.FIRE, 8).add(Aspect.MINE, 8).add(Aspect.SENSES, 8),
                new ItemStack(ConfigItems.itemPickThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(Items.DIAMOND), new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        registerInfusionRecipe("ElementalSword", "ELEMENTALSWORD",
                new ItemStack(ConfigItems.itemSwordElemental),
                1,
                new AspectList().add(Aspect.AIR, 8).add(Aspect.MOTION, 8).add(Aspect.ENERGY, 8),
                new ItemStack(ConfigItems.itemSwordThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(Items.DIAMOND), new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        registerInfusionRecipe("ElementalShovel", "ELEMENTALSHOVEL",
                new ItemStack(ConfigItems.itemShovelElemental),
                1,
                new AspectList().add(Aspect.EARTH, 16).add(Aspect.CRAFT, 8),
                new ItemStack(ConfigItems.itemShovelThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.DIAMOND), new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        registerInfusionRecipe("ElementalHoe", "ELEMENTALHOE",
                new ItemStack(ConfigItems.itemHoeElemental),
                1,
                new AspectList().add(Aspect.HARVEST, 8).add(Aspect.PLANT, 8).add(Aspect.EARTH, 8),
                new ItemStack(ConfigItems.itemHoeThaumium),
                new ItemStack(ConfigItems.itemShard, 1, 4), new ItemStack(ConfigItems.itemShard, 1, 5),
                new ItemStack(Items.DIAMOND), new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        registerInfusionRecipe("BootsTraveller", "BOOTSTRAVELLER",
                new ItemStack(ConfigItems.itemBootsTraveller),
                1,
                new AspectList().add(Aspect.FLIGHT, 25).add(Aspect.TRAVEL, 25),
                new ItemStack(Items.LEATHER_BOOTS),
                new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 7),
                new ItemStack(Items.FEATHER), new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE));

        registerInfusionRecipe("TravelTrunk", "TRAVELTRUNK",
                new ItemStack(ConfigItems.itemTrunkSpawner),
                3,
                new AspectList().add(Aspect.MOTION, 4).add(Aspect.SOUL, 4).add(Aspect.TRAVEL, 4).add(Aspect.VOID, 16),
                new ItemStack(ConfigBlocks.blockChestHungry),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 1),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6));

        registerInfusionRecipe("ThaumiumFortressHelm", "ARMORFORTRESS",
                new ItemStack(ConfigItems.itemHelmFortress),
                3,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 16).add(Aspect.MAGIC, 16),
                new ItemStack(ConfigItems.itemHelmThaumium),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD));

        registerInfusionRecipe("ThaumiumFortressChest", "ARMORFORTRESS",
                new ItemStack(ConfigItems.itemChestFortress),
                3,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 24).add(Aspect.MAGIC, 16),
                new ItemStack(ConfigItems.itemChestThaumium),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.LEATHER));

        registerInfusionRecipe("ThaumiumFortressLegs", "ARMORFORTRESS",
                new ItemStack(ConfigItems.itemLegsFortress),
                3,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 20).add(Aspect.MAGIC, 16),
                new ItemStack(ConfigItems.itemLegsThaumium),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.LEATHER));

        registerInfusionRecipe("VoidRobeHelm", "ARMORVOIDFORTRESS",
                new ItemStack(ConfigItems.itemHelmVoidRobe),
                6,
                new AspectList().add(Aspect.METAL, 16).add(Aspect.SENSES, 16).add(Aspect.ARMOR, 16)
                        .add(Aspect.CLOTH, 16).add(Aspect.MAGIC, 16).add(Aspect.ELDRITCH, 16).add(Aspect.VOID, 16),
                new ItemStack(ConfigItems.itemHelmVoid),
                new ItemStack(ConfigItems.itemGoggles), new ItemStack(ConfigItems.itemResource, 1, 7),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(ConfigItems.itemResource, 1, 7));

        registerInfusionRecipe("VoidRobeChest", "ARMORVOIDFORTRESS",
                new ItemStack(ConfigItems.itemChestVoidRobe),
                6,
                new AspectList().add(Aspect.METAL, 24).add(Aspect.ARMOR, 24).add(Aspect.CLOTH, 24)
                        .add(Aspect.MAGIC, 16).add(Aspect.ELDRITCH, 16).add(Aspect.VOID, 24),
                new ItemStack(ConfigItems.itemChestVoid),
                new ItemStack(ConfigItems.itemChestRobe), new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(Items.LEATHER));

        registerInfusionRecipe("VoidRobeLegs", "ARMORVOIDFORTRESS",
                new ItemStack(ConfigItems.itemLegsVoidRobe),
                6,
                new AspectList().add(Aspect.METAL, 20).add(Aspect.ARMOR, 20).add(Aspect.CLOTH, 20)
                        .add(Aspect.MAGIC, 16).add(Aspect.ELDRITCH, 16).add(Aspect.VOID, 20),
                new ItemStack(ConfigItems.itemLegsVoid),
                new ItemStack(ConfigItems.itemLegsRobe), new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 7), new ItemStack(Items.LEATHER));

        registerInfusionRecipe("HelmGoggles", "HELMGOGGLES",
                new Object[]{"goggles", new NBTTagByte((byte) 1)},
                5,
                new AspectList().add(Aspect.SENSES, 32).add(Aspect.AURA, 16).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(Items.SLIME_BALL), new ItemStack(ConfigItems.itemGoggles, 1, OreDictionary.WILDCARD_VALUE));

        registerInfusionRecipe("MaskGrinningDevil", "MASKGRINNINGDEVIL",
                new Object[]{"mask", new NBTTagInt(0)},
                8,
                new AspectList().add(Aspect.MIND, 64).add(Aspect.HEAL, 64).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(Items.DYE, 1, 0), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.LEATHER),
                new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2), new ItemStack(ConfigItems.itemZombieBrain),
                new ItemStack(Items.IRON_INGOT));

        registerInfusionRecipe("MaskAngryGhost", "MASKANGRYGHOST",
                new Object[]{"mask", new NBTTagInt(1)},
                8,
                new AspectList().add(Aspect.ENTROPY, 64).add(Aspect.DEATH, 64).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.LEATHER),
                new ItemStack(Items.POISONOUS_POTATO), new ItemStack(Items.SKULL, 1, 1), new ItemStack(Items.IRON_INGOT));

        registerInfusionRecipe("MaskSippingFiend", "MASKSIPPINGFIEND",
                new Object[]{"mask", new NBTTagInt(2)},
                8,
                new AspectList().add(Aspect.UNDEAD, 64).add(Aspect.LIFE, 64).add(Aspect.ARMOR, 16),
                new ItemStack(ConfigItems.itemHelmFortress, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(Items.DYE, 1, 1), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.LEATHER),
                new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.IRON_INGOT));

        registerInfusionRecipe("SanityCheck", "SANITYCHECK",
                new ItemStack(ConfigItems.itemSanityChecker),
                4,
                new AspectList().add(Aspect.MIND, 24).add(Aspect.SENSES, 24).add(Aspect.ELDRITCH, 8),
                new ItemStack(ConfigItems.itemThaumometer),
                new ItemStack(ConfigItems.itemResource, 1, 10), new ItemStack(ConfigItems.itemZombieBrain),
                new ItemStack(Items.DIAMOND));

        registerInfusionRecipe("SinStone", "SINSTONE",
                new ItemStack(ConfigItems.itemCompassStone),
                5,
                new AspectList().add(Aspect.SENSES, 8).add(Aspect.DARKNESS, 8).add(Aspect.ELDRITCH, 8).add(Aspect.AURA, 8),
                new ItemStack(Items.FLINT),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 4),
                new ItemStack(ConfigItems.itemResource, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 5));

        registerInfusionRecipe("PrimalCrusher", "PRIMALCRUSHER",
                new ItemStack(ConfigItems.itemPrimalCrusher),
                6,
                new AspectList().add(Aspect.MINE, 24).add(Aspect.TOOL, 24).add(Aspect.ENTROPY, 16)
                        .add(Aspect.VOID, 16).add(Aspect.WEAPON, 16).add(Aspect.ELDRITCH, 16).add(Aspect.GREED, 16),
                new ItemStack(ConfigItems.itemEldritchObject, 1, 3),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigItems.itemPickVoid, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(ConfigItems.itemShovelVoid, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigItems.itemPickElemental, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(ConfigItems.itemShovelElemental, 1, OreDictionary.WILDCARD_VALUE));

        registerInfusionRecipe("EldritchEye", "OCULUS",
                new ItemStack(ConfigItems.itemEldritchObject),
                5,
                new AspectList().add(Aspect.ELDRITCH, 64).add(Aspect.VOID, 16).add(Aspect.DARKNESS, 16).add(Aspect.TRAVEL, 16),
                new ItemStack(Items.ENDER_EYE),
                new ItemStack(ConfigItems.itemResource, 1, 17), new ItemStack(Items.GOLD_INGOT));
    }

    private static void registerInfusionRecipe(String key, String research, Object output, int instability, AspectList aspects,
                                               ItemStack centralInput, ItemStack... components) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addInfusionCraftingRecipe(
                research,
                output,
                instability,
                aspects,
                centralInput,
                components));
    }

    private static void registerInfusionEnchantmentRecipe(String key, String research, Enchantment enchantment, int instability,
                                                          AspectList aspects, ItemStack... components) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addInfusionEnchantmentRecipe(
                research,
                enchantment,
                instability,
                aspects,
                components));
    }

    private static int getWandCapCost(String tag) {
        WandCap cap = WandCap.caps.get(tag);
        return cap != null ? cap.getCraftCost() : 0;
    }

    private static int getWandRodCost(String tag) {
        WandRod rod = WandRod.rods.get(tag);
        return rod != null ? rod.getCraftCost() : 0;
    }

    public static void oreDictRecipe(Object input, Object[] output) {
        // Stage 9: ore dictionary recipe registration.
    }

    public static void registerSpecialRecipes(IForgeRegistry<IRecipe> registry) {
        if (specialRecipesRegistered) {
            return;
        }
        registry.register(new RecipesRobeArmorDyes().setRegistryName("forge", "robearmordye"));
        registry.register(new RecipesVoidRobeArmorDyes().setRegistryName("forge", "voidrobearmordye"));
        recipeArcaneStone2 = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 7),
                "SS",
                "SS",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6))
                .setRegistryName("thaumcraft", "arcanestone2");
        registry.register(recipeArcaneStone2);
        recipeArcaneStone3 = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockStairsArcaneStone, 4, 0),
                "K  ",
                "KK ",
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7))
                .setRegistryName("thaumcraft", "arcanestone3");
        registry.register(recipeArcaneStone3);
        recipeArcaneStone4 = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockSlabStone, 6, 0),
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7))
                .setRegistryName("thaumcraft", "arcanestone4");
        registry.register(recipeArcaneStone4);

        recipeKnowFrag = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemResearchNotes, 1, 42),
                "KKK",
                "KKK",
                "KKK",
                'K', new ItemStack(ConfigItems.itemResource, 1, 9))
                .setRegistryName("thaumcraft", "knowfrag");
        registry.register(recipeKnowFrag);

        recipePlankGreatwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 4, 6),
                "W",
                'W', new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0))
                .setRegistryName("thaumcraft", "plankgreatwood");
        registry.register(recipePlankGreatwood);

        recipePlankSilverwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 4, 7),
                "W",
                'W', new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1))
                .setRegistryName("thaumcraft", "planksilverwood");
        registry.register(recipePlankSilverwood);

        recipeGrate = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 5),
                "#",
                "T",
                '#', new ItemStack(Blocks.IRON_BARS),
                'T', new ItemStack(Blocks.TRAPDOOR))
                .setRegistryName("thaumcraft", "grate");
        registry.register(recipeGrate);

        recipePhial = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemEssence, 8, 0),
                " C ",
                "G G",
                " G ",
                'G', Blocks.GLASS,
                'C', Items.CLAY_BALL)
                .setRegistryName("thaumcraft", "phial");
        registry.register(recipePhial);

        recipeTable = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockTable, 1, 0),
                "SSS",
                "W W",
                'S', "slabWood",
                'W', "plankWood")
                .setRegistryName("thaumcraft", "table");
        registry.register(recipeTable);

        recipeScribe1 = new ShapelessOreRecipe(null,
                new ItemStack(ConfigItems.itemInkwell),
                new ItemStack(ConfigItems.itemEssence, 1, 0),
                Items.FEATHER,
                "dyeBlack")
                .setRegistryName("thaumcraft", "scribe1");
        registry.register(recipeScribe1);

        recipeScribe2 = new ShapelessOreRecipe(null,
                new ItemStack(ConfigItems.itemInkwell),
                Items.FEATHER,
                Items.DYE,
                "dyeBlack")
                .setRegistryName("thaumcraft", "scribe2");
        registry.register(recipeScribe2);

        recipeScribe3 = new ShapelessOreRecipe(null,
                new ItemStack(ConfigItems.itemInkwell),
                new ItemStack(ConfigItems.itemInkwell, 1, OreDictionary.WILDCARD_VALUE),
                "dyeBlack")
                .setRegistryName("thaumcraft", "scribe3");
        registry.register(recipeScribe3);

        recipeThaumometer = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemThaumometer),
                " 1 ",
                "IGI",
                " 1 ",
                'I', Items.IRON_INGOT,
                'G', Blocks.GLASS,
                '1', new ItemStack(ConfigItems.itemShard, 1, OreDictionary.WILDCARD_VALUE))
                .setRegistryName("thaumcraft", "thaumometer");
        registry.register(recipeThaumometer);

        recipeWandCapIron = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemWandCap, 1, 0),
                "NNN",
                "N N",
                'N', "nuggetIron")
                .setRegistryName("thaumcraft", "wandcapiron");
        registry.register(recipeWandCapIron);

        recipeWandBasic = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemWandCasting, 1, 0),
                "  I",
                " S ",
                "I  ",
                'I', new ItemStack(ConfigItems.itemWandCap, 1, 0),
                'S', "stickWood")
                .setRegistryName("thaumcraft", "wandbasic");
        registry.register(recipeWandBasic);
        specialRecipesRegistered = true;
    }
}
