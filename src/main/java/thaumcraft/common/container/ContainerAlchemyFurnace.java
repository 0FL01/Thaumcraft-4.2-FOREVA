package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.tiles.TileAlchemyFurnace;

public class ContainerAlchemyFurnace extends Container {
    private final TileAlchemyFurnace furnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;
    private int lastVis;
    private int lastSmeltTime;

    public ContainerAlchemyFurnace() {
        this(null, null);
    }

    public ContainerAlchemyFurnace(InventoryPlayer playerInventory, TileAlchemyFurnace furnace) {
        this.furnace = furnace;
        if (furnace != null) {
            this.addSlotToContainer(new SlotLimitedHasAspects(furnace, 0, 80, 8));
            this.addSlotToContainer(new Slot(furnace, 1, 80, 48));
        }
        if (playerInventory != null) {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
                }
            }
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return isUsableTile(playerIn, this.furnace);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        if (this.furnace == null) return;
        listener.sendWindowProperty(this, 0, this.furnace.furnaceCookTime);
        listener.sendWindowProperty(this, 1, this.furnace.furnaceBurnTime);
        listener.sendWindowProperty(this, 2, this.furnace.currentItemBurnTime);
        listener.sendWindowProperty(this, 3, this.furnace.vis);
        listener.sendWindowProperty(this, 4, this.furnace.smeltTime);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (this.furnace == null) return;
        for (IContainerListener listener : this.listeners) {
            if (this.lastCookTime != this.furnace.furnaceCookTime) {
                listener.sendWindowProperty(this, 0, this.furnace.furnaceCookTime);
            }
            if (this.lastBurnTime != this.furnace.furnaceBurnTime) {
                listener.sendWindowProperty(this, 1, this.furnace.furnaceBurnTime);
            }
            if (this.lastItemBurnTime != this.furnace.currentItemBurnTime) {
                listener.sendWindowProperty(this, 2, this.furnace.currentItemBurnTime);
            }
            if (this.lastVis != this.furnace.vis) {
                listener.sendWindowProperty(this, 3, this.furnace.vis);
            }
            if (this.lastSmeltTime != this.furnace.smeltTime) {
                listener.sendWindowProperty(this, 4, this.furnace.smeltTime);
            }
        }
        this.lastCookTime = this.furnace.furnaceCookTime;
        this.lastBurnTime = this.furnace.furnaceBurnTime;
        this.lastItemBurnTime = this.furnace.currentItemBurnTime;
        this.lastVis = this.furnace.vis;
        this.lastSmeltTime = this.furnace.smeltTime;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (this.furnace == null) return;
        switch (id) {
            case 0: this.furnace.furnaceCookTime = data; break;
            case 1: this.furnace.furnaceBurnTime = data; break;
            case 2: this.furnace.currentItemBurnTime = data; break;
            case 3: this.furnace.vis = data; break;
            case 4: this.furnace.smeltTime = data; break;
            default: break;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (index < 2) {
            if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (TileAlchemyFurnace.isItemFuel(stack)) {
            if (!this.mergeItemStack(stack, 1, 2, false)
                    && (this.furnace == null || !this.furnace.isItemValidForSlot(0, stack)
                    || !this.mergeItemStack(stack, 0, 1, false))) return ItemStack.EMPTY;
        } else if (this.furnace != null && this.furnace.isItemValidForSlot(0, stack)) {
            if (!this.mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index >= 2 && index < 29) {
            if (!this.mergeItemStack(stack, 29, 38, false)) return ItemStack.EMPTY;
        } else if (index >= 29 && index < 38) {
            if (!this.mergeItemStack(stack, 2, 29, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }
}
