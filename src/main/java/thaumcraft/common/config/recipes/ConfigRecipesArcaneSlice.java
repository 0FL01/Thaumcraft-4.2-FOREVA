package thaumcraft.common.config.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.research.ConfigResearch;

public class ConfigRecipesArcaneSlice {

    public static void initializeArcaneRecipeBaseline() {
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

        registerArcaneRecipe("IronKey", "WARDEDARCANA",
                new ItemStack(ConfigItems.itemKey, 2, 0),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                "NNI", "N  ",
                'I', Items.IRON_INGOT,
                'N', "nuggetIron");

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

        registerArcaneRecipe("ArcaneStone1", "ARCANESTONE",
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6),
                new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1),
                "SSS", "SCS", "SSS",
                'S', "stone",
                'C', new ItemStack(ConfigItems.itemShard, 1, OreDictionary.WILDCARD_VALUE));

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

        registerArcaneRecipe("WardedJar", "DISTILESSENTIA",
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new AspectList().add(Aspect.WATER, 1),
                "GWG", "G G", "GGG",
                'W', "slabWood",
                'G', Blocks.GLASS_PANE);

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

        registerArcaneRecipe("HungryChest", "HUNGRYCHEST",
                new ItemStack(ConfigBlocks.blockChestHungry),
                new AspectList().add(Aspect.AIR, 5).add(Aspect.ORDER, 3).add(Aspect.ENTROPY, 3),
                "WTW", "W W", "WWW",
                'W', "plankWood",
                'T', Blocks.TRAPDOOR);

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

    private static int getWandCapCost(String tag) {
        WandCap cap = WandCap.caps.get(tag);
        return cap != null ? cap.getCraftCost() : 0;
    }

    private static int getWandRodCost(String tag) {
        WandRod rod = WandRod.rods.get(tag);
        return rod != null ? rod.getCraftCost() : 0;
    }

}
