package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.container.SlotGhost;
import thaumcraft.common.container.SlotGhostFluid;
import thaumcraft.common.entities.InventoryMob;
import thaumcraft.common.container.ContainerGhostSlots;

public class ContainerGolem extends ContainerGhostSlots {
    private static final int GHOST_COUNT_PROPERTY_BASE = 100;

    private EntityGolemBase golem;
    private InventoryMob mobInv;
    private final java.util.Set<Slot> ghostSlots = new java.util.HashSet<>();
    private final java.util.Map<Integer, Integer> lastGhostCounts = new java.util.HashMap<>();
    public int currentScroll = 0;
    public int maxScroll = 0;

    public ContainerGolem() {}

    public ContainerGolem(InventoryPlayer playerInv, EntityGolemBase golem) {
        this.setGolem(golem != null && playerInv != null && golem.isOwnedBy(playerInv.player) ? golem : null);
        if (this.golem != null) {
            if (this.golem.inventory == null && ItemGolemCore.hasInventory(this.golem.getCore())) {
                this.golem.setupGolemInventory();
            }
            this.mobInv = this.golem.inventory;
            this.golem.paused = true;
        }
        bindGolemInventory();
        this.bindPlayerInventory(playerInv);
    }

    public void setGolem(EntityGolemBase golem) {
        this.golem = golem;
    }

    private void bindPlayerInventory(InventoryPlayer playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    private void bindGolemInventory() {
        if (this.golem == null || this.mobInv == null || !ItemGolemCore.hasInventory(this.golem.getCore())) {
            this.maxScroll = 0;
            return;
        }
        int slots = Math.max(0, this.mobInv.slotCount);
        this.maxScroll = Math.max(0, (slots - 1) / 6);
        this.currentScroll = Math.min(this.currentScroll, this.maxScroll);

        int visibleSlots = Math.min(6, slots);
        for (int a = 0; a < visibleSlots; a++) {
            int slotIndex = a + this.currentScroll * 6;
            if (slotIndex >= slots) break;
            Slot slot;
            if (this.golem.getCore() == 0) {
                slot = new SlotGhost(this.mobInv, slotIndex, 100 + a / 2 * 28, 16 + a % 2 * 31);
            } else if (this.golem.getCore() == 5) {
                slot = new SlotGhostFluid(this.mobInv, slotIndex, 100 + a / 2 * 28, 16 + a % 2 * 31);
            } else {
                slot = new SlotGhost(this.mobInv, slotIndex, 100 + a / 2 * 28, 16 + a % 2 * 31, 1);
            }
            this.ghostSlots.add(slot);
            this.addSlotToContainer(slot);
        }
    }

    public void refreshInventory(InventoryPlayer playerInv) {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.ghostSlots.clear();
        this.lastGhostCounts.clear();
        bindGolemInventory();
        bindPlayerInventory(playerInv);
    }

    @Override
    protected boolean isGhostSlot(Slot slot) {
        return this.ghostSlots.contains(slot);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (this.golem == null || this.golem.isDead) return false;
        return this.golem.isOwnedBy(player) && player.getDistanceSq(this.golem) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (!this.canInteractWith(player)) return false;

        boolean changed = false;
        if (id == 66 && this.currentScroll > 0) {
            this.currentScroll--;
            refreshInventory(player.inventory);
            changed = true;
        } else if (id == 67 && this.currentScroll < this.maxScroll) {
            this.currentScroll++;
            refreshInventory(player.inventory);
            changed = true;
        } else if (isToggleAllowed(id)) {
            int toggle = id - 50;
            this.golem.setToggle(toggle, !this.golem.getToggles()[toggle]);
            changed = true;
        }

        int slots = this.mobInv != null ? this.mobInv.slotCount : 0;
        int firstVisibleSlot = this.currentScroll * 6;
        int lastVisibleSlot = Math.min(slots, firstVisibleSlot + 6);
        if (this.golem.getUpgradeAmount(4) > 0 && id >= firstVisibleSlot && id < lastVisibleSlot) {
            int color = this.golem.getColors(id) - 1;
            if (color < -1) color = 15;
            this.golem.setColors(id, color);
            changed = true;
        } else if (this.golem.getUpgradeAmount(4) > 0
                && id - slots >= firstVisibleSlot && id - slots < lastVisibleSlot) {
            int slot = id - slots;
            int color = this.golem.getColors(slot) + 1;
            if (color > 15) color = -1;
            this.golem.setColors(slot, color);
            changed = true;
        }

        if (!changed) return false;
        if (player.world != null) {
            player.world.playSound(
                    null,
                    player.posX,
                    player.posY,
                    player.posZ,
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundCategory.PLAYERS,
                    0.2F,
                    0.8F
            );
        }
        return true;
    }

    private boolean isToggleAllowed(int id) {
        int core = this.golem.getCore();
        if (id == 50) return core == 0 || core == 8;
        if (id >= 51 && id <= 52 && core == 8) return true;
        if (id >= 51 && id <= 54 && core == 4) return this.golem.getUpgradeAmount(4) > 0;
        return id >= 55 && id <= 57
                && this.golem.getUpgradeAmount(5) > 0
                && ItemGolemCore.canSort(core);
    }

    @Override
    public void addListener(IContainerListener listener) {
        setGhostNetworkSyncView(true);
        try {
            super.addListener(listener);
        } finally {
            setGhostNetworkSyncView(false);
        }
        sendGhostCounts(listener, true);
    }

    @Override
    public void detectAndSendChanges() {
        setGhostNetworkSyncView(true);
        try {
            super.detectAndSendChanges();
        } finally {
            setGhostNetworkSyncView(false);
        }
        for (IContainerListener listener : this.listeners) {
            sendGhostCounts(listener, false);
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        int slotIndex = id - GHOST_COUNT_PROPERTY_BASE;
        if (slotIndex < 0 || slotIndex >= this.inventorySlots.size()) return;
        Slot slot = this.inventorySlots.get(slotIndex);
        if (!isGhostSlot(slot) || !slot.getHasStack()) return;
        ItemStack stack = slot.getStack().copy();
        stack.setCount(data);
        slot.putStack(stack);
    }

    private void setGhostNetworkSyncView(boolean enabled) {
        for (Slot slot : this.ghostSlots) {
            if (slot instanceof SlotGhost) ((SlotGhost) slot).setNetworkSyncView(enabled);
        }
    }

    private void sendGhostCounts(IContainerListener listener, boolean force) {
        for (int slotIndex = 0; slotIndex < this.inventorySlots.size(); slotIndex++) {
            Slot slot = this.inventorySlots.get(slotIndex);
            if (!isGhostSlot(slot)) continue;
            int count = slot.getHasStack() ? slot.getStack().getCount() : 0;
            Integer previous = this.lastGhostCounts.get(slotIndex);
            if (force || previous == null || previous != count) {
                listener.sendWindowProperty(this, GHOST_COUNT_PROPERTY_BASE + slotIndex, count);
                this.lastGhostCounts.put(slotIndex, count);
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.golem != null) {
            this.golem.paused = false;
        }
    }
}
