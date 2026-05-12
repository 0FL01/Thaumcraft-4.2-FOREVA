package thaumcraft.common.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.EntityPech;

public class ContainerPech extends Container {

    private EntityPech pech;
    private InventoryPech pechInv;
    private int playerInventoryStart;

    public ContainerPech() {}

    public ContainerPech(InventoryPlayer playerInv, World world, EntityPech pech) {
        this.pech = pech;
        this.pechInv = new InventoryPech();
        this.pechInv.setPlayer(playerInv.player);
        this.pechInv.setMerchant(pech);
        this.pechInv.setContainer(this);
        if (this.pech != null) {
            this.pech.trading = true;
        }

        this.addSlotToContainer(new Slot(this.pechInv, 0, 36, 29));
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                this.addSlotToContainer(new SlotOutput(this.pechInv, 1 + col + row * 2, 106 + col * 18, 20 + row * 18));
            }
        }

        this.playerInventoryStart = this.inventorySlots.size();
        this.bindPlayerInventory(playerInv);
    }

    private void bindPlayerInventory(InventoryPlayer playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.pech != null && !this.pech.isDead && this.pech.isTamed() && player.getDistanceSq(this.pech) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < this.playerInventoryStart) {
                if (!this.mergeItemStack(stack, this.playerInventoryStart, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(stack, 0, 1, false)) {
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
        if (this.pech != null) {
            this.pech.trading = false;
        }
        if (this.pechInv != null && !player.world.isRemote) {
            for (int i = 0; i < this.pechInv.getSizeInventory(); i++) {
                ItemStack stack = this.pechInv.removeStackFromSlot(i);
                if (!stack.isEmpty()) {
                    player.dropItem(stack, false);
                }
            }
        }
    }

    private static class SlotOutput extends Slot {
        SlotOutput(InventoryPech inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
