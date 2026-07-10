package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class ContainerArcaneWorkbench extends Container {
    private final TileArcaneWorkbench tileEntity;
    private final InventoryPlayer playerInventory;
    private final InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerDummy(), 3, 3);
    private final InventoryCraftResult craftResult = new InventoryCraftResult();
    private ArcaneWorkbenchRecipeResolver.Resolution resolution = ArcaneWorkbenchRecipeResolver.Resolution.NONE;

    public ContainerArcaneWorkbench() {
        this(null, null);
    }

    public ContainerArcaneWorkbench(InventoryPlayer playerInventory, TileArcaneWorkbench tileEntity) {
        this.tileEntity = tileEntity;
        this.playerInventory = playerInventory;
        if (this.tileEntity != null) {
            this.tileEntity.addWorkbenchListener(this);
            this.tileEntity.setInventorySlotContentsSoftly(9, ItemStack.EMPTY);
            this.addSlotToContainer(new SlotCraftingArcaneWorkbench(playerInventory.player, this.tileEntity, this.craftResult, this, 0, 160, 64));
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
        if (this.tileEntity == null || this.playerInventory == null || this.playerInventory.player == null) return;

        for (int i = 0; i < 9; i++) {
            this.craftMatrix.setInventorySlotContents(i, this.tileEntity.getStackInSlot(i));
        }

        this.resolution = ArcaneWorkbenchRecipeResolver.resolve(this.tileEntity, this.craftMatrix, this.playerInventory.player);
        this.craftResult.setRecipeUsed(this.resolution.vanillaRecipe);
        this.craftResult.setInventorySlotContents(0,
                this.resolution.craftable ? this.resolution.output.copy() : ItemStack.EMPTY);
    }

    @Override
    public void detectAndSendChanges() {
        this.refreshResult();
        super.detectAndSendChanges();
    }

    public void refreshResult() {
        if (this.tileEntity != null) {
            this.onCraftMatrixChanged(this.tileEntity);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.tileEntity != null) {
            this.tileEntity.removeWorkbenchListener(this);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack() || !slot.canTakeStack(playerIn)) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        original = stack.copy();

        if (index == 0) {
            if (!this.canFullyMerge(stack, 11, 47)) return ItemStack.EMPTY;
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
        slot.onTake(playerIn, index == 0 ? original : stack);
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
        return slotIn.inventory != this.tileEntity
                && slotIn.inventory != this.craftResult
                && super.canDragIntoSlot(slotIn);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

    private boolean canFullyMerge(ItemStack stack, int startIndex, int endIndex) {
        int remaining = stack.getCount();
        for (int i = startIndex; i < endIndex && remaining > 0; i++) {
            Slot target = this.inventorySlots.get(i);
            ItemStack existing = target.getStack();
            if (!existing.isEmpty() && ItemStack.areItemsEqual(existing, stack)
                    && ItemStack.areItemStackTagsEqual(existing, stack)) {
                int limit = Math.min(target.getSlotStackLimit(), stack.getMaxStackSize());
                remaining -= Math.max(0, limit - existing.getCount());
            }
        }
        for (int i = startIndex; i < endIndex && remaining > 0; i++) {
            Slot target = this.inventorySlots.get(i);
            if (!target.getHasStack() && target.isItemValid(stack)) {
                remaining -= Math.min(target.getSlotStackLimit(), stack.getMaxStackSize());
            }
        }
        return remaining <= 0;
    }

    private static boolean isValidWorkbenchWand(ItemStack stack) {
        return ArcaneWorkbenchRecipeResolver.isValidWand(stack);
    }

    boolean canTakeResult(EntityPlayer player, ItemStack expected) {
        ArcaneWorkbenchRecipeResolver.Resolution fresh = this.resolveCurrent(player);
        return fresh.craftable && ItemStack.areItemStacksEqual(fresh.output, expected);
    }

    CraftTransaction prepareCraft(EntityPlayer player, ItemStack expected) {
        ArcaneWorkbenchRecipeResolver.Resolution fresh = this.resolveCurrent(player);
        if (!fresh.craftable || !ItemStack.areItemStacksEqual(fresh.output, expected)) {
            this.onCraftMatrixChanged(this.tileEntity);
            return null;
        }

        NonNullList<ItemStack> remainders;
        ForgeHooks.setCraftingPlayer(player);
        try {
            remainders = fresh.isVanilla()
                    ? fresh.vanillaRecipe.getRemainingItems(this.craftMatrix)
                    : ForgeHooks.defaultRecipeGetRemainingItems(this.craftMatrix);
        } finally {
            ForgeHooks.setCraftingPlayer(null);
        }
        if (remainders == null) {
            remainders = NonNullList.withSize(9, ItemStack.EMPTY);
        }

        if (fresh.isArcane()) {
            ItemStack wandStack = this.tileEntity.getStackInSlot(10);
            if (!ArcaneWorkbenchRecipeResolver.isValidWand(wandStack)
                    || !((ItemWandCasting) wandStack.getItem()).consumeAllVisCrafting(wandStack, player, fresh.cost, true)) {
                this.onCraftMatrixChanged(this.tileEntity);
                return null;
            }
        }
        this.craftResult.setRecipeUsed(fresh.vanillaRecipe);
        return new CraftTransaction(player, remainders);
    }

    void finishCraft(CraftTransaction transaction) {
        EntityPlayer player = transaction.player;
        NonNullList<ItemStack> remainders = transaction.remainders;
        for (int i = 0; i < 9; i++) {
            if (!this.tileEntity.getStackInSlot(i).isEmpty()) {
                this.tileEntity.decrStackSize(i, 1);
            }
            ItemStack remainder = i < remainders.size() ? remainders.get(i) : ItemStack.EMPTY;
            if (remainder.isEmpty()) continue;

            ItemStack inputLeft = this.tileEntity.getStackInSlot(i);
            if (inputLeft.isEmpty()) {
                this.tileEntity.setInventorySlotContents(i, remainder);
            } else if (ItemStack.areItemsEqual(inputLeft, remainder)
                    && ItemStack.areItemStackTagsEqual(inputLeft, remainder)
                    && inputLeft.getCount() + remainder.getCount() <= inputLeft.getMaxStackSize()) {
                inputLeft.grow(remainder.getCount());
                this.tileEntity.setInventorySlotContents(i, inputLeft);
            } else if (!player.inventory.addItemStackToInventory(remainder)) {
                player.dropItem(remainder, false);
            }
        }
        this.tileEntity.onWandVisChanged();
    }

    private ArcaneWorkbenchRecipeResolver.Resolution resolveCurrent(EntityPlayer player) {
        if (this.tileEntity == null || player == null) return ArcaneWorkbenchRecipeResolver.Resolution.NONE;
        for (int i = 0; i < 9; i++) {
            this.craftMatrix.setInventorySlotContents(i, this.tileEntity.getStackInSlot(i));
        }
        return ArcaneWorkbenchRecipeResolver.resolve(this.tileEntity, this.craftMatrix, player);
    }

    public ItemStack getArcanePreviewResult() {
        return this.resolution.isArcane() ? this.resolution.output.copy() : ItemStack.EMPTY;
    }

    public AspectList getArcanePreviewCost() {
        return this.resolution.isArcane() ? this.resolution.cost.copy() : new AspectList();
    }

    static final class CraftTransaction {
        final EntityPlayer player;
        final NonNullList<ItemStack> remainders;

        CraftTransaction(EntityPlayer player, NonNullList<ItemStack> remainders) {
            this.player = player;
            this.remainders = remainders;
        }
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
