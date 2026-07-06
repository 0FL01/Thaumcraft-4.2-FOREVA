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
    public static Item crystalEssence;
    public static Item curio;
    public static Item nuggets;

    private ItemsTC() {
    }

    public static void init() {
        amber = ConfigItems.itemResource;
        brain = ConfigItems.itemZombieBrain;
        celestialNotes = ConfigItems.itemResearchNotes;
        chunks = ConfigItems.itemResource;
        crystalEssence = ConfigItems.itemCrystalEssence;
        curio = ConfigItems.itemEldritchObject;
        nuggets = ConfigItems.itemNugget;
    }
}
