package thaumcraft.common.items;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.entities.projectile.EntityAlumentum;

public class BehaviorDispenseAlumetum extends BehaviorProjectileDispense {

    private static final BehaviorDefaultDispenseItem FALLBACK = new BehaviorDefaultDispenseItem();

    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        if (stack.getItemDamage() != ItemResource.META_ALUMENTUM) {
            return FALLBACK.dispense(source, stack);
        }
        return super.dispenseStack(source, stack);
    }

    @Override
    protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
        return new EntityAlumentum(worldIn, position.getX(), position.getY(), position.getZ());
    }

    @Override
    protected void playDispenseSound(IBlockSource source) {
        source.getWorld().playEvent(1009, source.getBlockPos(), 0);
    }
}
