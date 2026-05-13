package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.items.wands.foci.FocusExcavation;

public class TileArcaneBore extends TileThaumcraft implements ITickable, IInventory, IWandable {
    public int spiral = 0;
    public float currentRadius = 0.0F;
    public int maxRadius = 2;
    public int topRotation = 0;
    public ItemStack[] contents = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};
    public EnumFacing orientation = EnumFacing.UP;
    public EnumFacing baseOrientation = EnumFacing.UP;
    public boolean hasFocus = false;
    public boolean hasPickaxe = false;
    public int fortune = 0;
    public int speed = 0;
    public int area = 0;

    private boolean first = true;

    @Override
    public void update() {
        if (this.world != null && this.world.isRemote && this.first) {
            this.setOrientation(this.orientation, true);
            this.first = false;
        }
        this.topRotation = (this.topRotation + (this.hasFocus && this.hasPickaxe ? 4 : 1)) % 360;
    }

    public void setOrientation(EnumFacing orientation, boolean initial) {
        this.orientation = orientation == null ? EnumFacing.UP : orientation;
        if (initial) {
            this.baseOrientation = this.orientation;
        }
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.hasFocus = !this.getStackInSlot(0).isEmpty()
                && this.getStackInSlot(0).getItem() instanceof FocusExcavation;
        this.hasPickaxe = !this.getStackInSlot(1).isEmpty()
                && this.getStackInSlot(1).getItem() instanceof ItemPickaxe;
        this.fortune = 0;
        this.speed = 0;
        this.area = 0;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = EnumFacing.byIndex(nbt.getInteger("orientation"));
        this.baseOrientation = EnumFacing.byIndex(nbt.getInteger("baseOrientation"));
        this.contents = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};
        NBTTagList list = nbt.getTagList("Inventory", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.contents.length) {
                this.contents[slot] = new ItemStack(itemTag);
            }
        }
        this.markDirty();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("orientation", this.orientation.getIndex());
        nbt.setInteger("baseOrientation", this.baseOrientation.getIndex());
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i].isEmpty()) continue;
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            this.contents[i].writeToNBT(itemTag);
            list.appendTag(itemTag);
        }
        nbt.setTag("Inventory", list);
    }

    @Override
    public int getSizeInventory() {
        return this.contents.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.contents.length ? this.contents[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < 0 || index >= this.contents.length || this.contents[index].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack;
        if (this.contents[index].getCount() <= count) {
            stack = this.contents[index];
            this.contents[index] = ItemStack.EMPTY;
        } else {
            stack = this.contents[index].splitStack(count);
            if (this.contents[index].getCount() <= 0) this.contents[index] = ItemStack.EMPTY;
        }
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index < 0 || index >= this.contents.length || this.contents[index].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.contents[index];
        this.contents[index] = ItemStack.EMPTY;
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= this.contents.length) return;
        this.contents[index] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return "container.arcanebore";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public net.minecraft.util.text.ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (index == 0) return stack.getItem() instanceof FocusExcavation;
        if (index == 1) return stack.getItem() instanceof ItemPickaxe;
        return false;
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
        this.contents[0] = ItemStack.EMPTY;
        this.contents[1] = ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return this.contents[0].isEmpty() && this.contents[1].isEmpty();
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        this.setOrientation(EnumFacing.byIndex(side), false);
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return wandstack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, -1, -1), this.pos.add(2, 2, 2));
    }
}
