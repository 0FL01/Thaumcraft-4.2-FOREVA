package thaumcraft.common.config;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.items.equipment.ItemElementalAxe;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.lib.enchantment.EnchantmentFrugal;
import thaumcraft.common.lib.enchantment.EnchantmentHaste;
import thaumcraft.common.lib.enchantment.EnchantmentPotency;
import thaumcraft.common.lib.enchantment.EnchantmentRepair;
import thaumcraft.common.lib.enchantment.EnchantmentWandFortune;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.potions.PotionSunScorned;
import thaumcraft.common.lib.potions.PotionThaumarhia;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.lib.potions.PotionWarpWard;

public class Config {

    public static Configuration config;

    // Categories
    public static final String CATEGORY_ENCH = "Enchantments";
    public static final String CATEGORY_ENTITIES = "Entities";
    public static final String CATEGORY_BIOMES = "Biomes";
    public static final String CATEGORY_RESEARCH = "Research";
    public static final String CATEGORY_GEN = "World_Generation";
    public static final String CATEGORY_REGEN = "World_Regeneration";
    public static final String CATEGORY_SPAWN = "Monster_Spawning";
    public static final String CATEGORY_RUNIC = "Runic_Shielding";

    // Biomes
    public static int biomeTaintID = 192;
    public static int biomeMagicalForestID = 193;
    public static int biomeEerieID = 194;
    public static int biomeEldritchID = 195;
    public static int biomeTaintWeight = 2;
    public static int biomeMagicalForestWeight = 5;

    // Taint
    public static int taintSpreadRate = 200;
    public static boolean taintFromFlux = true;

    // Difficulty
    public static boolean hardNode = true;
    public static boolean wuss = false;

    // Dimension
    public static int dimensionOuterId = -42;

    // Mobs
    public static boolean championMobs = true;

    // Runic shielding
    public static int shieldRecharge = 2000;
    public static int shieldWait = 80;
    public static int shieldCost = 50;

    // Misc
    public static boolean colorBlind = false;
    public static boolean shaders = true;
    public static boolean crooked = true;
    public static boolean showTags = false;
    public static boolean blueBiome = false;
    public static boolean allowMirrors = true;
    public static boolean dialBottom = false;
    public static int nodeRefresh = 10;

    // World gen
    public static boolean genAura = true;
    public static boolean genStructure = true;
    public static boolean genCinnibar = true;
    public static boolean genAmber = true;
    public static boolean genInfusedStone = true;
    public static boolean genTrees = true;
    public static boolean genTaint = true;

    // World regen
    public static boolean regenAura = false;
    public static boolean regenStructure = false;
    public static boolean regenCinnibar = false;
    public static boolean regenAmber = false;
    public static boolean regenInfusedStone = false;
    public static boolean regenTrees = false;
    public static boolean regenTaint = false;
    public static String regenKey = "DEFAULT";

    // General
    public static boolean wardedStone = true;
    public static boolean allowCheatSheet = true;
    public static boolean golemChestInteract = true;
    public static int nodeRarity = 36;
    public static int specialNodeRarity = 18;
    public static int notificationDelay = 5000;
    public static int notificationMax = 15;
    public static boolean glowyTaint = true;
    public static int researchDifficulty = 0;
    public static int aspectTotalCap = 100;
    public static int golemDelay = 5;
    public static int golemIgnoreDelay = 10000;
    public static int golemLinkQuality = 16;
    public static boolean CwardedStone = true;
    public static boolean CallowCheatSheet = true;
    public static boolean CallowMirrors = true;
    public static boolean ChardNode = true;
    public static boolean Cwuss = false;
    public static int CresearchDifficulty = 0;
    public static int CaspectTotalCap = 100;

    // Spawning
    public static boolean spawnAngryZombie = true;
    public static boolean spawnFireBat = true;
    public static boolean spawnTaintacle = true;
    public static boolean spawnWisp = true;
    public static boolean spawnTaintSpore = true;
    public static boolean spawnPech = true;
    public static boolean spawnElder = true;

