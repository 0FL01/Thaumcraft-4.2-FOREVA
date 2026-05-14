package thaumcraft.common.items.armor;

import net.minecraft.item.ItemArmor;

public class RecipesVoidRobeArmorDyes extends RecipesRobeArmorDyes {

    @Override
    protected boolean isTargetArmor(ItemArmor armor) {
        return armor instanceof ItemVoidRobeArmor;
    }
}
