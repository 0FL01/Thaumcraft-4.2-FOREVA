package thaumcraft.common.config.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.research.ConfigResearch;

public class ConfigRecipesCrucibleSlice {

    public static CrucibleRecipeHandles initializeCrucibleRecipeBaseline() {
        Aspect[] primal = new Aspect[]{Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY};
        for (int a = 0; a < 6; a++) {
            AspectList costs = new AspectList();
            for (int b = 0; b < 6; b++) {
                if (b != a) {
                    costs.add(primal[b], 2);
                }
            }
            ConfigResearch.recipes.put("BalancedShard_" + a,
                    ThaumcraftApi.addCrucibleRecipe(
                            "CRUCIBLE",
                            new ItemStack(ConfigItems.itemShard, 1, 6),
                            new ItemStack(ConfigItems.itemShard, 1, a),
                            costs));
        }

        CrucibleRecipe recipeAlumentum = ThaumcraftApi.addCrucibleRecipe(
                "ALUMENTUM",
                new ItemStack(ConfigItems.itemResource, 1, 0),
                new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().merge(Aspect.ENERGY, 3).merge(Aspect.FIRE, 3).merge(Aspect.ENTROPY, 3));

        CrucibleRecipe recipeNitor = ThaumcraftApi.addCrucibleRecipe(
                "NITOR",
                new ItemStack(ConfigItems.itemResource, 1, 1),
                "dustGlowstone",
                new AspectList().merge(Aspect.ENERGY, 3).merge(Aspect.FIRE, 3).merge(Aspect.LIGHT, 3));

        CrucibleRecipe recipeThaumium = ThaumcraftApi.addCrucibleRecipe(
                "THAUMIUM",
                new ItemStack(ConfigItems.itemResource, 1, 2),
                new ItemStack(Items.IRON_INGOT),
                new AspectList().merge(Aspect.MAGIC, 4));
        ConfigResearch.recipes.put("Tallow",
                ThaumcraftApi.addCrucibleRecipe(
                        "TALLOW",
                        new ItemStack(ConfigItems.itemResource, 1, 4),
                        new ItemStack(Items.ROTTEN_FLESH),
                        new AspectList().merge(Aspect.MAGIC, 2)));
        ConfigResearch.recipes.put("AltGunpowder",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALDUPLICATION",
                        new ItemStack(Items.GUNPOWDER, 2, 0),
                        new ItemStack(Items.GUNPOWDER),
                        new AspectList().merge(Aspect.FIRE, 4).merge(Aspect.ENTROPY, 4)));
        ConfigResearch.recipes.put("AltSlime",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALDUPLICATION",
                        new ItemStack(Items.SLIME_BALL, 2, 0),
                        new ItemStack(Items.SLIME_BALL),
                        new AspectList().merge(Aspect.WATER, 2).merge(Aspect.LIFE, 2)));
        ConfigResearch.recipes.put("AltClay",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALDUPLICATION",
                        new ItemStack(Items.CLAY_BALL, 2, 0),
                        new ItemStack(Items.CLAY_BALL),
                        new AspectList().merge(Aspect.WATER, 1).merge(Aspect.EARTH, 2)));
        ConfigResearch.recipes.put("AltGlowstone",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALDUPLICATION",
                        new ItemStack(Items.GLOWSTONE_DUST, 2, 0),
                        "dustGlowstone",
                        new AspectList().merge(Aspect.LIGHT, 3).merge(Aspect.SENSES, 1)));
        ConfigResearch.recipes.put("AltInk",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALDUPLICATION",
                        new ItemStack(Items.DYE, 2, 0),
                        new ItemStack(Items.DYE, 1, 0),
                        new AspectList().merge(Aspect.WATER, 2).merge(Aspect.SENSES, 2)));
        ConfigResearch.recipes.put("AltWeb",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALMANUFACTURE",
                        new ItemStack(Blocks.WEB),
                        new ItemStack(Items.STRING),
                        new AspectList().merge(Aspect.TRAP, 2).merge(Aspect.CLOTH, 2)));
        ConfigResearch.recipes.put("AltMossyCobble",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALMANUFACTURE",
                        new ItemStack(Blocks.MOSSY_COBBLESTONE),
                        new ItemStack(Blocks.COBBLESTONE),
                        new AspectList().merge(Aspect.PLANT, 2).merge(Aspect.MAGIC, 1)));
        ConfigResearch.recipes.put("AltIce",
                ThaumcraftApi.addCrucibleRecipe(
                        "ALCHEMICALMANUFACTURE",
                        new ItemStack(Blocks.ICE),
                        new ItemStack(Blocks.PACKED_ICE),
                        new AspectList().merge(Aspect.ORDER, 1).merge(Aspect.COLD, 1)));
        ConfigResearch.recipes.put("AltCrackedBrick",
                ThaumcraftApi.addCrucibleRecipe(
                        "ENTROPICPROCESSING",
                        new ItemStack(Blocks.STONEBRICK, 1, 2),
                        new ItemStack(Blocks.STONEBRICK),
                        new AspectList().merge(Aspect.ENTROPY, 2)));
        ConfigResearch.recipes.put("AltBonemeal",
                ThaumcraftApi.addCrucibleRecipe(
                        "ENTROPICPROCESSING",
                        new ItemStack(Items.DYE, 4, 15),
                        new ItemStack(Items.BONE),
                        new AspectList().merge(Aspect.ENTROPY, 1)));

        ConfigResearch.recipes.put("PureIron",
                ThaumcraftApi.addCrucibleRecipe(
                        "PUREIRON",
                        new ItemStack(ConfigItems.itemNugget, 1, 16),
                        "oreIron",
                        new AspectList().merge(Aspect.METAL, 1).merge(Aspect.ORDER, 1)));
        ConfigResearch.recipes.put("PureGold",
                ThaumcraftApi.addCrucibleRecipe(
                        "PUREGOLD",
                        new ItemStack(ConfigItems.itemNugget, 1, 31),
                        "oreGold",
                        new AspectList().merge(Aspect.METAL, 1).merge(Aspect.ORDER, 1)));
        if (Config.foundCopperIngot) {
            ConfigResearch.recipes.put("PureCopper",
                    ThaumcraftApi.addCrucibleRecipe(
                            "PURECOPPER",
                            new ItemStack(ConfigItems.itemNugget, 1, 17),
                            "oreCopper",
                            new AspectList().merge(Aspect.METAL, 1).merge(Aspect.ORDER, 1)));
        }
        if (Config.foundTinIngot) {
            ConfigResearch.recipes.put("PureTin",
                    ThaumcraftApi.addCrucibleRecipe(
                            "PURETIN",
                            new ItemStack(ConfigItems.itemNugget, 1, 18),
                            "oreTin",
                            new AspectList().merge(Aspect.METAL, 1).merge(Aspect.ORDER, 1)));
        }
        if (Config.foundSilverIngot) {
            ConfigResearch.recipes.put("PureSilver",
                    ThaumcraftApi.addCrucibleRecipe(
                            "PURESILVER",
                            new ItemStack(ConfigItems.itemNugget, 1, 19),
                            "oreSilver",
                            new AspectList().merge(Aspect.METAL, 1).merge(Aspect.ORDER, 1)));
        }
        if (Config.foundLeadIngot) {
            ConfigResearch.recipes.put("PureLead",
                    ThaumcraftApi.addCrucibleRecipe(
                            "PURELEAD",
                            new ItemStack(ConfigItems.itemNugget, 1, 20),
                            "oreLead",
                            new AspectList().merge(Aspect.METAL, 1).merge(Aspect.ORDER, 1)));
        }

        ConfigResearch.recipes.put("TransIron",
                ThaumcraftApi.addCrucibleRecipe(
                        "TRANSIRON",
                        new ItemStack(ConfigItems.itemNugget, 3, 0),
                        "nuggetIron",
                        new AspectList().merge(Aspect.METAL, 2)));
        ConfigResearch.recipes.put("TransGold",
                ThaumcraftApi.addCrucibleRecipe(
                        "TRANSGOLD",
                        new ItemStack(Items.GOLD_NUGGET, 3, 0),
                        new ItemStack(Items.GOLD_NUGGET),
                        new AspectList().merge(Aspect.METAL, 2).merge(Aspect.GREED, 1)));
        if (Config.foundCopperIngot) {
            ConfigResearch.recipes.put("TransCopper",
                    ThaumcraftApi.addCrucibleRecipe(
                            "TRANSCOPPER",
                            new ItemStack(ConfigItems.itemNugget, 3, 1),
                            "nuggetCopper",
                            new AspectList().merge(Aspect.METAL, 2).merge(Aspect.EXCHANGE, 1)));
        }
        if (Config.foundTinIngot) {
            ConfigResearch.recipes.put("TransTin",
                    ThaumcraftApi.addCrucibleRecipe(
                            "TRANSTIN",
                            new ItemStack(ConfigItems.itemNugget, 3, 2),
                            "nuggetTin",
                            new AspectList().merge(Aspect.METAL, 2).merge(Aspect.CRYSTAL, 1)));
        }
        if (Config.foundSilverIngot) {
            ConfigResearch.recipes.put("TransSilver",
                    ThaumcraftApi.addCrucibleRecipe(
                            "TRANSSILVER",
                            new ItemStack(ConfigItems.itemNugget, 3, 3),
                            "nuggetSilver",
                            new AspectList().merge(Aspect.METAL, 2).merge(Aspect.GREED, 1)));
        }
        if (Config.foundLeadIngot) {
            ConfigResearch.recipes.put("TransLead",
                    ThaumcraftApi.addCrucibleRecipe(
                            "TRANSLEAD",
                            new ItemStack(ConfigItems.itemNugget, 3, 4),
                            "nuggetLead",
                            new AspectList().merge(Aspect.METAL, 2).merge(Aspect.ORDER, 1)));
        }

        ConfigResearch.recipes.put("EtherealBloom",
                ThaumcraftApi.addCrucibleRecipe(
                        "ETHEREALBLOOM",
                        new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4),
                        new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2),
                        new AspectList().add(Aspect.MAGIC, 16).add(Aspect.PLANT, 16).add(Aspect.HEAL, 16).add(Aspect.TAINT, 8)));
        ConfigResearch.recipes.put("LiquidDeath",
                ThaumcraftApi.addCrucibleRecipe(
                        "LIQUIDDEATH",
                        new ItemStack(ConfigItems.itemBucketDeath),
                        new ItemStack(Items.GLASS_BOTTLE),
                        new AspectList().add(Aspect.DEATH, 32).add(Aspect.POISON, 32).add(Aspect.ENTROPY, 32)));
        ItemStack bottledTaintCatalyst = new ItemStack(ConfigItems.itemEssence, 1, 1);
        if (bottledTaintCatalyst.getItem() instanceof IEssentiaContainerItem) {
            ((IEssentiaContainerItem) bottledTaintCatalyst.getItem()).setAspects(
                    bottledTaintCatalyst, new AspectList().add(Aspect.TAINT, 8));
        }
        ConfigResearch.recipes.put("BottleTaint",
                ThaumcraftApi.addCrucibleRecipe(
                        "BOTTLETAINT",
                        new ItemStack(ConfigItems.itemBottleTaint),
                        bottledTaintCatalyst,
                        new AspectList().add(Aspect.TAINT, 8).add(Aspect.MAGIC, 8)));

        CrucibleRecipe recipeVoidMetal = ThaumcraftApi.addCrucibleRecipe(
                "VOIDMETAL",
                new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigItems.itemResource, 1, 17),
                new AspectList().merge(Aspect.METAL, 8));

        CrucibleRecipe recipeVoidSeed = ThaumcraftApi.addCrucibleRecipe(
                "VOIDMETAL",
                new ItemStack(ConfigItems.itemResource, 1, 17),
                new ItemStack(Items.ENDER_PEARL),
                new AspectList().merge(Aspect.DARKNESS, 8).merge(Aspect.VOID, 8).merge(Aspect.ELDRITCH, 2));

        ConfigResearch.recipes.put("GolemStraw",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMSTRAW",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 0),
                        new ItemStack(Blocks.HAY_BLOCK),
                        new AspectList().add(Aspect.MAN, 4).add(Aspect.MOTION, 4).add(Aspect.SOUL, 4)));
        ConfigResearch.recipes.put("GolemWood",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMWOOD",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 1),
                        new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0),
                        new AspectList().add(Aspect.MAN, 4).add(Aspect.MOTION, 4).add(Aspect.SOUL, 4)));
        ConfigResearch.recipes.put("GolemTallow",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMTALLOW",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 2),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5),
                        new AspectList().add(Aspect.MAN, 8).add(Aspect.MOTION, 8).add(Aspect.SOUL, 8)));
        ConfigResearch.recipes.put("GolemClay",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMCLAY",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 3),
                        new ItemStack(Blocks.CLAY),
                        new AspectList().add(Aspect.MAN, 4).add(Aspect.MOTION, 4).add(Aspect.SOUL, 4)));
        ConfigResearch.recipes.put("GolemFlesh",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMFLESH",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 4),
                        new ItemStack(ConfigBlocks.blockTaint, 1, 2),
                        new AspectList().add(Aspect.MAN, 8).add(Aspect.MOTION, 8).add(Aspect.SOUL, 8)));
        ConfigResearch.recipes.put("GolemStone",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMSTONE",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 5),
                        new ItemStack(Blocks.STONE),
                        new AspectList().add(Aspect.MAN, 4).add(Aspect.MOTION, 4).add(Aspect.SOUL, 4)));
        ConfigResearch.recipes.put("GolemIron",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMIRON",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 6),
                        new ItemStack(Blocks.IRON_BLOCK),
                        new AspectList().add(Aspect.MAN, 4).add(Aspect.MOTION, 4).add(Aspect.SOUL, 4)));
        ConfigResearch.recipes.put("GolemThaumium",
                ThaumcraftApi.addCrucibleRecipe(
                        "GOLEMTHAUMIUM",
                        new ItemStack(ConfigItems.itemGolemPlacer, 1, 7),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4),
                        new AspectList().add(Aspect.MAN, 8).add(Aspect.MOTION, 8).add(Aspect.SOUL, 8)));

        ItemStack coreBlank = new ItemStack(ConfigItems.itemGolemCore, 1, 100);
        ConfigResearch.recipes.put("CoreGather",
                ThaumcraftApi.addCrucibleRecipe(
                        "COREGATHER",
                        new ItemStack(ConfigItems.itemGolemCore, 1, 2),
                        coreBlank,
                        new AspectList().add(Aspect.GREED, 5).add(Aspect.EARTH, 5)));
        ConfigResearch.recipes.put("CoreFill",
                ThaumcraftApi.addCrucibleRecipe(
                        "COREFILL",
                        new ItemStack(ConfigItems.itemGolemCore, 1, 0),
                        coreBlank,
                        new AspectList().add(Aspect.HUNGER, 5).add(Aspect.VOID, 5)));
        ConfigResearch.recipes.put("CoreEmpty",
                ThaumcraftApi.addCrucibleRecipe(
                        "COREEMPTY",
                        new ItemStack(ConfigItems.itemGolemCore, 1, 1),
                        coreBlank,
                        new AspectList().add(Aspect.GREED, 5).add(Aspect.VOID, 5)));
        ConfigResearch.recipes.put("CoreHarvest",
                ThaumcraftApi.addCrucibleRecipe(
                        "COREHARVEST",
                        new ItemStack(ConfigItems.itemGolemCore, 1, 3),
                        coreBlank,
                        new AspectList().add(Aspect.HARVEST, 5).add(Aspect.CROP, 5)));
        ConfigResearch.recipes.put("CoreGuard",
                ThaumcraftApi.addCrucibleRecipe(
                        "COREGUARD",
                        new ItemStack(ConfigItems.itemGolemCore, 1, 4),
                        coreBlank,
                        new AspectList().add(Aspect.WEAPON, 5).add(Aspect.TRAP, 5)));
        ConfigResearch.recipes.put("CoreButcher",
                ThaumcraftApi.addCrucibleRecipe(
                        "COREBUTCHER",
                        new ItemStack(ConfigItems.itemGolemCore, 1, 9),
                        new ItemStack(ConfigItems.itemGolemCore, 1, 4),
                        new AspectList().add(Aspect.FLESH, 5).add(Aspect.BEAST, 5)));
        ConfigResearch.recipes.put("CoreLiquid",
                ThaumcraftApi.addCrucibleRecipe(
                        "CORELIQUID",
                        new ItemStack(ConfigItems.itemGolemCore, 1, 5),
                        coreBlank,
                        new AspectList().add(Aspect.WATER, 5).add(Aspect.VOID, 5)));
        ConfigResearch.recipes.put("BathSalts",
                ThaumcraftApi.addCrucibleRecipe(
                        "BATHSALTS",
                        new ItemStack(ConfigItems.itemBathSalts),
                        new ItemStack(ConfigItems.itemResource, 1, 14),
                        new AspectList().add(Aspect.MIND, 6).add(Aspect.AURA, 6).add(Aspect.ORDER, 6).add(Aspect.HEAL, 6)));
        ConfigResearch.recipes.put("SaneSoap",
                ThaumcraftApi.addCrucibleRecipe(
                        "SANESOAP",
                        new ItemStack(ConfigItems.itemSanitySoap),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5),
                        new AspectList().add(Aspect.MIND, 16).add(Aspect.ELDRITCH, 16).add(Aspect.ORDER, 16).add(Aspect.HEAL, 16)));

        return new CrucibleRecipeHandles(recipeNitor, recipeAlumentum, recipeThaumium, recipeVoidMetal, recipeVoidSeed);
    }

    public static class CrucibleRecipeHandles {
        public final CrucibleRecipe recipeNitor;
        public final CrucibleRecipe recipeAlumentum;
        public final CrucibleRecipe recipeThaumium;
        public final CrucibleRecipe recipeVoidMetal;
        public final CrucibleRecipe recipeVoidSeed;

        public CrucibleRecipeHandles(CrucibleRecipe recipeNitor, CrucibleRecipe recipeAlumentum, CrucibleRecipe recipeThaumium, CrucibleRecipe recipeVoidMetal, CrucibleRecipe recipeVoidSeed) {
            this.recipeNitor = recipeNitor;
            this.recipeAlumentum = recipeAlumentum;
            this.recipeThaumium = recipeThaumium;
            this.recipeVoidMetal = recipeVoidMetal;
            this.recipeVoidSeed = recipeVoidSeed;
        }
    }
}
