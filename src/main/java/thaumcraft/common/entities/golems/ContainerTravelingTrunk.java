package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerTravelingTrunk extends Container {
    public ContainerTravelingTrunk() {}
    @Override public boolean canInteractWith(EntityPlayer player) { return false; }
}
