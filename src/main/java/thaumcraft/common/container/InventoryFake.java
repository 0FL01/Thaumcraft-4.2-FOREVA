package thaumcraft.common.container;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryFake implements IInventory {

    private final NonNullList<ItemStack> stacks;
    private final String name;

    public InventoryFake(int size) {
        this("container.inventoryfake", size);
    }

    public InventoryFake(String name, int size) {
        this.name = name;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public InventoryFake(ItemStack[] stackList) {
        this("container.inventoryfake", stackList.length);
        for (int i = 0; i < stackList.length; i++) {
            if (stackList[i] != null && !stackList[i].isEmpty()) {
                this.stacks.set(i, stackList[i].copy());
            }
        }
    }

    public InventoryFake(List<ItemStack> stackList) {
        this(stackList.toArray(new ItemStack[0]));
    }

    @Override
    public int getSizeInventory() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return stacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = stacks.get(index);
        if (!stack.isEmpty()) {
            if (stack.getCount() <= count) {
                ItemStack result = stack.copy();
                stacks.set(index, ItemStack.EMPTY);
                return result;
            } else {
                ItemStack result = stack.splitStack(count);
                if (stack.isEmpty()) {
                    stacks.set(index, ItemStack.EMPTY);
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = stacks.get(index);
        stacks.set(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        stacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(name);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        stacks.clear();
    }
}
