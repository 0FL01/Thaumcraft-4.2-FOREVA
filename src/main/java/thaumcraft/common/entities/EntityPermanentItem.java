package thaumcraft.common.entities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Entity item that never despawns and is immune to explosions.
 * Used for key items on pedestals in Eldritch dimension rooms.
 */
public class EntityPermanentItem extends EntitySpecialItem {

    public EntityPermanentItem(World world) {
        super(world);
        this.lifespan = Integer.MAX_VALUE;
    }

    public EntityPermanentItem(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.lifespan = Integer.MAX_VALUE;
    }

    public EntityPermanentItem(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
        this.lifespan = Integer.MAX_VALUE;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        // Vanilla EntityItem does not persist lifespan; restore it after every load
        // so the tablet never despawns after a server restart.
        this.lifespan = Integer.MAX_VALUE;
    }

}
