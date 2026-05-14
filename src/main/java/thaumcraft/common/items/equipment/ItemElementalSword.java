package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemElementalSword extends ItemSword implements IRepairable {

    public ItemElementalSword(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return super.getIsRepairable(toRepair, repair);
    }
}
