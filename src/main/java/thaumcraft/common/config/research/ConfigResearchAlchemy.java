package thaumcraft.common.config.research;

import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

final class ConfigResearchAlchemy {
    private ConfigResearchAlchemy() {}

    static void initAlchemyResearchBaseline() {
        new ResearchItem(
                "PHIAL",
                "ALCHEMY",
                new AspectList(),
                0,
                -2,
                0,
                new ItemStack(ConfigItems.itemEssence, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.PHIAL.1"),
                        new ResearchPage(ConfigResearch.recipeI("Phial")))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "CRUCIBLE",
                "ALCHEMY",
                new AspectList(),
                0,
                0,
                0,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.CRUCIBLE.1"),
                        new ResearchPage("tc.research_page.CRUCIBLE.2"),
                        new ResearchPage("tc.research_page.CRUCIBLE.3"),
                        new ResearchPage(ConfigResearch.recipeList("Crucible")),
                        new ResearchPage("tc.research_page.CRUCIBLE.4"),
                        new ResearchPage("tc.research_page.CRUCIBLE.5"),
                        new ResearchPage(new ItemStack(ConfigItems.itemShard, 1, 6)))
                .setStub()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "NITOR",
                "ALCHEMY",
                new AspectList().add(Aspect.LIGHT, 3).add(Aspect.FIRE, 1),
                2,
                -1,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.NITOR.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("Nitor")))
                .setParents("CRUCIBLE")
                .registerResearchItem();

        new ResearchItem(
                "ALUMENTUM",
                "ALCHEMY",
                new AspectList().add(Aspect.ENERGY, 3).add(Aspect.FIRE, 1),
                2,
                1,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.ALUMENTUM.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("Alumentum")))
                .setParents("CRUCIBLE")
                .registerResearchItem();

        new ResearchItem(
                "DISTILESSENTIA",
                "ALCHEMY",
                new AspectList().add(Aspect.MAGIC, 3).add(Aspect.WATER, 3).add(Aspect.SLIME, 3),
                5,
                -1,
                1,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.DISTILESSENTIA.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("AlchemyFurnace")),
                        new ResearchPage("tc.research_page.DISTILESSENTIA.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("Filter")),
                        new ResearchPage(ConfigResearch.recipeArcane("Alembic")),
                        new ResearchPage(ConfigResearch.recipeArcane("AlchemicalConstruct")))
                .setSiblings("JARLABEL")
                .setParents("NITOR", "ALUMENTUM")
                .registerResearchItem();

        new ResearchItem(
                "THAUMIUM",
                "ALCHEMY",
                new AspectList().add(Aspect.METAL, 3).add(Aspect.MAGIC, 3),
                -1,
                3,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.THAUMIUM.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("Thaumium")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumAxe")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumSword")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumPick")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumShovel")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumHoe")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumHelm")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumChest")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumLegs")),
                        new ResearchPage(ConfigResearch.recipeI("ThaumiumBoots")))
                .setHidden()
                .setAspectTriggers(Aspect.METAL)
                .setParents("CRUCIBLE")
                .registerResearchItem();

        new ResearchItem(
                "TUBES",
                "ALCHEMY",
                new AspectList().add(Aspect.WATER, 3).add(Aspect.MAGIC, 3).add(Aspect.EXCHANGE, 3),
                7,
                0,
                1,
                new ItemStack(ConfigBlocks.blockTube, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.TUBES.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("Tube")),
                        new ResearchPage("tc.research_page.TUBES.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("TubeValve")),
                        new ResearchPage("tc.research_page.TUBES.3"),
                        new ResearchPage(ConfigResearch.recipeArcane("Resonator")),
                        new ResearchPage("tc.research_page.TUBES.4"))
                .setParents("DISTILESSENTIA")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "TUBEFILTER",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.ORDER, 3),
                9,
                1,
                2,
                new ItemStack(ConfigBlocks.blockTube, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.TUBEFILTER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("TubeFilter")),
                        new ResearchPage("tc.research_page.TUBEFILTER.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("TubeRestrict")),
                        new ResearchPage(ConfigResearch.recipeArcane("TubeOneway")))
                .setParents("TUBES")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ESSENTIACRYSTAL",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.WATER, 5)
                        .add(Aspect.CRYSTAL, 5)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.MAGIC, 5),
                8,
                -2,
                1,
                new ItemStack(ConfigBlocks.blockTube, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.ESSENTIACRYSTAL.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("EssentiaCrystalizer")))
                .setConcealed()
                .setParents("TUBES")
                .registerResearchItem();

        new ResearchItem(
                "CENTRIFUGE",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.ENTROPY, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.CRAFT, 3),
                10,
                0,
                2,
                new ItemStack(ConfigBlocks.blockTube, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.CENTRIFUGE.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("Centrifuge")),
                        new ResearchPage("tc.research_page.CENTRIFUGE.2"),
                        new ResearchPage("tc.research_page.CENTRIFUGE.3"),
                        new ResearchPage(ConfigResearch.recipeArcane("TubeBuffer")))
                .setParents("TUBEFILTER")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "THAUMATORIUM",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.CRAFT, 3),
                10,
                -2,
                3,
                new ResourceLocation("thaumcraft", "textures/blocks/alchemyblock.png"))
                .setPages(
                        new ResearchPage("tc.research_page.THAUMATORIUM.1"),
                        new ResearchPage(ConfigResearch.recipeList("Thaumatorium")),
                        new ResearchPage("tc.research_page.THAUMATORIUM.2"),
                        new ResearchPage("tc.research_page.THAUMATORIUM.3"),
                        new ResearchPage(ConfigResearch.recipeArcane("MnemonicMatrix")))
                .setParents("CENTRIFUGE")
                .setConcealed()
                .registerResearchItem();
    }

    static void initAlchemyResearchTextOnlyBaseline() {
        new ResearchItem(
                "TALLOW",
                "ALCHEMY",
                new AspectList().add(Aspect.FLESH, 3).add(Aspect.MAGIC, 1),
                -2,
                0,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.TALLOW.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("Tallow")),
                        new ResearchPage(ConfigResearch.recipeI("TallowCandle")))
                .setParents("CRUCIBLE")
                .registerResearchItem();

        new ResearchItem(
                "ALCHEMICALDUPLICATION",
                "ALCHEMY",
                new AspectList().add(Aspect.MAGIC, 3).add(Aspect.GREED, 3).add(Aspect.CRAFT, 3),
                -4,
                0,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_alchmult.png"))
                .setPages(
                        new ResearchPage("tc.research_page.ALCHEMICALDUPLICATION.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltGunpowder")),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltSlime")),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltClay")),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltGlowstone")),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltInk")))
                .setConcealed()
                .setSecondary()
                .setParents("TALLOW")
                .registerResearchItem();

        new ResearchItem(
                "ALCHEMICALMANUFACTURE",
                "ALCHEMY",
                new AspectList().add(Aspect.MAGIC, 3).add(Aspect.EXCHANGE, 3).add(Aspect.CRAFT, 3),
                -5,
                -2,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_alchman.png"))
                .setPages(
                        new ResearchPage("tc.research_page.ALCHEMICALMANUFACTURE.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltWeb")),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltMossyCobble")),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltIce")))
                .setConcealed()
                .setSecondary()
                .setParents("ALCHEMICALDUPLICATION")
                .registerResearchItem();

        new ResearchItem(
                "ENTROPICPROCESSING",
                "ALCHEMY",
                new AspectList().add(Aspect.MAGIC, 1).add(Aspect.ENTROPY, 3).add(Aspect.CRAFT, 1),
                -6,
                1,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_alchent.png"))
                .setPages(
                        new ResearchPage("tc.research_page.ENTROPICPROCESSING.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltCrackedBrick")),
                        new ResearchPage(ConfigResearch.recipeCrucible("AltBonemeal")))
                .setConcealed()
                .setSecondary()
                .setParents("ALCHEMICALDUPLICATION")
                .registerResearchItem();

        new ResearchItem(
                "LIQUIDDEATH",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.DEATH, 3)
                        .add(Aspect.POISON, 3)
                        .add(Aspect.ENTROPY, 1)
                        .add(Aspect.WATER, 1),
                -7,
                3,
                2,
                new ItemStack(ConfigItems.itemBucketDeath))
                .setPages(
                        new ResearchPage("tc.research_page.LIQUIDDEATH.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("LiquidDeath")))
                .setHidden()
                .setAspectTriggers(Aspect.DEATH, Aspect.POISON)
                .setParents("ENTROPICPROCESSING")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("LIQUIDDEATH", 3);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.itemBucketDeath), 1);

        new ResearchItem(
                "BOTTLETAINT",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.TAINT, 5)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ENTROPY, 1)
                        .add(Aspect.WATER, 1),
                -8,
                1,
                2,
                new ItemStack(ConfigItems.itemBottleTaint))
                .setPages(
                        new ResearchPage("tc.research_page.BOTTLETAINT.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("BottleTaint")))
                .setHidden()
                .setAspectTriggers(Aspect.TAINT)
                .setParents("ENTROPICPROCESSING")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("BOTTLETAINT", 2);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.itemBottleTaint), 1);

        new ResearchItem(
                "PUREIRON",
                "ALCHEMY",
                new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 3),
                -2,
                5,
                1,
                new ItemStack(ConfigItems.itemNugget, 1, 16))
                .setPages(
                        new ResearchPage("tc.research_page.PUREIRON.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("PureIron")))
                .setConcealed()
                .setParents("THAUMIUM")
                .registerResearchItem();

        new ResearchItem(
                "PUREGOLD",
                "ALCHEMY",
                new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 2).add(Aspect.GREED, 1),
                -4,
                3,
                1,
                new ItemStack(ConfigItems.itemNugget, 1, 31))
                .setPages(
                        new ResearchPage("tc.research_page.PUREGOLD.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("PureGold")))
                .setConcealed()
                .setSecondary()
                .setParents("PUREIRON")
                .registerResearchItem();

        if (Config.foundCopperOre && Config.foundCopperIngot) {
            new ResearchItem(
                    "PURECOPPER",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 1),
                    -4,
                    5,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 17))
                    .setPages(
                            new ResearchPage("tc.research_page.PURECOPPER.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("PureCopper")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("PUREIRON")
                    .registerResearchItem();
        }

        if (Config.foundTinOre && Config.foundTinIngot) {
            new ResearchItem(
                    "PURETIN",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 1),
                    -4,
                    7,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 18))
                    .setPages(
                            new ResearchPage("tc.research_page.PURETIN.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("PureTin")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("PUREIRON")
                    .registerResearchItem();
        }

        if (Config.foundSilverOre && Config.foundSilverIngot) {
            new ResearchItem(
                    "PURESILVER",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 2).add(Aspect.GREED, 1),
                    -3,
                    8,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 19))
                    .setPages(
                            new ResearchPage("tc.research_page.PURESILVER.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("PureSilver")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("PUREIRON")
                    .registerResearchItem();
        }

        if (Config.foundLeadOre && Config.foundLeadIngot) {
            new ResearchItem(
                    "PURELEAD",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 3),
                    -2,
                    9,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 20))
                    .setPages(
                            new ResearchPage("tc.research_page.PURELEAD.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("PureLead")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("PUREIRON")
                    .registerResearchItem();
        }

        new ResearchItem(
                "TRANSIRON",
                "ALCHEMY",
                new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 3),
                0,
                5,
                1,
                new ItemStack(ConfigItems.itemNugget, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.TRANSIRON.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("TransIron")))
                .setConcealed()
                .setParents("THAUMIUM")
                .registerResearchItem();

        new ResearchItem(
                "TRANSGOLD",
                "ALCHEMY",
                new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 3),
                2,
                3,
                1,
                new ItemStack(Items.GOLD_NUGGET))
                .setPages(
                        new ResearchPage("tc.research_page.TRANSGOLD.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("TransGold")))
                .setConcealed()
                .setSecondary()
                .setParents("TRANSIRON")
                .registerResearchItem();

        if (Config.foundCopperIngot) {
            new ResearchItem(
                    "TRANSCOPPER",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 3),
                    2,
                    5,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 1))
                    .setPages(
                            new ResearchPage("tc.research_page.TRANSCOPPER.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("TransCopper")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("TRANSIRON")
                    .registerResearchItem();
        }

        if (Config.foundTinIngot) {
            new ResearchItem(
                    "TRANSTIN",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 2).add(Aspect.CRYSTAL, 1),
                    2,
                    7,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 2))
                    .setPages(
                            new ResearchPage("tc.research_page.TRANSTIN.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("TransTin")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("TRANSIRON")
                    .registerResearchItem();
        }

        if (Config.foundSilverIngot) {
            new ResearchItem(
                    "TRANSSILVER",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 2).add(Aspect.GREED, 1),
                    1,
                    8,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 3))
                    .setPages(
                            new ResearchPage("tc.research_page.TRANSSILVER.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("TransSilver")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("TRANSIRON")
                    .registerResearchItem();
        }

        if (Config.foundLeadIngot) {
            new ResearchItem(
                    "TRANSLEAD",
                    "ALCHEMY",
                    new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 2).add(Aspect.ORDER, 1),
                    0,
                    9,
                    1,
                    new ItemStack(ConfigItems.itemNugget, 1, 4))
                    .setPages(
                            new ResearchPage("tc.research_page.TRANSLEAD.1"),
                            new ResearchPage(ConfigResearch.recipeCrucible("TransLead")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("TRANSIRON")
                    .registerResearchItem();
        }

        new ResearchItem(
                "ETHEREALBLOOM",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.MAGIC, 1)
                        .add(Aspect.PLANT, 6)
                        .add(Aspect.HEAL, 3)
                        .add(Aspect.TAINT, 6),
                -2,
                -3,
                2,
                new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.ETHEREALBLOOM.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("EtherealBloom")),
                        new ResearchPage("tc.research_page.ETHEREALBLOOM.2"))
                .setHidden()
                .setAspectTriggers(Aspect.TAINT)
                .setConcealed()
                .setParents("CRUCIBLE")
                .registerResearchItem();

        new ResearchItem(
                "BATHSALTS",
                "ALCHEMY",
                new AspectList().add(Aspect.MIND, 3).add(Aspect.AURA, 3).add(Aspect.ORDER, 3).add(Aspect.HEAL, 3),
                -4,
                -4,
                2,
                new ItemStack(ConfigItems.itemBathSalts))
                .setPages(
                        new ResearchPage("tc.research_page.BATHSALTS.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("BathSalts")))
                .setHidden()
                .registerResearchItem();

        new ResearchItem(
                "SANESOAP",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.MIND, 5)
                        .add(Aspect.ORDER, 5)
                        .add(Aspect.HEAL, 5)
                        .add(Aspect.ELDRITCH, 5),
                -3,
                -6,
                1,
                new ItemStack(ConfigItems.itemSanitySoap))
                .setPages(
                        new ResearchPage("tc.research_page.SANESOAP.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("SaneSoap")))
                .setParents("BATHSALTS")
                .registerResearchItem();

        new ResearchItem(
                "ARCANESPA",
                "ALCHEMY",
                new AspectList().add(Aspect.WATER, 3).add(Aspect.MECHANISM, 3).add(Aspect.ORDER, 3),
                -6,
                -5,
                1,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 12))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANESPA.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("ArcaneSpa")))
                .setSecondary()
                .setParents("BATHSALTS")
                .registerResearchItem();

        new ResearchItem(
                "JARLABEL",
                "ALCHEMY",
                new AspectList(),
                4,
                -3,
                0,
                new ItemStack(ConfigBlocks.blockJar))
                .setPages(
                        new ResearchPage("tc.research_page.JARLABEL.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WardedJar")),
                        new ResearchPage("tc.research_page.JARLABEL.2"),
                        new ResearchPage(ConfigResearch.recipeI("JarLabel")),
                        new ResearchPage("tc.research_page.JARLABEL.3"),
                        new ResearchPage(buildJarLabelAspectPages()),
                        new ResearchPage(ConfigResearch.recipeI("JarLabelNull")))
                .setParents("DISTILESSENTIA")
                .setStub()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "JARVOID",
                "ALCHEMY",
                new AspectList().add(Aspect.WATER, 3).add(Aspect.ENTROPY, 3).add(Aspect.VOID, 6),
                5,
                -5,
                1,
                new ItemStack(ConfigBlocks.blockJar, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.JARVOID.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("JarVoid")))
                .setParents("JARLABEL")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();
    }

    private static IRecipe[] buildJarLabelAspectPages() {
        ArrayList<IRecipe> pages = new ArrayList<>();
        for (int index = 0; index < Aspect.aspects.values().size(); index++) {
            pages.add(ConfigResearch.recipeI("JarLabel" + index));
        }
        return pages.toArray(new IRecipe[0]);
    }
}
