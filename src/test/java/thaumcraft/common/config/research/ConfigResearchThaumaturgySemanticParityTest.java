package thaumcraft.common.config.research;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.WandRodPrimalOnUpdate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ConfigResearchThaumaturgySemanticParityTest {
    private static final int WARDING = 1;
    private static final int COPPER = 2;
    private static final int SILVER = 4;

    private static final Entry[] ENTRIES = {
            e("BASICTHAUMATURGY", 0, 0, 0, 1, "i:itemWandCasting:0", "round,stub,auto", "", "", "", "", "", "T=tc.research_page.BASICTHAUMATURGY.1|T=tc.research_page.BASICTHAUMATURGY.2|N=WandCapIron|N=WandBasic", 0),
            e("FOCUSFIRE", 0, 2, -2, 1, "i:focusFire:0", "", "FIRE=3,MAGIC=3", "BASICTHAUMATURGY", "", "", "", "T=tc.research_page.FOCUSFIRE.1|T=tc.research_page.FOCUSFIRE.2|A=FocusFire", 0),
            e("FOCUSFROST", 0, 1, -5, 1, "i:focusFrost:0", "secondary,concealed", "WATER=3,MAGIC=3,COLD=6", "FOCUSFIRE", "", "", "", "T=tc.research_page.FOCUSFROST.1|A=FocusFrost", 0),
            e("FOCUSHELLBAT", 0, 3, -7, 2, "i:focusHellbat:0", "hidden", "TRAVEL=3,BEAST=6,FIRE=3,MAGIC=3", "", "FOCUSFIRE,INFUSION", "Thaumcraft.Firebat", "FIRE", "T=tc.research_page.FOCUSHELLBAT.1|I=FocusHellbat", 2),
            e("FOCUSEXCAVATION", 0, 0, -3, 2, "i:focusExcavation:0", "concealed", "EARTH=3,ENTROPY=3,MAGIC=3", "FOCUSFIRE", "", "", "", "T=tc.research_page.FOCUSEXCAVATION.1|A=FocusExcavation", 0),
            e("FOCUSWARDING", WARDING, -2, -4, 3, "i:focusWarding:0", "concealed", "EARTH=6,ARMOR=3,ORDER=3,MIND=3", "FOCUSEXCAVATION,INFUSION", "", "", "", "T=tc.research_page.FOCUSWARDING.1|I=FocusWarding", 0),
            e("FOCUSSHOCK", 0, 3, -5, 1, "i:focusShock:0", "secondary,concealed", "AIR=3,ENERGY=6,MAGIC=3", "FOCUSFIRE", "", "", "", "T=tc.research_page.FOCUSSHOCK.1|A=FocusShock", 0),
            e("FOCUSTRADE", 0, 4, -3, 2, "i:focusTrade:0", "concealed", "EARTH=3,EXCHANGE=6,MAGIC=3", "FOCUSFIRE", "", "", "", "T=tc.research_page.FOCUSTRADE.1|A=FocusTrade", 0),
            e("FOCUSPORTABLEHOLE", 0, 7, -2, 2, "i:focusPortableHole:0", "concealed", "TRAVEL=3,ENTROPY=3,ELDRITCH=6,AIR=3", "FOCUSTRADE,INFUSION", "", "", "", "T=tc.research_page.FOCUSPORTABLEHOLE.1|I=FocusPortableHole", 0),
            e("FOCUSPOUCH", 0, 4, -1, 1, "i:itemFocusPouch:0", "secondary", "VOID=6,MAGIC=3,TOOL=3", "FOCUSFIRE", "", "", "", "T=tc.research_page.FOCUSPOUCH.1|A=FocusPouch", 0),
            e("CAP_iron", 0, 0, 0, 0, "-", "virtual,auto", "", "", "", "", "", "", 0),
            e("CAP_gold", 0, 3, 2, 1, "i:itemWandCap:1", "", "METAL=3,GREED=3,TOOL=3", "BASICTHAUMATURGY", "", "", "", "T=tc.research_page.CAP_gold.1|A=WandCapGold", 0),
            e("CAP_thaumium", 0, 5, 4, 2, "i:itemWandCap:2", "", "METAL=6,MAGIC=6,TOOL=3,AURA=3", "CAP_gold,THAUMIUM,INFUSION", "", "", "", "T=tc.research_page.CAP_thaumium.1|A=WandCapThaumiumInert|I=WandCapThaumium", 0),
            e("CAP_copper", COPPER, 2, 0, 1, "i:itemWandCap:3", "", "METAL=3,EXCHANGE=3,TOOL=3", "BASICTHAUMATURGY", "", "", "", "T=tc.research_page.CAP_copper.1|A=WandCapCopper", 0),
            e("CAP_silver", SILVER, 5, 1, 1, "i:itemWandCap:4", "concealed", "METAL=3,GREED=3,TOOL=3,AURA=3", "CAP_gold,INFUSION", "", "", "", "T=tc.research_page.CAP_silver.1|A=WandCapSilverInert|I=WandCapSilver", 0),
            e("ROD_wood", 0, 0, 0, 0, "-", "virtual,auto", "", "", "", "", "", "", 0),
            e("ROD_greatwood", 0, -5, 2, 1, "i:itemWandRod:0", "", "TOOL=3,TREE=6,MAGIC=3", "BASICTHAUMATURGY", "", "", "", "T=tc.research_page.ROD_greatwood.1|A=WandRodGreatwood", 0),
            e("ROD_reed", 0, -5, -1, 2, "i:itemWandRod:5", "secondary,concealed", "TOOL=3,AIR=6,PLANT=3,MAGIC=3", "ROD_greatwood,INFUSION", "", "", "", "T=tc.research_page.ROD_reed.1|I=WandRodReed", 0),
            e("ROD_blaze", 0, -7, 0, 2, "i:itemWandRod:6", "secondary,concealed", "TOOL=3,FIRE=6,ENERGY=3,MAGIC=3", "ROD_greatwood,INFUSION", "", "", "", "T=tc.research_page.ROD_blaze.1|I=WandRodBlaze", 0),
            e("ROD_obsidian", 0, -8, 2, 2, "i:itemWandRod:1", "secondary,concealed", "TOOL=3,EARTH=6,FIRE=3,MAGIC=3", "ROD_greatwood,INFUSION", "", "", "", "T=tc.research_page.ROD_obsidian.1|I=WandRodObsidian", 0),
            e("ROD_ice", 0, -7, 4, 2, "i:itemWandRod:3", "secondary,concealed", "TOOL=3,COLD=6,WATER=3,MAGIC=3", "ROD_greatwood,INFUSION", "", "", "", "T=tc.research_page.ROD_ice.1|I=WandRodIce", 0),
            e("ROD_quartz", 0, -5, 5, 2, "i:itemWandRod:4", "secondary,concealed", "TOOL=3,ORDER=6,CRYSTAL=3,MAGIC=3", "ROD_greatwood,INFUSION", "", "", "", "T=tc.research_page.ROD_quartz.1|I=WandRodQuartz", 0),
            e("ROD_bone", 0, -3, 0, 2, "i:itemWandRod:7", "secondary,concealed", "TOOL=3,ENTROPY=6,UNDEAD=3,MAGIC=3", "ROD_greatwood,INFUSION", "", "", "", "T=tc.research_page.ROD_bone.1|I=WandRodBone", 1),
            e("ROD_silverwood", 0, -2, 5, 3, "i:itemWandRod:2", "", "TOOL=6,TREE=6,MAGIC=9", "ROD_greatwood,INFUSION", "", "", "", "T=tc.research_page.ROD_silverwood.1|I=WandRodSilverwood", 0),
            e("SCEPTRE", 0, 0, 4, 3, "s:thaumium:silverwood:81", "concealed", "TOOL=6,CRAFT=6,TREE=6,MAGIC=9", "ROD_silverwood", "", "", "", "T=tc.research_page.SCEPTRE.1|S", 0),
            e("ROD_greatwood_staff", 0, -1, 7, 1, "i:itemWandRod:50", "", "TOOL=3,TREE=6,MAGIC=3", "ROD_silverwood", "", "", "", "T=tc.research_page.ROD_greatwood_staff.1|T=tc.research_page.ROD_greatwood_staff.2|A=WandRodGreatwoodStaff", 0),
            e("ROD_reed_staff", 0, -5, -2, 2, "i:itemWandRod:55", "secondary,concealed", "TOOL=3,AIR=6,PLANT=3,MAGIC=3", "ROD_reed", "ROD_greatwood_staff", "", "", "T=tc.research_page.ROD_reed_staff.1|A=WandRodReedStaff", 0),
            e("ROD_blaze_staff", 0, -8, -1, 2, "i:itemWandRod:56", "secondary,concealed", "TOOL=3,FIRE=6,ENERGY=3,MAGIC=3", "ROD_blaze", "ROD_greatwood_staff", "", "", "T=tc.research_page.ROD_blaze_staff.1|A=WandRodBlazeStaff", 0),
            e("ROD_obsidian_staff", 0, -9, 2, 2, "i:itemWandRod:51", "secondary,concealed", "TOOL=3,EARTH=6,FIRE=3,MAGIC=3", "ROD_obsidian", "ROD_greatwood_staff", "", "", "T=tc.research_page.ROD_obsidian_staff.1|A=WandRodObsidianStaff", 0),
            e("ROD_ice_staff", 0, -8, 5, 2, "i:itemWandRod:53", "secondary,concealed", "TOOL=3,COLD=6,WATER=3,MAGIC=3", "ROD_ice", "ROD_greatwood_staff", "", "", "T=tc.research_page.ROD_ice_staff.1|A=WandRodIceStaff", 0),
            e("ROD_quartz_staff", 0, -4, 6, 2, "i:itemWandRod:54", "secondary,concealed", "TOOL=3,ORDER=6,CRYSTAL=3,MAGIC=3", "ROD_quartz", "ROD_greatwood_staff", "", "", "T=tc.research_page.ROD_quartz_staff.1|A=WandRodQuartzStaff", 0),
            e("ROD_bone_staff", 0, -2, -1, 2, "i:itemWandRod:57", "secondary,concealed", "TOOL=3,ENTROPY=6,UNDEAD=3,MAGIC=3", "ROD_bone", "ROD_greatwood_staff", "", "", "T=tc.research_page.ROD_bone_staff.1|A=WandRodBoneStaff", 1),
            e("ROD_silverwood_staff", 0, -1, 5, 3, "i:itemWandRod:52", "secondary,concealed", "TOOL=6,TREE=6,MAGIC=9", "ROD_silverwood", "ROD_greatwood_staff", "", "", "T=tc.research_page.ROD_silverwood_staff.1|A=WandRodSilverwoodStaff", 0),
            e("WANDPED", 0, -9, -6, 2, "b:blockStoneDevice:5", "concealed", "AURA=6,MAGIC=3,EXCHANGE=3,ENERGY=3", "INFUSION,NODEPRESERVE,NODESTABILIZER", "", "", "", "T=tc.research_page.WANDPED.1|I=WandPed", 0),
            e("VISAMULET", 0, -9, -8, 2, "i:itemAmuletVis:1", "concealed", "AURA=3,MAGIC=6,ENERGY=3,VOID=3", "WANDPED", "", "", "", "T=tc.research_page.VISAMULET.1|I=VisAmulet|T=tc.research_page.VISAMULET.2", 0),
            e("WANDPEDFOC", 0, -10, -7, 3, "b:blockStoneDevice:8", "secondary,concealed", "AURA=6,MAGIC=6,EXCHANGE=6,ENERGY=3,TOOL=3", "WANDPED", "", "", "", "T=tc.research_page.WANDPEDFOC.1|I=WandPedFocus", 0),
            e("NODESTABILIZER", 0, -7, -4, 1, "b:blockStoneDevice:9", "", "AURA=4,ORDER=4,ENERGY=4", "NODEPRESERVE", "", "", "", "T=tc.research_page.NODESTABILIZER.1|A=NodeStabilizer|T=tc.research_page.NODESTABILIZER.2", 0),
            e("NODESTABILIZERADV", 0, -8, -3, 2, "b:blockStoneDevice:9", "secondary,concealed", "AURA=9,MAGIC=6,ORDER=6,ENERGY=6", "NODESTABILIZER", "", "", "", "T=tc.research_page.NODESTABILIZERADV.1|I=NodeStabilizerAdv", 0),
            e("VISPOWER", 0, -5, -6, 2, "b:blockStoneDevice:11", "special", "AURA=3,MECHANISM=3,ENERGY=6", "NODESTABILIZER", "", "", "", "T=tc.research_page.VISPOWER.1|A=NodeTransducer|T=tc.research_page.VISPOWER.2|T=tc.research_page.VISPOWER.3|A=NodeRelay|T=tc.research_page.VISPOWER.4|T=tc.research_page.VISPOWER.5", 0),
            e("VISCHARGERELAY", 0, -7, -6, 2, "b:blockMetalDevice:2", "secondary,concealed", "MAGIC=3,AURA=3,MECHANISM=3,ENERGY=6", "VISPOWER,WANDPED", "ROD_greatwood", "", "", "T=tc.research_page.VISCHARGERELAY.1|A=NodeChargeRelay", 0),
            e("FOCALMANIPULATION", 0, -3, -8, 2, "b:blockStoneDevice:13", "", "MAGIC=8,TOOL=8,CRAFT=5,CRYSTAL=5,ENERGY=5", "VISPOWER", "INFUSION,FOCUSFIRE", "", "", "T=tc.research_page.FOCALMANIPULATION.1|A=FocalManipulator|T=tc.research_page.FOCALMANIPULATION.2", 0),
            e("VAMPBAT", 0, 4, -8, 1, "r:thaumcraft:textures/foci/vampirebats.png", "secondary", "HUNGER=5,LIFE=5,MAGIC=5", "FOCUSHELLBAT", "FOCALMANIPULATION", "", "", "T=focus.upgrade.vampirebats.text", 0)
    };

    private static final CapTuple[] CAPS = {
            cap("iron", 1.1F, 1, 0, "", 0.0F),
            cap("gold", 1.0F, 3, 1, "", 0.0F),
            cap("thaumium", 0.9F, 6, 2, "", 0.0F),
            cap("void", 0.8F, 9, 7, "", 0.0F),
            cap("copper", 1.1F, 2, 3, "ORDER,ENTROPY", 1.0F),
            cap("silver", 1.0F, 4, 4, "AIR,EARTH,FIRE,WATER", 0.95F)
    };

    private static final RodTuple[] RODS = {
            rod("wood", false, 25, 1, "stick", 0, "", false, false),
            rod("greatwood", false, 50, 3, "wand", 0, "", false, false),
            rod("obsidian", false, 75, 6, "wand", 1, "EARTH", false, false),
            rod("blaze", false, 75, 6, "wand", 6, "FIRE", true, false),
            rod("ice", false, 75, 6, "wand", 3, "WATER", false, false),
            rod("quartz", false, 75, 6, "wand", 4, "ORDER", false, false),
            rod("bone", false, 75, 6, "wand", 7, "ENTROPY", false, false),
            rod("reed", false, 75, 6, "wand", 5, "AIR", false, false),
            rod("silverwood", false, 100, 9, "wand", 2, "", false, false),
            rod("greatwood_staff", true, 125, 8, "wand", 50, "", false, false),
            rod("obsidian_staff", true, 175, 14, "wand", 51, "EARTH", false, false),
            rod("blaze_staff", true, 175, 14, "wand", 56, "FIRE", true, false),
            rod("ice_staff", true, 175, 14, "wand", 53, "WATER", false, false),
            rod("quartz_staff", true, 175, 14, "wand", 54, "ORDER", false, false),
            rod("bone_staff", true, 175, 14, "wand", 57, "ENTROPY", false, false),
            rod("reed_staff", true, 175, 14, "wand", 55, "AIR", false, false),
            rod("silverwood_staff", true, 250, 24, "wand", 52, "", false, false),
            rod("primal_staff", true, 250, 32, "wand", 100, "PRIMALS", false, true)
    };

    private LinkedHashMap<String, ResearchCategoryList> oldCategories;
    private Map<String, Object> oldRecipes;
    private LinkedHashMap<String, WandCap> oldCaps;
    private LinkedHashMap<String, WandRod> oldRods;
    private Map<Object, Integer> oldWarp;
    private Map<Block, Item> oldBlockItems;
    private boolean oldWardedStone;
    private boolean oldCopper;
    private boolean oldSilver;

    @BeforeClass
    public static void initializeDependencies() {
        Bootstrap.register();
        if (ConfigBlocks.blockStoneDevice == null) {
            ConfigBlocks.init();
        }
        if (ConfigItems.itemWandCasting == null) {
            ConfigItems.init();
        }
    }

    @Before
    public void saveGlobalState() throws Exception {
        this.oldCategories = new LinkedHashMap<>(ResearchCategories.researchCategories);
        this.oldRecipes = new HashMap<>(ConfigResearch.recipes);
        this.oldCaps = new LinkedHashMap<>(WandCap.caps);
        this.oldRods = new LinkedHashMap<>(WandRod.rods);
        this.oldWarp = new HashMap<>(warpMap());
        this.oldBlockItems = new HashMap<>(blockItems());
        this.oldWardedStone = Config.wardedStone;
        this.oldCopper = Config.foundCopperIngot;
        this.oldSilver = Config.foundSilverIngot;
        ResearchCategories.researchCategories.clear();
        ConfigResearch.recipes.clear();
        WandCap.caps.clear();
        WandRod.rods.clear();
        warpMap().clear();
        blockItems().put(ConfigBlocks.blockStoneDevice, new ItemBlock(ConfigBlocks.blockStoneDevice));
        blockItems().put(ConfigBlocks.blockMetalDevice, new ItemBlock(ConfigBlocks.blockMetalDevice));
    }

    @After
    public void restoreGlobalState() throws Exception {
        ResearchCategories.researchCategories.clear();
        ResearchCategories.researchCategories.putAll(this.oldCategories);
        ConfigResearch.recipes.clear();
        ConfigResearch.recipes.putAll(this.oldRecipes);
        WandCap.caps.clear();
        WandCap.caps.putAll(this.oldCaps);
        WandRod.rods.clear();
        WandRod.rods.putAll(this.oldRods);
        warpMap().clear();
        warpMap().putAll(this.oldWarp);
        blockItems().clear();
        blockItems().putAll(this.oldBlockItems);
        Config.wardedStone = this.oldWardedStone;
        Config.foundCopperIngot = this.oldCopper;
        Config.foundSilverIngot = this.oldSilver;
    }

    @Test
    public void thaumaturgyResearchMatchesTc4235ForAllOptionalFlagCombinations() throws Exception {
        invokeThaumcraft("initWandComponents");
        IdentityHashMap<Object, String> handleNames = installNamedHandles();

        for (int enabled = 0; enabled < 8; ++enabled) {
            ResearchCategories.researchCategories.clear();
            warpMap().clear();
            invokeConfigResearchCategories();
            Config.wardedStone = (enabled & WARDING) != 0;
            Config.foundCopperIngot = (enabled & COPPER) != 0;
            Config.foundSilverIngot = (enabled & SILVER) != 0;

            ConfigResearchThaumaturgy.initThaumaturgyResearchBaseline();
            ConfigResearchThaumaturgy.initThaumaturgyResearchTextOnlyBaseline();

            ResearchCategoryList category = ResearchCategories.getResearchList("THAUMATURGY");
            Set<String> expectedKeys = enabledKeys(enabled);
            assertEquals("optional mask " + enabled, 39 + Integer.bitCount(enabled), category.research.size());
            assertEquals("optional mask " + enabled, expectedKeys, category.research.keySet());

            if (enabled == 7) {
                assertEquals(new ResourceLocation("thaumcraft", "textures/misc/r_thaumaturgy.png"), category.icon);
                assertEquals(new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"), category.background);
                assertFullResearchSemantics(category, handleNames);
            }
        }
    }

    @Test
    public void builtInWandComponentTuplesMatchTc4235AndPreserveAddonRegistrations() throws Exception {
        WandCap addonCap = new WandCap("addon_cap", 0.5F, new ItemStack(Items.DIAMOND), 99);
        WandRod addonRod = new WandRod("addon_rod", 999, new ItemStack(Items.DIAMOND), 99);

        invokeThaumcraft("initWandComponents");

        assertSame(addonCap, WandCap.caps.get("addon_cap"));
        assertSame(addonRod, WandRod.rods.get("addon_rod"));
        assertEquals(5, WandCap.caps.size());
        assertEquals(19, WandRod.rods.size());
        for (int i = 0; i < 4; ++i) {
            assertCap(CAPS[i], WandCap.caps.get(CAPS[i].tag));
        }
        for (RodTuple expected : RODS) {
            assertRod(expected, WandRod.rods.get(expected.tag));
        }

        LinkedHashMap<String, WandCap> requiredCaps = new LinkedHashMap<>(WandCap.caps);
        for (int enabled = 0; enabled < 4; ++enabled) {
            WandCap.caps.clear();
            WandCap.caps.putAll(requiredCaps);
            Config.foundCopperIngot = (enabled & 1) != 0;
            Config.foundSilverIngot = (enabled & 2) != 0;
            invokeThaumcraft("initOptionalWandComponents");

            assertEquals((enabled & 1) != 0, WandCap.caps.containsKey("copper"));
            assertEquals((enabled & 2) != 0, WandCap.caps.containsKey("silver"));
            assertSame(addonCap, WandCap.caps.get("addon_cap"));
            if ((enabled & 1) != 0) {
                assertCap(CAPS[4], WandCap.caps.get("copper"));
            }
            if ((enabled & 2) != 0) {
                assertCap(CAPS[5], WandCap.caps.get("silver"));
            }
        }

        WandCap.caps.clear();
        WandCap.caps.putAll(requiredCaps);
        WandCap addonCopper = new WandCap("copper", 0.25F, new ItemStack(Items.EMERALD), 77);
        WandCap addonSilver = new WandCap("silver", 0.25F, new ItemStack(Items.EMERALD), 77);
        Config.foundCopperIngot = true;
        Config.foundSilverIngot = true;
        invokeThaumcraft("initOptionalWandComponents");
        assertSame(addonCopper, WandCap.caps.get("copper"));
        assertSame(addonSilver, WandCap.caps.get("silver"));
        assertSame(addonCap, WandCap.caps.get("addon_cap"));
    }

    private static void assertFullResearchSemantics(
            ResearchCategoryList category, IdentityHashMap<Object, String> handleNames) throws Exception {
        List<String> expectedHandleOrder = new ArrayList<>();
        List<String> actualHandleOrder = new ArrayList<>();
        for (Entry expected : ENTRIES) {
            ResearchItem actual = category.research.get(expected.key);
            assertResearch(expected, actual, handleNames, expectedHandleOrder, actualHandleOrder);
        }
        assertEquals(42, expectedHandleOrder.size());
        assertEquals(42, new LinkedHashSet<>(expectedHandleOrder).size());
        assertEquals(expectedHandleOrder, actualHandleOrder);
        assertEquals(1, ThaumcraftApi.getWarp(new ItemStack(ConfigItems.focusHellbat)));
        assertEquals(4, warpMap().size());
    }

    private static void assertResearch(Entry expected, ResearchItem actual,
                                       IdentityHashMap<Object, String> handleNames,
                                       List<String> expectedHandleOrder,
                                       List<String> actualHandleOrder) throws Exception {
        assertEquals(expected.key, actual.key);
        assertEquals(expected.key, "THAUMATURGY", actual.category);
        assertEquals(expected.key, expected.column, actual.displayColumn);
        assertEquals(expected.key, expected.row, actual.displayRow);
        assertEquals(expected.key, expected.complexity, actual.getComplexity());
        assertEquals(expected.key, expected.flags, flags(actual));
        assertAspects(expected.key, expected.aspects, actual.tags);
        assertArrayEquals(expected.key, values(expected.parents), actual.parents);
        assertArrayEquals(expected.key, values(expected.hiddenParents), actual.parentsHidden);
        assertNull(expected.key, actual.getItemTriggers());
        assertArrayEquals(expected.key, values(expected.entityTriggers), actual.getEntityTriggers());
        assertAspectArray(expected.key, expected.aspectTriggers, actual.getAspectTriggers());
        assertIcon(expected, actual);
        assertPages(expected, actual.getPages(), handleNames, expectedHandleOrder, actualHandleOrder);
        assertEquals(expected.key, expected.warp, ThaumcraftApi.getWarp(expected.key));
    }

    private static void assertPages(Entry expected, ResearchPage[] actual,
                                    IdentityHashMap<Object, String> handleNames,
                                    List<String> expectedHandleOrder,
                                    List<String> actualHandleOrder) {
        String[] pages = pages(expected.pages);
        if (pages == null) {
            assertNull(expected.key, actual);
            return;
        }
        assertEquals(expected.key, pages.length, actual.length);
        for (int i = 0; i < pages.length; ++i) {
            String descriptor = pages[i];
            ResearchPage page = actual[i];
            if (descriptor.startsWith("T=")) {
                assertEquals(expected.key + " page " + i, ResearchPage.PageType.TEXT, page.type);
                assertEquals(expected.key + " page " + i, descriptor.substring(2), page.text);
                assertNull(expected.key + " page " + i, page.recipe);
            } else if (descriptor.equals("S")) {
                assertEquals(ResearchPage.PageType.ARCANE_CRAFTING, page.type);
                assertSceptrePreviews((IArcaneRecipe[]) page.recipe);
            } else {
                char family = descriptor.charAt(0);
                String name = descriptor.substring(2);
                assertEquals(expected.key + " page " + i, pageType(family), page.type);
                expectedHandleOrder.add(name);
                actualHandleOrder.add(handleNames.get(page.recipe));
                assertEquals(expected.key + " page " + i, name, handleNames.get(page.recipe));
            }
        }
    }

    private static void assertSceptrePreviews(IArcaneRecipe[] previews) {
        String[] caps = {"iron", "gold", "thaumium"};
        String[] rods = {"wood", "greatwood", "silverwood"};
        int[] costs = {1, 13, 81};
        assertEquals(3, previews.length);
        for (int i = 0; i < previews.length; ++i) {
            ShapedArcaneRecipe recipe = (ShapedArcaneRecipe) previews[i];
            assertEquals("SCEPTRE", recipe.getResearch());
            assertEquals(3, recipe.width);
            assertEquals(3, recipe.height);
            assertSceptreStack(recipe.getRecipeOutput(), caps[i], rods[i], costs[i]);
            assertAspectArray("sceptre cost " + i, "AIR,EARTH,FIRE,WATER,ORDER,ENTROPY",
                    recipe.getAspects().getAspects());
            for (Aspect primal : recipe.getAspects().getAspects()) {
                assertEquals(costs[i], recipe.getAspects().getAmount(primal));
            }

            Object[] input = recipe.getInput();
            assertEquals(9, input.length);
            assertNull(input[0]);
            assertStack((ItemStack) input[1], ConfigItems.itemWandCap, i, 1);
            assertStack((ItemStack) input[2], ConfigItems.itemResource, 15, 1);
            assertNull(input[3]);
            Item expectedRod = i == 0 ? Items.STICK : ConfigItems.itemWandRod;
            int expectedRodMeta = i == 2 ? 2 : 0;
            assertStack((ItemStack) input[4], expectedRod, expectedRodMeta, 1);
            assertStack((ItemStack) input[5], ConfigItems.itemWandCap, i, 1);
            assertStack((ItemStack) input[6], ConfigItems.itemWandCap, i, 1);
            assertNull(input[7]);
            assertNull(input[8]);
        }
    }

    private static void assertSceptreStack(ItemStack stack, String cap, String rod, int meta) {
        assertStack(stack, ConfigItems.itemWandCasting, meta, 1);
        assertEquals(new HashSet<>(Arrays.asList("cap", "rod", "sceptre")), stack.getTagCompound().getKeySet());
        assertEquals(cap, stack.getTagCompound().getString("cap"));
        assertEquals(rod, stack.getTagCompound().getString("rod"));
        assertEquals(1, stack.getTagCompound().getByte("sceptre"));
    }

    private static void assertIcon(Entry expected, ResearchItem actual) throws Exception {
        if (expected.icon.equals("-")) {
            assertNull(expected.key, actual.icon_item);
            assertNull(expected.key, actual.icon_resource);
            return;
        }
        if (expected.icon.startsWith("r:")) {
            assertNull(expected.key, actual.icon_item);
            assertEquals(expected.key, new ResourceLocation(expected.icon.substring(2)), actual.icon_resource);
            return;
        }
        assertNull(expected.key, actual.icon_resource);
        String[] parts = expected.icon.split(":");
        if (parts[0].equals("s")) {
            assertSceptreStack(actual.icon_item, parts[1], parts[2], Integer.parseInt(parts[3]));
            return;
        }
        Class<?> holder = parts[0].equals("i") ? ConfigItems.class : ConfigBlocks.class;
        Object value = holder.getField(parts[1]).get(null);
        Item item = value instanceof Block ? Item.getItemFromBlock((Block) value) : (Item) value;
        assertStack(actual.icon_item, item, Integer.parseInt(parts[2]), 1);
    }

    private static void assertAspects(String label, String descriptor, AspectList actual) throws Exception {
        String[] expected = values(descriptor);
        int size = expected == null ? 0 : expected.length;
        assertEquals(label, size, actual.size());
        if (expected == null) {
            return;
        }
        Aspect[] actualAspects = actual.getAspects();
        for (int i = 0; i < expected.length; ++i) {
            String[] pair = expected[i].split("=");
            Aspect aspect = aspect(pair[0]);
            assertSame(label + " aspect " + i, aspect, actualAspects[i]);
            assertEquals(label + " amount " + i, Integer.parseInt(pair[1]), actual.getAmount(aspect));
        }
    }

    private static void assertAspectArray(String label, String descriptor, Aspect[] actual) {
        String[] expected = values(descriptor);
        if (expected == null) {
            assertNull(label, actual);
            return;
        }
        assertEquals(label, expected.length, actual.length);
        for (int i = 0; i < expected.length; ++i) {
            assertSame(label + " aspect " + i, aspect(expected[i]), actual[i]);
        }
    }

    private static void assertCap(CapTuple expected, WandCap actual) {
        assertEquals(expected.tag, actual.getTag());
        assertEquals(expected.tag, expected.discount, actual.getBaseCostModifier(), 0.0F);
        assertEquals(expected.tag, expected.craftCost, actual.getCraftCost());
        assertStack(actual.getItem(), ConfigItems.itemWandCap, expected.meta, 1);
        assertAspectList(expected.tag, expected.specials, actual.getSpecialCostModifierAspects());
        assertEquals(expected.tag, expected.specialDiscount, actual.getSpecialCostModifier(), 0.0F);
    }

    @SuppressWarnings("unchecked")
    private static void assertRod(RodTuple expected, WandRod actual) throws Exception {
        assertEquals(expected.tag, actual.getTag());
        assertEquals(expected.tag, expected.capacity, actual.getCapacity());
        assertEquals(expected.tag, expected.craftCost, actual.getCraftCost());
        Item item = expected.item.equals("stick") ? Items.STICK : ConfigItems.itemWandRod;
        assertStack(actual.getItem(), item, expected.meta, 1);
        assertEquals(expected.tag, expected.staff, actual instanceof StaffRod);
        assertEquals(expected.tag, expected.glow, actual.isGlowing());
        assertEquals(expected.tag, expected.runes, actual instanceof StaffRod && ((StaffRod) actual).hasRunes());

        if (expected.callback.isEmpty()) {
            assertNull(expected.tag, actual.getOnUpdate());
            return;
        }
        assertEquals(expected.tag, WandRodPrimalOnUpdate.class, actual.getOnUpdate().getClass());
        Field aspect = WandRodPrimalOnUpdate.class.getDeclaredField("aspect");
        aspect.setAccessible(true);
        Field primals = WandRodPrimalOnUpdate.class.getDeclaredField("primals");
        primals.setAccessible(true);
        if (expected.callback.equals("PRIMALS")) {
            assertNull(expected.tag, aspect.get(actual.getOnUpdate()));
            assertAspectList(expected.tag, "AIR,EARTH,FIRE,WATER,ORDER,ENTROPY",
                    (List<Aspect>) primals.get(actual.getOnUpdate()));
        } else {
            assertSame(expected.tag, aspect(expected.callback), aspect.get(actual.getOnUpdate()));
            assertNull(expected.tag, primals.get(actual.getOnUpdate()));
        }
    }

    private static void assertAspectList(String label, String descriptor, List<Aspect> actual) {
        String[] expected = values(descriptor);
        if (expected == null) {
            assertNull(label, actual);
            return;
        }
        assertEquals(label, expected.length, actual.size());
        for (int i = 0; i < expected.length; ++i) {
            assertSame(label + " aspect " + i, aspect(expected[i]), actual.get(i));
        }
    }

    private static void assertStack(ItemStack stack, Item item, int meta, int count) {
        assertSame(item, stack.getItem());
        assertEquals(meta, stack.getMetadata());
        assertEquals(count, stack.getCount());
    }

    private static String flags(ResearchItem item) {
        List<String> flags = new ArrayList<>();
        if (item.isSpecial()) flags.add("special");
        if (item.isSecondary()) flags.add("secondary");
        if (item.isRound()) flags.add("round");
        if (item.isStub()) flags.add("stub");
        if (item.isVirtual()) flags.add("virtual");
        if (item.isConcealed()) flags.add("concealed");
        if (item.isHidden()) flags.add("hidden");
        if (item.isLost()) flags.add("lost");
        if (item.isAutoUnlock()) flags.add("auto");
        return String.join(",", flags);
    }

    private static Set<String> enabledKeys(int enabled) {
        Set<String> keys = new HashSet<>();
        for (Entry entry : ENTRIES) {
            if ((entry.gate & enabled) == entry.gate) {
                keys.add(entry.key);
            }
        }
        return keys;
    }

    private static IdentityHashMap<Object, String> installNamedHandles() {
        LinkedHashMap<String, Character> handles = new LinkedHashMap<>();
        for (Entry entry : ENTRIES) {
            String[] pages = pages(entry.pages);
            if (pages == null) continue;
            for (String page : pages) {
                if (page.length() > 2 && page.charAt(1) == '=' && page.charAt(0) != 'T') {
                    Character old = handles.put(page.substring(2), page.charAt(0));
                    assertNull("duplicate named handle " + page, old);
                }
            }
        }
        assertEquals(42, handles.size());

        IdentityHashMap<Object, String> names = new IdentityHashMap<>();
        for (Map.Entry<String, Character> handle : handles.entrySet()) {
            Object recipe = newHandle(handle.getValue());
            ConfigResearch.recipes.put(handle.getKey(), recipe);
            names.put(recipe, handle.getKey());
        }
        return names;
    }

    private static Object newHandle(char family) {
        if (family == 'N') {
            return new DummyRecipe();
        }
        if (family == 'A') {
            return new DummyArcaneRecipe();
        }
        if (family == 'I') {
            return new InfusionRecipe("", new ItemStack(Items.STICK), 0, new AspectList(),
                    new ItemStack(Items.STICK), new ItemStack[0]);
        }
        throw new AssertionError("Unknown recipe family " + family);
    }

    private static ResearchPage.PageType pageType(char family) {
        if (family == 'N') return ResearchPage.PageType.NORMAL_CRAFTING;
        if (family == 'A') return ResearchPage.PageType.ARCANE_CRAFTING;
        if (family == 'I') return ResearchPage.PageType.INFUSION_CRAFTING;
        throw new AssertionError("Unknown recipe family " + family);
    }

    private static Aspect aspect(String name) {
        try {
            return (Aspect) Aspect.class.getField(name).get(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Unknown aspect " + name, e);
        }
    }

    private static String[] values(String descriptor) {
        return descriptor.isEmpty() ? null : descriptor.split(",");
    }

    private static String[] pages(String descriptor) {
        return descriptor.isEmpty() ? null : descriptor.split("\\|");
    }

    private static void invokeConfigResearchCategories() throws Exception {
        Method method = ConfigResearch.class.getDeclaredMethod("initCategories");
        method.setAccessible(true);
        method.invoke(null);
    }

    private static void invokeThaumcraft(String name) throws Exception {
        Method method = Thaumcraft.class.getDeclaredMethod(name);
        method.setAccessible(true);
        method.invoke(new Thaumcraft());
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Integer> warpMap() throws Exception {
        Field field = ThaumcraftApi.class.getDeclaredField("warpMap");
        field.setAccessible(true);
        return (Map<Object, Integer>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private static Map<Block, Item> blockItems() throws Exception {
        Field field = Item.class.getDeclaredField("BLOCK_TO_ITEM");
        field.setAccessible(true);
        return (Map<Block, Item>) field.get(null);
    }

    private static Entry e(String key, int gate, int column, int row, int complexity,
                           String icon, String flags, String aspects, String parents,
                           String hiddenParents, String entityTriggers, String aspectTriggers,
                           String pages, int warp) {
        return new Entry(key, gate, column, row, complexity, icon, flags, aspects, parents,
                hiddenParents, entityTriggers, aspectTriggers, pages, warp);
    }

    private static CapTuple cap(String tag, float discount, int craftCost, int meta,
                                String specials, float specialDiscount) {
        return new CapTuple(tag, discount, craftCost, meta, specials, specialDiscount);
    }

    private static RodTuple rod(String tag, boolean staff, int capacity, int craftCost,
                                String item, int meta, String callback, boolean glow, boolean runes) {
        return new RodTuple(tag, staff, capacity, craftCost, item, meta, callback, glow, runes);
    }

    private static final class Entry {
        final String key;
        final int gate;
        final int column;
        final int row;
        final int complexity;
        final String icon;
        final String flags;
        final String aspects;
        final String parents;
        final String hiddenParents;
        final String entityTriggers;
        final String aspectTriggers;
        final String pages;
        final int warp;

        Entry(String key, int gate, int column, int row, int complexity, String icon,
              String flags, String aspects, String parents, String hiddenParents,
              String entityTriggers, String aspectTriggers, String pages, int warp) {
            this.key = key;
            this.gate = gate;
            this.column = column;
            this.row = row;
            this.complexity = complexity;
            this.icon = icon;
            this.flags = flags;
            this.aspects = aspects;
            this.parents = parents;
            this.hiddenParents = hiddenParents;
            this.entityTriggers = entityTriggers;
            this.aspectTriggers = aspectTriggers;
            this.pages = pages;
            this.warp = warp;
        }
    }

    private static final class CapTuple {
        final String tag;
        final float discount;
        final int craftCost;
        final int meta;
        final String specials;
        final float specialDiscount;

        CapTuple(String tag, float discount, int craftCost, int meta,
                 String specials, float specialDiscount) {
            this.tag = tag;
            this.discount = discount;
            this.craftCost = craftCost;
            this.meta = meta;
            this.specials = specials;
            this.specialDiscount = specialDiscount;
        }
    }

    private static final class RodTuple {
        final String tag;
        final boolean staff;
        final int capacity;
        final int craftCost;
        final String item;
        final int meta;
        final String callback;
        final boolean glow;
        final boolean runes;

        RodTuple(String tag, boolean staff, int capacity, int craftCost, String item,
                 int meta, String callback, boolean glow, boolean runes) {
            this.tag = tag;
            this.staff = staff;
            this.capacity = capacity;
            this.craftCost = craftCost;
            this.item = item;
            this.meta = meta;
            this.callback = callback;
            this.glow = glow;
            this.runes = runes;
        }
    }

    private static final class DummyRecipe extends IForgeRegistryEntry.Impl<net.minecraft.item.crafting.IRecipe>
            implements net.minecraft.item.crafting.IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            return false;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            return new ItemStack(Items.STICK);
        }

        @Override
        public boolean canFit(int width, int height) {
            return true;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return new ItemStack(Items.STICK);
        }
    }

    private static final class DummyArcaneRecipe implements IArcaneRecipe {
        @Override
        public boolean matches(IInventory inv, World world, net.minecraft.entity.player.EntityPlayer player) {
            return false;
        }

        @Override
        public ItemStack getCraftingResult(IInventory inv) {
            return new ItemStack(Items.STICK);
        }

        @Override
        public int getRecipeSize() {
            return 0;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return new ItemStack(Items.STICK);
        }

        @Override
        public AspectList getAspects() {
            return new AspectList();
        }

        @Override
        public AspectList getAspects(IInventory inv) {
            return new AspectList();
        }

        @Override
        public String getResearch() {
            return "";
        }
    }
}
