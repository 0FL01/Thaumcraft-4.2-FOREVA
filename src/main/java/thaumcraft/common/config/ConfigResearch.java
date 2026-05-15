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
    }

    private static void initArtificeResearchBaseline() {
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
