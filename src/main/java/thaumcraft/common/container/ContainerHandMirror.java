package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.items.relics.ItemHandMirror;

public class ContainerHandMirror extends Container {
    private final EntityPlayer player;
    private final World worldObj;
    private final ItemStack mirror;
    private final int mirrorSlot;
    private final InventoryBasic input;
    private boolean changing;

    public ContainerHandMirror() {
        this(null, null, 0, 0, 0);
    }

    public ContainerHandMirror(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        this.player = playerInventory != null ? playerInventory.player : null;
        this.worldObj = world;
        this.mirrorSlot = findMirrorSlot(playerInventory);
        this.mirror = findMirrorStack(playerInventory, this.mirrorSlot);
        this.input = new MirrorInventory(this);
        this.addSlotToContainer(new Slot(this.input, 0, 80, 24) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return !stack.isEmpty() && !(stack.getItem() instanceof ItemHandMirror);
            }
        });

        if (playerInventory != null) {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    int index = col + row * 9 + 9;
                    addPlayerSlot(playerInventory, index, 8 + col * 18, 84 + row * 18);
                }
            }
            for (int col = 0; col < 9; ++col) {
                addPlayerSlot(playerInventory, col, 8 + col * 18, 142);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn != null && playerIn == this.player && !playerIn.isDead && playerIn.world == this.worldObj;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack stack = slot.getStack();
        original = stack.copy();

        if (index == 0) {
            if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.getItem() instanceof ItemHandMirror || !this.mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return original;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        ItemStack stack = this.input.getStackInSlot(0);
        if (!stack.isEmpty()) {
            this.input.setInventorySlotContents(0, ItemStack.EMPTY);
            if (!playerIn.inventory.addItemStackToInventory(stack)) {
                playerIn.dropItem(stack, false);
            }
        }
    }

    private void addPlayerSlot(InventoryPlayer inventory, int index, int x, int y) {
        if (index == this.mirrorSlot) {
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

    private void onInputChanged() {
        if (this.changing || this.worldObj == null || this.worldObj.isRemote || this.player == null) return;
        ItemStack stack = this.input.getStackInSlot(0);
        if (stack.isEmpty()) return;
        if (ItemHandMirror.transport(this.mirror, stack, this.player, this.worldObj)) {
            this.changing = true;
            this.input.setInventorySlotContents(0, ItemStack.EMPTY);
            this.changing = false;
            this.detectAndSendChanges();
        }
    }

    private static int findMirrorSlot(InventoryPlayer inventory) {
        if (inventory == null) return -1;
        ItemStack held = inventory.player.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof ItemHandMirror) return inventory.currentItem;
        for (int i = 0; i < inventory.mainInventory.size(); ++i) {
            ItemStack stack = inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemHandMirror) return i;
        }
        return -1;
    }

    private static ItemStack findMirrorStack(InventoryPlayer inventory, int slot) {
        if (inventory == null) return ItemStack.EMPTY;
        ItemStack main = inventory.player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() instanceof ItemHandMirror) return main;
        ItemStack off = inventory.player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ItemHandMirror) return off;
        if (slot >= 0 && slot < inventory.mainInventory.size()) {
            return inventory.mainInventory.get(slot);
        }
        return ItemStack.EMPTY;
    }

    private static final class MirrorInventory extends InventoryBasic {
        private final ContainerHandMirror container;

        private MirrorInventory(ContainerHandMirror container) {
            super("container.handmirror", false, 1);
            this.container = container;
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {
            super.markDirty();
            this.container.onInputChanged();
        }
    }
}
