package thaumcraft.common.entities.golems;

public class GolemHelper extends java.lang.Object {
    public static final int TIMEOUT = 200;

    public GolemHelper() {}

    public static boolean isMarkerWithinDistance(thaumcraft.common.entities.golems.EntityGolemBase golem, thaumcraft.common.entities.golems.Marker marker, double distance) {
        return golem.getDistanceSq(marker.x + 0.5, marker.y + 0.5, marker.z + 0.5) < distance * distance;
    }

    public static boolean hasInventorySpace(thaumcraft.common.entities.golems.EntityGolemBase golem, net.minecraft.item.ItemStack stack) {
        if (golem.inventory == null) return false;
        for (int a = 0; a < golem.inventory.getSizeInventory(); a++) {
            net.minecraft.item.ItemStack s = golem.inventory.getStackInSlot(a);
            if (s.isEmpty()) return true;
            if (s.isItemEqual(stack) && net.minecraft.item.ItemStack.areItemStackTagsEqual(s, stack) && s.getCount() < s.getMaxStackSize()) return true;
        }
        return false;
    }

    public static boolean putStackInInventory(thaumcraft.common.entities.golems.EntityGolemBase golem, net.minecraft.item.ItemStack stack) {
        for (int a = 0; a < golem.inventory.getSizeInventory(); a++) {
            net.minecraft.item.ItemStack s = golem.inventory.getStackInSlot(a);
            if (s.isEmpty()) {
                golem.inventory.setInventorySlotContents(a, stack.copy());
                return true;
            }
            if (s.isItemEqual(stack) && net.minecraft.item.ItemStack.areItemStackTagsEqual(s, stack) && s.getCount() < s.getMaxStackSize()) {
                s.grow(stack.getCount());
                return true;
            }
        }
        return false;
    }

    public static boolean isStackInInventory(thaumcraft.common.entities.golems.EntityGolemBase golem, net.minecraft.item.ItemStack stack) {
        for (int a = 0; a < golem.inventory.getSizeInventory(); a++) {
            net.minecraft.item.ItemStack s = golem.inventory.getStackInSlot(a);
            if (!s.isEmpty() && s.isItemEqual(stack)) return true;
        }
        return false;
    }
}
