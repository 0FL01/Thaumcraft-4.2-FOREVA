package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemHoe;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumiumHoe extends ItemHoe implements IRepairable {

    public ItemThaumiumHoe(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
