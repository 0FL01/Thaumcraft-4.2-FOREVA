package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class ContainerArcaneWorkbench extends Container {
    private final TileArcaneWorkbench tileEntity;
    private final InventoryPlayer playerInventory;
    private final InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerDummy(), 3, 3);

    public ContainerArcaneWorkbench() {
        this(null, null);
    }

    public ContainerArcaneWorkbench(InventoryPlayer playerInventory, TileArcaneWorkbench tileEntity) {
        this.tileEntity = tileEntity;
        this.playerInventory = playerInventory;
        if (this.tileEntity != null) {
            this.tileEntity.eventHandler = this;
            this.addSlotToContainer(new SlotCraftingArcaneWorkbench(playerInventory.player, this.tileEntity, this.tileEntity, 9, 160, 64));
            this.addSlotToContainer(new SlotLimitedByWand(this.tileEntity, 10, 160, 24));

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    this.addSlotToContainer(new Slot(this.tileEntity, col + row * 3, 40 + col * 24, 40 + row * 24));
                }
            }
        }
        if (playerInventory != null) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 16 + col * 18, 151 + row * 18));
                }
            }
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col, 16 + col * 18, 209));
            }
        }
        if (this.tileEntity != null) {
            this.onCraftMatrixChanged(this.tileEntity);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity != null && this.tileEntity.isUsableByPlayer(playerIn) && isUsableTile(playerIn, this.tileEntity);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        if (this.tileEntity == null || this.playerInventory == null || this.playerInventory.player == null) return;

        for (int i = 0; i < 9; i++) {
            this.craftMatrix.setInventorySlotContents(i, this.tileEntity.getStackInSlot(i));
        }

        ItemStack vanillaResult = CraftingManager.findMatchingResult(this.craftMatrix, this.tileEntity.getWorld());
        this.tileEntity.setInventorySlotContentsSoftly(9, vanillaResult);

        ItemStack output = this.tileEntity.getStackInSlot(9);
        ItemStack wandStack = this.tileEntity.getStackInSlot(10);
        if (!output.isEmpty() || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return;

        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        AspectList cost = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(this.tileEntity, this.playerInventory.player);
        if (cost.size() <= 0 || !wand.consumeAllVisCrafting(wandStack, this.playerInventory.player, cost, false)) return;

        this.tileEntity.setInventorySlotContentsSoftly(9,
                ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.playerInventory.player));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.tileEntity != null && this.tileEntity.getWorld() != null && !this.tileEntity.getWorld().isRemote) {
            this.tileEntity.eventHandler = null;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        original = stack.copy();

        if (index == 0) {
            if (!this.mergeItemStack(stack, 11, 47, true)) return ItemStack.EMPTY;
            slot.onSlotChange(stack, original);
        } else if (index >= 11 && index < 38) {
            if (isValidWorkbenchWand(stack)) {
                if (!this.mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
                slot.onSlotChange(stack, original);
            } else if (!this.mergeItemStack(stack, 38, 47, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= 38 && index < 47) {
            if (isValidWorkbenchWand(stack)) {
                if (!this.mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
                slot.onSlotChange(stack, original);
            } else if (!this.mergeItemStack(stack, 11, 38, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.mergeItemStack(stack, 11, 47, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (isThrowClick(clickTypeIn)) {
            return super.slotClick(slotId, 1, clickTypeIn, player);
        }
        return super.slotClick(slotId, normalizeDragType(slotId, dragType), clickTypeIn, player);
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return slotIn.inventory != this.tileEntity && super.canDragIntoSlot(slotIn);
    }

    private static boolean isValidWorkbenchWand(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemWandCasting && !((ItemWandCasting) stack.getItem()).isStaff(stack);
    }

    static boolean isThrowClick(ClickType clickTypeIn) {
        return clickTypeIn == ClickType.THROW;
    }

    static int normalizeDragType(int slotId, int dragType) {
        if ((slotId == 0 || slotId == 1) && dragType > 0) {
            return 0;
        }
        return dragType;
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    private static final class ContainerDummy extends Container {
        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return false;
        }
    }
}
