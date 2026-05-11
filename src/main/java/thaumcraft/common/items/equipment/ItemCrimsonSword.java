package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemCrimsonSword extends ItemSword implements IRepairable {

    public ItemCrimsonSword(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }
}
