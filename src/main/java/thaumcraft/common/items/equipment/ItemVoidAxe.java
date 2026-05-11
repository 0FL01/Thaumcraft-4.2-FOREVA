package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemAxe;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidAxe extends ItemAxe implements IRepairable {

    public ItemVoidAxe(ToolMaterial material) {
        super(material, 9.0f, -3.0f);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
