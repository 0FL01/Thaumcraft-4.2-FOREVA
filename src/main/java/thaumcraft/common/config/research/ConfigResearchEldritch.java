package thaumcraft.common.config.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

final class ConfigResearchEldritch {
    private ConfigResearchEldritch() {}

    static void initEldritchResearchBaseline() {
        new ResearchItem(
                "ELDRITCHMINOR",
                "ELDRITCH",
                new AspectList(),
                1,
                0,
                0,
                new ResourceLocation("thaumcraft", "textures/misc/r_eldritchminor.png"))
                .setPages(
                        new ResearchPage("tc.research_page.ELDRITCHMINOR.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("VoidSeed")))
                .setHidden()
                .setRound()
                .setSpecial()
                .registerResearchItem();

        new ResearchItem(
                "OCULUS",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.MIND, 3)
                        .add(Aspect.DARKNESS, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.TRAVEL, 6)
                        .add(Aspect.ELDRITCH, 6),
                -2,
                2,
                1,
                new ItemStack(ConfigItems.itemEldritchObject, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.OCULUS.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("EldritchEye")),
                        new ResearchPage("tc.research_page.OCULUS.2"))
                .setRound()
                .setConcealed()
                .setParents("CRIMSON", "ELDRITCHMAJOR")
                .setSpecial()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("OCULUS", 6);

        new ResearchItem(
                "ENTEROUTER",
                "ELDRITCH",
                new AspectList(),
                -3,
                4,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_outer.png"))
                .setPages(new ResearchPage("tc.research_page.ENTEROUTER.1"))
                .setStub()
                .setHidden()
                .setRound()
                .setParents("OCULUS")
                .registerResearchItem();

        new ResearchItem(
                "OUTERREV",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.ELDRITCH, 4)
                        .add(Aspect.MIND, 4),
                -5,
                3,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_outerrev.png"))
                .setPages(new ResearchPage("tc.research_page.OUTERREV.1"))
                .setItemTriggers(
                        new ItemStack(ConfigBlocks.blockEldritch, 1, 5),
                        new ItemStack(ConfigBlocks.blockEldritch, 1, 10))
                .setLost()
                .setSecondary()
                .setSpecial()
                .setParents("ENTEROUTER")
                .registerResearchItem();

        new ResearchItem(
                "PRIMPEARL",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.AIR, 8)
                        .add(Aspect.EARTH, 8)
                        .add(Aspect.FIRE, 8)
                        .add(Aspect.WATER, 8)
                        .add(Aspect.ORDER, 8)
                        .add(Aspect.ENTROPY, 8),
                0,
                4,
                1,
                new ItemStack(ConfigItems.itemEldritchObject, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.PRIMPEARL.1"),
                        new ResearchPage("tc.research_page.PRIMPEARL.2"))
                .setItemTriggers(new ItemStack(ConfigItems.itemEldritchObject, 1, 3))
                .setLost()
                .setSecondary()
                .setSpecial()
                .setParents("ELDRITCHMINOR")
                .registerResearchItem();

        new ResearchItem(
                "PRIMNODE",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.AURA, 1)
                        .add(Aspect.MAGIC, 1)
                        .add(Aspect.ORDER, 1)
                        .add(Aspect.ENTROPY, 1),
                0,
                6,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_nodes_2.png"))
                .setPages(new ResearchPage("tc.research_page.PRIMNODE.1"))
                .setSecondary()
                .setParents("PRIMPEARL")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("PRIMNODE", 1);

        new ResearchItem(
                "ADVALCHEMYFURNACE",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.AURA, 1)
                        .add(Aspect.MAGIC, 1)
                        .add(Aspect.ORDER, 1)
                        .add(Aspect.ENTROPY, 1),
                -2,
                6,
                1,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.ADVALCHEMYFURNACE.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("AdvAlchemyConstruct")),
                        new ResearchPage("tc.research_page.ADVALCHEMYFURNACE.2"),
                        new ResearchPage(ConfigResearch.recipeList("AdvAlchemyFurnace")))
                .setSecondary()
                .setParents("PRIMPEARL", "DISTILESSENTIA", "VISPOWER")
                .registerResearchItem();

        new ResearchItem(
                "PRIMALCRUSHER",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.MINE, 6)
                        .add(Aspect.TOOL, 6)
                        .add(Aspect.ENTROPY, 6)
                        .add(Aspect.VOID, 6)
                        .add(Aspect.WEAPON, 6)
                        .add(Aspect.ELDRITCH, 6)
                        .add(Aspect.GREED, 6),
                2,
                5,
                2,
                new ItemStack(ConfigItems.itemPrimalCrusher))
                .setPages(
                        new ResearchPage("tc.research_page.PRIMALCRUSHER.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("PrimalCrusher")),
                        new ResearchPage("tc.research_page.PRIMALCRUSHER.2"))
                .setConcealed()
                .setParents("PRIMPEARL")
                .setParentsHidden("VOIDMETAL", "ELEMENTALPICK", "ELEMENTALSHOVEL")
                .registerResearchItem();

        new ResearchItem(
                "VOIDMETAL",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.METAL, 3)
                        .add(Aspect.ELDRITCH, 3)
                        .add(Aspect.DARKNESS, 3)
                        .add(Aspect.VOID, 5),
                2,
                -2,
                2,
                new ItemStack(ConfigItems.itemResource, 1, 16))
                .setPages(
                        new ResearchPage("tc.research_page.VOIDMETAL.1"),
                        new ResearchPage(ConfigResearch.recipeCrucible("VoidMetal")),
                        new ResearchPage("tc.research_page.VOIDMETAL.2"),
                        new ResearchPage(ConfigResearch.recipeI("VoidAxe")),
                        new ResearchPage(ConfigResearch.recipeI("VoidSword")),
                        new ResearchPage(ConfigResearch.recipeI("VoidPick")),
                        new ResearchPage(ConfigResearch.recipeI("VoidShovel")),
                        new ResearchPage(ConfigResearch.recipeI("VoidHoe")),
                        new ResearchPage(ConfigResearch.recipeI("VoidHelm")),
                        new ResearchPage(ConfigResearch.recipeI("VoidChest")),
                        new ResearchPage(ConfigResearch.recipeI("VoidLegs")),
                        new ResearchPage(ConfigResearch.recipeI("VoidBoots")))
                .setParents("THAUMIUM", "ELDRITCHMINOR")
                .registerResearchItem();

