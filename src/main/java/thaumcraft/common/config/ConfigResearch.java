package thaumcraft.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ConfigResearch {
    public static final Map<String, Object> recipes = new HashMap<>();

    public static void init() {
        initCategories();
        initBasicResearchBaseline();
        initBasicResearchProgressionBaseline();
        initAlchemyResearchBaseline();
        initAlchemyResearchTextOnlyBaseline();
        initArtificeResearchBaseline();
        initGolemancyResearchBaseline();
        initGolemancyResearchTextOnlyBaseline();
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

        ArrayList<IRecipe> clusterRecipes = new ArrayList<>();
        for (int a = 0; a <= 6; ++a) {
            clusterRecipes.add((IRecipe) recipes.get("Clusters" + a));
        }
        new ResearchItem(
                "ORE",
                "BASICS",
                new AspectList(),
                -2,
                -2,
                0,
                new ItemStack(ConfigBlocks.blockCustomOre, 1, Short.MAX_VALUE))
                .setPages(
                        new ResearchPage("tc.research_page.ORE.1"),
                        new ResearchPage("tc.research_page.ORE.2"),
                        new ResearchPage(clusterRecipes.toArray(new IRecipe[0])),
                        new ResearchPage("tc.research_page.ORE.3"),
                        new ResearchPage("tc.research_page.ORE.4"))
                .setStub()
                .setRound()
                .setAutoUnlock()
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

        new ResearchItem(
                "TUBES",
                "ALCHEMY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.EXCHANGE, 3),
                7,
                0,
                1,
                new ItemStack(ConfigBlocks.blockTube, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.TUBES.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("Tube")),
                        new ResearchPage("tc.research_page.TUBES.2"),
                        new ResearchPage((IArcaneRecipe) recipes.get("TubeValve")),
                        new ResearchPage("tc.research_page.TUBES.3"),
                        new ResearchPage((IArcaneRecipe) recipes.get("Resonator")),
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TubeFilter")),
                        new ResearchPage("tc.research_page.TUBEFILTER.2"),
                        new ResearchPage((IArcaneRecipe) recipes.get("TubeRestrict")),
                        new ResearchPage((IArcaneRecipe) recipes.get("TubeOneway")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("EssentiaCrystalizer")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("Centrifuge")),
                        new ResearchPage("tc.research_page.CENTRIFUGE.2"),
                        new ResearchPage("tc.research_page.CENTRIFUGE.3"),
                        new ResearchPage((IArcaneRecipe) recipes.get("TubeBuffer")))
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
                        new ResearchPage((List<?>) recipes.get("Thaumatorium")),
                        new ResearchPage("tc.research_page.THAUMATORIUM.2"),
                        new ResearchPage("tc.research_page.THAUMATORIUM.3"),
                        new ResearchPage((IArcaneRecipe) recipes.get("MnemonicMatrix")))
                .setParents("CENTRIFUGE")
                .setConcealed()
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
                "INFERNALFURNACE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FIRE, 6)
                        .add(Aspect.METAL, 3)
                        .add(Aspect.CRAFT, 3)
                        .add(Aspect.AURA, 3),
                -4,
                -1,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_infernalfurnace.png"))
                .setPages(
                        new ResearchPage("tc.research_page.INFERNALFURNACE.1"),
                        new ResearchPage((List<?>) recipes.get("InfernalFurnace")),
                        new ResearchPage("tc.research_page.INFERNALFURNACE.2"))
                .setParents("NITOR", "ALUMENTUM")
                .setConcealed()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("INFERNALFURNACE", 2);

        new ResearchItem(
                "BELLOWS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.AIR, 6)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.MOTION, 3),
                -6,
                -2,
                1,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.BELLOWS.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("Bellows")),
                        new ResearchPage("tc.research_page.BELLOWS.2"))
                .setParents("INFERNALFURNACE")
                .setSecondary()
                .setConcealed()
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
                "RUNICARMOR",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.ARMOR, 6)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.MIND, 3),
                3,
                4,
                3,
                new ItemStack(ConfigItems.itemRingRunic, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICARMOR.1"),
                        new ResearchPage("tc.research_page.RUNICARMOR.2"),
                        new ResearchPage((InfusionRecipe) recipes.get("RunicRing")),
                        new ResearchPage((InfusionRecipe) recipes.get("RunicAmulet")),
                        new ResearchPage((InfusionRecipe) recipes.get("RunicGirdle")))
                .setParentsHidden("INFUSION")
                .setParents("ENCHFABRIC")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICCHARGED",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.ENERGY, 6),
                2,
                3,
                2,
                new ItemStack(ConfigItems.itemRingRunic, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICCHARGED.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("RunicRingCharged")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICHEALING",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.HEAL, 4)
                        .add(Aspect.WATER, 4),
                4,
                3,
                2,
                new ItemStack(ConfigItems.itemRingRunic, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICHEALING.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("RunicRingHealing")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICKINETIC",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.AIR, 6),
                2,
                5,
                2,
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICKINETIC.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("RunicGirdleKinetic")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICEMERGENCY",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.EARTH, 4)
                        .add(Aspect.VOID, 4),
                4,
                5,
                2,
                new ItemStack(ConfigItems.itemAmuletRunic, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICEMERGENCY.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("RunicAmuletEmergency")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        ArrayList<InfusionRunicAugmentRecipe> runicAugmentRecipes = new ArrayList<>();
        for (int a = 0; a <= 4; ++a) {
            ItemStack runicChestplate = new ItemStack(ConfigItems.itemChestRobe);
            if (a > 0) {
                runicChestplate.setTagInfo("RS.HARDEN", new NBTTagByte((byte) a));
            }
            runicAugmentRecipes.add(new InfusionRunicAugmentRecipe(runicChestplate));
        }
        new ResearchItem(
                "RUNICAUGMENTATION",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.EXCHANGE, 4)
                        .add(Aspect.GREED, 4),
                6,
                4,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_runicupg.png"))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICAUGMENTATION.1"),
                        new ResearchPage(runicAugmentRecipes.toArray(new InfusionRecipe[0])),
                        new ResearchPage("tc.research_page.RUNICAUGMENTATION.2"))
                .setParents("RUNICARMOR")
                .setConcealed()
                .registerResearchItem();

        ArrayList<IArcaneRecipe> bannerRecipes = new ArrayList<>();
        for (int a = 0; a < 16; ++a) {
            bannerRecipes.add((IArcaneRecipe) recipes.get("Banner_" + a));
        }
        ItemStack bannerAnchor = new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 8);
        bannerAnchor.setTagCompound(new NBTTagCompound());
        bannerAnchor.getTagCompound().setByte("color", (byte) 10);
        new ResearchItem(
                "BANNERS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.CLOTH, 3)
                        .add(Aspect.MAGIC, 1),
                4,
                8,
                1,
                bannerAnchor)
                .setPages(
                        new ResearchPage("tc.research_page.BANNERS.1"),
                        new ResearchPage(bannerRecipes.toArray(new IArcaneRecipe[0])))
                .setHidden()
                .setItemTriggers(new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 8))
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

        if (Config.wardedStone) {
            new ResearchItem(
                    "WARDEDARCANA",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TOOL, 6)
                            .add(Aspect.MIND, 3)
                            .add(Aspect.MECHANISM, 3)
                            .add(Aspect.ARMOR, 3),
                    -5,
                    -4,
                    2,
                    new ItemStack(ConfigItems.itemArcaneDoor))
                    .setPages(
                            new ResearchPage("tc.research_page.WARDEDARCANA.1"),
                            new ResearchPage((IArcaneRecipe) recipes.get("ArcaneDoor")),
                            new ResearchPage("tc.research_page.WARDEDARCANA.2"),
                            new ResearchPage((IArcaneRecipe) recipes.get("IronKey")),
                            new ResearchPage((IArcaneRecipe) recipes.get("GoldKey")),
                            new ResearchPage("tc.research_page.WARDEDARCANA.3"),
                            new ResearchPage((IArcaneRecipe) recipes.get("ArcanePressurePlate")),
                            new ResearchPage("tc.research_page.WARDEDARCANA.4"),
                            new ResearchPage((IArcaneRecipe) recipes.get("WardedGlass")))
                    .setParents("THAUMIUM")
                    .registerResearchItem();
        }

        new ResearchItem(
                "BONEBOW",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.MOTION, 3),
                -7,
                1,
                1,
                new ItemStack(ConfigItems.itemBowBone))
                .setPages(
                        new ResearchPage("tc.research_page.BONEBOW.1"),
                        new ResearchPage((IArcaneRecipe) recipes.get("BoneBow")))
                .setHidden()
                .setItemTriggers(
                        new ItemStack(Items.BOW, 1, Short.MAX_VALUE),
                        new ItemStack(Items.BONE))
                .registerResearchItem();

        ArrayList<IArcaneRecipe> primalArrowRecipes = new ArrayList<>();
        for (int a = 0; a < 6; ++a) {
            primalArrowRecipes.add((IArcaneRecipe) recipes.get("PrimalArrow_" + a));
        }
        new ResearchItem(
                "PRIMALARROW",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.ORDER, 3)
                        .add(Aspect.ENTROPY, 3),
                -9,
                0,
                2,
                new ItemStack(ConfigItems.itemPrimalArrow, 1, Short.MAX_VALUE))
                .setPages(
                        new ResearchPage("tc.research_page.PRIMALARROW.1"),
                        new ResearchPage(primalArrowRecipes.toArray(new IArcaneRecipe[0])),
                        new ResearchPage("tc.research_page.PRIMALARROW.2"),
                        new ResearchPage("tc.research_page.PRIMALARROW.3"))
                .setConcealed()
                .setParents("BONEBOW")
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

        new ResearchItem(
                "LAMPGROWTH",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.LIGHT, 3)
                        .add(Aspect.PLANT, 6)
                        .add(Aspect.LIFE, 3)
                        .add(Aspect.CROP, 3),
                -4,
                3,
                2,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 8))
                .setPages(
                        new ResearchPage("tc.research_page.LAMPGROWTH.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("LampGrowth")))
                .setHidden()
                .setAspectTriggers(Aspect.LIGHT, Aspect.CROP)
                .setParents("ARCANELAMP", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "LAMPFERTILITY",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.BEAST, 6)
                        .add(Aspect.LIFE, 6)
                        .add(Aspect.LIGHT, 3),
                -2,
                3,
                2,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 13))
                .setPages(
                        new ResearchPage("tc.research_page.LAMPFERTILITY.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("LampFertility")))
                .setHidden()
                .setAspectTriggers(Aspect.LIGHT, Aspect.LIFE)
                .setParents("ARCANELAMP", "INFUSION")
                .registerResearchItem();

        if (Config.allowMirrors) {
            new ResearchItem(
                    "MIRROR",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TRAVEL, 6)
                            .add(Aspect.ELDRITCH, 3)
                            .add(Aspect.DARKNESS, 3)
                            .add(Aspect.CRYSTAL, 3),
                    -1,
                    8,
                    2,
                    new ItemStack(ConfigBlocks.blockMirror, 1, 0))
                    .setPages(
                            new ResearchPage("tc.research_page.MIRROR.1"),
                            new ResearchPage("tc.research_page.MIRROR.2"),
                            new ResearchPage((InfusionRecipe) recipes.get("Mirror")),
                            new ResearchPage("tc.research_page.MIRROR.3"))
                    .setHidden()
                    .setEntityTriggers("Enderman")
                    .setItemTriggers(
                            new ItemStack(Items.ENDER_PEARL),
                            new ItemStack(Blocks.STAINED_GLASS, 1, Short.MAX_VALUE),
                            new ItemStack(Blocks.STAINED_GLASS_PANE, 1, Short.MAX_VALUE),
                            new ItemStack(Blocks.OBSIDIAN, 1, Short.MAX_VALUE))
                    .setParents("INFUSION")
                    .registerResearchItem();

            new ResearchItem(
                    "MIRRORHAND",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TOOL, 6)
                            .add(Aspect.ELDRITCH, 3)
                            .add(Aspect.CRYSTAL, 3)
                            .add(Aspect.TRAVEL, 3),
                    1,
                    9,
                    2,
                    new ItemStack(ConfigItems.itemHandMirror))
                    .setPages(
                            new ResearchPage("tc.research_page.MIRRORHAND.1"),
                            new ResearchPage((InfusionRecipe) recipes.get("MirrorHand")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("MIRROR")
                    .registerResearchItem();

            new ResearchItem(
                    "MIRRORESSENTIA",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TRAVEL, 6)
                            .add(Aspect.ELDRITCH, 3)
                            .add(Aspect.WATER, 3)
                            .add(Aspect.MAGIC, 3),
                    -1,
                    10,
                    2,
                    new ItemStack(ConfigBlocks.blockMirror, 1, 6))
                    .setPages(
                            new ResearchPage("tc.research_page.MIRRORESSENTIA.1"),
                            new ResearchPage((InfusionRecipe) recipes.get("MirrorEssentia")),
                            new ResearchPage("tc.research_page.MIRRORESSENTIA.2"))
                    .setSecondary()
                    .setConcealed()
                    .setParents("MIRROR")
                    .registerResearchItem();
        }

        new ResearchItem(
                "JARBRAIN",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.HUNGER, 3)
                        .add(Aspect.MIND, 3)
                        .add(Aspect.UNDEAD, 3)
                        .add(Aspect.GREED, 3),
                -5,
                9,
                2,
                new ItemStack(ConfigBlocks.blockJar, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.JARBRAIN.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("JarBrain")))
                .setParents("INFUSION")
                .setHidden()
                .setItemTriggers(new ItemStack(ConfigItems.itemResource, 1, 3))
                .setEntityTriggers("Thaumcraft.BrainyZombie", "Thaumcraft.GiantBrainyZombie")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("JARBRAIN", 3);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigBlocks.blockJar, 1, 1), 1);

        new ResearchItem(
                "INFUSIONENCHANTMENT",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.MIND, 3)
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.TOOL, 3),
                -6,
                11,
                3,
                new ResourceLocation("thaumcraft", "textures/misc/r_enchant.png"))
                .setPages(
                        new ResearchPage("tc.research_page.INFUSIONENCHANTMENT.1"),
                        new ResearchPage("tc.research_page.INFUSIONENCHANTMENT.2"),
                        new ResearchPage("tc.research_page.INFUSIONENCHANTMENT.3"),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnchRepair")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnchHaste")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch0")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch1")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch2")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch3")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch4")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch5")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch6")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch7")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch8")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch9")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch10")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch11")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch12")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch13")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch14")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch15")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch16")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch17")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch18")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch19")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch20")),
                        new ResearchPage((InfusionEnchantmentRecipe) recipes.get("InfEnch21")))
                .setConcealed()
                .setParents("JARBRAIN")
                .registerResearchItem();

        new ResearchItem(
                "ARMORFORTRESS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.METAL, 3)
                        .add(Aspect.ARMOR, 5)
                        .add(Aspect.CRAFT, 5),
                -8,
                9,
                2,
                new ItemStack(ConfigItems.itemHelmFortress))
                .setPages(
                        new ResearchPage("tc.research_page.ARMORFORTRESS.1"),
                        new ResearchPage("tc.research_page.ARMORFORTRESS.2"),
                        new ResearchPage((InfusionRecipe) recipes.get("ThaumiumFortressHelm")),
                        new ResearchPage((InfusionRecipe) recipes.get("ThaumiumFortressChest")),
                        new ResearchPage((InfusionRecipe) recipes.get("ThaumiumFortressLegs")))
                .setParents("THAUMIUM", "INFUSIONENCHANTMENT")
                .setHidden()
                .setAspectTriggers(Aspect.ARMOR)
                .registerResearchItem();

        new ResearchItem(
                "HELMGOGGLES",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 5)
                        .add(Aspect.AURA, 3)
                        .add(Aspect.ARMOR, 3),
                -9,
                7,
                2,
                new ItemStack(ConfigItems.itemGoggles))
                .setPages(
                        new ResearchPage("tc.research_page.HELMGOGGLES.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("HelmGoggles")))
                .setParentsHidden("GOGGLES")
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "MASKGRINNINGDEVIL",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.HEAL, 5)
                        .add(Aspect.MIND, 5)
                        .add(Aspect.ARMOR, 3),
                -10,
                8,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_mask0.png"))
                .setPages(
                        new ResearchPage("tc.research_page.MASKGRINNINGDEVIL.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("MaskGrinningDevil")))
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "MASKANGRYGHOST",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.ENTROPY, 5)
                        .add(Aspect.DEATH, 5)
                        .add(Aspect.ARMOR, 3),
                -10,
                9,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_mask1.png"))
                .setPages(
                        new ResearchPage("tc.research_page.MASKANGRYGHOST.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("MaskAngryGhost")))
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "MASKSIPPINGFIEND",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.UNDEAD, 5)
                        .add(Aspect.LIFE, 5)
                        .add(Aspect.ARMOR, 3),
                -10,
                10,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_mask2.png"))
                .setPages(
                        new ResearchPage("tc.research_page.MASKSIPPINGFIEND.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("MaskSippingFiend")))
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("MASKANGRYGHOST", 1);
        ThaumcraftApi.addWarpToResearch("MASKSIPPINGFIEND", 1);

        new ResearchItem(
                "BOOTSTRAVELLER",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.FLIGHT, 3)
                        .add(Aspect.WATER, 3),
                -1,
                5,
                2,
                new ItemStack(ConfigItems.itemBootsTraveller))
                .setPages(
                        new ResearchPage("tc.research_page.BOOTSTRAVELLER.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("BootsTraveller")))
                .setParents("ENCHFABRIC", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "HOVERHARNESS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FLIGHT, 6)
                        .add(Aspect.TRAVEL, 6)
                        .add(Aspect.AIR, 6)
                        .add(Aspect.MECHANISM, 3),
                1,
                7,
                3,
                new ItemStack(ConfigItems.itemHoverHarness))
                .setPages(
                        new ResearchPage("tc.research_page.HOVERHARNESS.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("HoverHarness")),
                        new ResearchPage("tc.research_page.HOVERHARNESS.2"))
                .setParents("BOOTSTRAVELLER")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "HOVERGIRDLE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FLIGHT, 6)
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.MOTION, 6),
                2,
                7,
                3,
                new ItemStack(ConfigItems.itemGirdleHover))
                .setPages(
                        new ResearchPage("tc.research_page.HOVERGIRDLE.1"),
                        new ResearchPage((InfusionRecipe) recipes.get("HoverGirdle")))
                .setHidden()
                .setAspectTriggers(Aspect.FLIGHT)
                .setParents("HOVERHARNESS")
                .setSecondary()
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

        ArrayList<ShapedArcaneRecipe> sceptreRecipes = new ArrayList<>();

        WandCap wandCapIron = WandCap.caps.get("iron");
        WandCap wandCapGold = WandCap.caps.get("gold");
        WandCap wandCapThaumium = WandCap.caps.get("thaumium");
        WandRod wandRodWood = WandRod.rods.get("wood");
        WandRod wandRodGreatwood = WandRod.rods.get("greatwood");
        WandRod wandRodSilverwood = WandRod.rods.get("silverwood");

        int sceptreCost1 = (int) ((float) (wandCapIron.getCraftCost() * wandRodWood.getCraftCost()) * 1.5f);
        AspectList sceptreCostVis1 = new AspectList();
        for (Aspect primal : Aspect.getPrimalAspects()) {
            sceptreCostVis1.add(primal, sceptreCost1);
        }
        ItemStack sceptre1 = new ItemStack(ConfigItems.itemWandCasting, 1, sceptreCost1);
        ItemWandCasting.setCap(sceptre1, wandCapIron);
        ItemWandCasting.setRod(sceptre1, wandRodWood);
        sceptre1.setTagInfo("sceptre", new NBTTagByte((byte) 1));
        sceptreRecipes.add(new ShapedArcaneRecipe(
                "SCEPTRE",
                sceptre1,
                sceptreCostVis1,
                " TF",
                " RT",
                "T  ",
                'T', wandCapIron.getItem(),
                'R', wandRodWood.getItem(),
                'F', new ItemStack(ConfigItems.itemResource, 1, 15)));

        int sceptreCost2 = (int) ((float) (wandCapGold.getCraftCost() * wandRodGreatwood.getCraftCost()) * 1.5f);
        AspectList sceptreCostVis2 = new AspectList();
        for (Aspect primal : Aspect.getPrimalAspects()) {
            sceptreCostVis2.add(primal, sceptreCost2);
        }
        ItemStack sceptre2 = new ItemStack(ConfigItems.itemWandCasting, 1, sceptreCost2);
        ItemWandCasting.setCap(sceptre2, wandCapGold);
        ItemWandCasting.setRod(sceptre2, wandRodGreatwood);
        sceptre2.setTagInfo("sceptre", new NBTTagByte((byte) 1));
        sceptreRecipes.add(new ShapedArcaneRecipe(
                "SCEPTRE",
                sceptre2,
                sceptreCostVis2,
                " TF",
                " RT",
                "T  ",
                'T', wandCapGold.getItem(),
                'R', wandRodGreatwood.getItem(),
                'F', new ItemStack(ConfigItems.itemResource, 1, 15)));

        int sceptreCost3 = (int) ((float) (wandCapThaumium.getCraftCost() * wandRodSilverwood.getCraftCost()) * 1.5f);
        AspectList sceptreCostVis3 = new AspectList();
        for (Aspect primal : Aspect.getPrimalAspects()) {
            sceptreCostVis3.add(primal, sceptreCost3);
        }
        ItemStack sceptre3 = new ItemStack(ConfigItems.itemWandCasting, 1, sceptreCost3);
        ItemWandCasting.setCap(sceptre3, wandCapThaumium);
        ItemWandCasting.setRod(sceptre3, wandRodSilverwood);
        sceptre3.setTagInfo("sceptre", new NBTTagByte((byte) 1));
        sceptreRecipes.add(new ShapedArcaneRecipe(
                "SCEPTRE",
                sceptre3,
                sceptreCostVis3,
                " TF",
                " RT",
                "T  ",
                'T', wandCapThaumium.getItem(),
                'R', wandRodSilverwood.getItem(),
                'F', new ItemStack(ConfigItems.itemResource, 1, 15)));

        new ResearchItem(
                "SCEPTRE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 6)
                        .add(Aspect.CRAFT, 6)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 9),
                0,
                4,
                3,
                sceptre3)
                .setPages(
                        new ResearchPage("tc.research_page.SCEPTRE.1"),
                        new ResearchPage(sceptreRecipes.toArray(new IArcaneRecipe[0])))
                .setConcealed()
                .setParents("ROD_silverwood")
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

    private static void initGolemancyResearchBaseline() {
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
                        new ResearchPage((IArcaneRecipe) recipes.get("HungryChest")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyHat")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyGlasses")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyBowtie")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyFez")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyDart")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyVisor")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyArmor")))
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
                        new ResearchPage((IArcaneRecipe) recipes.get("TinyHammer")))
                .setHidden()
                .setSecondary()
                .setAspectTriggers(Aspect.WEAPON)
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
                "NODEJAR",
                "BASICS",
                new AspectList()
                        .add(Aspect.AURA, 6)
                        .add(Aspect.GREED, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.MOTION, 3),
                -7,
                4,
                3,
                new ItemStack(ConfigBlocks.blockJar, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.NODEJAR.1"),
                        new ResearchPage("tc.research_page.NODEJAR.2"))
                .setParents("NODEPRESERVE")
                .setConcealed()
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

    private static void initGolemancyResearchTextOnlyBaseline() {
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
                        new ResearchPage("tc.research_page.GOLEMBELL.2"))
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
                .setPages(new ResearchPage("tc.research_page.GOLEMWOOD.1"))
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
                .setPages(new ResearchPage("tc.research_page.GOLEMCLAY.1"))
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
                .setPages(new ResearchPage("tc.research_page.GOLEMSTONE.1"))
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
                .setPages(new ResearchPage("tc.research_page.GOLEMIRON.1"))
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
                .setPages(new ResearchPage("tc.research_page.GOLEMTHAUMIUM.1"))
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
                .setPages(new ResearchPage("tc.research_page.GOLEMFLESH.1"))
                .setConcealed()
                .setParents("GOLEMWOOD")
                .registerResearchItem();

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
                .setPages(new ResearchPage("tc.research_page.GOLEMTALLOW.1"))
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
                .setPages(new ResearchPage("tc.research_page.GOLEMFETTER.1"))
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
                .setPages(new ResearchPage("tc.research_page.UPGRADEAIR.1"))
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
                .setPages(new ResearchPage("tc.research_page.UPGRADEEARTH.1"))
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
                .setPages(new ResearchPage("tc.research_page.UPGRADEFIRE.1"))
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
                .setPages(new ResearchPage("tc.research_page.UPGRADEWATER.1"))
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
                .setPages(new ResearchPage("tc.research_page.UPGRADEORDER.1"))
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
                .setPages(new ResearchPage("tc.research_page.UPGRADEENTROPY.1"))
                .setConcealed()
                .setSecondary()
                .setParents("GOLEMBELL")
                .registerResearchItem();
    }

    private static void initAlchemyResearchTextOnlyBaseline() {
        new ResearchItem(
                "TALLOW",
                "ALCHEMY",
                new AspectList().add(Aspect.FLESH, 3).add(Aspect.MAGIC, 1),
                -2,
                0,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 4))
                .setPages(new ResearchPage("tc.research_page.TALLOW.1"))
                .setParents("CRUCIBLE")
                .registerResearchItem();
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
                        new ResearchPage((IArcaneRecipe) recipes.get("AdvAlchemyConstruct")),
                        new ResearchPage("tc.research_page.ADVALCHEMYFURNACE.2"),
                        new ResearchPage((List<?>) recipes.get("AdvAlchemyFurnace")))
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
                        new ResearchPage((InfusionRecipe) recipes.get("PrimalCrusher")),
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
                        new ResearchPage((CrucibleRecipe) recipes.get("VoidMetal")),
                        new ResearchPage("tc.research_page.VOIDMETAL.2"))
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
                        new ResearchPage((InfusionRecipe) recipes.get("EssentiaReservoir")),
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
                        new ResearchPage((IArcaneRecipe) recipes.get("WandCapVoidInert")),
                        new ResearchPage((InfusionRecipe) recipes.get("WandCapVoid")))
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
                        new ResearchPage((InfusionRecipe) recipes.get("VoidRobeHelm")),
                        new ResearchPage((InfusionRecipe) recipes.get("VoidRobeChest")),
                        new ResearchPage((InfusionRecipe) recipes.get("VoidRobeLegs")))
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
