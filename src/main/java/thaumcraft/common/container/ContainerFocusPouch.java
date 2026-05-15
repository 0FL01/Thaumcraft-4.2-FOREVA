package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemFocusPouch;

public class ContainerFocusPouch extends Container {
    private final EntityPlayer player;
    private final World worldObj;
    private final ItemStack pouchStack;
    private final ItemFocusPouch pouchItem;
    private final InventoryFocusPouch pouchInventory;
    private final int pouchPlayerSlot;
    private final int blockedContainerSlot;

    public ContainerFocusPouch() {
        this(null, null, 0, 0, 0);
    }

    public ContainerFocusPouch(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        this.player = playerInventory != null ? playerInventory.player : null;
        this.worldObj = world;
        this.pouchPlayerSlot = findPouchSlot(playerInventory);
        this.blockedContainerSlot = toContainerSlot(this.pouchPlayerSlot);
        this.pouchStack = findPouch(playerInventory, this.pouchPlayerSlot);
        this.pouchItem = !this.pouchStack.isEmpty() && this.pouchStack.getItem() instanceof ItemFocusPouch
                ? (ItemFocusPouch) this.pouchStack.getItem() : null;
        this.pouchInventory = new InventoryFocusPouch();
        if (this.pouchItem != null) {
            ItemStack[] stacks = this.pouchItem.getInventory(this.pouchStack);
            for (int i = 0; i < stacks.length; i++) {
                this.pouchInventory.setInventorySlotContents(i, stacks[i]);
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 6; col++) {
                this.addSlotToContainer(new SlotLimitedByClass(ItemFocusBasic.class, this.pouchInventory, col + row * 6, 37 + col * 18, 51 + row * 18, 1));
            }
        }

        if (playerInventory != null) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addPlayerSlot(playerInventory, col + row * 9 + 9, 8 + col * 18, 151 + row * 18);
                }
            }
            for (int col = 0; col < 9; col++) {
                this.addPlayerSlot(playerInventory, col, 8 + col * 18, 209);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn != null && playerIn == this.player && !playerIn.isDead && playerIn.world == this.worldObj
                && this.pouchItem != null && !this.pouchStack.isEmpty();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.pouchItem == null || this.pouchStack.isEmpty()) return;
        ItemStack[] stacks = new ItemStack[18];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = this.pouchInventory.getStackInSlot(i);
        }
        this.pouchItem.setInventory(this.pouchStack, stacks);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (index == this.blockedContainerSlot) return ItemStack.EMPTY;
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index < 18) {
                if (!this.mergeItemStack(stack, 18, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            } else if (stack.getItem() instanceof ItemFocusBasic) {
                if (!this.mergeItemStack(stack, 0, 18, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        }
        return copy;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId == this.blockedContainerSlot) return ItemStack.EMPTY;
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private void addPlayerSlot(InventoryPlayer inventory, int index, int x, int y) {
        if (index == this.pouchPlayerSlot) {
            this.addSlotToContainer(new Slot(inventory, index, x, y) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    return false;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }
            });
        } else {
            this.addSlotToContainer(new Slot(inventory, index, x, y));
        }
    }

    private static ItemStack findPouch(InventoryPlayer inventory, int slot) {
        if (inventory == null) return ItemStack.EMPTY;
        ItemStack main = inventory.player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() instanceof ItemFocusPouch) return main;
        ItemStack off = inventory.player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ItemFocusPouch) return off;
        if (slot >= 0 && slot < inventory.mainInventory.size()) {
            ItemStack stack = inventory.mainInventory.get(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusPouch) return stack;
        }
        ItemStack current = inventory.getCurrentItem();
        if (!current.isEmpty() && current.getItem() instanceof ItemFocusPouch) return current;
        return ItemStack.EMPTY;
    }

    private static int findPouchSlot(InventoryPlayer inventory) {
        if (inventory == null) return -1;
        ItemStack held = inventory.player.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof ItemFocusPouch) return inventory.currentItem;
        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ItemStack stack = inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusPouch) return i;
        }
        return -1;
    }

    private static int toContainerSlot(int playerSlot) {
        if (playerSlot >= 0 && playerSlot < 9) return 45 + playerSlot;
        if (playerSlot >= 9 && playerSlot < 36) return 18 + (playerSlot - 9);
        return -1;
    }

    private static final class InventoryFocusPouch extends InventoryBasic {
        InventoryFocusPouch() {
            super("container.focus_pouch", false, 18);
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() instanceof ItemFocusBasic;
        }
    }
}
