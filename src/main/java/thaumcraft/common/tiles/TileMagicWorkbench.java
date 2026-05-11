package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;

public class TileMagicWorkbench
extends TileThaumcraft
implements IInventory,
ISidedInventory {
    public ItemStack[] stackList = new ItemStack[11];
    public Container eventHandler;

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int par1) {
        return par1 >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList[par1];
    }

    public ItemStack getStackInRowAndColumn(int par1, int par2) {
        if (par1 >= 0 && par1 < 3) {
            int var3 = par1 + par2 * 3;
            return this.getStackInSlot(var3);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int par1) {
        if (!this.stackList[par1].isEmpty()) {
            ItemStack var2 = this.stackList[par1];
            this.stackList[par1] = ItemStack.EMPTY;
            this.markDirty();
            return var2;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (!this.stackList[par1].isEmpty()) {
            ItemStack var3;
            if (this.stackList[par1].getCount() <= par2) {
                var3 = this.stackList[par1];
                this.stackList[par1] = ItemStack.EMPTY;
                if (this.eventHandler != null) {
                    this.eventHandler.onCraftMatrixChanged(this);
                }
                this.markDirty();
                return var3;
            }
            var3 = this.stackList[par1].splitStack(par2);
            if (this.stackList[par1].getCount() == 0) {
                this.stackList[par1] = ItemStack.EMPTY;
            }
            if (this.eventHandler != null) {
                this.eventHandler.onCraftMatrixChanged(this);
            }
            this.markDirty();
            return var3;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack;
        this.markDirty();
        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged(this);
        }
    }

    public void setInventorySlotContentsSoftly(int par1, ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer par1EntityPlayer) {
        return true;
    }

    @Override
    public void readCustomNBT(NBTTagCompound par1NBTTagCompound) {
        NBTTagList var2 = par1NBTTagCompound.getTagList("Inventory", 10);
        this.stackList = new ItemStack[this.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < 0 || var5 >= this.stackList.length) continue;
            this.stackList[var5] = new ItemStack(var4);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound par1NBTTagCompound) {
        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.stackList.length; ++var3) {
            if (this.stackList[var3].isEmpty()) continue;
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte) var3);
            this.stackList[var3].writeToNBT(var4);
            var2.appendTag(var4);
        }
        par1NBTTagCompound.setTag("Inventory", var2);
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stackList) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "container.magicworkbench";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{10};
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, EnumFacing direction) {
        return i == 10 && !itemstack.isEmpty() && itemstack.getItem() instanceof ItemWandCasting;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, EnumFacing direction) {
        return i == 10;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
    }
}
