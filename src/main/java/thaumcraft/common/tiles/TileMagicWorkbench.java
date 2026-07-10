package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class TileMagicWorkbench
extends TileThaumcraft
implements IInventory,
ISidedInventory {
    public ItemStack[] stackList = new ItemStack[11];
    private final Set<Container> eventHandlers = Collections.newSetFromMap(new IdentityHashMap<Container, Boolean>());
    private final IItemHandler[] itemHandlers = new IItemHandler[EnumFacing.values().length];

    public TileMagicWorkbench() {
        for (int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int par1) {
        if (par1 < 0 || par1 >= this.getSizeInventory()) return ItemStack.EMPTY;
        ItemStack stack = this.stackList[par1];
        return stack == null ? ItemStack.EMPTY : stack;
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
        if (par1 >= 0 && par1 < this.stackList.length && this.stackList[par1] != null && !this.stackList[par1].isEmpty()) {
            ItemStack var2 = this.stackList[par1];
            this.stackList[par1] = ItemStack.EMPTY;
            this.inventoryChanged();
            return var2;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (this.stackList[par1] != null && !this.stackList[par1].isEmpty()) {
            ItemStack var3;
            if (this.stackList[par1].getCount() <= par2) {
                var3 = this.stackList[par1];
                this.stackList[par1] = ItemStack.EMPTY;
                this.inventoryChanged();
                return var3;
            }
            var3 = this.stackList[par1].splitStack(par2);
            if (this.stackList[par1].getCount() == 0) {
                this.stackList[par1] = ItemStack.EMPTY;
            }
            this.inventoryChanged();
            return var3;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack == null ? ItemStack.EMPTY : par2ItemStack;
        this.inventoryChanged();
    }

    public void setInventorySlotContentsSoftly(int par1, ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack == null ? ItemStack.EMPTY : par2ItemStack;
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
        for (int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < 0 || var5 >= this.stackList.length || var5 == 9) continue;
            this.stackList[var5] = new ItemStack(var4);
        }
        this.stackList[9] = ItemStack.EMPTY;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound par1NBTTagCompound) {
        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.stackList.length; ++var3) {
            if (var3 == 9) continue;
            if (this.stackList[var3] == null || this.stackList[var3].isEmpty()) continue;
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
            if (stack != null && !stack.isEmpty()) return false;
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
        return new TextComponentTranslation(this.getName());
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        if (i >= 0 && i < 9) return true;
        return i == 10 && isValidWorkbenchWand(itemstack);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{10};
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, EnumFacing direction) {
        return i == 10 && isValidWorkbenchWand(itemstack);
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
        this.inventoryChanged();
    }

    public void addWorkbenchListener(Container listener) {
        if (listener != null) this.eventHandlers.add(listener);
    }

    public void removeWorkbenchListener(Container listener) {
        this.eventHandlers.remove(listener);
    }

    public void onWandVisChanged() {
        this.inventoryChanged();
    }

    private void inventoryChanged() {
        this.stackList[9] = ItemStack.EMPTY;
        this.markDirty();
        for (Container listener : this.eventHandlers.toArray(new Container[0])) {
            listener.onCraftMatrixChanged(this);
        }
        if (this.world != null && this.world.isBlockLoaded(this.pos)) {
            net.minecraft.block.state.IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }

    private static boolean isValidWorkbenchWand(ItemStack stack) {
        return stack != null && !stack.isEmpty()
                && stack.getItem() instanceof ItemWandCasting
                && !((ItemWandCasting) stack.getItem()).isStaff(stack);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            EnumFacing side = facing == null ? EnumFacing.UP : facing;
            int index = side.ordinal();
            if (this.itemHandlers[index] == null) {
                this.itemHandlers[index] = new SidedInvWrapper(this, side);
            }
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandlers[index]);
        }
        return super.getCapability(capability, facing);
    }
}