    // Potion IDs (1.12.2: unused, kept for reference; potions use registry)
    public static int potionVisExhaustID = 18;
    public static int potionInfVisExhaustID = 18;
    public static int potionBlurredID = 18;
    public static int potionThaumarhiaID = 18;
    public static int potionTaintPoisonID = 19;
    public static int potionUnHungerID = 17;
    public static int potionSunScornedID = 17;
    public static int potionWarpWardID = 23;
    public static int potionDeathGazeID = 17;

    // Enchantments
    public static Enchantment enchHaste = null;
    public static Enchantment enchRepair = null;
    public static Enchantment enchFrugal = null;
    public static Enchantment enchPotency = null;
    public static Enchantment enchWandFortune = null;

    // Potions
    public static PotionFluxTaint potionFluxTaint;
    public static PotionVisExhaust potionVisExhaust;
    public static PotionInfectiousVisExhaust potionInfectiousVisExhaust;
    public static PotionUnnaturalHunger potionUnnaturalHunger;
    public static PotionWarpWard potionWarpWard;
    public static PotionDeathGaze potionDeathGaze;
    public static PotionBlurredVision potionBlurredVision;
    public static PotionSunScorned potionSunScorned;
    public static PotionThaumarhia potionThaumarhia;

    // Aspects
    public static ArrayList<Aspect> aspectOrder = new ArrayList<>();

    // Ore dict tracking
    public static boolean foundCopperIngot = false;
    public static boolean foundTinIngot = false;
    public static boolean foundSilverIngot = false;
    public static boolean foundLeadIngot = false;
    public static boolean foundCopperOre = false;
    public static boolean foundTinOre = false;
    public static boolean foundSilverOre = false;
    public static boolean foundLeadOre = false;

    // Materials
    public static final Material airyMaterial;
    public static final Material fluxGoomaterial;
    public static final Material taintMaterial;

    static {
        airyMaterial = new MaterialAiry(MapColor.AIR);
        fluxGoomaterial = new Material(MapColor.TNT);
        taintMaterial = new Material(MapColor.TNT);
    }

    public static void init(File file) {
        config = new Configuration(file);
        config.addCustomCategoryComment(CATEGORY_ENCH, "Custom enchantments");
        config.addCustomCategoryComment(CATEGORY_SPAWN, "Will these mobs spawn");
        config.addCustomCategoryComment(CATEGORY_RESEARCH, "Various research related things.");
        config.addCustomCategoryComment(CATEGORY_GEN, "Settings to turn certain world-gen on or off.");
        config.addCustomCategoryComment(CATEGORY_REGEN, "Regeneration settings for chunks that skipped TC worldgen.");
        config.addCustomCategoryComment(CATEGORY_BIOMES, "Biomes and effects");
        config.addCustomCategoryComment(CATEGORY_RUNIC, "Runic Shielding");
        config.load();
        syncConfigurable();
        config.save();
    }

    public static void save() {
        if (config != null) {
            config.save();
        }
    }

    public static void initPotions() {
        potionFluxTaint = new PotionFluxTaint(true, 0x800080);
        PotionFluxTaint.instance = potionFluxTaint;
        PotionFluxTaint.init();
        potionFluxTaint.setRegistryName("thaumcraft", "flux_taint");

        potionVisExhaust = new PotionVisExhaust(true, 0x8888FF);
        PotionVisExhaust.instance = potionVisExhaust;
        PotionVisExhaust.init();
        potionVisExhaust.setRegistryName("thaumcraft", "vis_exhaust");

        potionInfectiousVisExhaust = new PotionInfectiousVisExhaust(true, 0x4444AA);
        potionInfectiousVisExhaust.setRegistryName("thaumcraft", "infectious_vis_exhaust");

        potionUnnaturalHunger = new PotionUnnaturalHunger(true, 0x55AA55);
        potionUnnaturalHunger.setRegistryName("thaumcraft", "unnatural_hunger");

        potionWarpWard = new PotionWarpWard(false, 0xFFAA00);
        potionWarpWard.setRegistryName("thaumcraft", "warp_ward");

        potionDeathGaze = new PotionDeathGaze(true, 0x440044);
        potionDeathGaze.setRegistryName("thaumcraft", "death_gaze");

        potionBlurredVision = new PotionBlurredVision(true, 0x888888);
        potionBlurredVision.setRegistryName("thaumcraft", "blurred_vision");

        potionSunScorned = new PotionSunScorned(true, 0xFFAA00);
        potionSunScorned.setRegistryName("thaumcraft", "sun_scorned");

        potionThaumarhia = new PotionThaumarhia(true, 0xAA0000);
        potionThaumarhia.setRegistryName("thaumcraft", "thaumarhia");
    }

