
package thaumcraft.common.lib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityUtils {
    public static EntityItem dropItemStack(World world, BlockPos pos, ItemStack stack) {
        return dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }
    
    public static EntityItem dropItemStack(World world, double x, double y, double z, ItemStack stack) {
        return new EntityItem(world, x, y, z, stack);
    }
}
