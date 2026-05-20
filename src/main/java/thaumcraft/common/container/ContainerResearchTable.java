package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.IScribeTools;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.tiles.TileResearchTable;

public class ContainerResearchTable extends Container {
    private final TileResearchTable tileEntity;

    public ContainerResearchTable() {
        this(null, null);
    }

    public ContainerResearchTable(InventoryPlayer playerInventory, TileResearchTable tileEntity) {
        this.tileEntity = tileEntity;
        if (playerInventory == null || tileEntity == null) return;
        this.addSlotToContainer(new SlotLimitedByClass(IScribeTools.class, tileEntity, 0, 14, 10));
        this.addSlotToContainer(new SlotLimitedByClass(ItemResearchNotes.class, tileEntity, 1, 70, 10));
        bindPlayerInventory(playerInventory);
    }

    public TileResearchTable getTileEntity() {
        return this.tileEntity;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity != null && this.tileEntity.isUsableByPlayer(playerIn) && isUsableTile(playerIn, this.tileEntity);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, col + row * 9 + 9, 48 + col * 18, 175 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, col, 48 + col * 18, 233));
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int button) {
        if (button == 1) return true;
        if (button == 5 && this.tileEntity != null) {
            this.tileEntity.duplicate(player);
            return true;
        }
        return super.enchantItem(player, button);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < 2) {
                if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return result;
    }
}
