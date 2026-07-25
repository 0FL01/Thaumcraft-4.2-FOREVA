package thaumcraft.api.items;

import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;

/**
 * Minimal Thaumcraft 6 item compatibility surface for 1.12 addons.
 *
 * <p>Where TC4 keeps variants as metadata on {@code itemResource}, these fields
 * expose the closest registered Item instance. Addons that only need a stable,
 * non-null TC6 item reference can link without crashing.</p>
 */
public final class ItemsTC {

    public static Item amber;
    public static Item brain;
    public static Item celestialNotes;
    public static Item chunks;
    public static Item crimsonBlade;
    public static Item crimsonBoots;
    public static Item crimsonPlateChest;
    public static Item crimsonPlateHelm;
    public static Item crimsonPlateLegs;
    public static Item crimsonPraetorChest;
    public static Item crimsonPraetorHelm;
    public static Item crimsonPraetorLegs;
    public static Item crimsonRobeChest;
    public static Item crimsonRobeHelm;
    public static Item crimsonRobeLegs;
    public static Item crystalEssence;
    public static Item curio;
    public static Item ingots;
    public static Item mechanismComplex;
    public static Item mechanismSimple;
    public static Item nuggets;
    public static Item lootBag;
    public static Item plate;
    public static Item primordialPearl;
    public static Item thaumiumSword;
    public static Item voidSeed;
    public static Item voidSword;

    private ItemsTC() {
    }

    public static void init() {
        amber = ConfigItems.itemResource;
        brain = ConfigItems.itemZombieBrain;
        celestialNotes = ConfigItems.itemResearchNotes;
        chunks = ConfigItems.itemResource;
        crimsonBlade = ConfigItems.itemCrimsonSword;
        crimsonBoots = ConfigItems.itemCultistBoots;
        crimsonPlateChest = ConfigItems.itemChestCultistPlate;
        crimsonPlateHelm = ConfigItems.itemHelmetCultistPlate;
        crimsonPlateLegs = ConfigItems.itemLegsCultistPlate;
        crimsonPraetorChest = ConfigItems.itemChestCultistLeader;
        crimsonPraetorHelm = ConfigItems.itemHelmetCultistLeader;
        crimsonPraetorLegs = ConfigItems.itemLegsCultistLeader;
        crimsonRobeChest = ConfigItems.itemChestCultistRobe;
        crimsonRobeHelm = ConfigItems.itemHelmetCultistRobe;
        crimsonRobeLegs = ConfigItems.itemLegsCultistRobe;
        crystalEssence = ConfigItems.itemCrystalEssence;
        curio = ConfigItems.itemEldritchObject;
        ingots = ConfigItems.itemResource;
        mechanismComplex = ConfigItems.itemResource;
        mechanismSimple = ConfigItems.itemResource;
        nuggets = ConfigItems.itemNugget;
        lootBag = ConfigItems.itemLootBag;
        plate = ConfigItems.itemResource;
        primordialPearl = ConfigItems.itemEldritchObject;
        thaumiumSword = ConfigItems.itemSwordThaumium;
        voidSeed = ConfigItems.itemResource;
        voidSword = ConfigItems.itemSwordVoid;
    }
}
