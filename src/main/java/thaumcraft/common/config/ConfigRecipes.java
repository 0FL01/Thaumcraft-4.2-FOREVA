package thaumcraft.common.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;
import thaumcraft.common.items.armor.RecipesRobeArmorDyes;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.items.armor.RecipesVoidRobeArmorDyes;

public class ConfigRecipes {
    private static boolean specialRecipesRegistered = false;

    public static void init() {
        ConfigResearch.recipes.clear();
        initializeArcaneRecipeBaseline();

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

    private static void initializeArcaneRecipeBaseline() {
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

        if (Config.wardedStone) {
            registerArcaneRecipe("GoldKey", "WARDEDARCANA",
                    new ItemStack(ConfigItems.itemKey, 2, 1),
                    new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                    "NNI", "N  ",
                    'I', Items.GOLD_INGOT,
                    'N', Items.GOLD_NUGGET);
        }

        registerArcaneRecipe("ArcaneStone1", "ARCANESTONE",
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6),
                new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1),
                "SSS", "SCS", "SSS",
                'S', "stone",
                'C', new ItemStack(ConfigItems.itemShard, 1, OreDictionary.WILDCARD_VALUE));

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
        specialRecipesRegistered = true;
    }
}
