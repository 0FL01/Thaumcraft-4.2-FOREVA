package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemPickaxe;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidPickaxe extends ItemPickaxe implements IRepairable {

    public ItemVoidPickaxe(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
