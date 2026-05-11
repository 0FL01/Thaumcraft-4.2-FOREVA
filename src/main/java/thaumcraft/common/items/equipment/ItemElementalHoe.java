package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemHoe;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemElementalHoe extends ItemHoe implements IRepairable {

    public ItemElementalHoe(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
