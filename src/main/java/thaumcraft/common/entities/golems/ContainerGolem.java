package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import thaumcraft.common.container.ContainerGhostSlots;

public class ContainerGolem extends ContainerGhostSlots {

    private EntityGolemBase golem;

    public ContainerGolem() {}

    public void setGolem(EntityGolemBase golem) {
        this.golem = golem;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (this.golem == null || this.golem.isDead) return false;
        return player.getDistanceSq(this.golem) <= 64.0D;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.golem != null) {
            this.golem.paused = false;
        }
    }
}
