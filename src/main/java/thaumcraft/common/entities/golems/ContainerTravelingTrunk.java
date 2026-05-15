package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerTravelingTrunk extends Container {

    private EntityTravelingTrunk trunk;
    private InventoryTrunk trunkInv;
    private int trunkSlotCount;

    public ContainerTravelingTrunk() {}

    public ContainerTravelingTrunk(InventoryPlayer playerInv, World world, EntityTravelingTrunk trunk) {
        this.trunk = trunk;
        this.trunkInv = trunk != null ? trunk.inventory : null;
        if (this.trunkInv != null) {
            this.trunkInv.openInventory(playerInv.player);
        }
        if (this.trunk != null) {
            this.trunk.setOpen(true);
        }

        int rows = this.trunk != null ? this.trunk.getRows() : 0;
        this.trunkSlotCount = rows * 9;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(this.trunkInv, col + row * 9, 8 + col * 18, 15 + row * 23));
            }
        }

        this.bindPlayerInventory(playerInv);
    }

    private void bindPlayerInventory(InventoryPlayer playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 118 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 176));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.trunk != null && !this.trunk.isDead && player.getDistanceSq(this.trunk) <= 64.0D;
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (id == 1 && this.trunk != null && !this.trunk.isDead) {
            this.trunk.setStay(!this.trunk.getStay());
            return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < this.trunkSlotCount) {
                if (!this.mergeItemStack(stack, this.trunkSlotCount, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(stack, 0, this.trunkSlotCount, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return result;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.trunkInv != null) {
            this.trunkInv.closeInventory(player);
        }
        if (this.trunk != null) {
            this.trunk.setOpen(false);
        }
    }
}
