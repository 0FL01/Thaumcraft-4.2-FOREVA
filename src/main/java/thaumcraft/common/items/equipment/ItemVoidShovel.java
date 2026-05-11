package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemSpade;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidShovel extends ItemSpade implements IRepairable {

    public ItemVoidShovel(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
