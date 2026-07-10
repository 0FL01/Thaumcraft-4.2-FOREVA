package thaumcraft.common.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.common.config.recipes.ConfigRecipesArcaneSlice;
import thaumcraft.common.config.research.ConfigResearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ConfigRecipesArcaneSliceBehaviorTest {
    private List<Object> oldRecipes;
    private Map<String, Object> oldResearchRecipes;
    private boolean oldWardedStone;
    private boolean oldFoundCopperIngot;
    private boolean oldFoundSilverIngot;

    @BeforeClass
    public static void initializeRecipeDependencies() {
        Bootstrap.register();
        if (ConfigBlocks.blockJar == null) {
            ConfigBlocks.init();
        }
        if (ConfigItems.itemShard == null) {
            ConfigItems.init();
        }
    }

    @Before
    public void saveState() {
        this.oldRecipes = new ArrayList<>(ThaumcraftApi.getCraftingRecipes());
        this.oldResearchRecipes = new LinkedHashMap<>(ConfigResearch.recipes);
        this.oldWardedStone = Config.wardedStone;
        this.oldFoundCopperIngot = Config.foundCopperIngot;
        this.oldFoundSilverIngot = Config.foundSilverIngot;
        ThaumcraftApi.getCraftingRecipes().clear();
        ConfigResearch.recipes.clear();
        Config.wardedStone = true;
        Config.foundCopperIngot = true;
        Config.foundSilverIngot = true;
    }

    @After
    public void restoreState() {
        ThaumcraftApi.getCraftingRecipes().clear();
        ThaumcraftApi.getCraftingRecipes().addAll(this.oldRecipes);
        ConfigResearch.recipes.clear();
        ConfigResearch.recipes.putAll(this.oldResearchRecipes);
        Config.wardedStone = this.oldWardedStone;
        Config.foundCopperIngot = this.oldFoundCopperIngot;
        Config.foundSilverIngot = this.oldFoundSilverIngot;
    }

    @Test
    public void staticArcaneCorpusShouldKeepOriginalFirstMatchOrderAndIngredientContracts() {
        ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline();

        assertEquals(expectedKeys(), registeredKeysInOrder());

        ShapedArcaneRecipe wardedJar = (ShapedArcaneRecipe) ConfigResearch.recipes.get("WardedJar");
        ItemStack jarGlass = (ItemStack) wardedJar.getInput()[0];
        assertSame(Item.getItemFromBlock(Blocks.GLASS_PANE), jarGlass.getItem());
        assertEquals(OreDictionary.WILDCARD_VALUE, jarGlass.getMetadata());

        ShapedArcaneRecipe hungryChest = (ShapedArcaneRecipe) ConfigResearch.recipes.get("HungryChest");
        ItemStack trapdoor = (ItemStack) hungryChest.getInput()[1];
        assertSame(Item.getItemFromBlock(Blocks.TRAPDOOR), trapdoor.getItem());
        assertEquals(OreDictionary.WILDCARD_VALUE, trapdoor.getMetadata());
    }

    private List<String> registeredKeysInOrder() {
        Map<Object, String> keysByRecipe = new IdentityHashMap<>();
        for (Map.Entry<String, Object> entry : ConfigResearch.recipes.entrySet()) {
            keysByRecipe.put(entry.getValue(), entry.getKey());
        }
        List<String> keys = new ArrayList<>();
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            keys.add(keysByRecipe.get(recipe));
        }
        return keys;
    }

    private static List<String> expectedKeys() {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 16; ++i) {
            keys.add("Banner_" + i);
        }
        keys.addAll(Arrays.asList(
                "PrimalCharm", "ArcaneDoor", "WardedGlass", "IronKey", "FluxScrubber",
                "GoldKey", "ArcanePressurePlate", "NodeStabilizer", "NodeTransducer", "NodeRelay",
                "NodeChargeRelay", "FocalManipulator", "GolemFetter", "ArcaneStone1", "PaveTravel",
                "ArcaneLamp", "ArcaneSpa", "PaveWard", "Levitator", "ArcaneEar", "MirrorGlass", "BoneBow"));
        for (int i = 0; i < 6; ++i) {
            keys.add("PrimalArrow_" + i);
        }
        keys.addAll(Arrays.asList(
                "InfusionMatrix", "ArcanePedestal", "WardedJar", "JarVoid", "WandCapGold",
                "WandCapCopper", "WandCapSilverInert", "WandCapThaumiumInert", "WandCapVoidInert",
                "WandRodGreatwood", "WandRodGreatwoodStaff", "WandRodObsidianStaff",
                "WandRodSilverwoodStaff", "WandRodIceStaff", "WandRodQuartzStaff", "WandRodReedStaff",
                "WandRodBlazeStaff", "WandRodBoneStaff", "FocusFire", "FocusFrost", "FocusShock",
                "FocusTrade", "FocusExcavation", "FocusPrimal", "FocusPouch", "Deconstructor",
                "ArcaneBoreBase", "EnchantedFabric", "RobeChest", "RobeLegs", "RobeBoots", "Goggles",
                "HungryChest", "GolemBell", "CoreBlank", "UpgradeAir", "UpgradeEarth", "UpgradeFire",
                "UpgradeWater", "UpgradeOrder", "UpgradeEntropy", "TinyHat", "TinyFez", "TinyBowtie",
                "TinyGlasses", "TinyDart", "TinyVisor", "TinyArmor", "TinyHammer", "Filter",
                "AlchemyFurnace", "Alembic", "Bellows", "Tube", "Resonator", "TubeValve", "TubeFilter",
                "TubeRestrict", "TubeOneway", "TubeBuffer", "AlchemicalConstruct", "AdvAlchemyConstruct",
                "Centrifuge", "EssentiaCrystalizer", "MnemonicMatrix"));
        return keys;
    }
}
