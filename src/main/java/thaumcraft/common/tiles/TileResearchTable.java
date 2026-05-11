package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.api.TileThaumcraft;

public class TileResearchTable
extends TileThaumcraft
implements IInventory, ITickable {

    private ItemStack[] stackList = new ItemStack[2];

    public TileResearchTable() {
        for (int i = 0; i < stackList.length; i++) {
            stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getSizeInventory() { return this.stackList.length; }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.stackList.length ? this.stackList[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (!this.stackList[index].isEmpty()) {
            if (this.stackList[index].getCount() <= count) {
                ItemStack stack = this.stackList[index];
                this.stackList[index] = ItemStack.EMPTY;
                this.markDirty();
                return stack;
            }
            ItemStack stack = this.stackList[index].splitStack(count);
            if (this.stackList[index].getCount() == 0) {
                this.stackList[index] = ItemStack.EMPTY;
            }
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!this.stackList[index].isEmpty()) {
            ItemStack stack = this.stackList[index];
            this.stackList[index] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stackList[index] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public boolean hasCustomName() { return false; }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) { return true; }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) { return true; }

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() {
        for (int i = 0; i < stackList.length; i++) {
            stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public String getName() { return "container.researchtable"; }

    @Override
    public ITextComponent getDisplayName() { return null; }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : stackList) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Inventory", 10);
        this.stackList = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.stackList.length) {
                this.stackList[slot] = new ItemStack(item);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.stackList.length; i++) {
            if (!this.stackList[i].isEmpty()) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                this.stackList[i].writeToNBT(item);
                list.appendTag(item);
            }
        }
        compound.setTag("Inventory", list);
    }

    @Override
    public void update() {
        // Research scanning logic will be added later
    }
}
