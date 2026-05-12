package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryTrunk implements IInventory {

    private NonNullList<ItemStack> contents;
    private int slotCount;
    public int stackLimit = 64;
    public EntityTravelingTrunk ent;
    public boolean inventoryChanged;

    public InventoryTrunk() {
        this.contents = NonNullList.withSize(36, ItemStack.EMPTY);
        this.slotCount = 27;
    }

    public void setEntity(EntityTravelingTrunk entity) {
        this.ent = entity;
    }

    public void setSlotCount(int slots) {
        this.slotCount = Math.min(slots, this.contents.size());
    }

    @Override
    public int getSizeInventory() {
        return this.slotCount;
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
        ItemStack stack = ItemStackHelper.getAndSplit(this.contents, index, count);
        if (!stack.isEmpty()) this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.contents, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.contents.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return this.stackLimit;
    }

    @Override
    public void markDirty() {
        this.inventoryChanged = true;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.ent != null && !this.ent.isDead && player.getDistanceSq(this.ent) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (this.ent != null) {
            this.ent.setOpen(true);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (this.ent != null) {
            this.ent.setOpen(false);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
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
        return "container.trunk";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    public void readFromNBT(NBTTagList tagList) {
        this.contents = NonNullList.withSize(36, ItemStack.EMPTY);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
            int slot = slotTag.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.contents.size()) {
                this.contents.set(slot, new ItemStack(slotTag));
            }
        }
    }

    public NBTTagList writeToNBT(NBTTagList tagList) {
        for (int i = 0; i < this.contents.size(); i++) {
            if (!this.contents.get(i).isEmpty()) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                this.contents.get(i).writeToNBT(slotTag);
                tagList.appendTag(slotTag);
            }
        }
        return tagList;
    }

    public void dropAllItems() {
        if (this.ent == null || this.ent.world.isRemote) return;
        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (!stack.isEmpty()) {
                this.ent.entityDropItem(stack, 0.0F);
                this.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }
}
