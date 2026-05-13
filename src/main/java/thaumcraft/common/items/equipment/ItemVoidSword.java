package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidSword extends ItemSword implements IRepairable {

    public ItemVoidSword(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return super.getIsRepairable(toRepair, repair);
    }
}
