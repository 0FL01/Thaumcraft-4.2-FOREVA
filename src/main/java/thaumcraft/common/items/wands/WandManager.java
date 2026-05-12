package thaumcraft.common.items.wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.IArchitect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.lib.TCSounds;

import java.util.TreeMap;

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

    public static void changeFocus(ItemStack wandStack, World world, EntityPlayer player, String focusKey) {
        if (wandStack == null || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting) || player == null) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack current = wand.getFocusItem(wandStack);
        if (focusKey == null) focusKey = "";

        TreeMap<String, Integer> foci = new TreeMap<>();
        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusBasic) {
                foci.put(((ItemFocusBasic) stack.getItem()).getSortingHelper(stack), slot);
            }
        }

        if ("REMOVE".equals(focusKey) || foci.isEmpty()) {
            if (!current.isEmpty() && player.inventory.addItemStackToInventory(current.copy())) {
                wand.setFocus(wandStack, ItemStack.EMPTY);
                playFocusSound(world, player, 0.9F);
            }
            return;
        }

        String selectedKey = focusKey;
        if (!foci.containsKey(selectedKey)) {
            selectedKey = foci.higherKey(selectedKey);
        }
        if (selectedKey == null || !foci.containsKey(selectedKey)) {
            selectedKey = foci.firstKey();
        }

        int focusSlot = foci.get(selectedKey);
        ItemStack selected = player.inventory.mainInventory.get(focusSlot);
        if (selected.isEmpty() || !(selected.getItem() instanceof ItemFocusBasic)) return;

        player.inventory.mainInventory.set(focusSlot, current.isEmpty() ? ItemStack.EMPTY : current.copy());
        wand.setFocus(wandStack, selected.copy());
        player.inventory.markDirty();
        playFocusSound(world, player, 1.0F);
    }

    public static void toggleMisc(ItemStack wandStack, World world, EntityPlayer player) {
        if (wandStack == null || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemFocusBasic focus = wand.getFocus(wandStack);
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (!(focus instanceof IArchitect) || focusStack.isEmpty() || !focus.isUpgradedWith(focusStack, FocusUpgradeType.architect)) return;

        if (player != null && player.isSneaking()) {
            int dim = getAreaDim(wandStack) + 1;
            if (dim > 3) dim = 0;
            setAreaDim(wandStack, dim);
        } else {
            int max = focus.getMaxAreaSize(focusStack);
            int dim = getAreaDim(wandStack);
            int x = getAreaX(wandStack, max);
            int y = getAreaY(wandStack, max);
            int z = getAreaZ(wandStack, max);
            if (dim == 0) {
                x++;
                y++;
                z++;
            } else if (dim == 1) {
                x++;
            } else if (dim == 2) {
                z++;
            } else if (dim == 3) {
                y++;
            }
            setAreaX(wandStack, x > max ? 0 : x);
            setAreaY(wandStack, y > max ? 0 : y);
            setAreaZ(wandStack, z > max ? 0 : z);
        }
        if (world != null && player != null) {
            world.playSound(null, player.getPosition(), TCSounds.CAMERATICKS, SoundCategory.PLAYERS, 0.3F, 1.0F);
        }
    }

    public static int getAreaDim(ItemStack stack) {
        return stack != null && stack.hasTagCompound() ? stack.getTagCompound().getInteger("aread") : 0;
    }

    public static int getAreaX(ItemStack stack, int max) {
        return getClampedArea(stack, "areax", max);
    }

    public static int getAreaY(ItemStack stack, int max) {
        return getClampedArea(stack, "areay", max);
    }

    public static int getAreaZ(ItemStack stack, int max) {
        return getClampedArea(stack, "areaz", max);
    }

    public static void setAreaX(ItemStack stack, int area) {
        ItemWandCasting.ensureTag(stack).setInteger("areax", area);
    }

    public static void setAreaY(ItemStack stack, int area) {
        ItemWandCasting.ensureTag(stack).setInteger("areay", area);
    }

    public static void setAreaZ(ItemStack stack, int area) {
        ItemWandCasting.ensureTag(stack).setInteger("areaz", area);
    }

    public static void setAreaDim(ItemStack stack, int dim) {
        ItemWandCasting.ensureTag(stack).setInteger("aread", dim);
    }

    private static int getClampedArea(ItemStack stack, String key, int max) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
            return Math.min(max, Math.max(0, stack.getTagCompound().getInteger(key)));
        }
        return max;
    }

    private static void playFocusSound(World world, EntityPlayer player, float pitch) {
        if (world != null && player != null) {
            world.playSound(null, player.getPosition(), TCSounds.CAMERATICKS, SoundCategory.PLAYERS, 0.3F, pitch);
        }
    }
}
