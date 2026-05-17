package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.blocks.BlockMetalDevice;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.EntityItemGrate;

public class TileGrate extends TileThaumcraft implements IInventory, ISidedInventory {

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (this.world != null && !this.world.isRemote && stack != null && !stack.isEmpty()) {
            EntityItemGrate item = new EntityItemGrate(
                    this.world,
                    this.pos.getX() + 0.5D,
                    this.pos.getY() + 0.6D,
                    this.pos.getZ() + 0.5D,
                    stack.copy());
            item.motionY = -0.1D;
            item.motionX = 0.0D;
            item.motionZ = 0.0D;
            this.world.spawnEntity(item);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return isOpenInputMeta();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (isOpenInputMeta() && side == EnumFacing.UP) {
            return new int[]{0};
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isOpenInputMeta() && direction == EnumFacing.UP;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public String getName() {
        return "thaumcraft.grate";
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
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    private boolean isOpenInputMeta() {
        if (this.world == null || this.pos == null) {
            return false;
        }
        if (this.world.getBlockState(this.pos).getBlock() != ConfigBlocks.blockMetalDevice) {
            return false;
        }
        return this.world.getBlockState(this.pos).getValue(BlockMetalDevice.TYPE) == 5;
    }
}
