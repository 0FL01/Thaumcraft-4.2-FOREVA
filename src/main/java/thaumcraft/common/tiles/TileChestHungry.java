package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.ItemStackHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.config.ConfigBlocks;

public class TileChestHungry extends TileThaumcraft implements IInventory, ITickable {
    public static final int EAT_LID_TICKS = 6;
    private static final float NORMAL_LID_SPEED = 0.1F;
    private static final float EAT_LID_SPEED = 0.35F;

    private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;
    private int ticksSinceSync;
    private int forcedLidTicks;
    private boolean eatCloseActive;

    /**
     * Triggers a short, fast lid open/close animation when the chest eats an item.
     * On the server, syncs the timer to the client via block event id=3.
     * Speed is controlled by EAT_LID_SPEED (fast) vs NORMAL_LID_SPEED (slow for GUI).
     */
    public void animateEating(int ticks) {
        if (ticks <= 0 || this.world == null) {
            return;
        }
        this.forcedLidTicks = Math.max(this.forcedLidTicks, ticks);
        this.eatCloseActive = true;
        if (!this.world.isRemote) {
            this.world.addBlockEvent(this.pos, ConfigBlocks.blockChestHungry, 3, this.forcedLidTicks);
        }
    }

    @Override
    public int getSizeInventory() {
        return this.chestContents.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.chestContents) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.chestContents.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(this.chestContents, index, count);
        if (!stack.isEmpty()) {
            this.markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.chestContents, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.chestContents.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return "Hungry Chest";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world == null) {
            return false;
        }
        return this.world.getTileEntity(this.pos) == this &&
                player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (this.world == null) {
            return;
        }
        ++this.numUsingPlayers;
        this.world.addBlockEvent(this.pos, ConfigBlocks.blockChestHungry, 1, this.numUsingPlayers);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (this.world == null) {
            return;
        }
        --this.numUsingPlayers;
        this.world.addBlockEvent(this.pos, ConfigBlocks.blockChestHungry, 1, this.numUsingPlayers);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
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
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public void update() {
        if (this.world == null) {
            return;
        }

        if (++this.ticksSinceSync % 80 == 0) {
            // Reference keeps this timing branch for lid sync cadence.
        }

        this.prevLidAngle = this.lidAngle;

        boolean forcedOpen = this.forcedLidTicks > 0;
        boolean shouldOpen = this.numUsingPlayers > 0 || forcedOpen;

        boolean eatMotion = forcedOpen || (this.eatCloseActive && this.numUsingPlayers <= 0);
        float delta = eatMotion && this.numUsingPlayers <= 0 ? EAT_LID_SPEED : NORMAL_LID_SPEED;

        if (shouldOpen && this.lidAngle == 0.0F) {
            this.world.playSound(null, this.pos, net.minecraft.init.SoundEvents.BLOCK_CHEST_OPEN,
                    net.minecraft.util.SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if ((!shouldOpen && this.lidAngle > 0.0F) || (shouldOpen && this.lidAngle < 1.0F)) {
            float previous = this.lidAngle;

            this.lidAngle = shouldOpen ? this.lidAngle + delta : this.lidAngle - delta;

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            if (this.lidAngle < 0.5F && previous >= 0.5F) {
                this.world.playSound(null, this.pos, net.minecraft.init.SoundEvents.BLOCK_CHEST_CLOSE,
                        net.minecraft.util.SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }

        if (this.forcedLidTicks > 0) {
            --this.forcedLidTicks;
        }

        if (this.eatCloseActive && this.forcedLidTicks == 0 && this.lidAngle <= 0.0F) {
            this.eatCloseActive = false;
        }

        if ((!shouldOpen && this.lidAngle > 0.0F) || (shouldOpen && this.lidAngle < 1.0F)) {
            float previous = this.lidAngle;
            this.lidAngle = shouldOpen ? this.lidAngle + delta : this.lidAngle - delta;

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            if (this.lidAngle < 0.5F && previous >= 0.5F) {
                this.world.playSound(null, this.pos, net.minecraft.init.SoundEvents.BLOCK_CHEST_CLOSE,
                        net.minecraft.util.SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }

        if (this.forcedLidTicks > 0) {
            --this.forcedLidTicks;
        }

        if (this.eatCloseActive && this.forcedLidTicks == 0 && this.lidAngle == 0.0F) {
            this.eatCloseActive = false;
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numUsingPlayers = type;
            return true;
        }
        if (id == 2) {
            float open = type / 10.0F;
            if (this.lidAngle < open) {
                this.lidAngle = open;
            }
            return true;
        }
        if (id == 3) {
            this.forcedLidTicks = Math.max(this.forcedLidTicks, type);
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.chestContents);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        ItemStackHelper.saveAllItems(compound, this.chestContents);
    }
}
