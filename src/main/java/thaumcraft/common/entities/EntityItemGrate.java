package thaumcraft.common.entities;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityItemGrate extends net.minecraft.entity.item.EntityItem {
    public EntityItemGrate(World world) {
        super(world);
    }

    public EntityItemGrate(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }
}
