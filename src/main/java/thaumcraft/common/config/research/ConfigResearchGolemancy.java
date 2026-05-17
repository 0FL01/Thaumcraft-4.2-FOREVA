package thaumcraft.common.config.research;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

final class ConfigResearchGolemancy {
    private ConfigResearchGolemancy() {}

    static void initGolemancyResearchBaseline() {
        new ResearchItem(
                "HUNGRYCHEST",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.HUNGER, 3)
                        .add(Aspect.VOID, 3),
                -1,
                0,
                1,
                new ItemStack(ConfigBlocks.blockChestHungry))
                .setPages(
                        new ResearchPage("tc.research_page.HUNGRYCHEST.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("HungryChest")))
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "TINYHAT",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.CLOTH, 2)
                        .add(Aspect.LIFE, 1)
                        .add(Aspect.GREED, 1),
                5,
                10,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.TINYHAT.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyHat")))
                .setHidden()
                .setSecondary()
                .setItemTriggers(new ItemStack(Blocks.WOOL, 1, Short.MAX_VALUE))
                .setAspectTriggers(Aspect.CLOTH)
                .registerResearchItem();

        new ResearchItem(
                "TINYGLASSES",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.CLOTH, 2)
                        .add(Aspect.SENSES, 1)
                        .add(Aspect.GREED, 1),
                6,
                10,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.TINYGLASSES.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyGlasses")))
                .setHidden()
                .setSecondary()
                .setItemTriggers(new ItemStack(Blocks.WOOL, 1, Short.MAX_VALUE))
                .setAspectTriggers(Aspect.CLOTH)
                .registerResearchItem();

        new ResearchItem(
                "TINYBOWTIE",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.CLOTH, 2)
                        .add(Aspect.TRAVEL, 1)
                        .add(Aspect.GREED, 1),
                7,
                10,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.TINYBOWTIE.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyBowtie")))
                .setHidden()
                .setSecondary()
                .setItemTriggers(new ItemStack(Blocks.WOOL, 1, Short.MAX_VALUE))
                .setAspectTriggers(Aspect.CLOTH)
                .registerResearchItem();

        new ResearchItem(
                "TINYFEZ",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.CLOTH, 2)
                        .add(Aspect.ENERGY, 1)
                        .add(Aspect.GREED, 1),
                8,
                10,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.TINYFEZ.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyFez")))
                .setHidden()
                .setSecondary()
                .setItemTriggers(new ItemStack(Blocks.WOOL, 1, Short.MAX_VALUE))
                .setAspectTriggers(Aspect.CLOTH)
                .registerResearchItem();

        new ResearchItem(
                "TINYDART",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.FLIGHT, 1)
                        .add(Aspect.WEAPON, 2)
                        .add(Aspect.GREED, 1),
                5,
                11,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.TINYDART.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyDart")))
                .setHidden()
                .setSecondary()
                .setAspectTriggers(Aspect.WEAPON)
                .registerResearchItem();

        new ResearchItem(
                "TINYVISOR",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SENSES, 1)
                        .add(Aspect.ARMOR, 2)
                        .add(Aspect.GREED, 1),
                6,
                11,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.TINYVISOR.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyVisor")))
                .setHidden()
                .setSecondary()
                .setAspectTriggers(Aspect.ARMOR)
                .registerResearchItem();

        new ResearchItem(
                "TINYARMOR",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.METAL, 1)
                        .add(Aspect.ARMOR, 2)
                        .add(Aspect.GREED, 1),
                7,
                11,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 6))
                .setPages(
                        new ResearchPage("tc.research_page.TINYARMOR.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyArmor")))
                .setHidden()
                .setSecondary()
                .setAspectTriggers(Aspect.ARMOR)
                .registerResearchItem();

        new ResearchItem(
                "TINYHAMMER",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.METAL, 1)
                        .add(Aspect.WEAPON, 2)
                        .add(Aspect.GREED, 1),
                8,
                11,
                1,
                new ItemStack(ConfigItems.itemGolemDecoration, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.TINYHAMMER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TinyHammer")))
                .setHidden()
                .setSecondary()
                .setAspectTriggers(Aspect.WEAPON)
                .registerResearchItem();
    }


    static void initGolemancyResearchTextOnlyBaseline() {
        new ResearchItem(
                "GOLEMSTRAW",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 3)
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.CROP, 3)
                        .add(Aspect.EXCHANGE, 3),
                0,
                2,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMSTRAW.1"),
                        new ResearchPage("tc.research_page.GOLEMSTRAW.2"),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemStraw")),
                        new ResearchPage("tc.research_page.GOLEMSTRAW.3"))
                .setParents("HUNGRYCHEST")
                .registerResearchItem();

        new ResearchItem(
                "GOLEMBELL",
                "GOLEMANCY",
                new AspectList(),
                3,
                0,
                0,
                new ItemStack(ConfigItems.itemGolemBell))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMBELL.1"),
                        new ResearchPage("tc.research_page.GOLEMBELL.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("GolemBell")))
                .setParents("GOLEMSTRAW")
                .setStub()
                .registerResearchItem();

        new ResearchItem(
                "GOLEMWOOD",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 4)
                        .add(Aspect.MOTION, 4)
                        .add(Aspect.TREE, 3)
                        .add(Aspect.EXCHANGE, 3),
                2,
                4,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMWOOD.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemWood")))
                .setSecondary()
                .setParents("GOLEMSTRAW")
                .registerResearchItem();

        new ResearchItem(
                "GOLEMCLAY",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 6)
                        .add(Aspect.MOTION, 6)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.EXCHANGE, 3),
                2,
                6,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMCLAY.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemClay")))
                .setSecondary()
                .setConcealed()
                .setParents("GOLEMWOOD")
                .registerResearchItem();

        new ResearchItem(
                "GOLEMSTONE",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 6)
                        .add(Aspect.MOTION, 6)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.EXCHANGE, 3),
                2,
                8,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMSTONE.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemStone")))
                .setSecondary()
                .setConcealed()
                .setParents("GOLEMCLAY")
                .registerResearchItem();

        new ResearchItem(
                "GOLEMIRON",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 9)
                        .add(Aspect.MOTION, 9)
                        .add(Aspect.METAL, 3)
                        .add(Aspect.EXCHANGE, 3),
                0,
                10,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 6))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMIRON.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemIron")))
                .setSecondary()
                .setConcealed()
                .setParents("GOLEMSTONE")
                .registerResearchItem();

        new ResearchItem(
                "GOLEMTHAUMIUM",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 10)
                        .add(Aspect.MOTION, 10)
                        .add(Aspect.METAL, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.EXCHANGE, 3),
                2,
                10,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMTHAUMIUM.1"),
                        new ResearchPage(ConfigResearch.recipeI("BlockThaumium")),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemThaumium")))
                .setConcealed()
                .setParents("GOLEMIRON", "THAUMIUM")
                .registerResearchItem();

        new ResearchItem(
                "GOLEMFLESH",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 7)
                        .add(Aspect.MOTION, 7)
                        .add(Aspect.FLESH, 6)
                        .add(Aspect.EXCHANGE, 3),
                4,
                4,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMFLESH.1"),
                        new ResearchPage(ConfigResearch.recipeI("BlockFlesh")),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemFlesh")))
                .setConcealed()
                .setParents("GOLEMWOOD")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("GOLEMFLESH", 3);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.itemGolemPlacer, 1, 4), 1);

        new ResearchItem(
                "GOLEMTALLOW",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 3)
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.FLESH, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.EXCHANGE, 3),
                4,
                6,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMTALLOW.1"),
                        new ResearchPage(ConfigResearch.recipeI("BlockTallow")),
                        new ResearchPage(ConfigResearch.recipeCrucible("GolemTallow")))
                .setConcealed()
                .setParents("GOLEMCLAY", "TALLOW")
                .registerResearchItem();

        new ResearchItem(
                "GOLEMFETTER",
                "GOLEMANCY",
                new AspectList().add(Aspect.TRAP, 3).add(Aspect.MECHANISM, 3),
                4,
                8,
                1,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 9))
                .setPages(
                        new ResearchPage("tc.research_page.GOLEMFETTER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("GolemFetter")))
                .setParents("GOLEMSTONE")
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "UPGRADEAIR",
                "GOLEMANCY",
                new AspectList().add(Aspect.AIR, 6).add(Aspect.MOTION, 3),
                7,
                -3,
                1,
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.UPGRADEAIR.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("UpgradeAir")))
                .setConcealed()
                .setSecondary()
                .setParents("GOLEMBELL")
                .registerResearchItem();

        new ResearchItem(
                "UPGRADEEARTH",
                "GOLEMANCY",
                new AspectList().add(Aspect.EARTH, 6).add(Aspect.LIFE, 3),
                6,
                -2,
                1,
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.UPGRADEEARTH.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("UpgradeEarth")))
                .setConcealed()
                .setSecondary()
                .setParents("GOLEMBELL")
                .registerResearchItem();

        new ResearchItem(
                "UPGRADEFIRE",
                "GOLEMANCY",
                new AspectList().add(Aspect.FIRE, 6).add(Aspect.ENERGY, 3),
                5,
                -1,
                1,
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.UPGRADEFIRE.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("UpgradeFire")))
                .setConcealed()
                .setSecondary()
                .setParents("GOLEMBELL")
                .registerResearchItem();

        new ResearchItem(
                "UPGRADEWATER",
                "GOLEMANCY",
                new AspectList().add(Aspect.WATER, 6).add(Aspect.SENSES, 3),
                5,
                1,
                1,
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.UPGRADEWATER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("UpgradeWater")))
                .setConcealed()
                .setSecondary()
                .setParents("GOLEMBELL")
                .registerResearchItem();

        new ResearchItem(
                "UPGRADEORDER",
                "GOLEMANCY",
                new AspectList().add(Aspect.ORDER, 6).add(Aspect.MIND, 3),
                6,
                2,
                1,
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.UPGRADEORDER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("UpgradeOrder")))
                .setConcealed()
                .setSecondary()
                .setParents("GOLEMBELL")
                .registerResearchItem();

        new ResearchItem(
                "UPGRADEENTROPY",
                "GOLEMANCY",
                new AspectList().add(Aspect.ENTROPY, 6).add(Aspect.MIND, 3),
                7,
                3,
                1,
                new ItemStack(ConfigItems.itemGolemUpgrade, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.UPGRADEENTROPY.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("UpgradeEntropy")))
                .setConcealed()
                .setSecondary()
                .setParents("GOLEMBELL")
                .registerResearchItem();

        new ResearchItem(
                "TRAVELTRUNK",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.SOUL, 3)
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.TREE, 3)
                        .add(Aspect.VOID, 3),
                0,
                4,
                2,
                new ItemStack(ConfigItems.itemTrunkSpawner))
                .setPages(
                        new ResearchPage("tc.research_page.TRAVELTRUNK.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("TravelTrunk")),
                        new ResearchPage("tc.research_page.TRAVELTRUNK.2"),
                        new ResearchPage("UPGRADEAIR", "tc.research_page.TRAVELTRUNK.UAI"),
                        new ResearchPage("UPGRADEEARTH", "tc.research_page.TRAVELTRUNK.UEA"),
                        new ResearchPage("UPGRADEFIRE", "tc.research_page.TRAVELTRUNK.UFI"),
                        new ResearchPage("UPGRADEWATER", "tc.research_page.TRAVELTRUNK.UWA"),
                        new ResearchPage("UPGRADEORDER", "tc.research_page.TRAVELTRUNK.UOR"),
                        new ResearchPage("UPGRADEENTROPY", "tc.research_page.TRAVELTRUNK.UEN"))
                .setConcealed()
                .setParents("INFUSION", "GOLEMWOOD")
                .registerResearchItem();

        new ResearchItem(
                "COREGATHER",
                "GOLEMANCY",
                new AspectList(),
                -3,
                3,
                1,
                new ItemStack(ConfigItems.itemGolemCore, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.COREGATHER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("CoreBlank")),
                        new ResearchPage("tc.research_page.COREGATHER.2"),
                        new ResearchPage(ConfigResearch.recipeCrucible("CoreGather")))
                .setConcealed()
                .setParents("GOLEMSTRAW")
                .setStub()
                .registerResearchItem();

        new ResearchItem(
                "COREFILL",
                "GOLEMANCY",
                new AspectList().add(Aspect.HUNGER, 3).add(Aspect.EXCHANGE, 3).add(Aspect.VOID, 3),
                -5,
                3,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.COREFILL.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("CoreFill")))
                .setConcealed()
                .setSecondary()
                .setParents("COREGATHER")
                .registerResearchItem();

        new ResearchItem(
                "COREEMPTY",
                "GOLEMANCY",
                new AspectList().add(Aspect.VOID, 3).add(Aspect.EXCHANGE, 3).add(Aspect.GREED, 3),
                -5,
                1,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.COREEMPTY.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("CoreEmpty")))
                .setConcealed()
                .setSecondary()
                .setParents("COREGATHER")
                .registerResearchItem();

        new ResearchItem(
                "CORESORTING",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.VOID, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.GREED, 3)
                        .add(Aspect.HUNGER, 3),
                -7,
                2,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 10))
                .setPages(
                        new ResearchPage("tc.research_page.CORESORTING.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("CoreSorting")))
                .setConcealed()
                .setSecondary()
                .setParents("COREEMPTY", "COREFILL", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "COREUSE",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.MAN, 3),
                -7,
                0,
                3,
                new ItemStack(ConfigItems.itemGolemCore, 1, 8))
                .setPages(
                        new ResearchPage("tc.research_page.COREUSE.1"),
                        new ResearchPage("tc.research_page.COREUSE.2"),
                        new ResearchPage(ConfigResearch.recipeInfusion("CoreUse")),
                        new ResearchPage("UPGRADEAIR", "tc.research_page.COREUSE.3"))
                .setConcealed()
                .setParents("COREEMPTY", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "COREHARVEST",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.HARVEST, 6)
                        .add(Aspect.CROP, 3)
                        .add(Aspect.TRAVEL, 3),
                -2,
                5,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.COREHARVEST.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("CoreHarvest")),
                        new ResearchPage("UPGRADEORDER", "tc.research_page.COREHARVEST.2"))
                .setConcealed()
                .setParents("COREGATHER")
                .registerResearchItem();

        new ResearchItem(
                "COREFISHING",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.HARVEST, 3)
                        .add(Aspect.BEAST, 3)
                        .add(Aspect.HUNGER, 3),
                -2,
                7,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 11))
                .setPages(
                        new ResearchPage("tc.research_page.COREFISHING.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("CoreFishing")),
                        new ResearchPage("UPGRADEAIR", "tc.research_page.COREFISHING.2"),
                        new ResearchPage("UPGRADEFIRE", "tc.research_page.COREFISHING.3"),
                        new ResearchPage("UPGRADEORDER", "tc.research_page.COREFISHING.4"),
                        new ResearchPage("UPGRADEENTROPY", "tc.research_page.COREFISHING.5"))
                .setConcealed()
                .setSecondary()
                .setParents("COREHARVEST", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "CORELUMBER",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.TREE, 6)
                        .add(Aspect.HARVEST, 3)
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ENERGY, 3),
                -1,
                7,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.CORELUMBER.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("CoreLumber")))
                .setConcealed()
                .setSecondary()
                .setParents("COREHARVEST", "ELEMENTALAXE")
                .registerResearchItem();

        new ResearchItem(
                "COREGUARD",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.TRAP, 3)
                        .add(Aspect.SENSES, 3),
                -4,
                5,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.COREGUARD.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("CoreGuard")),
                        new ResearchPage("UPGRADEORDER", "tc.research_page.COREGUARD.2"))
                .setConcealed()
                .setParents("COREGATHER")
                .registerResearchItem();

        new ResearchItem(
                "COREBUTCHER",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.BEAST, 3)
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.HARVEST, 3),
                -3,
                7,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 9))
                .setPages(
                        new ResearchPage("tc.research_page.COREBUTCHER.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("CoreButcher")))
                .setConcealed()
                .setSecondary()
                .setParents("COREGUARD", "COREHARVEST")
                .registerResearchItem();

        new ResearchItem(
                "CORELIQUID",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.TRAVEL, 3),
                -7,
                4,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.CORELIQUID.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("CoreLiquid")),
                        new ResearchPage("UPGRADEENTROPY", "tc.research_page.CORELIQUID.2"))
                .setConcealed()
                .setParents("COREFILL")
                .registerResearchItem();

        new ResearchItem(
                "COREALCHEMY",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ENERGY, 3),
                -9,
                3,
                2,
                new ItemStack(ConfigItems.itemGolemCore, 1, 6))
                .setPages(
                        new ResearchPage("tc.research_page.COREALCHEMY.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("CoreAlchemy")),
                        new ResearchPage("tc.research_page.COREALCHEMY.2"))
                .setConcealed()
                .setSecondary()
                .setParents("CORELIQUID", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ADVANCEDGOLEM",
                "GOLEMANCY",
                new AspectList()
                        .add(Aspect.LIFE, 3)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.MIND, 6)
                        .add(Aspect.SENSES, 3),
                8,
                0,
                2,
                new ItemStack(ConfigItems.itemGolemPlacer, 1, 8))
                .setPages(
                        new ResearchPage("tc.research_page.ADVANCEDGOLEM.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("AdvancedGolem")))
                .setConcealed()
                .setParents(
                        "INFUSION",
                        "UPGRADEAIR",
                        "UPGRADEEARTH",
                        "UPGRADEFIRE",
                        "UPGRADEWATER",
                        "UPGRADEORDER",
                        "UPGRADEENTROPY")
                .registerResearchItem();
    }
}
