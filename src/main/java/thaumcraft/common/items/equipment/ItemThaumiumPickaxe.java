package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemPickaxe;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumiumPickaxe extends ItemPickaxe implements IRepairable {

    public ItemThaumiumPickaxe(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