    public static void syncConfigurable() {
        genAura = config.get(CATEGORY_GEN, "generate_aura_nodes", true).getBoolean(true);
        genStructure = config.get(CATEGORY_GEN, "generate_structures", true).getBoolean(true);
        genCinnibar = config.get(CATEGORY_GEN, "generate_cinnibar_ore", true).getBoolean(true);
        genAmber = config.get(CATEGORY_GEN, "generate_amber_ore", true).getBoolean(true);
        genInfusedStone = config.get(CATEGORY_GEN, "generate_infused_stone", true).getBoolean(true);
        genTrees = config.get(CATEGORY_GEN, "generate_trees", true).getBoolean(true);
        genTaint = config.get(CATEGORY_GEN, "generate_taint", true).getBoolean(true);

        regenKey = config.get(CATEGORY_REGEN, "regen_key", "DEFAULT").getString();
        regenAura = config.get(CATEGORY_REGEN, "aura_nodes", false).getBoolean(false);
        regenStructure = config.get(CATEGORY_REGEN, "structures", false).getBoolean(false);
        regenCinnibar = config.get(CATEGORY_REGEN, "cinnibar_ore", false).getBoolean(false);
        regenAmber = config.get(CATEGORY_REGEN, "amber_ore", false).getBoolean(false);
        regenInfusedStone = config.get(CATEGORY_REGEN, "infused_stone", false).getBoolean(false);
        regenTrees = config.get(CATEGORY_REGEN, "trees", false).getBoolean(false);
        regenTaint = config.get(CATEGORY_REGEN, "taint", false).getBoolean(false);

        biomeTaintWeight = config.get(CATEGORY_BIOMES, "taint_biome_weight", 2).getInt();
        biomeTaintID = config.get(CATEGORY_BIOMES, "biome_taint", 192).getInt();
        biomeMagicalForestWeight = config.get(CATEGORY_BIOMES, "magical_forest_biome_weight", 5).getInt();
        biomeMagicalForestID = config.get(CATEGORY_BIOMES, "biome_magical_forest", 193).getInt();
        biomeEerieID = config.get(CATEGORY_BIOMES, "biome_eerie", 194).getInt();
        biomeEldritchID = config.get(CATEGORY_BIOMES, "biome_eldritch", 195).getInt();
        dimensionOuterId = config.get(CATEGORY_BIOMES, "outer_lands_dim", -42).getInt();

        ThaumcraftApi.enchantHaste = config.get(CATEGORY_ENCH, "ench_haste", 150).getInt();
        ThaumcraftApi.enchantRepair = config.get(CATEGORY_ENCH, "ench_repair", 151).getInt();

        researchDifficulty = config.get(CATEGORY_RESEARCH, "research_difficulty", 0).getInt();
        CresearchDifficulty = researchDifficulty;
        aspectTotalCap = config.get(CATEGORY_RESEARCH, "aspect_total_cap", 100).getInt();
        CaspectTotalCap = aspectTotalCap;

        spawnAngryZombie = config.get(CATEGORY_SPAWN, "spawn_angry_zombies", true).getBoolean(true);
        spawnFireBat = config.get(CATEGORY_SPAWN, "spawn_fire_bats", true).getBoolean(true);
        spawnWisp = config.get(CATEGORY_SPAWN, "spawn_wisps", true).getBoolean(true);
        spawnTaintacle = config.get(CATEGORY_SPAWN, "spawn_taintacles", true).getBoolean(true);
        spawnTaintSpore = config.get(CATEGORY_SPAWN, "spawn_taint_spores", true).getBoolean(true);
        spawnPech = config.get(CATEGORY_SPAWN, "spawn_pechs", true).getBoolean(true);
        spawnElder = config.get(CATEGORY_SPAWN, "spawn_eldercreatures", true).getBoolean(true);

        championMobs = config.get(CATEGORY_SPAWN, "champion_mobs", true).getBoolean(true);

        allowMirrors = config.get("general", "allow_mirrors", true).getBoolean(true);
        CallowMirrors = allowMirrors;
        colorBlind = config.get("general", "color_blind", false).getBoolean(false);
        shaders = config.get("general", "shaders", true).getBoolean(false);
        crooked = config.get("general", "crooked", true).getBoolean(true);
        hardNode = config.get("general", "hard_mode_nodes", true).getBoolean(true);
        ChardNode = hardNode;
        wuss = config.get("general", "wuss_mode", false).getBoolean(false);
        Cwuss = wuss;
        dialBottom = config.get("general", "wand_dial_bottom", false).getBoolean(false);
        golemDelay = config.get("general", "golem_delay", 5).getInt();
        if (golemDelay < 1) golemDelay = 1;
        golemIgnoreDelay = config.get("general", "golem_ignore_delay", 10000).getInt();
        if (golemIgnoreDelay < 1000) golemIgnoreDelay = 1000;
        golemLinkQuality = config.get("general", "golem_link_quality", 16).getInt();
        if (golemLinkQuality < 4) golemLinkQuality = 0;
        notificationDelay = config.get("general", "notification_delay", 5000).getInt();
        notificationMax = config.get("general", "notification_max", 15).getInt();
        nodeRarity = config.get("general", "node_rarity", 36).getInt();
        specialNodeRarity = config.get("general", "special_node_rarity", 18).getInt();
        if (specialNodeRarity < 3) specialNodeRarity = 3;
        showTags = config.get("general", "display_aspects", false).getBoolean(false);
        allowCheatSheet = config.get("general", "allow_cheat_sheet", true).getBoolean(false);
        CallowCheatSheet = allowCheatSheet;
        wardedStone = config.get("general", "allow_warded_stone", true).getBoolean(false);
        CwardedStone = wardedStone;
        ConfigEntities.entWizardId = config.get("general", "thaumcraft_villager_id", 190).getInt();
        ConfigEntities.entBankerId = config.get("general", "thaumcraft_banker_id", 191).getInt();
        golemChestInteract = config.get("general", "golem_chest_interact", true).getBoolean(false);
        syncPortableHoleBlacklist(config.get("general", "portablehole_blacklist", "iron_door").getString());
        blueBiome = config.get("general", "blue_magical_forest", false).getBoolean(false);
        taintFromFlux = config.get("general", "biome_taint_from_flux", true).getBoolean(true);
        taintSpreadRate = config.get("general", "biome_taint_spread", 200).getInt();
        glowyTaint = config.get("general", "glowing_taint", true).getBoolean(true);

        shieldRecharge = Math.max(500, config.get(CATEGORY_RUNIC, "runic_recharge_speed", 2000).getInt());
        shieldWait = Math.max(0, config.get(CATEGORY_RUNIC, "runic_recharge_delay", 80).getInt());
        shieldCost = Math.max(0, config.get(CATEGORY_RUNIC, "runic_cost", 50).getInt());
    }

