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
        for (ItemStack s : this.inventory) { if (s != null && !s.isEmpty()) return false; }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if (i < 0 || i >= this.inventory.length) return ItemStack.EMPTY;
        return this.inventory[i] == null ? ItemStack.EMPTY : this.inventory[i];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.inventory[index] == null || this.inventory[index].isEmpty()) return ItemStack.EMPTY;
        if (this.inventory[index].getCount() <= count) {
            ItemStack s = this.inventory[index];
            this.inventory[index] = ItemStack.EMPTY;
            return s;
        }
        return this.inventory[index].splitStack(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.inventory[index] == null || this.inventory[index].isEmpty()) return ItemStack.EMPTY;
        ItemStack s = this.inventory[index];
        this.inventory[index] = ItemStack.EMPTY;
        return s;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory[index] = stack == null ? ItemStack.EMPTY : stack;
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
            if (this.inventory[i] != null && !this.inventory[i].isEmpty()) {
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
        for (int i = 0; i < this.inventory.length; i++) this.inventory[i] = ItemStack.EMPTY;
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tc = list.getCompoundTagAt(i);
            int slot = tc.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.inventory.length) {
                ItemStack stack = new ItemStack(tc);
                this.inventory[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack;
            }
        }
    }

    public boolean hasSomething() {
        for (ItemStack s : this.inventory) { if (s != null && !s.isEmpty()) return true; }
        return false;
    }

    public boolean allEmpty() { return this.isEmpty(); }

    public int getAmountNeededSmart(ItemStack stack, boolean fuzzy) {
        if (stack == null || stack.isEmpty()) return 0;
        int needed = stack.getMaxStackSize();
        int total = 0;
        for (ItemStack s : this.inventory) {
            if (s == null || s.isEmpty()) continue;
            if (fuzzy) {
                if (s.getItem() == stack.getItem()) total += s.getCount();
            } else {
                if (s.getItem() == stack.getItem() && s.getMetadata() == stack.getMetadata()
                    && net.minecraft.item.ItemStack.areItemStackTagsEqual(s, stack)) total += s.getCount();
            }
        }
        int maxStack = Math.min(stack.getMaxStackSize(), this.getInventoryStackLimit());
        int capacity = this.slotCount * maxStack;
        if (total >= capacity) return 0;
        int perSlotRoom = maxStack;
        int totalRoom = 0;
        for (ItemStack s : this.inventory) {
            if (s == null || s.isEmpty()) {
                totalRoom += perSlotRoom;
            } else {
                boolean match = fuzzy ? (s.getItem() == stack.getItem())
                    : (s.getItem() == stack.getItem() && s.getMetadata() == stack.getMetadata()
                        && net.minecraft.item.ItemStack.areItemStackTagsEqual(s, stack));
                if (match) totalRoom += (perSlotRoom - s.getCount());
            }
        }
        return Math.min(needed, Math.max(0, totalRoom));
    }

    public java.util.ArrayList<ItemStack> getItemsNeeded(boolean fuzzy) {
        java.util.ArrayList<ItemStack> needed = new java.util.ArrayList<>();
        for (int a = 0; a < this.slotCount; a++) {
            if (this.inventory[a] == null || this.inventory[a].isEmpty()) continue;
            if (fuzzy) {
                int[] ids = net.minecraftforge.oredict.OreDictionary.getOreIDs(this.inventory[a]);
                if (ids.length > 0) {
                    for (int id : ids) {
                        String oreName = net.minecraftforge.oredict.OreDictionary.getOreName(id);
                        if (oreName != null && !oreName.isEmpty()) {
                            net.minecraft.util.NonNullList<ItemStack> ores = net.minecraftforge.oredict.OreDictionary.getOres(oreName);
                            for (ItemStack ore : ores) {
                                needed.add(ore.copy());
                            }
                        }
                    }
                    continue;
                }
                needed.add(this.inventory[a].copy());
            } else {
                needed.add(this.inventory[a].copy());
            }
        }
        return needed;
    }
}
