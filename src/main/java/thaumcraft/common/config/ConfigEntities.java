package thaumcraft.common.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.common.entities.*;
import thaumcraft.common.entities.golems.*;
import thaumcraft.common.entities.monster.*;
import thaumcraft.common.entities.monster.boss.*;
import thaumcraft.common.entities.projectile.*;

import java.util.ArrayList;
import java.util.List;

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

    // Helper to build an entity entry
    @SuppressWarnings("unchecked")
    private static EntityEntry makeEntry(
            Class<? extends net.minecraft.entity.Entity> cls, String name, int trackingRange, int updateFrequency, boolean sendsVelocity,
            boolean hasEgg, int eggPrimary, int eggSecondary) {

        EntityEntryBuilder<?> builder = EntityEntryBuilder.create()
                .entity(cls)
                .id(new ResourceLocation("thaumcraft", name), id++)
                .name("thaumcraft." + name)
                .tracker(trackingRange, updateFrequency, sendsVelocity);

        if (hasEgg) {
            builder.egg(eggPrimary, eggSecondary);
        }

        return (EntityEntry) builder.build();
    }

    public static void init() {
        id = 0;
        ENTITIES.clear();

        // Base entities
        ENTITIES.add(makeEntry(EntitySpecialItem.class, "special_item", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPermanentItem.class, "permanent_item", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityFollowingItem.class, "follow_item", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityAspectOrb.class, "aspect_orb", 120, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityFallingTaint.class, "falling_taint", 64, 3, true, false, 0, 0));

        // Projectiles
        ENTITIES.add(makeEntry(EntityAlumentum.class, "alumentum", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPrimalOrb.class, "primal_orb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityFrostShard.class, "frost_shard", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityDart.class, "dart", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPrimalArrow.class, "primal_arrow", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityPechBlast.class, "pech_blast", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEldritchOrb.class, "eldritch_orb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityBottleTaint.class, "bottle_taint", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityGolemOrb.class, "golem_orb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityShockOrb.class, "shock_orb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityExplosiveOrb.class, "explosive_orb", 64, 20, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEmber.class, "ember", 64, 20, true, false, 0, 0));

        // Golems
        ENTITIES.add(makeEntry(EntityGolemBase.class, "golem", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTravelingTrunk.class, "traveling_trunk", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityGolemBobber.class, "golem_bobber", 64, 64, false, false, 0, 0));

        // Monsters - Zombies
        ENTITIES.add(makeEntry(EntityBrainyZombie.class, "brainy_zombie", 64, 3, true, true, 0x4F784F, 0x0B0B0B));
        ENTITIES.add(makeEntry(EntityGiantBrainyZombie.class, "giant_brainy_zombie", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityInhabitedZombie.class, "inhabited_zombie", 64, 3, true, false, 0, 0));

        // Monsters - Wisps and Bats
        ENTITIES.add(makeEntry(EntityWisp.class, "wisp", 64, 3, true, true, 0x4E7A9F, 0xBDD0DB));
        ENTITIES.add(makeEntry(EntityFireBat.class, "fire_bat", 64, 3, true, true, 0x4F784F, 0xCD3700));

        // Monsters - Pech
        ENTITIES.add(makeEntry(EntityPech.class, "pech", 64, 3, true, true, 0x936E4B, 0xDED3B4));

        // Monsters - Eldritch
        ENTITIES.add(makeEntry(EntityMindSpider.class, "mind_spider", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEldritchGuardian.class, "eldritch_guardian", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEldritchCrab.class, "eldritch_crab", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityWatcher.class, "watcher", 64, 3, true, false, 0, 0));

        // Monsters - Cultists
        ENTITIES.add(makeEntry(EntityCultistKnight.class, "cultist_knight", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityCultistCleric.class, "cultist_cleric", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityCultist.class, "cultist", 64, 3, true, false, 0, 0));

        // Bosses
        ENTITIES.add(makeEntry(EntityCultistLeader.class, "cultist_leader", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityCultistPortal.class, "cultist_portal", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEldritchGolem.class, "eldritch_golem", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityEldritchWarden.class, "eldritch_warden", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintacleGiant.class, "taintacle_giant", 64, 3, false, false, 0, 0));

        // Monsters - Thaumic Slime
        ENTITIES.add(makeEntry(EntityThaumicSlime.class, "thaumic_slime", 64, 3, true, false, 0, 0));

        // Monsters - Taint mobs
        ENTITIES.add(makeEntry(EntityTaintSpider.class, "taint_spider", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintacle.class, "taintacle", 64, 3, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintacleSmall.class, "taintacle_tiny", 64, 3, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintSpore.class, "taint_spore", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintSporeSwarmer.class, "taint_swarmer", 64, 20, false, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintSwarm.class, "taint_swarm", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintChicken.class, "taint_chicken", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintCow.class, "taint_cow", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintCreeper.class, "taint_creeper", 64, 3, true, true, 0x5BDD5B, 0x0B0B0B));
        ENTITIES.add(makeEntry(EntityTaintPig.class, "taint_pig", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintSheep.class, "taint_sheep", 64, 3, true, false, 0, 0));
        ENTITIES.add(makeEntry(EntityTaintVillager.class, "taint_villager", 64, 3, true, false, 0, 0));

        // Item grate
        ENTITIES.add(makeEntry(EntityItemGrate.class, "item_grate", 64, 20, true, false, 0, 0));

        // Villager professions (textures use vanilla farmer as placeholder)
        PROFESSIONS.clear();
        PROF_WIZARD = new VillagerRegistry.VillagerProfession(
                "thaumcraft:wizard",
                "minecraft:textures/entity/villager/farmer.png",
                "minecraft:textures/entity/zombie_villager/zombie_farmer.png"
        ).setRegistryName("thaumcraft:wizard");
        PROFESSIONS.add(PROF_WIZARD);

        PROF_BANKER = new VillagerRegistry.VillagerProfession(
                "thaumcraft:banker",
                "minecraft:textures/entity/villager/farmer.png",
                "minecraft:textures/entity/zombie_villager/zombie_farmer.png"
        ).setRegistryName("thaumcraft:banker");
        PROFESSIONS.add(PROF_BANKER);
    }
}
