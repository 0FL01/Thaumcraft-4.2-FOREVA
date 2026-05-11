package thaumcraft.common.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Hover mixin for Boots of the Traveler — provides step assist and jump boost.
 */
public class Hover {

    public static void doHover(ItemStack stack, EntityPlayer player, World world, int slot) {
        if (player.isSneaking()) return;
        // Provides step assist (auto step-up) and slight jump boost
        if (player.onGround && !player.isInWater()) {
            player.stepHeight = 1.0f;
        }
    }

    public static void resetHover(EntityPlayer player) {
        player.stepHeight = 0.5f;
    }
}
