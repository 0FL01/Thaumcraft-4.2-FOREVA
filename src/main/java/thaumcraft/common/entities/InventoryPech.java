package thaumcraft.common.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.common.entities.monster.EntityPech;

public class InventoryPech implements IInventory {

    private NonNullList<ItemStack> contents;
    private EntityPlayer thePlayer;
    public EntityPech theMerchant;
    public Container eventHandler;

    public InventoryPech() {
        this.contents = NonNullList.withSize(5, ItemStack.EMPTY);
    }

    public void setPlayer(EntityPlayer player) {
        this.thePlayer = player;
    }

    public void setMerchant(EntityPech pech) {
        this.theMerchant = pech;
    }

    public void setContainer(Container container) {
        this.eventHandler = container;
    }

    @Override
    public int getSizeInventory() {
        return this.contents.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.contents) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0 || index >= this.contents.size()) return ItemStack.EMPTY;
        return this.contents.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = this.contents.get(index);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack ret;
        if (stack.getCount() <= count) {
            ret = stack.copy();
            this.contents.set(index, ItemStack.EMPTY);
        } else {
            ret = stack.splitStack(count);
        }
        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged(this);
        }
        this.markDirty();
        return ret;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.contents.get(index);
        this.contents.set(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.contents.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged(this);
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged(this);
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.theMerchant != null && this.theMerchant.isTamed();
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0; // Only slot 0 (input) accepts items
    }

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() {
        this.contents.clear();
    }

    @Override
    public String getName() {
        return "entity.Pech.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }
}
