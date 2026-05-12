package thaumcraft.common.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Hover mixin for Boots of the Traveler — provides step assist and jump boost.
 */
public class Hover {

    private static final Map<Integer, Boolean> HOVERING = new HashMap<>();

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

    public static void setHover(int playerId, boolean hover) {
        HOVERING.put(playerId, hover);
    }

    public static boolean getHover(int playerId) {
        Boolean hover = HOVERING.get(playerId);
        return hover != null && hover;
    }

    public static boolean getHover(EntityPlayer player) {
        return player != null && getHover(player.getEntityId());
    }
}
