package thaumcraft.common.items.wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;

public class WandManager {

    /**
     * Try to consume vis from any wand in the player's inventory.
     */
    public static boolean consumeVisFromInventory(EntityPlayer player, AspectList cost) {
        if (player == null || cost == null) return false;

        // Check main hand
        ItemStack mainHand = player.getHeldItemMainhand();
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) mainHand.getItem();
            if (wand.consumeAllVis(mainHand, player, cost, true, false)) {
                return true;
            }
        }

        // Check off hand
        ItemStack offHand = player.getHeldItemOffhand();
        if (!offHand.isEmpty() && offHand.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) offHand.getItem();
            if (wand.consumeAllVis(offHand, player, cost, true, false)) {
                return true;
            }
        }

        // Check hotbar and inventory
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                if (wand.consumeAllVis(stack, player, cost, true, false)) {
                    return true;
                }
            }
        }

        return false;
    }
}