    private static void syncPortableHoleBlacklist(String rawList) {
        ThaumcraftApi.portableHoleBlackList.clear();
        if (rawList == null || rawList.trim().isEmpty()) {
            return;
        }
        String[] names = rawList.split(",");
        for (String name : names) {
            Block block = resolveBlockName(name.trim());
            if (block != null && block != Blocks.AIR) {
                ThaumcraftApi.portableHoleBlackList.add(block);
            }
        }
    }

    private static Block resolveBlockName(String name) {
        if (name.isEmpty()) {
            return null;
        }
        try {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
            if (block == null && name.indexOf(':') < 0) {
                block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft", name));
            }
            return block;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static void initLoot() {
        // Phase 4: add loot table entries
    }

    public static void initModCompatibility() {
        foundCopperIngot = false;
        foundTinIngot = false;
        foundSilverIngot = false;
        foundLeadIngot = false;
        foundCopperOre = false;
        foundTinOre = false;
        foundSilverOre = false;
        foundLeadOre = false;
        ItemElementalAxe.oreDictLogs.clear();

        for (String ore : OreDictionary.getOreNames()) {
            if (ore == null) {
                continue;
            }
            List<ItemStack> entries = OreDictionary.getOres(ore);
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            if ("oreCopper".equals(ore)) {
                foundCopperOre = true;
                for (ItemStack is : entries) {
                    Utils.addSpecialMiningResult(is, new ItemStack(ConfigItems.itemNugget, 1, 17), 1.0F);
                }
                continue;
            }
            if ("oreTin".equals(ore)) {
                foundTinOre = true;
                for (ItemStack is : entries) {
                    Utils.addSpecialMiningResult(is, new ItemStack(ConfigItems.itemNugget, 1, 18), 1.0F);
                }
                continue;
            }
            if ("oreSilver".equals(ore)) {
                foundSilverOre = true;
                for (ItemStack is : entries) {
                    Utils.addSpecialMiningResult(is, new ItemStack(ConfigItems.itemNugget, 1, 19), 1.0F);
                }
                continue;
            }
            if ("oreLead".equals(ore)) {
                foundLeadOre = true;
                for (ItemStack is : entries) {
                    Utils.addSpecialMiningResult(is, new ItemStack(ConfigItems.itemNugget, 1, 20), 1.0F);
                }
                continue;
            }
            if ("ingotCopper".equals(ore)) {
                foundCopperIngot = true;
                continue;
            }
            if ("ingotTin".equals(ore)) {
                foundTinIngot = true;
                continue;
            }
            if ("ingotSilver".equals(ore)) {
                foundSilverIngot = true;
                continue;
            }
            if ("ingotLead".equals(ore)) {
                foundLeadIngot = true;
                continue;
            }
            if ("oreUranium".equals(ore) || "itemDropUranium".equals(ore) || "ingotUranium".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.METAL, 2).add(Aspect.POISON, 2).add(Aspect.ENERGY, 2));
                continue;
            }
            if ("ingotBrass".equals(ore) || "ingotBronze".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.METAL, 3).add(Aspect.TOOL, 1));
                continue;
            }
            if ("dustBrass".equals(ore) || "dustBronze".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.TOOL, 1));
                continue;
            }
            if ("gemRuby".equals(ore) || "gemGreenSapphire".equals(ore) || "gemSapphire".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.GREED, 2));
                continue;
            }
            if ("woodRubber".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.TREE, 3).add(Aspect.TOOL, 1));
                continue;
            }
            if ("itemRubber".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.MOTION, 2).add(Aspect.TOOL, 2));
                continue;
            }
            if ("ingotSteel".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 1));
                continue;
            }
            if ("crystalQuartz".equals(ore)) {
                registerCompatTag(entries, new AspectList().add(Aspect.CRYSTAL, 1).add(Aspect.ENERGY, 1));
                continue;
            }
            if ("woodLog".equals(ore)) {
                for (ItemStack is : entries) {
                    ItemElementalAxe.oreDictLogs.add(Arrays.<Object>asList(Item.getIdFromItem(is.getItem()), is.getItemDamage()));
                }
            }
        }
    }

    private static void registerCompatTag(List<ItemStack> stacks, AspectList aspects) {
        for (ItemStack stack : stacks) {
            ThaumcraftApi.registerObjectTag(stack, aspects);
        }
    }

    public static void registerBiomes() {
        net.minecraftforge.common.BiomeDictionary.Type frozenType =
                net.minecraftforge.common.BiomeDictionary.Type.getType("FROZEN", net.minecraftforge.common.BiomeDictionary.Type.SNOWY);

        // Register BiomeDictionary types for Thaumcraft biomes
        if (ThaumcraftWorldGenerator.biomeMagicalForest != null) {
            net.minecraftforge.common.BiomeDictionary.addTypes(ThaumcraftWorldGenerator.biomeMagicalForest,
                    net.minecraftforge.common.BiomeDictionary.Type.MAGICAL,
                    net.minecraftforge.common.BiomeDictionary.Type.FOREST);
        }
        if (ThaumcraftWorldGenerator.biomeTaint != null) {
            net.minecraftforge.common.BiomeDictionary.addTypes(ThaumcraftWorldGenerator.biomeTaint,
                    net.minecraftforge.common.BiomeDictionary.Type.MAGICAL,
                    net.minecraftforge.common.BiomeDictionary.Type.WASTELAND);
        }
        if (ThaumcraftWorldGenerator.biomeEerie != null) {
            net.minecraftforge.common.BiomeDictionary.addTypes(ThaumcraftWorldGenerator.biomeEerie,
                    net.minecraftforge.common.BiomeDictionary.Type.MAGICAL,
                    net.minecraftforge.common.BiomeDictionary.Type.SPOOKY);
        }
        if (ThaumcraftWorldGenerator.biomeEldritchLands != null) {
            net.minecraftforge.common.BiomeDictionary.addTypes(ThaumcraftWorldGenerator.biomeEldritchLands,
                    net.minecraftforge.common.BiomeDictionary.Type.MAGICAL,
                    net.minecraftforge.common.BiomeDictionary.Type.SPOOKY,
                    net.minecraftforge.common.BiomeDictionary.Type.END);
        }
        for (Biome biome : ForgeRegistries.BIOMES.getValuesCollection()) {
            if (biome != null
                    && net.minecraftforge.common.BiomeDictionary.hasType(biome, net.minecraftforge.common.BiomeDictionary.Type.SNOWY)
                    && biome.getDefaultTemperature() <= 0.0f) {
                net.minecraftforge.common.BiomeDictionary.addTypes(biome, frozenType);
            }
        }

        // Register biome info for aura/aspect/greatwood support
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.WATER, 100, Aspect.WATER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.OCEAN, 120, Aspect.WATER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.RIVER, 100, Aspect.WATER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.WET, 80, Aspect.WATER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.HOT, 100, Aspect.FIRE, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.SANDY, 100, Aspect.FIRE, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.NETHER, 120, Aspect.FIRE, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.MESA, 80, Aspect.FIRE, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.DENSE, 100, Aspect.ORDER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.SNOWY, 80, Aspect.ORDER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.COLD, 80, Aspect.ORDER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                frozenType, 100, Aspect.ORDER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.MUSHROOM, 140, Aspect.ORDER, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.CONIFEROUS, 100, Aspect.EARTH, true, 0.2f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.FOREST, 120, Aspect.EARTH, true, 1.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.SANDY, 80, Aspect.EARTH, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.BEACH, 80, Aspect.EARTH, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.SAVANNA, 80, Aspect.AIR, true, 0.2f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN, 100, Aspect.AIR, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.HILLS, 120, Aspect.AIR, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.PLAINS, 80, Aspect.AIR, true, 0.2f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.DRY, 80, Aspect.ENTROPY, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.SPARSE, 80, Aspect.ENTROPY, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.SWAMP, 120, Aspect.ENTROPY, true, 0.2f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.WASTELAND, 80, Aspect.ENTROPY, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.JUNGLE, 100, Aspect.PLANT, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.LUSH, 100, Aspect.PLANT, true, 0.5f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.MAGICAL, 100, null, true, 1.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.END, 80, Aspect.VOID, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.SPOOKY, 80, Aspect.SOUL, false, 0.0f);
        thaumcraft.common.lib.world.biomes.BiomeHandler.registerBiomeInfo(
                net.minecraftforge.common.BiomeDictionary.Type.DEAD, 50, Aspect.DEATH, false, 0.0f);
    }

    public static void initMisc() {
        // Phase 4: misc registrations
    }
}
