package thaumcraft.common.entities;

public class EntitySpecialItem extends net.minecraft.entity.item.EntityItem {
    public EntitySpecialItem(net.minecraft.world.World world) { super(world); }
    public EntitySpecialItem(net.minecraft.world.World world, double x, double y, double z) { super(world, x, y, z); }
    public EntitySpecialItem(net.minecraft.world.World world, double x, double y, double z, net.minecraft.item.ItemStack stack) { super(world, x, y, z, stack); }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }
}
