package thaumcraft.api;

import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;

/**
 * Thaumcraft 6 material names projected onto the canonical Thaumcraft 4
 * materials used by this port.
 */
public class ThaumcraftMaterials {

    public static Item.ToolMaterial TOOLMAT_THAUMIUM;
    public static Item.ToolMaterial TOOLMAT_VOID;

    public static void init() {
        TOOLMAT_THAUMIUM = ConfigItems.TOOLMAT_THAUMIUM;
        TOOLMAT_VOID = ConfigItems.TOOLMAT_VOID;
    }
}
