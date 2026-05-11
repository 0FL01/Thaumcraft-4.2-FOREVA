package thaumcraft.common.entities.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerGolem extends Container {
    public ContainerGolem() {}
    @Override public boolean canInteractWith(EntityPlayer player) { return false; }
}
