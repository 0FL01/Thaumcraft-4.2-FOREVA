package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.entities.InventoryMob;
import thaumcraft.common.container.ContainerGhostSlots;

public class ContainerGolem extends ContainerGhostSlots {

    private EntityGolemBase golem;
    private InventoryMob mobInv;
    private final java.util.Set<Slot> ghostSlots = new java.util.HashSet<>();

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

        if (this.mobInv != null && ItemGolemCore.hasInventory(this.golem.getCore())) {
            int visibleSlots = Math.min(6, this.mobInv.getSizeInventory());
            for (int a = 0; a < visibleSlots; a++) {
                Slot slot = new Slot(this.mobInv, a, 100 + a / 2 * 28, 16 + a % 2 * 31);
                this.ghostSlots.add(slot);
                this.addSlotToContainer(slot);
            }
        }

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
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.golem != null) {
            this.golem.paused = false;
        }
    }
}
