package thaumcraft.common.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryMob implements IInventory {
    public ItemStack[] inventory;
    public int slotCount;
    private String name = "";

    public InventoryMob() { this(36); }

    public InventoryMob(int size) {
        this.slotCount = size;
        this.inventory = new ItemStack[size];
        for (int a = 0; a < size; a++) this.inventory[a] = ItemStack.EMPTY;
    }

    @Override
    public int getSizeInventory() { return this.inventory.length; }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : this.inventory) { if (!s.isEmpty()) return false; }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if (i < 0 || i >= this.inventory.length) return ItemStack.EMPTY;
        return this.inventory[i];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.inventory[index] == ItemStack.EMPTY) return ItemStack.EMPTY;
        if (this.inventory[index].getCount() <= count) {
            ItemStack s = this.inventory[index];
            this.inventory[index] = ItemStack.EMPTY;
            return s;
        }
        return this.inventory[index].splitStack(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.inventory[index] == ItemStack.EMPTY) return ItemStack.EMPTY;
        ItemStack s = this.inventory[index];
        this.inventory[index] = ItemStack.EMPTY;
        return s;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory[index] = stack;
    }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override public void markDirty() {}
    @Override public boolean isUsableByPlayer(EntityPlayer p) { return false; }
    @Override public void openInventory(EntityPlayer p) {}
    @Override public void closeInventory(EntityPlayer p) {}
    @Override public boolean isItemValidForSlot(int i, ItemStack s) { return true; }
    @Override public int getField(int i) { return 0; }
    @Override public void setField(int i, int v) {}
    @Override public int getFieldCount() { return 0; }
    @Override public void clear() { for (int i = 0; i < this.inventory.length; i++) this.inventory[i] = ItemStack.EMPTY; }
    @Override public String getName() { return this.name; }
    @Override public boolean hasCustomName() { return false; }
    @Override public ITextComponent getDisplayName() { return new TextComponentString(this.name); }

    public NBTTagList writeToNBT(NBTTagList list) {
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != ItemStack.EMPTY) {
                NBTTagCompound tc = new NBTTagCompound();
                tc.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(tc);
                list.appendTag(tc);
            }
        }
        return list;
    }

    public void readFromNBT(NBTTagList list) {
        this.inventory = new ItemStack[this.inventory.length];
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tc = list.getCompoundTagAt(i);
            int slot = tc.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.inventory.length) {
                this.inventory[slot] = new ItemStack(tc);
            }
        }
    }

    public boolean hasSomething() {
        for (ItemStack s : this.inventory) { if (!s.isEmpty()) return true; }
        return false;
    }
}
