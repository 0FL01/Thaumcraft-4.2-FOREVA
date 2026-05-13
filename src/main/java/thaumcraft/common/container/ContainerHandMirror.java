package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public class ContainerHandMirror extends Container {
    private final EntityPlayer player;
    private final World worldObj;

    public ContainerHandMirror() {
        this(null, null, 0, 0, 0);
    }

    public ContainerHandMirror(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        this.player = playerInventory != null ? playerInventory.player : null;
        this.worldObj = world;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn != null && playerIn == this.player && !playerIn.isDead && playerIn.world == this.worldObj;
    }
}
