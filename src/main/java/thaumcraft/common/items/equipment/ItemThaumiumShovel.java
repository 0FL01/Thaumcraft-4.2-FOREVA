package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemSpade;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumiumShovel extends ItemSpade implements IRepairable {

    public ItemThaumiumShovel(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
