package thaumcraft.common.config;

import java.io.File;
import java.util.ArrayList;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import thaumcraft.api.aspects.Aspect;

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
        airyMaterial = new Material(MapColor.AIR);
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
        // Potions registered via RegistryEvent in 1.12.2 — stub for now
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
        golemChestInteract = config.get("general", "golem_chest_interact", true).getBoolean(false);
        blueBiome = config.get("general", "blue_magical_forest", false).getBoolean(false);
        taintFromFlux = config.get("general", "biome_taint_from_flux", true).getBoolean(true);
        taintSpreadRate = config.get("general", "biome_taint_spread", 200).getInt();
        glowyTaint = config.get("general", "glowing_taint", true).getBoolean(true);

        shieldRecharge = Math.max(500, config.get(CATEGORY_RUNIC, "runic_recharge_speed", 2000).getInt());
        shieldWait = Math.max(0, config.get(CATEGORY_RUNIC, "runic_recharge_delay", 80).getInt());
        shieldCost = Math.max(0, config.get(CATEGORY_RUNIC, "runic_cost", 50).getInt());
    }

    public static void initLoot() {
        // Phase 4: add loot table entries
    }

    public static void initModCompatibility() {
        // Phase 4: ore dict compatibility
    }

    public static void registerBiomes() {
        // Phase 6: register biome dictionary types
    }

    public static void initMisc() {
        // Phase 4: misc registrations
    }
}
