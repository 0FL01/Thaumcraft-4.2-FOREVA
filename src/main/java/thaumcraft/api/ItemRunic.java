package thaumcraft.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IRunicArmor;

public class ItemRunic
extends Item
implements IRunicArmor {
    int charge;

    public ItemRunic(int charge) {
        this.charge = charge;
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return this.charge;
    }
}

