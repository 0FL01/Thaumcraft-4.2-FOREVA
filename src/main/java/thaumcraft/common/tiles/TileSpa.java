package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.items.ItemBathSalts;

public class TileSpa extends TileThaumcraft implements ISidedInventory, ITickable {

    private static final int[] SIDES = new int[]{0};

    public final FluidTank tank;
    private final ItemStack[] itemStacks = new ItemStack[]{ItemStack.EMPTY};
    private boolean mix = true;

    public TileSpa() {
        this.tank = new FluidTank(5000) {
            @Override
            protected void onContentsChanged() {
                TileSpa.this.notifyUpdate();
            }
        };
    }

    public void toggleMix() {
        this.mix = !this.mix;
        this.notifyUpdate();
    }

    public boolean getMix() {
        return this.mix;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        if (compound.hasKey("mix")) {
            this.mix = compound.getBoolean("mix");
        }
        this.tank.readFromNBT(compound);

        this.itemStacks[0] = ItemStack.EMPTY;
        NBTTagList list = compound.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 255;
            if (slot == 0) {
                this.itemStacks[0] = new ItemStack(item);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setBoolean("mix", this.mix);
        this.tank.writeToNBT(compound);

        NBTTagList list = new NBTTagList();
        if (!this.itemStacks[0].isEmpty()) {
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte) 0);
            this.itemStacks[0].writeToNBT(item);
            list.appendTag(item);
        }
        compound.setTag("Items", list);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? this.itemStacks[0] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index != 0 || this.itemStacks[0].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack;
        if (this.itemStacks[0].getCount() <= count) {
            stack = this.itemStacks[0];
            this.itemStacks[0] = ItemStack.EMPTY;
        } else {
            stack = this.itemStacks[0].splitStack(count);
            if (this.itemStacks[0].getCount() <= 0) {
                this.itemStacks[0] = ItemStack.EMPTY;
            }
        }
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index != 0 || this.itemStacks[0].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.itemStacks[0];
        this.itemStacks[0] = ItemStack.EMPTY;
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index != 0) return;
        this.itemStacks[0] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq((double) this.pos.getX() + 0.5D,
                (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && !stack.isEmpty() && stack.getItem() instanceof ItemBathSalts;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? new int[0] : SIDES;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.UP && this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.UP;
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
        this.itemStacks[0] = ItemStack.EMPTY;
    }

    @Override
    public String getName() {
        return "thaumcraft.spa";
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
    public boolean isEmpty() {
        return this.itemStacks[0].isEmpty();
    }

    @Override
    public void update() {
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.UP
                || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.UP) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.tank);
        }
        return super.getCapability(capability, facing);
    }

    private void notifyUpdate() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
