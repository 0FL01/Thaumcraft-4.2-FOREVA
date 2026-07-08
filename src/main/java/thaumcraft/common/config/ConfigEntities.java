package thaumcraft.common.config;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.*;
import thaumcraft.common.entities.golems.*;
import thaumcraft.common.entities.monster.*;
import thaumcraft.common.entities.monster.boss.*;
import thaumcraft.common.entities.projectile.*;
import thaumcraft.common.lib.world.ThaumcraftVillagerTrades;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ConfigEntities {

    public static int entWizardId = 190;
    public static int entBankerId = 191;

    // Villager professions
    public static VillagerRegistry.VillagerProfession PROF_WIZARD;
    public static VillagerRegistry.VillagerProfession PROF_BANKER;
    public static List<VillagerRegistry.VillagerProfession> PROFESSIONS = new ArrayList<>();

    private static int id = 0;

    // Static list of all entity entries
    public static List<EntityEntry> ENTITIES = new ArrayList<>();
    public static final Map<String, Integer> CHAMPION_WHITELIST = new LinkedHashMap<>();

    private static boolean entitySpawnsInitialized = false;

    // Helper to build an entity entry
    @SuppressWarnings("unchecked")
    private static EntityEntry makeEntry(
            Class<? extends net.minecraft.entity.Entity> cls, String legacyToken, int trackingRange, int updateFrequency, boolean sendsVelocity,
            boolean hasEgg, int eggPrimary, int eggSecondary) {
        String path = ConfigBlocks.legacyPath(legacyToken);

        EntityEntryBuilder<?> builder = EntityEntryBuilder.create()
                .entity(cls)
                .id(new ResourceLocation(Thaumcraft.MODID, path), id++)
                .name(Thaumcraft.MODID + "." + path)
                .tracker(trackingRange, updateFrequency, sendsVelocity);

        if (hasEgg) {
            builder.egg(eggPrimary, eggSecondary);
        }

        return (EntityEntry) builder.build();
    }

    public static void init() {
        id = 0;
        ENTITIES.clear();
        CHAMPION_WHITELIST.clear();
        entitySpawnsInitialized = false;

        // Base entities
        ENTITIES.add(makeEntry(EntitySpecialItem.class, "SpecialItem", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPermanentItem.class, "PermanentItem", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityFollowingItem.class, "FollowItem", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityAspectOrb.class, "AspectOrb", 120, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityFallingTaint.class, "FallingTaint", 64, 3, true, false, 0, 0));

        // Projectiles
        ENTITIES.add(makeEntry(EntityAlumentum.class, "Alumentum", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPrimalOrb.class, "PrimalOrb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityFrostShard.class, "FrostShard", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityDart.class, "Dart", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPrimalArrow.class, "PrimalArrow", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPechBlast.class, "PechBlast", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEldritchOrb.class, "EldritchOrb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityBottleTaint.class, "BottleTaint", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityGolemOrb.class, "GolemOrb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityShockOrb.class, "ShockOrb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityExplosiveOrb.class, "ExplosiveOrb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEmber.class, "Ember", 64, 20, true, false, 0, 0));

        // Golems
        ENTITIES.add(makeEntry(EntityGolemBase.class, "Golem", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTravelingTrunk.class, "TravelingTrunk", 64, 3, true, false, 0, 0));

        // Monsters - Zombies
        ENTITIES.add(makeEntry(EntityBrainyZombie.class, "BrainyZombie", 64, 3, true, true, 0xFFC0FF, 0x008000));
        ENTITIES.add(makeEntry(EntityGiantBrainyZombie.class, "GiantBrainyZombie", 64, 3, true, true, 0xFFC0FF, 0x004000));

        // Monsters - Wisps and Bats
        ENTITIES.add(makeEntry(EntityWisp.class, "Wisp", 64, 3, true, true, 0xFFC0FF, 0xFFFFFF));
        ENTITIES.add(makeEntry(EntityFireBat.class, "Firebat", 64, 3, true, true, 0xFFC0FF, 0xC00000));

        // Monsters - Pech
        ENTITIES.add(makeEntry(EntityPech.class, "Pech", 64, 3, true, true, 0xFFC0FF, 0x400040));

        // Monsters - Eldritch
        ENTITIES.add(makeEntry(EntityMindSpider.class, "MindSpider", 64, 3, true, true, 0xAAAAAA, 0x404040));
        ENTITIES.add(makeEntry(EntityEldritchGuardian.class, "EldritchGuardian", 64, 3, true, true, 0x222222, 0x404040));
        ENTITIES.add(makeEntry(EntityEldritchWarden.class, "EldritchWarden", 64, 3, true, true, 0x552222, 0x404040));

        // Monsters - Cultists
        ENTITIES.add(makeEntry(EntityCultistKnight.class, "CultistKnight", 64, 3, true, true, 0xFF5055, 0x000080));
        ENTITIES.add(makeEntry(EntityCultistCleric.class, "CultistCleric", 64, 3, true, true, 0xFF5055, 0x800000));

        // Bosses
        ENTITIES.add(makeEntry(EntityCultistLeader.class, "CultistLeader", 64, 3, true, true, 0xFF5055, 0x505050));
        ENTITIES.add(makeEntry(EntityCultistPortal.class, "CultistPortal", 64, 20, false, true, 0xFF5055, 0xFF50FF));
        ENTITIES.add(makeEntry(EntityEldritchGolem.class, "EldritchGolem", 64, 3, true, true, 0x555555, 0x404040));
        ENTITIES.add(makeEntry(EntityEldritchCrab.class, "EldritchCrab", 64, 3, true, true, 0x555555, 0x550000));
        ENTITIES.add(makeEntry(EntityInhabitedZombie.class, "InhabitedZombie", 64, 3, true, true, 0x557755, 0x550000));

        // Monsters - Thaumic Slime
        ENTITIES.add(makeEntry(EntityThaumicSlime.class, "ThaumSlime", 64, 3, true, true, 0xFFC0FF, 0xFF80FF));

        // Monsters - Taint mobs
        ENTITIES.add(makeEntry(EntityTaintSpider.class, "TaintSpider", 64, 3, true, true, 0xFFC0FF, 0x404040));
        ENTITIES.add(makeEntry(EntityTaintacle.class, "Taintacle", 64, 3, false, true, 0xFFC0FF, 0x800080));
        ENTITIES.add(makeEntry(EntityTaintacleSmall.class, "TaintacleTiny", 64, 3, false, true, 0xFFC0FF, 0x800090));
        ENTITIES.add(makeEntry(EntityTaintSpore.class, "TaintSpore", 64, 20, false, true, 0xFFC0FF, 0x800070));
        ENTITIES.add(makeEntry(EntityTaintSporeSwarmer.class, "TaintSwarmer", 64, 20, false, true, 0xFFC0FF, 0x800060));
        ENTITIES.add(makeEntry(EntityTaintSwarm.class, "TaintSwarm", 64, 3, true, true, 0xFFC0FF, 0x800050));
        ENTITIES.add(makeEntry(EntityTaintChicken.class, "TaintedChicken", 64, 3, true, true, 0xFFC0FF, 0xC0C0C0));
        ENTITIES.add(makeEntry(EntityTaintCow.class, "TaintedCow", 64, 3, true, true, 0xFFC0FF, 0x7E3C3B));
        ENTITIES.add(makeEntry(EntityTaintCreeper.class, "TaintedCreeper", 64, 3, true, true, 0xFFC0FF, 0x00FF00));
        ENTITIES.add(makeEntry(EntityTaintPig.class, "TaintedPig", 64, 3, true, true, 0xFFC0FF, 0xEF99EF));
        ENTITIES.add(makeEntry(EntityTaintSheep.class, "TaintedSheep", 64, 3, true, true, 0xFFC0FF, 0x808080));
        ENTITIES.add(makeEntry(EntityTaintVillager.class, "TaintedVillager", 64, 3, true, true, 0xFFC0FF, 0x00FFFF));
        ENTITIES.add(makeEntry(EntityTaintacleGiant.class, "TaintacleGiant", 64, 3, false, true, 0xFFC0FF, 0x808080));

        // Item grate
        ENTITIES.add(makeEntry(EntityItemGrate.class, "SpecialItemGrate", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityGolemBobber.class, "GolemBobber", 64, 64, false, false, 0, 0));

        registerChampionWhitelistDefaults();

        // Villager professions with original Thaumcraft skins
        PROFESSIONS.clear();
        PROF_WIZARD = new VillagerRegistry.VillagerProfession(
                "thaumcraft:wizard",
                "thaumcraft:textures/models/wizard.png",
                "minecraft:textures/entity/zombie_villager/zombie_farmer.png"
        );
        PROFESSIONS.add(PROF_WIZARD);

        PROF_BANKER = new VillagerRegistry.VillagerProfession(
                "thaumcraft:banker",
                "thaumcraft:textures/models/moneychanger.png",
                "minecraft:textures/entity/zombie_villager/zombie_farmer.png"
        );
        PROFESSIONS.add(PROF_BANKER);

        // Initialize villager careers with level-gated trade lists.
        VillagerRegistry.VillagerCareer wizardCareer = new VillagerRegistry.VillagerCareer(PROF_WIZARD, "wizard");
        registerCareerTrades(wizardCareer, ThaumcraftVillagerTrades.WIZARD_TRADE_LEVELS);

        VillagerRegistry.VillagerCareer bankerCareer = new VillagerRegistry.VillagerCareer(PROF_BANKER, "banker");
        registerCareerTrades(bankerCareer, ThaumcraftVillagerTrades.BANKER_TRADE_LEVELS);
    }

    private static void registerCareerTrades(VillagerRegistry.VillagerCareer career, EntityVillager.ITradeList[][] tradeLevels) {
        for (int i = 0; i < tradeLevels.length; i++) {
            EntityVillager.ITradeList[] trades = tradeLevels[i];
            if (trades != null && trades.length > 0) {
                career.addTrade(i + 1, trades);
            }
        }
    }

    public static void initEntitySpawns() {
        if (entitySpawnsInitialized) {
            return;
        }
        entitySpawnsInitialized = true;
        if (Config.spawnAngryZombie) {
            addSpawn(EntityBrainyZombie.class, 10, 1, 1, EnumCreatureType.MONSTER,
                    biome -> biome != ThaumcraftWorldGenerator.biomeEldritchLands
                            && !biome.getSpawnableList(EnumCreatureType.MONSTER).isEmpty());
        }
        if (Config.spawnPech) {
            addSpawn(EntityPech.class, 10, 1, 1, EnumCreatureType.MONSTER,
                    biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL)
                            && !biome.getSpawnableList(EnumCreatureType.MONSTER).isEmpty());
        }
        if (Config.spawnFireBat) {
            addSpawn(EntityFireBat.class, 10, 1, 2, EnumCreatureType.MONSTER,
                    biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER));
        }
        if (isHalloween()) {
            addSpawn(EntityFireBat.class, 5, 1, 2, EnumCreatureType.MONSTER,
                    biome -> !biome.getSpawnableList(EnumCreatureType.MONSTER).isEmpty());
        }
        if (Config.spawnWisp) {
            addSpawn(EntityWisp.class, 5, 1, 1, EnumCreatureType.MONSTER,
                    biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER));
        }
    }

    public static void processIMC(List<FMLInterModComms.IMCMessage> messages) {
        for (FMLInterModComms.IMCMessage message : messages) {
            if ("championWhiteList".equals(message.key) && message.isStringMessage()) {
                registerChampionWhitelistToken(message.getStringValue());
            }
        }
    }

    private static void addSpawn(Class<? extends EntityLiving> entityClass, int weight, int min, int max,
                                 EnumCreatureType creatureType, Predicate<Biome> filter) {
        for (Biome biome : ForgeRegistries.BIOMES.getValuesCollection()) {
            if (biome != null && filter.test(biome)) {
                List<Biome.SpawnListEntry> spawns = biome.getSpawnableList(creatureType);
                if (!hasSpawnEntry(spawns, entityClass)) {
                    spawns.add(new Biome.SpawnListEntry(entityClass, weight, min, max));
                }
            }
        }
    }

    private static boolean hasSpawnEntry(List<Biome.SpawnListEntry> spawns, Class<? extends EntityLiving> entityClass) {
        for (Biome.SpawnListEntry entry : spawns) {
            if (entry.entityClass == entityClass) {
                return true;
            }
        }
        return false;
    }

    private static boolean isHalloween() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) == 31;
    }

    private static void registerChampionWhitelistDefaults() {
        registerChampionWhitelist("minecraft:zombie", 0);
        registerChampionWhitelist("minecraft:spider", 0);
        registerChampionWhitelist("minecraft:blaze", 0);
        registerChampionWhitelist("minecraft:enderman", 0);
        registerChampionWhitelist("minecraft:skeleton", 0);
        registerChampionWhitelist("minecraft:witch", 1);
        registerChampionWhitelist("thaumcraft:" + ConfigBlocks.legacyPath("EldritchCrab"), 0);
        registerChampionWhitelist("thaumcraft:" + ConfigBlocks.legacyPath("Taintacle"), 2);
        registerChampionWhitelist("thaumcraft:" + ConfigBlocks.legacyPath("Wisp"), 1);
        registerChampionWhitelist("thaumcraft:" + ConfigBlocks.legacyPath("InhabitedZombie"), 3);
        registerChampionWhitelist(EntityCultist.class.getName(), 1);
        registerChampionWhitelist(EntityWatcher.class.getName(), 2);
        registerChampionWhitelist(EntityPech.class.getName(), 1);
        registerChampionWhitelist(EntityThaumcraftBoss.class.getName(), 200);
    }

    public static void registerChampionWhitelist(String entityName, int tier) {
        if (entityName != null && !entityName.trim().isEmpty()) {
            CHAMPION_WHITELIST.put(entityName.trim(), tier);
        }
    }

    private static void registerChampionWhitelistToken(String value) {
        if (value == null) {
            return;
        }
        int split = value.lastIndexOf(':');
        if (split <= 0 || split >= value.length() - 1) {
            return;
        }
        try {
            registerChampionWhitelist(value.substring(0, split), Integer.parseInt(value.substring(split + 1)));
        } catch (NumberFormatException ignored) {
        }
    }
}
