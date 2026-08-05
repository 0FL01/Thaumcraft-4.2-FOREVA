package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemFocusPouch;

public class ContainerFocusPouch extends Container {
    private final EntityPlayer player;
    private final World worldObj;
    private final EnumHand hand;
    private final ItemStack pouchStack;
    private final ItemFocusPouch pouchItem;
    private final InventoryFocusPouch pouchInventory;
    private final int pouchPlayerSlot;
    private final int blockedContainerSlot;

    public ContainerFocusPouch() {
        this(null, null, null);
    }

    public ContainerFocusPouch(InventoryPlayer playerInventory, World world, EnumHand hand) {
        this.player = playerInventory != null ? playerInventory.player : null;
        this.worldObj = world;
        this.hand = hand;
        this.pouchPlayerSlot = playerInventory != null && hand == EnumHand.MAIN_HAND
                ? playerInventory.currentItem : -1;
        this.blockedContainerSlot = toContainerSlot(this.pouchPlayerSlot);
        this.pouchStack = this.player != null && hand != null ? this.player.getHeldItem(hand) : ItemStack.EMPTY;
        this.pouchItem = !this.pouchStack.isEmpty() && this.pouchStack.getItem() instanceof ItemFocusPouch
                ? (ItemFocusPouch) this.pouchStack.getItem() : null;
        this.pouchInventory = new InventoryFocusPouch();
        if (this.pouchItem != null) {
            this.pouchInventory.load(this.pouchItem.getInventory(this.pouchStack));
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
        return this.hasValidBinding(playerIn);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.hasValidBinding(playerIn)) this.persistInventory();
    }

    private void persistInventory() {
        if (this.worldObj == null || this.worldObj.isRemote || !this.hasValidBinding(this.player)) return;
        ItemStack[] stacks = new ItemStack[18];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = this.pouchInventory.getStackInSlot(i);
        }
        this.pouchItem.setInventory(this.pouchStack, stacks);
        this.player.inventory.markDirty();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (!this.hasValidBinding(playerIn) || index == this.blockedContainerSlot) return ItemStack.EMPTY;
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
        if (clickTypeIn == ClickType.SWAP || !this.hasValidBinding(player)
                || slotId == this.blockedContainerSlot) return ItemStack.EMPTY;
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private boolean hasValidBinding(EntityPlayer playerIn) {
        if (playerIn == null || playerIn != this.player || playerIn.isDead || this.worldObj == null
                || playerIn.world != this.worldObj || this.hand == null || this.pouchItem == null
                || this.pouchStack.isEmpty() || !(this.pouchStack.getItem() instanceof ItemFocusPouch)) {
            return false;
        }
        if (this.hand == EnumHand.MAIN_HAND
                && (playerIn.inventory.currentItem != this.pouchPlayerSlot
                || playerIn.inventory.mainInventory.get(this.pouchPlayerSlot) != this.pouchStack)) {
            return false;
        }
        return playerIn.getHeldItem(this.hand) == this.pouchStack;
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

    private static int toContainerSlot(int playerSlot) {
        if (playerSlot >= 0 && playerSlot < 9) return 45 + playerSlot;
        if (playerSlot >= 9 && playerSlot < 36) return 18 + (playerSlot - 9);
        return -1;
    }

    private final class InventoryFocusPouch extends InventoryBasic {
        private boolean loading;

        InventoryFocusPouch() {
            super("container.focus_pouch", false, 18);
        }

        void load(ItemStack[] stacks) {
            this.loading = true;
            try {
                for (int i = 0; i < this.getSizeInventory(); i++) {
                    this.setInventorySlotContents(i, i < stacks.length ? stacks[i] : ItemStack.EMPTY);
                }
            } finally {
                this.loading = false;
            }
        }

        @Override
        public void markDirty() {
            if (this.loading) return;
            super.markDirty();
            ContainerFocusPouch.this.persistInventory();
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            ItemStack removed = super.removeStackFromSlot(index);
            if (!removed.isEmpty()) this.markDirty();
            return removed;
        }

        @Override
        public void clear() {
            boolean wasEmpty = this.isEmpty();
            super.clear();
            if (!wasEmpty) this.markDirty();
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