        new ResearchItem(
                "ESSENTIARESERVOIR",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.WATER, 5)
                        .add(Aspect.VOID, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.MAGIC, 5)
                        .add(Aspect.VOID, 5),
                4,
                -3,
                2,
                new ItemStack(ConfigBlocks.blockEssentiaReservoir))
                .setPages(
                        new ResearchPage("tc.research_page.ESSENTIARESERVOIR.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("EssentiaReservoir")),
                        new ResearchPage("tc.research_page.ESSENTIARESERVOIR.2"))
                .setParents("VOIDMETAL", "CENTRIFUGE", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "CAP_void",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.VOID, 5)
                        .add(Aspect.ELDRITCH, 5)
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.AURA, 3),
                5,
                -1,
                3,
                new ItemStack(ConfigItems.itemWandCap, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.CAP_void.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandCapVoidInert")),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandCapVoid")))
                .setConcealed()
                .setParents("CAP_thaumium", "VOIDMETAL")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("CAP_void", 1);

        new ResearchItem(
                "ARMORVOIDFORTRESS",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.ARMOR, 5)
                        .add(Aspect.ELDRITCH, 3)
                        .add(Aspect.CLOTH, 3)
                        .add(Aspect.DARKNESS, 3)
                        .add(Aspect.VOID, 5),
                0,
                -3,
                3,
                new ItemStack(ConfigItems.itemHelmVoidRobe))
                .setPages(
                        new ResearchPage("tc.research_page.ARMORVOIDFORTRESS.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("VoidRobeHelm")),
                        new ResearchPage(ConfigResearch.recipeInfusion("VoidRobeChest")),
                        new ResearchPage(ConfigResearch.recipeInfusion("VoidRobeLegs")))
                .setParents("VOIDMETAL", "ENCHFABRIC", "ELDRITCHMAJOR")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "FOCUSPRIMAL",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.AIR, 6)
                        .add(Aspect.WATER, 6)
                        .add(Aspect.FIRE, 6)
                        .add(Aspect.EARTH, 6)
                        .add(Aspect.ORDER, 6)
                        .add(Aspect.ENTROPY, 6)
                        .add(Aspect.MAGIC, 6),
                4,
                1,
                2,
                new ItemStack(ConfigItems.focusPrimal))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSPRIMAL.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocusPrimal")))
                .setConcealed()
                .setParents("ELDRITCHMINOR")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("FOCUSPRIMAL", 2);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.focusPrimal), 1);

        new ResearchItem(
                "SANITYCHECK",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.MIND, 5)
                        .add(Aspect.ELDRITCH, 3)
                        .add(Aspect.SENSES, 5),
                2,
                2,
                1,
                new ItemStack(ConfigItems.itemSanityChecker))
                .setPages(
                        new ResearchPage("tc.research_page.SANITYCHECK.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("SanityCheck")))
                .setParents("ELDRITCHMINOR")
                .registerResearchItem();

        new ResearchItem(
                "ROD_primal_staff",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.AIR, 9)
                        .add(Aspect.EARTH, 9)
                        .add(Aspect.FIRE, 9)
                        .add(Aspect.WATER, 9)
                        .add(Aspect.ORDER, 9)
                        .add(Aspect.ENTROPY, 9)
                        .add(Aspect.TOOL, 9)
                        .add(Aspect.MAGIC, 12),
                6,
                2,
                3,
                new ItemStack(ConfigItems.itemWandRod, 1, 100))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_primal_staff.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodPrimalStaff")))
                .setHidden()
                .setEntityTriggers("Thaumcraft.PrimalOrb")
                .setItemTriggers(new ItemStack(ConfigItems.focusPrimal))
                .setParents("FOCUSPRIMAL")
                .setParentsHidden(
                        "ROD_silverwood_staff",
                        "ROD_bone_staff",
                        "ROD_greatwood_staff",
                        "ROD_blaze_staff",
                        "ROD_reed_staff",
                        "ROD_obsidian_staff",
                        "ROD_quartz_staff",
                        "ROD_ice_staff")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("ROD_primal_staff", 3);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.itemWandRod, 1, 100), 1);
    }

    static void initEldritchResearchTextOnlyBaseline() {
        new ResearchItem(
                "ELDRITCHMAJOR",
                "ELDRITCH",
                new AspectList(),
                -1,
                0,
                0,
                new ResourceLocation("thaumcraft", "textures/misc/r_eldritchmajor.png"))
                .setPages(
                        new ResearchPage("tc.research_page.ELDRITCHMAJOR.1"),
                        new ResearchPage("tc.research_page.ELDRITCHMAJOR.2"))
                .setStub()
                .setHidden()
                .setRound()
                .setSpecial()
                .registerResearchItem();
    }
}
