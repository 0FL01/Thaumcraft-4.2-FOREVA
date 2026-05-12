package thaumcraft.common.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerPech extends Container {

    public ContainerPech() {}

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
