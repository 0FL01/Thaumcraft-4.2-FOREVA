package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.items.armor.ItemHoverHarness;

public class ContainerHoverHarness extends Container {
    private final EntityPlayer player;
    private final World worldObj;
    private final ItemStack harness;
    private final int harnessPlayerSlot;
    private final int blockedContainerSlot;
    private final InventoryHoverHarness input;

    public ContainerHoverHarness() {
        this(null, null, 0, 0, 0);
    }

    public ContainerHoverHarness(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        this.player = playerInventory != null ? playerInventory.player : null;
        this.worldObj = world;
        this.harnessPlayerSlot = findHarnessSlot(playerInventory);
        this.blockedContainerSlot = toContainerSlot(this.harnessPlayerSlot);
        this.harness = findHarness(playerInventory, this.harnessPlayerSlot);
        this.input = new InventoryHoverHarness(this);
        this.addSlotToContainer(new Slot(this.input, 0, 80, 32) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return ContainerHoverHarness.this.input.isItemValidForSlot(0, stack);
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });

        if (playerInventory != null) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addPlayerSlot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18);
                }
            }
            for (int col = 0; col < 9; col++) {
                this.addPlayerSlot(playerInventory, col, 8 + col * 18, 142);
            }
        }

        if (world != null && !world.isRemote && !this.harness.isEmpty() && this.harness.hasTagCompound()) {
            ItemStack jar = new ItemStack(this.harness.getTagCompound().getCompoundTag("jar"));
            if (!jar.isEmpty()) {
                this.input.setInventorySlotContents(0, jar);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn != null && playerIn == this.player && !playerIn.isDead && playerIn.world == this.worldObj
                && !this.harness.isEmpty() && this.harness.getItem() instanceof ItemHoverHarness;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (index == this.blockedContainerSlot) return ItemStack.EMPTY;
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            } else if (this.input.isItemValidForSlot(0, stack)) {
                if (!this.mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        }
        return copy;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId == this.blockedContainerSlot) return ItemStack.EMPTY;
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.worldObj == null || this.worldObj.isRemote || this.harness.isEmpty()) return;
        NBTTagCompound harnessTag = this.harness.hasTagCompound() ? this.harness.getTagCompound() : new NBTTagCompound();
        ItemStack jar = this.input.removeStackFromSlot(0);
        if (!jar.isEmpty()) {
            NBTTagCompound jarTag = new NBTTagCompound();
            jar.writeToNBT(jarTag);
            harnessTag.setTag("jar", jarTag);
        } else {
            harnessTag.setTag("jar", new NBTTagCompound());
        }
        this.harness.setTagCompound(harnessTag);
        playerIn.inventory.markDirty();
    }

    private void addPlayerSlot(InventoryPlayer inventory, int index, int x, int y) {
        if (index == this.harnessPlayerSlot) {
            this.addSlotToContainer(new Slot(inventory, index, x, y) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    return false;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }
            });
        } else {
            this.addSlotToContainer(new Slot(inventory, index, x, y));
        }
    }

    private static ItemStack findHarness(InventoryPlayer inventory, int slot) {
        if (inventory == null) return ItemStack.EMPTY;
        ItemStack main = inventory.player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() instanceof ItemHoverHarness) return main;
        ItemStack off = inventory.player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ItemHoverHarness) return off;
        if (slot >= 0 && slot < inventory.mainInventory.size()) {
            ItemStack stack = inventory.mainInventory.get(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemHoverHarness) return stack;
        }
        ItemStack current = inventory.getCurrentItem();
        if (!current.isEmpty() && current.getItem() instanceof ItemHoverHarness) return current;
        return ItemStack.EMPTY;
    }

    private static int findHarnessSlot(InventoryPlayer inventory) {
        if (inventory == null) return -1;
        ItemStack held = inventory.player.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof ItemHoverHarness) return inventory.currentItem;
        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ItemStack stack = inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemHoverHarness) return i;
        }
        return -1;
    }

    private static int toContainerSlot(int playerSlot) {
        if (playerSlot >= 0 && playerSlot < 9) return 28 + playerSlot;
        if (playerSlot >= 9 && playerSlot < 36) return 1 + (playerSlot - 9);
        return -1;
    }

    private static final class InventoryHoverHarness extends InventoryBasic {
        private final Container container;

        private InventoryHoverHarness(Container container) {
            super("container.hoverharness", false, 1);
            this.container = container;
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BlockJarItem)) return false;
            AspectList aspects = ((BlockJarItem) stack.getItem()).getAspects(stack);
            return aspects != null && aspects.size() > 0 && aspects.getAmount(Aspect.ENERGY) > 0;
        }

        @Override
        public void markDirty() {
            super.markDirty();
            this.container.detectAndSendChanges();
        }
    }
}
