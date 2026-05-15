package thaumcraft.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ConfigResearch {
    public static final Map<String, Object> recipes = new HashMap<>();

    public static void init() {
        initCategories();
        initBasicResearchBaseline();
        initBasicResearchProgressionBaseline();
        initAlchemyResearchBaseline();
        initArtificeResearchBaseline();
        initThaumaturgyResearchBaseline();
        initBasicResearchTextOnlyExtended();
        initThaumaturgyResearchTextOnlyBaseline();
        initEldritchResearchTextOnlyBaseline();
        initEldritchResearchBaseline();
        // Stage 9-e research item/page graph registration remains in progress.
    }

    private static void initCategories() {
        ResearchCategories.registerCategory(
                "BASICS",
                new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "THAUMATURGY",
                new ResourceLocation("thaumcraft", "textures/misc/r_thaumaturgy.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "ALCHEMY",
                new ResourceLocation("thaumcraft", "textures/misc/r_crucible.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "ARTIFICE",
                new ResourceLocation("thaumcraft", "textures/misc/r_artifice.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "GOLEMANCY",
                new ResourceLocation("thaumcraft", "textures/misc/r_golemancy.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "ELDRITCH",
                new ResourceLocation("thaumcraft", "textures/misc/r_eldritch.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png"));
    }

    private static void initBasicResearchBaseline() {
        new ResearchItem(
                "ASPECTS",
                "BASICS",
                new AspectList(),
                0,
                0,
                0,
                new ResourceLocation("thaumcraft", "textures/misc/r_aspects.png"))
                .setPages(
                        new ResearchPage("tc.research_page.ASPECTS.1"),
                        new ResearchPage("tc.research_page.ASPECTS.2"),
                        new ResearchPage("tc.research_page.ASPECTS.3"))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "PECH",
                "BASICS",
                new AspectList(),
                -4,
                -4,
                0,
                new ResourceLocation("thaumcraft", "textures/misc/r_pech.png"))
                .setPages(
                        new ResearchPage("tc.research_page.PECH.1"),
                        new ResearchPage("tc.research_page.PECH.2"))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "NODES",
                "BASICS",
                new AspectList(),
                -2,
                0,
                0,
                new ResourceLocation("thaumcraft", "textures/misc/r_nodes.png"))
                .setPages(
                        new ResearchPage("tc.research_page.NODES.1"),
                        new ResearchPage("tc.research_page.NODES.2"),
                        new ResearchPage("tc.research_page.NODES.3"))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "WARP",
                "BASICS",
                new AspectList(),
                0,
                2,
                0,
                new ResourceLocation("thaumcraft", "textures/misc/r_warp.png"))
                .setPages(
                        new ResearchPage("tc.research_page.WARP.1"),
                        new ResearchPage("tc.research_page.WARP.2"),
                        new ResearchPage("tc.research_page.WARP.3"))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();
    }

    private static void initBasicResearchProgressionBaseline() {
        new ResearchItem(
                "RESEARCH",
                "BASICS",
                new AspectList(),
                2,
                0,
                0,
                new ItemStack(ConfigItems.itemInkwell))
                .setPages(
                        new ResearchPage("tc.research_page.RESEARCH.1"),
                        new ResearchPage("tc.research_page.RESEARCH.2"),
                        new ResearchPage((IRecipe) recipes.get("Thaumometer")),
                        new ResearchPage("tc.research_page.RESEARCH.3"),
                        new ResearchPage("tc.research_page.RESEARCH.4"),
                        new ResearchPage((IRecipe) recipes.get("Scribe1")),
                        new ResearchPage((IRecipe) recipes.get("Scribe2")),
                        new ResearchPage((IRecipe) recipes.get("Scribe3")),
                        new ResearchPage("tc.research_page.RESEARCH.5"),
                        new ResearchPage("tc.research_page.RESEARCH.6"),
                        new ResearchPage("tc.research_page.RESEARCH.7"),
                        new ResearchPage("tc.research_page.RESEARCH.8"),
                        new ResearchPage("tc.research_page.RESEARCH.9"),
                        new ResearchPage("tc.research_page.RESEARCH.10"),
                        new ResearchPage("tc.research_page.RESEARCH.11"),
                        new ResearchPage("tc.research_page.RESEARCH.12"))
                .setAutoUnlock()
                .setStub()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "KNOWFRAG",
                "BASICS",
                new AspectList(),
                3,
                -2,
                0,
                new ItemStack(ConfigItems.itemResource, 1, 9))
                .setPages(
                        new ResearchPage("tc.research_page.KNOWFRAG.1"),
                        new ResearchPage((IRecipe) recipes.get("KnowFrag")))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .setParents("RESEARCH")
                .registerResearchItem();

        new ResearchItem(
                "THAUMONOMICON",
                "BASICS",
                new AspectList(),
                1,
                -2,
                0,
                new ItemStack(ConfigItems.itemThaumonomicon))
                .setPages(
                        new ResearchPage("tc.research_page.THAUMONOMICON.1"),
                        new ResearchPage((List<?>) recipes.get("Thaumonomicon")))
                .setAutoUnlock()
                .setStub()
                .setRound()
                .setParents("RESEARCH")
                .registerResearchItem();

        new ResearchItem(
                "PLANTS",
                "BASICS",
                new AspectList(),
                -2,
                -4,
                0,
                new ItemStack(ConfigBlocks.blockCustomPlant, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.PLANTS.1"),
                        new ResearchPage((IRecipe) recipes.get("PlankGreatwood")),
                        new ResearchPage("tc.research_page.PLANTS.2"),
                        new ResearchPage((IRecipe) recipes.get("PlankSilverwood")),
                        new ResearchPage("tc.research_page.PLANTS.3"),
                        new ResearchPage("tc.research_page.PLANTS.4"),
                        new ResearchPage("tc.research_page.PLANTS.5"),
                        new ResearchPage("tc.research_page.PLANTS.6"))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "RESEARCHER1",
                "BASICS",
                new AspectList()
                        .add(Aspect.MIND, 3)
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.ORDER, 3),
                4,
                1,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_researcher1.png"))
                .setPages(new ResearchPage("tc.research_page.RESEARCHER1.1"))
                .setRound()
                .setParents("RESEARCH")
                .registerResearchItem();

        new ResearchItem(
                "DECONSTRUCTOR",
                "BASICS",
                new AspectList()
                        .add(Aspect.MIND, 3)
                        .add(Aspect.CRAFT, 3)
                        .add(Aspect.ENTROPY, 3),
                6,
                2,
                1,
                new ItemStack(ConfigBlocks.blockTable, 1, 14))
                .setPages(
                        new ResearchPage("tc.research_page.DECONSTRUCTOR.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("Deconstructor")),
                        new ResearchPage("tc.research_page.DECONSTRUCTOR.2"))
                .setRound()
                .setParents("RESEARCHER1")
                .registerResearchItem();

        new ResearchItem(
                "RESEARCHER2",
                "BASICS",
                new AspectList()
                        .add(Aspect.MIND, 6)
                        .add(Aspect.ORDER, 3)
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.MAGIC, 3),
                3,
                3,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_researcher2.png"))
                .setPages(new ResearchPage("tc.research_page.RESEARCHER2.1"))
                .setRound()
                .setSpecial()
                .setParents("RESEARCHER1")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("RESEARCHER2", 1);

        new ResearchItem(
                "RESEARCHDUPE",
                "BASICS",
                new AspectList()
                        .add(Aspect.MIND, 6)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.GREED, 3)
                        .add(Aspect.CRAFT, 3),
                4,
                5,
                3,
                new ResourceLocation("thaumcraft", "textures/misc/r_resdupe.png"))
                .setPages(new ResearchPage("tc.research_page.RESEARCHDUPE.1"))
                .setRound()
                .setParents("RESEARCHER2")
                .registerResearchItem();
    }

    private static void initAlchemyResearchBaseline() {
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
                        new ResearchPage((IRecipe) recipes.get("Phial")))
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
                        new ResearchPage((List<?>) recipes.get("Crucible")),
                        new ResearchPage("tc.research_page.CRUCIBLE.4"),
                        new ResearchPage("tc.research_page.CRUCIBLE.5"),
                        new ResearchPage(new ItemStack(ConfigItems.itemShard, 1, 6)))
                .setStub()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "NITOR",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.LIGHT, 3)
                        .add(Aspect.FIRE, 1),
                2,
                -1,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.NITOR.1"),
                        new ResearchPage((CrucibleRecipe) recipes.get("Nitor")))
                .setParents("CRUCIBLE")
                .registerResearchItem();

        new ResearchItem(
                "ALUMENTUM",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.FIRE, 1),
                2,
                1,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.ALUMENTUM.1"),
                        new ResearchPage((CrucibleRecipe) recipes.get("Alumentum")))
                .setParents("CRUCIBLE")
                .registerResearchItem();

        new ResearchItem(
                "DISTILESSENTIA",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.SLIME, 3),
                5,
                -1,
                1,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.DISTILESSENTIA.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("AlchemyFurnace")),
                        new ResearchPage("tc.research_page.DISTILESSENTIA.2"),
                        new ResearchPage((IArcaneRecipe) recipes.get("Filter")),
                        new ResearchPage((IArcaneRecipe) recipes.get("Alembic")),
                        new ResearchPage((IArcaneRecipe) recipes.get("AlchemicalConstruct")))
                .setSiblings("JARLABEL")
                .setParents("NITOR", "ALUMENTUM")
                .registerResearchItem();

        new ResearchItem(
                "THAUMIUM",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.METAL, 3)
                        .add(Aspect.MAGIC, 3),
                -1,
                3,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.THAUMIUM.1"),
                        new ResearchPage((CrucibleRecipe) recipes.get("Thaumium")))
                .setHidden()
                .setAspectTriggers(Aspect.METAL)
                .setParents("CRUCIBLE")
                .registerResearchItem();
    }

    private static void initArtificeResearchBaseline() {
        new ResearchItem(
                "ARCANESTONE",
                "ARTIFICE",
                new AspectList(),
                5,
                -2,
                0,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANESTONE.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("ArcaneStone1")),
                        new ResearchPage((IRecipe) recipes.get("ArcaneStone2")),
                        new ResearchPage((IRecipe) recipes.get("ArcaneStone3")),
                        new ResearchPage((IRecipe) recipes.get("ArcaneStone4")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "GRATE",
                "ARTIFICE",
                new AspectList(),
                2,
                -1,
                0,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.GRATE.1"),
                        new ResearchPage((IRecipe) recipes.get("Grate")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "TABLE",
                "ARTIFICE",
                new AspectList(),
                0,
                -1,
                0,
                new ItemStack(ConfigBlocks.blockTable))
                .setPages(
                        new ResearchPage("tc.research_page.TABLE.1"),
                        new ResearchPage((IRecipe) recipes.get("Table")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "ARCTABLE",
                "ARTIFICE",
                new AspectList(),
                -1,
                -3,
                0,
                new ItemStack(ConfigBlocks.blockTable, 1, 15))
                .setPages(
                        new ResearchPage("tc.research_page.ARCTABLE.1"),
                        new ResearchPage((List<?>) recipes.get("ArcTable")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .setParents("TABLE")
                .registerResearchItem();

        new ResearchItem(
                "RESTABLE",
                "ARTIFICE",
                new AspectList(),
                1,
                -3,
                0,
                new ItemStack(ConfigBlocks.blockTable, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RESTABLE.1"),
                        new ResearchPage((List<?>) recipes.get("ResTable")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .setParents("TABLE")
                .registerResearchItem();

        new ResearchItem(
                "THAUMOMETER",
                "ARTIFICE",
                new AspectList(),
                2,
                1,
                0,
                new ItemStack(ConfigItems.itemThaumometer))
                .setPages(
                        new ResearchPage("tc.research_page.THAUMOMETER.1"),
                        new ResearchPage((IRecipe) recipes.get("Thaumometer")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "PAVETRAVEL",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.FLIGHT, 3),
                4,
                -4,
                1,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.PAVETRAVEL.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("PaveTravel")))
                .setParents("ARCANESTONE")
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "PAVEWARD",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.TRAP, 3)
                        .add(Aspect.BEAST, 3),
                6,
                -4,
                1,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.PAVEWARD.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("PaveWard")),
                        new ResearchPage("tc.research_page.PAVEWARD.2"))
                .setParents("ARCANESTONE")
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "GOGGLES",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MAGIC, 3),
                4,
                1,
                1,
                new ItemStack(ConfigItems.itemGoggles))
                .setPages(
                        new ResearchPage("tc.research_page.GOGGLES.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("Goggles")))
                .setParents("THAUMOMETER")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ARCANEEAR",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.AIR, 3),
                6,
                0,
                1,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANEEAR.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("ArcaneEar")))
                .setParents("GOGGLES")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "SINSTONE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.DARKNESS, 3)
                        .add(Aspect.ELDRITCH, 3)
                        .add(Aspect.AURA, 3),
                6,
                2,
                1,
                new ItemStack(ConfigItems.itemCompassStone, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.SINSTONE.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("SinStone")))
                .setParents("GOGGLES")
                .setConcealed()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("SINSTONE", 2);

        new ResearchItem(
                "LEVITATOR",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.FLIGHT, 3)
                        .add(Aspect.AIR, 3),
                -3,
                -3,
                1,
                new ItemStack(ConfigBlocks.blockLifter))
                .setPages(
                        new ResearchPage("tc.research_page.LEVITATOR.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("Levitator")))
                .setConcealed()
                .setParents("NITOR")
                .registerResearchItem();

        new ResearchItem(
                "ARCANEBORE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MINE, 6)
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.TOOL, 3),
                -3,
                8,
                2,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANEBORE.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("ArcaneBore")),
                        new ResearchPage("tc.research_page.ARCANEBORE.2"),
                        new ResearchPage((IArcaneRecipe) recipes.get("ArcaneBoreBase")),
                        new ResearchPage("tc.research_page.ARCANEBORE.3"))
                .setConcealed()
                .setParents("FOCUSEXCAVATION", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ARCANELAMP",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.LIGHT, 3)
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.DARKNESS, 3),
                -3,
                1,
                1,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANELAMP.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("ArcaneLamp")),
                        new ResearchPage("ARCANEBORE", "tc.research_page.ARCANELAMP.2"))
                .setSecondary()
                .setParents("NITOR")
                .registerResearchItem();

        new ResearchItem(
                "ENCHFABRIC",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.CLOTH, 3)
                        .add(Aspect.MAGIC, 3),
                0,
                3,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.ENCHFABRIC.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("EnchantedFabric")),
                        new ResearchPage("tc.research_page.ENCHFABRIC.2"),
                        new ResearchPage((IArcaneRecipe) recipes.get("RobeChest")),
                        new ResearchPage((IArcaneRecipe) recipes.get("RobeLegs")),
                        new ResearchPage((IArcaneRecipe) recipes.get("RobeBoots")))
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALAXE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MOTION, 3),
                -7,
                4,
                2,
                new ItemStack(ConfigItems.itemAxeElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALAXE.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("ElementalAxe")),
                        new ResearchPage("tc.research_page.ELEMENTALAXE.2"))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALPICK",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.SENSES, 3),
                -7,
                3,
                2,
                new ItemStack(ConfigItems.itemPickElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALPICK.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("ElementalPick")),
                        new ResearchPage("tc.research_page.ELEMENTALPICK.2"))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALSWORD",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.ENERGY, 3),
                -7,
                5,
                2,
                new ItemStack(ConfigItems.itemSwordElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALSWORD.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("ElementalSword")))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALSHOVEL",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.CRAFT, 3),
                -7,
                6,
                2,
                new ItemStack(ConfigItems.itemShovelElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALSHOVEL.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("ElementalShovel")),
                        new ResearchPage("tc.research_page.ELEMENTALSHOVEL.2"))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALHOE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.LIFE, 3)
                        .add(Aspect.CROP, 3),
                -7,
                7,
                2,
                new ItemStack(ConfigItems.itemHoeElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALHOE.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("ElementalHoe")))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "INFUSION",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.CRAFT, 6),
                -4,
                5,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.INFUSION.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("InfusionMatrix")),
                        new ResearchPage((IArcaneRecipe) recipes.get("ArcanePedestal")),
                        new ResearchPage("tc.research_page.INFUSION.2"),
                        new ResearchPage("tc.research_page.INFUSION.3"),
                        new ResearchPage("tc.research_page.INFUSION.4"),
                        new ResearchPage("tc.research_page.INFUSION.5"))
                .setParents("DISTILESSENTIA")
                .setConcealed()
                .registerResearchItem();
    }

    private static void initThaumaturgyResearchBaseline() {
        new ResearchItem(
                "BASICTHAUMATURGY",
                "THAUMATURGY",
                new AspectList(),
                0,
                0,
                0,
                new ItemStack(ConfigItems.itemWandCasting, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.BASICTHAUMATURGY.1"),
                        new ResearchPage("tc.research_page.BASICTHAUMATURGY.2"),
                        new ResearchPage((IRecipe) recipes.get("WandCapIron")),
                        new ResearchPage((IRecipe) recipes.get("WandBasic")))
                .setAutoUnlock()
                .setStub()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "FOCUSFIRE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                2,
                -2,
                1,
                new ItemStack(ConfigItems.focusFire))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSFIRE.1"),
                        new ResearchPage("tc.research_page.FOCUSFIRE.2"),
                        new ResearchPage((IArcaneRecipe) recipes.get("FocusFire")))
                .setParents("BASICTHAUMATURGY")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSFROST",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.COLD, 6),
                1,
                -5,
                1,
                new ItemStack(ConfigItems.focusFrost))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSFROST.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("FocusFrost")))
                .setConcealed()
                .setSecondary()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSHELLBAT",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.BEAST, 6)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                3,
                -7,
                2,
                new ItemStack(ConfigItems.focusHellbat))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSHELLBAT.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("FocusHellbat")))
                .setHidden()
                .setEntityTriggers("Thaumcraft.Firebat")
                .setAspectTriggers(Aspect.FIRE)
                .setParentsHidden("FOCUSFIRE", "INFUSION")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("FOCUSHELLBAT", 2);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.focusHellbat), 1);

        new ResearchItem(
                "FOCUSEXCAVATION",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.ENTROPY, 3)
                        .add(Aspect.MAGIC, 3),
                0,
                -3,
                2,
                new ItemStack(ConfigItems.focusExcavation))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSEXCAVATION.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("FocusExcavation")))
                .setConcealed()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        if (Config.wardedStone) {
            new ResearchItem(
                    "FOCUSWARDING",
                    "THAUMATURGY",
                    new AspectList()
                            .add(Aspect.EARTH, 6)
                            .add(Aspect.ARMOR, 3)
                            .add(Aspect.ORDER, 3)
                            .add(Aspect.MIND, 3),
                    -2,
                    -4,
                    3,
                    new ItemStack(ConfigItems.focusWarding))
                    .setPages(
                            new ResearchPage("tc.research_page.FOCUSWARDING.1"),
                            new ResearchPage((InfusionRecipe) recipes.get("FocusWarding")))
                    .setConcealed()
                    .setParents("FOCUSEXCAVATION", "INFUSION")
                    .registerResearchItem();
        }

        new ResearchItem(
                "FOCUSSHOCK",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AIR, 3)
                        .add(Aspect.ENERGY, 6)
                        .add(Aspect.MAGIC, 3),
                3,
                -5,
                1,
                new ItemStack(ConfigItems.focusShock))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSSHOCK.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("FocusShock")))
                .setConcealed()
                .setSecondary()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSTRADE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.EXCHANGE, 6)
                        .add(Aspect.MAGIC, 3),
                4,
                -3,
                2,
                new ItemStack(ConfigItems.focusTrade))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSTRADE.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("FocusTrade")))
                .setConcealed()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSPORTABLEHOLE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.ENTROPY, 3)
                        .add(Aspect.ELDRITCH, 6)
                        .add(Aspect.AIR, 3),
                7,
                -2,
                2,
                new ItemStack(ConfigItems.focusPortableHole))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSPORTABLEHOLE.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("FocusPortableHole")))
                .setConcealed()
                .setParents("FOCUSTRADE", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSPOUCH",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.VOID, 6)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.TOOL, 3),
                4,
                -1,
                1,
                new ItemStack(ConfigItems.itemFocusPouch))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSPOUCH.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("FocusPouch")))
                .setParents("FOCUSFIRE")
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "CAP_gold",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.METAL, 3)
                        .add(Aspect.GREED, 3)
                        .add(Aspect.TOOL, 3),
                3,
                2,
                1,
                new ItemStack(ConfigItems.itemWandCap, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.CAP_gold.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandCapGold")))
                .setParents("BASICTHAUMATURGY")
                .registerResearchItem();

        if (Config.foundCopperIngot) {
            new ResearchItem(
                    "CAP_copper",
                    "THAUMATURGY",
                    new AspectList()
                            .add(Aspect.METAL, 3)
                            .add(Aspect.EXCHANGE, 3)
                            .add(Aspect.TOOL, 3),
                    2,
                    0,
                    1,
                    new ItemStack(ConfigItems.itemWandCap, 1, 3))
                    .setPages(
                            new ResearchPage("tc.research_page.CAP_copper.1"),
                            new ResearchPage((IArcaneRecipe) recipes.get("WandCapCopper")))
                    .setParents("BASICTHAUMATURGY")
                    .registerResearchItem();
        }

        new ResearchItem(
                "CAP_thaumium",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.METAL, 6)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.AURA, 3),
                5,
                4,
                2,
                new ItemStack(ConfigItems.itemWandCap, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.CAP_thaumium.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandCapThaumiumInert")),
                        new ResearchPage((InfusionRecipe) recipes.get("WandCapThaumium")))
                .setParents("CAP_gold", "THAUMIUM", "INFUSION")
                .registerResearchItem();

        if (Config.foundSilverIngot) {
            new ResearchItem(
                    "CAP_silver",
                    "THAUMATURGY",
                    new AspectList()
                            .add(Aspect.METAL, 3)
                            .add(Aspect.GREED, 3)
                            .add(Aspect.TOOL, 3)
                            .add(Aspect.AURA, 3),
                    5,
                    1,
                    1,
                    new ItemStack(ConfigItems.itemWandCap, 1, 4))
                    .setPages(
                            new ResearchPage("tc.research_page.CAP_silver.1"),
                            new ResearchPage((IArcaneRecipe) recipes.get("WandCapSilverInert")),
                            new ResearchPage((InfusionRecipe) recipes.get("WandCapSilver")))
                    .setConcealed()
                    .setParents("CAP_gold", "INFUSION")
                    .registerResearchItem();
        }

        new ResearchItem(
                "ROD_greatwood",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 3),
                -5,
                2,
                1,
                new ItemStack(ConfigItems.itemWandRod, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_greatwood.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodGreatwood")))
                .setParents("BASICTHAUMATURGY")
                .registerResearchItem();

        new ResearchItem(
                "ROD_reed",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.AIR, 6)
                        .add(Aspect.PLANT, 3)
                        .add(Aspect.MAGIC, 3),
                -5,
                -1,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_reed.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodReed")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_blaze",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.FIRE, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.MAGIC, 3),
                -7,
                0,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 6))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_blaze.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodBlaze")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_obsidian",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.EARTH, 6)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                -8,
                2,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_obsidian.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodObsidian")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_ice",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.COLD, 6)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3),
                -7,
                4,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_ice.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodIce")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_quartz",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ORDER, 6)
                        .add(Aspect.CRYSTAL, 3)
                        .add(Aspect.MAGIC, 3),
                -5,
                5,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_quartz.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodQuartz")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_bone",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ENTROPY, 6)
                        .add(Aspect.UNDEAD, 3)
                        .add(Aspect.MAGIC, 3),
                -3,
                0,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_bone.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodBone")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("ROD_bone", 1);

        new ResearchItem(
                "ROD_silverwood",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 6)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 9),
                -2,
                5,
                3,
                new ItemStack(ConfigItems.itemWandRod, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_silverwood.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodSilverwood")))
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_greatwood_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 3),
                -1,
                7,
                1,
                new ItemStack(ConfigItems.itemWandRod, 1, 50))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_greatwood_staff.1"),
                        new ResearchPage("tc.research_page.ROD_greatwood_staff.2"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodGreatwoodStaff")))
                .setParents("ROD_silverwood")
                .registerResearchItem();

        new ResearchItem(
                "ROD_reed_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.AIR, 6)
                        .add(Aspect.PLANT, 3)
                        .add(Aspect.MAGIC, 3),
                -5,
                -2,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 55))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_reed_staff.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodReedStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_reed")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_blaze_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.FIRE, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.MAGIC, 3),
                -8,
                -1,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 56))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_blaze_staff.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodBlazeStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_blaze")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_obsidian_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.EARTH, 6)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                -9,
                2,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 51))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_obsidian_staff.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodObsidianStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_obsidian")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_ice_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.COLD, 6)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3),
                -8,
                5,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 53))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_ice_staff.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodIceStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_ice")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_quartz_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ORDER, 6)
                        .add(Aspect.CRYSTAL, 3)
                        .add(Aspect.MAGIC, 3),
                -4,
                6,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 54))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_quartz_staff.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodQuartzStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_quartz")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_bone_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ENTROPY, 6)
                        .add(Aspect.UNDEAD, 3)
                        .add(Aspect.MAGIC, 3),
                -2,
                -1,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 57))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_bone_staff.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodBoneStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_bone")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("ROD_bone_staff", 1);

        new ResearchItem(
                "ROD_silverwood_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 6)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 9),
                -1,
                5,
                3,
                new ItemStack(ConfigItems.itemWandRod, 1, 52))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_silverwood_staff.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("WandRodSilverwoodStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_silverwood")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "NODESTABILIZER",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 4)
                        .add(Aspect.ORDER, 4)
                        .add(Aspect.ENERGY, 4),
                -7,
                -4,
                1,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9))
                .setPages(
                        new ResearchPage("tc.research_page.NODESTABILIZER.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("NodeStabilizer")),
                        new ResearchPage("tc.research_page.NODESTABILIZER.2"))
                .setParents("NODEPRESERVE")
                .registerResearchItem();

        new ResearchItem(
                "NODESTABILIZERADV",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 9)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.ORDER, 6)
                        .add(Aspect.ENERGY, 6),
                -8,
                -3,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9))
                .setPages(
                        new ResearchPage("tc.research_page.NODESTABILIZERADV.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("NodeStabilizerAdv")))
                .setSecondary()
                .setConcealed()
                .setParents("NODESTABILIZER")
                .registerResearchItem();

        new ResearchItem(
                "VISPOWER",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.ENERGY, 6),
                -5,
                -6,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 11))
                .setPages(
                        new ResearchPage("tc.research_page.VISPOWER.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("NodeTransducer")),
                        new ResearchPage("tc.research_page.VISPOWER.2"),
                        new ResearchPage("tc.research_page.VISPOWER.3"),
                        new ResearchPage((IArcaneRecipe) recipes.get("NodeRelay")),
                        new ResearchPage("tc.research_page.VISPOWER.4"),
                        new ResearchPage("tc.research_page.VISPOWER.5"))
                .setParents("NODESTABILIZER")
                .setSpecial()
                .registerResearchItem();

        new ResearchItem(
                "FOCALMANIPULATION",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.MAGIC, 8)
                        .add(Aspect.TOOL, 8)
                        .add(Aspect.CRAFT, 5)
                        .add(Aspect.CRYSTAL, 5)
                        .add(Aspect.ENERGY, 5),
                -3,
                -8,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 13))
                .setPages(
                        new ResearchPage("tc.research_page.FOCALMANIPULATION.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("FocalManipulator")),
                        new ResearchPage("tc.research_page.FOCALMANIPULATION.2"))
                .setParentsHidden("INFUSION", "FOCUSFIRE")
                .setParents("VISPOWER")
                .registerResearchItem();

        new ResearchItem(
                "VAMPBAT",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.HUNGER, 5)
                        .add(Aspect.LIFE, 5)
                        .add(Aspect.MAGIC, 5),
                4,
                -8,
                1,
                new ResourceLocation("thaumcraft", "textures/foci/vampirebats.png"))
                .setPages(new ResearchPage("focus.upgrade.vampirebats.text"))
                .setSecondary()
                .setParents("FOCUSHELLBAT")
                .setParentsHidden("FOCALMANIPULATION")
                .registerResearchItem();

        new ResearchItem(
                "WANDPED",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 6)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.ENERGY, 3),
                -9,
                -6,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.WANDPED.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandPed")))
                .setConcealed()
                .setParents("INFUSION", "NODEPRESERVE", "NODESTABILIZER")
                .registerResearchItem();

        new ResearchItem(
                "VISAMULET",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.VOID, 3),
                -9,
                -8,
                2,
                new ItemStack(ConfigItems.itemAmuletVis, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.VISAMULET.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("VisAmulet")),
                        new ResearchPage("tc.research_page.VISAMULET.2"))
                .setConcealed()
                .setParents("WANDPED")
                .registerResearchItem();

        new ResearchItem(
                "WANDPEDFOC",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 6)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.EXCHANGE, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.TOOL, 3),
                -10,
                -7,
                3,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 8))
                .setPages(
                        new ResearchPage("tc.research_page.WANDPEDFOC.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("WandPedFocus")))
                .setSecondary()
                .setConcealed()
                .setParents("WANDPED")
                .registerResearchItem();

        new ResearchItem(
                "VISCHARGERELAY",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.ENERGY, 6),
                -7,
                -6,
                2,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.VISCHARGERELAY.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("NodeChargeRelay")))
                .setParents("VISPOWER", "WANDPED")
                .setParentsHidden("ROD_greatwood")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();
    }

    private static void initBasicResearchTextOnlyExtended() {
        new ResearchItem(
                "ENCHANT",
                "BASICS",
                new AspectList(),
                -4,
                -2,
                0,
                new ResourceLocation("thaumcraft", "textures/misc/r_enchant.png"))
                .setPages(
                        new ResearchPage("tc.research_page.ENCHANT.1"),
                        new ResearchPage("tc.research_page.ENCHANT.2"))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "NODETAPPER1",
                "BASICS",
                new AspectList()
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.EXCHANGE, 3),
                -4,
                1,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_nodetap1.png"))
                .setPages(new ResearchPage("tc.research_page.NODETAPPER1.1"))
                .setParents("NODES")
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "NODEPRESERVE",
                "BASICS",
                new AspectList()
                        .add(Aspect.AURA, 3)
                        .add(Aspect.GREED, 3)
                        .add(Aspect.SENSES, 3),
                -6,
                2,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_nodepreserve.png"))
                .setPages(new ResearchPage("tc.research_page.NODEPRESERVE"))
                .setParents("NODETAPPER1")
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "NODETAPPER2",
                "BASICS",
                new AspectList()
                        .add(Aspect.AURA, 6)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.EXCHANGE, 3),
                -3,
                3,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_nodetap2.png"))
                .setPages(new ResearchPage("tc.research_page.NODETAPPER2.1"))
                .setParents("NODETAPPER1")
                .setSpecial()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "CRIMSON",
                "BASICS",
                new AspectList(),
                0,
                4,
                0,
                new ItemStack(ConfigItems.itemEldritchObject, 1, 1))
                .setPages(new ResearchPage("tc.research_page.CRIMSON.1"))
                .setStub()
                .setHidden()
                .setRound()
                .setSpecial()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("CRIMSON", 3);
    }

    private static void initThaumaturgyResearchTextOnlyBaseline() {
        new ResearchItem("CAP_iron", "THAUMATURGY")
                .setAutoUnlock()
                .registerResearchItem();
        new ResearchItem("ROD_wood", "THAUMATURGY")
                .setAutoUnlock()
                .registerResearchItem();
    }

    private static void initEldritchResearchBaseline() {
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
                        new ResearchPage((CrucibleRecipe) recipes.get("VoidSeed")))
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
                        new ResearchPage((InfusionRecipe) recipes.get("EldritchEye")),
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
                        new ResearchPage((CrucibleRecipe) recipes.get("VoidMetal")),
                        new ResearchPage("tc.research_page.VOIDMETAL.2"))
                .setParents("THAUMIUM", "ELDRITCHMINOR")
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
                        new ResearchPage((IArcaneRecipe) recipes.get("WandCapVoidInert")),
                        new ResearchPage((InfusionRecipe) recipes.get("WandCapVoid")))
                .setConcealed()
                .setParents("CAP_thaumium", "VOIDMETAL")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("CAP_void", 1);

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
                        new ResearchPage((IArcaneRecipe) recipes.get("FocusPrimal")))
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
                        new ResearchPage((InfusionRecipe) recipes.get("SanityCheck")))
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
                        new ResearchPage((InfusionRecipe) recipes.get("WandRodPrimalStaff")))
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

    private static void initEldritchResearchTextOnlyBaseline() {
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
