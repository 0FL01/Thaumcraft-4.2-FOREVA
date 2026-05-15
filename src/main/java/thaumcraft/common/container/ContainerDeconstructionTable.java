package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class ContainerDeconstructionTable extends Container {
    private final TileDeconstructionTable table;
    private int lastBreakTime;

    public ContainerDeconstructionTable() {
        this(null, null);
    }

    public ContainerDeconstructionTable(InventoryPlayer playerInventory, TileDeconstructionTable table) {
        this.table = table;
        if (table != null) {
            this.addSlotToContainer(new SlotLimitedHasAspects(table, 0, 64, 16));
        }
        if (playerInventory != null) {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
                }
            }
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.table != null && this.table.isUsableByPlayer(playerIn) && isUsableTile(playerIn, this.table);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        if (this.table != null) {
            listener.sendWindowProperty(this, 0, this.table.breaktime);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (this.table == null || this.lastBreakTime == this.table.breaktime) return;
        for (IContainerListener listener : this.listeners) {
            listener.sendWindowProperty(this, 0, this.table.breaktime);
        }
        this.lastBreakTime = this.table.breaktime;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (this.table != null && id == 0) {
            this.table.breaktime = data;
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (this.table == null || id != 1 || this.table.aspect == null) return false;

        Aspect aspect = this.table.aspect;
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(playerIn);
        if (knowledge != null) {
            knowledge.addAspectPool(aspect, 1);
            ResearchManager.updateCache(playerIn);
            if (!playerIn.world.isRemote && playerIn instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), (short) 1,
                        knowledge.getAspectPoolFor(aspect)), (EntityPlayerMP) playerIn);
            }
        }

        this.table.aspect = null;
        this.table.markDirty();
        if (this.table.getWorld() != null && !this.table.getWorld().isRemote) {
            this.table.getWorld().notifyBlockUpdate(this.table.getPos(),
                    this.table.getWorld().getBlockState(this.table.getPos()),
                    this.table.getWorld().getBlockState(this.table.getPos()), 3);
        }
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (index == 0) {
            if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (this.table != null && this.table.isItemValidForSlot(0, stack)) {
            if (!this.mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index >= 1 && index < 28) {
            if (!this.mergeItemStack(stack, 28, 37, false)) return ItemStack.EMPTY;
        } else if (index >= 28 && index < 37) {
            if (!this.mergeItemStack(stack, 1, 28, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }
}
