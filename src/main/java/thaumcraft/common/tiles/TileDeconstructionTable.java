package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class TileDeconstructionTable
extends TileThaumcraft
implements ISidedInventory, ITickable {

    public Aspect aspect;
    public int breaktime;
    private ItemStack[] itemStacks = new ItemStack[1];
    private static final int[] sides = new int[]{0};

    public TileDeconstructionTable() {
        itemStacks[0] = ItemStack.EMPTY;
    }

    @Override
    public int getSizeInventory() { return 1; }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? itemStacks[0] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 0 && !itemStacks[0].isEmpty()) {
            if (itemStacks[0].getCount() <= count) {
                ItemStack stack = itemStacks[0];
                itemStacks[0] = ItemStack.EMPTY;
                this.markDirty();
                return stack;
            }
            ItemStack stack = itemStacks[0].splitStack(count);
            if (itemStacks[0].getCount() == 0) itemStacks[0] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 0 && !itemStacks[0].isEmpty()) {
            ItemStack stack = itemStacks[0];
            itemStacks[0] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            itemStacks[0] = stack;
            this.markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) { return true; }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index != 0 || stack.isEmpty()) return false;
        AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
        tags = ThaumcraftCraftingManager.getBonusTags(stack, tags);
        return tags != null && tags.size() > 0;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? new int[0] : sides;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.UP && this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) { return true; }

    @Override
    public int getField(int id) { return id == 0 ? this.breaktime : 0; }

    @Override
    public void setField(int id, int value) {
        if (id == 0) {
            this.breaktime = value;
        }
    }

    @Override
    public int getFieldCount() { return 1; }

    @Override
    public void clear() { itemStacks[0] = ItemStack.EMPTY; }

    @Override
    public String getName() { return "container.deconstruction"; }

    @Override
    public boolean hasCustomName() { return false; }

    @Override
    public ITextComponent getDisplayName() { return null; }

    @Override
    public boolean isEmpty() { return itemStacks[0].isEmpty(); }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Inventory", 10);
        if (list.tagCount() > 0) {
            NBTTagCompound item = list.getCompoundTagAt(0);
            itemStacks[0] = new ItemStack(item);
        }
        if (compound.hasKey("aspect")) {
            this.aspect = Aspect.getAspect(compound.getString("aspect"));
        }
        this.breaktime = compound.getInteger("breaktime");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        if (!itemStacks[0].isEmpty()) {
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte) 0);
            itemStacks[0].writeToNBT(item);
            list.appendTag(item);
        }
        compound.setTag("Inventory", list);
        if (this.aspect != null) {
            compound.setString("aspect", this.aspect.getTag());
        }
        compound.setInteger("breaktime", this.breaktime);
    }

    @Override
    public void update() {
        // Deconstruction logic will be added later
    }

    public int getBreakTimeScaled(int scale) {
        return this.breaktime * scale / 40;
    }
}
