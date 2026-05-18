package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesReferenceKeyCorpusStaticGuardTest {

    private static final Pattern RECIPE_PUT_PATTERN = Pattern.compile("ConfigResearch\\.recipes\\.put\\(\"([^\"]+)\"");
    private static final Pattern SPECIAL_RECIPE_HANDLE_PATTERN = Pattern.compile("specialResearchRecipeHandles\\.put\\(\"([^\"]+)\"");
    private static final Pattern SPECIAL_RECIPE_HANDLE_BRIDGE_PATTERN = Pattern.compile("addSpecialResearchRecipeHandle\\(\"([^\"]+)\"");
    private static final Pattern ARCANE_PATTERN = Pattern.compile("registerArcaneRecipe\\(\"([^\"]+)\"");
    private static final Pattern SHAPELESS_ARCANE_PATTERN = Pattern.compile("registerShapelessArcaneRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_PATTERN = Pattern.compile("registerInfusionRecipe\\(\"([^\"]+)\"");
    private static final Pattern INFUSION_ENCHANT_PATTERN = Pattern.compile("registerInfusionEnchantmentRecipe\\(\"([^\"]+)\"");

    private static final Set<String> REFERENCE_RECIPE_KEYS = new TreeSet<>(Arrays.asList(
            "AdvAlchemyConstruct",
            "AdvAlchemyFurnace",
            "AdvancedGolem",
            "AlchemicalConstruct",
            "AlchemyFurnace",
            "Alembic",
            "AltBonemeal",
            "AltClay",
            "AltCrackedBrick",
            "AltGlowstone",
            "AltGunpowder",
            "AltIce",
            "AltInk",
            "AltMossyCobble",
            "AltSlime",
            "AltWeb",
            "Alumentum",
            "ArcTable",
            "ArcaneBore",
            "ArcaneBoreBase",
            "ArcaneDoor",
            "ArcaneEar",
            "ArcaneLamp",
            "ArcanePedestal",
            "ArcanePressurePlate",
            "ArcaneSpa",
            "ArcaneStone1",
            "ArcaneStone2",
            "ArcaneStone3",
            "ArcaneStone4",
            "BalancedShard_",
            "Banner_",
            "BathSalts",
            "Bellows",
            "BlockFlesh",
            "BlockTallow",
            "BlockThaumium",
            "BoneBow",
            "BootsTraveller",
            "BottleTaint",
            "Centrifuge",
            "Clusters",
            "Clusters6",
            "CoreAlchemy",
            "CoreBlank",
            "CoreButcher",
            "CoreEmpty",
            "CoreFill",
            "CoreFishing",
            "CoreGather",
            "CoreGuard",
            "CoreHarvest",
            "CoreLiquid",
            "CoreLumber",
            "CoreSorting",
            "CoreUse",
            "Crucible",
            "Deconstructor",
            "EldritchEye",
            "ElementalAxe",
            "ElementalHoe",
            "ElementalPick",
            "ElementalShovel",
            "ElementalSword",
            "EnchantedFabric",
            "EssentiaCrystalizer",
            "EssentiaReservoir",
            "EtherealBloom",
            "Filter",
            "FluxScrubber",
            "FocalManipulator",
            "FocusExcavation",
            "FocusFire",
            "FocusFrost",
            "FocusHellbat",
            "FocusPortableHole",
            "FocusPouch",
            "FocusPrimal",
            "FocusShock",
            "FocusTrade",
            "FocusWarding",
            "Goggles",
            "GoldKey",
            "GolemBell",
            "GolemClay",
            "GolemFetter",
            "GolemFlesh",
            "GolemIron",
            "GolemStone",
            "GolemStraw",
            "GolemTallow",
            "GolemThaumium",
            "GolemWood",
            "Grate",
            "HelmGoggles",
            "HoverGirdle",
            "HoverHarness",
            "HungryChest",
            "InfEnch0",
            "InfEnch1",
            "InfEnch10",
            "InfEnch11",
            "InfEnch12",
            "InfEnch13",
            "InfEnch14",
            "InfEnch15",
            "InfEnch16",
            "InfEnch17",
            "InfEnch18",
            "InfEnch19",
            "InfEnch2",
            "InfEnch20",
            "InfEnch21",
            "InfEnch3",
            "InfEnch4",
            "InfEnch5",
            "InfEnch6",
            "InfEnch7",
            "InfEnch8",
            "InfEnch9",
            "InfEnchHaste",
            "InfEnchRepair",
            "InfernalFurnace",
            "InfusionAltar",
            "InfusionMatrix",
            "IronKey",
            "JarBrain",
            "JarLabel",
            "JarLabelNull",
            "JarVoid",
            "KnowFrag",
            "LampFertility",
            "LampGrowth",
            "Levitator",
            "LiquidDeath",
            "MaskAngryGhost",
            "MaskGrinningDevil",
            "MaskSippingFiend",
            "Mirror",
            "MirrorEssentia",
            "MirrorGlass",
            "MirrorHand",
            "MnemonicMatrix",
            "MundaneAmulet",
            "MundaneBelt",
            "MundaneRing",
            "Nitor",
            "NodeChargeRelay",
            "NodeJar",
            "NodeRelay",
            "NodeStabilizer",
            "NodeStabilizerAdv",
            "NodeTransducer",
            "PaveTravel",
            "PaveWard",
            "Phial",
            "PlankGreatwood",
            "PlankSilverwood",
            "PrimalArrow_",
            "PrimalCharm",
            "PrimalCrusher",
            "PureCopper",
            "PureGold",
            "PureIron",
            "PureLead",
            "PureSilver",
            "PureTin",
            "ResTable",
            "Resonator",
            "RobeBoots",
            "RobeChest",
            "RobeLegs",
            "RunicAmulet",
            "RunicAmuletEmergency",
            "RunicGirdle",
            "RunicGirdleKinetic",
            "RunicGirdleKinetic_2",
            "RunicRing",
            "RunicRingCharged",
            "RunicRingHealing",
            "SaneSoap",
            "SanityCheck",
            "Scribe1",
            "Scribe2",
            "Scribe3",
            "SinStone",
            "Table",
            "Tallow",
            "TallowCandle",
            "Thaumatorium",
            "Thaumium",
            "ThaumiumAxe",
            "ThaumiumBoots",
            "ThaumiumChest",
            "ThaumiumFortressChest",
            "ThaumiumFortressHelm",
            "ThaumiumFortressLegs",
            "ThaumiumHelm",
            "ThaumiumHoe",
            "ThaumiumLegs",
            "ThaumiumPick",
            "ThaumiumShovel",
            "ThaumiumSword",
            "Thaumometer",
            "Thaumonomicon",
            "TinyArmor",
            "TinyBowtie",
            "TinyDart",
            "TinyFez",
            "TinyGlasses",
            "TinyHammer",
            "TinyHat",
            "TinyVisor",
            "TransCopper",
            "TransGold",
            "TransIron",
            "TransLead",
            "TransSilver",
            "TransTin",
            "TravelTrunk",
            "Tube",
            "TubeBuffer",
            "TubeFilter",
            "TubeOneway",
            "TubeRestrict",
            "TubeValve",
            "UpgradeAir",
            "UpgradeEarth",
            "UpgradeEntropy",
            "UpgradeFire",
            "UpgradeOrder",
            "UpgradeWater",
            "VisAmulet",
            "VoidAxe",
            "VoidBoots",
            "VoidChest",
            "VoidHelm",
            "VoidHoe",
            "VoidLegs",
            "VoidMetal",
            "VoidPick",
            "VoidRobeChest",
            "VoidRobeHelm",
            "VoidRobeLegs",
            "VoidSeed",
            "VoidShovel",
            "VoidSword",
            "WandBasic",
            "WandCapCopper",
            "WandCapGold",
            "WandCapIron",
            "WandCapSilver",
            "WandCapSilverInert",
            "WandCapThaumium",
            "WandCapThaumiumInert",
            "WandCapVoid",
            "WandCapVoidInert",
            "WandPed",
            "WandPedFocus",
            "WandRodBlaze",
            "WandRodBlazeStaff",
            "WandRodBone",
            "WandRodBoneStaff",
            "WandRodGreatwood",
            "WandRodGreatwoodStaff",
            "WandRodIce",
            "WandRodIceStaff",
            "WandRodObsidian",
            "WandRodObsidianStaff",
            "WandRodPrimalStaff",
            "WandRodQuartz",
            "WandRodQuartzStaff",
            "WandRodReed",
            "WandRodReedStaff",
            "WandRodSilverwood",
            "WandRodSilverwoodStaff",
            "WardedGlass",
            "WardedJar"));

    @Test
    public void configRecipesLiteralKeyCorpusShouldMatchReferenceShape() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();
        Set<String> actual = extract(source);
        List<String> missing = new ArrayList<>();
        List<String> unexpected = new ArrayList<>();

        for (String key : REFERENCE_RECIPE_KEYS) {
            if (!actual.contains(key)) {
                missing.add(key);
            }
        }
        for (String key : actual) {
            if (!REFERENCE_RECIPE_KEYS.contains(key)) {
                unexpected.add(key);
            }
        }

        assertTrue("ConfigRecipes literal recipe-key corpus size should match reference 278, actual: " + actual.size(),
                actual.size() == REFERENCE_RECIPE_KEYS.size());
        assertTrue("Missing reference recipe keys in ConfigRecipes corpus: " + missing, missing.isEmpty());
        assertTrue("Unexpected non-reference recipe keys in ConfigRecipes corpus: " + unexpected, unexpected.isEmpty());
    }

    private static Set<String> extract(String source) {
        Set<String> out = new TreeSet<>();
        collect(out, source, RECIPE_PUT_PATTERN);
        collect(out, source, SPECIAL_RECIPE_HANDLE_PATTERN);
        collect(out, source, SPECIAL_RECIPE_HANDLE_BRIDGE_PATTERN);
        collect(out, source, ARCANE_PATTERN);
        collect(out, source, SHAPELESS_ARCANE_PATTERN);
        collect(out, source, INFUSION_PATTERN);
        collect(out, source, INFUSION_ENCHANT_PATTERN);
        return out;
    }

    private static void collect(Set<String> out, String source, Pattern pattern) {
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            out.add(matcher.group(1));
        }
    }
}
