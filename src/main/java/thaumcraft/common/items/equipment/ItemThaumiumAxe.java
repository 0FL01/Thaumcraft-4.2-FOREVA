package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemAxe;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumiumAxe extends ItemAxe implements IRepairable {

    public ItemThaumiumAxe(ToolMaterial material) {
        super(material, 8.0f, -3.2f);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
