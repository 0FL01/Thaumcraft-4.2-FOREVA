package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.entities.InventoryMob;
import thaumcraft.common.container.ContainerGhostSlots;

public class ContainerGolem extends ContainerGhostSlots {

    private EntityGolemBase golem;
    private InventoryMob mobInv;
    private final java.util.Set<Slot> ghostSlots = new java.util.HashSet<>();
    public int currentScroll = 0;
    public int maxScroll = 0;

    public ContainerGolem() {}

    public ContainerGolem(InventoryPlayer playerInv, EntityGolemBase golem) {
        this.setGolem(golem);
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
            Slot slot = new Slot(this.mobInv, slotIndex, 100 + a / 2 * 28, 16 + a % 2 * 31);
            this.ghostSlots.add(slot);
            this.addSlotToContainer(slot);
        }
    }

    public void refreshInventory(InventoryPlayer playerInv) {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.ghostSlots.clear();
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
        return player.getDistanceSq(this.golem) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (this.golem == null) return false;

        if (id == 66 && this.currentScroll > 0) {
            this.currentScroll--;
            refreshInventory(player.inventory);
        } else if (id == 67 && this.currentScroll < this.maxScroll) {
            this.currentScroll++;
            refreshInventory(player.inventory);
        } else if (id >= 50 && id <= 57) {
            int toggle = id - 50;
            this.golem.setToggle(toggle, !this.golem.getToggles()[toggle]);
        }

        int slots = this.mobInv != null ? this.mobInv.slotCount : 0;
        if (id >= 0 && id < slots) {
            int color = this.golem.getColors(id) - 1;
            if (color < -1) color = 15;
            this.golem.setColors(id, color);
        } else if (id >= slots && id < slots * 2) {
            int color = this.golem.getColors(id - slots) + 1;
            if (color > 15) color = -1;
            this.golem.setColors(id - slots, color);
        }

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

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.golem != null) {
            this.golem.paused = false;
        }
    }
}
