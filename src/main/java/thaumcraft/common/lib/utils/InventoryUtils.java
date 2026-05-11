
package thaumcraft.common.lib.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static boolean isInventoryEmpty(IInventory inv) {
        if (inv == null) return true;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }
    
    public static boolean addItemToInventory(IInventory inv, ItemStack stack) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).isEmpty()) {
                inv.setInventorySlotContents(i, stack.copy());
                return true;
            }
        }
        return false;
    }
}
