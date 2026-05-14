package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;

public class TilePedestal
extends TileThaumcraft
implements ISidedInventory {
    private static final int[] slots = new int[]{0};
    private ItemStack[] inventory = new ItemStack[1];
    private String customName;

    public TilePedestal() {
        inventory[0] = ItemStack.EMPTY;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    @Override
    public int getSizeInventory() { return 1; }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? inventory[0] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 0 && !inventory[0].isEmpty()) {
            if (inventory[0].getCount() <= count) {
                ItemStack stack = inventory[0];
                inventory[0] = ItemStack.EMPTY;
                this.markDirtyAndSync();
                return stack;
            }
            ItemStack stack = inventory[0].splitStack(count);
            if (inventory[0].getCount() == 0) inventory[0] = ItemStack.EMPTY;
            this.markDirtyAndSync();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 0 && !inventory[0].isEmpty()) {
            ItemStack stack = inventory[0];
            inventory[0] = ItemStack.EMPTY;
            this.markDirtyAndSync();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            inventory[0] = stack == null ? ItemStack.EMPTY : stack;
            if (!inventory[0].isEmpty() && inventory[0].getCount() > this.getInventoryStackLimit()) {
                inventory[0].setCount(this.getInventoryStackLimit());
            }
            this.markDirtyAndSync();
        }
    }

    public void setInventorySlotContentsFromInfusion(int index, ItemStack stack) {
        this.setInventorySlotContents(index, stack);
        if (this.world != null && !this.world.isRemote) {
            this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 12, 0);
        }
    }

    @Override
    public int getInventoryStackLimit() { return 1; }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) { return true; }

    @Override
    public int[] getSlotsForFace(EnumFacing side) { return slots; }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) { return index == 0 && inventory[0].isEmpty(); }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) { return true; }

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() {
        inventory[0] = ItemStack.EMPTY;
        this.markDirtyAndSync();
    }

    @Override
    public String getName() { return "container.pedestal"; }

    @Override
    public boolean hasCustomName() { return false; }

    @Override
    public net.minecraft.util.text.ITextComponent getDisplayName() { return null; }

    @Override
    public boolean isEmpty() { return inventory[0].isEmpty(); }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList(compound.hasKey("Items", 9) ? "Items" : "Inventory", 10);
        if (list.tagCount() > 0) {
            NBTTagCompound item = list.getCompoundTagAt(0);
            inventory[0] = new ItemStack(item);
        } else {
            inventory[0] = ItemStack.EMPTY;
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 11 || id == 12) return true;
        return super.receiveClientEvent(id, type);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        if (!inventory[0].isEmpty()) {
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte) 0);
            inventory[0].writeToNBT(item);
            list.appendTag(item);
        }
        compound.setTag("Items", list);
    }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World worldIn, net.minecraft.util.math.BlockPos pos, net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
