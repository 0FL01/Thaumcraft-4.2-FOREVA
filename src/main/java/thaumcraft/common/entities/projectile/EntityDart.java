package thaumcraft.common.entities.projectile;

public class EntityDart extends net.minecraft.entity.projectile.EntityArrow implements net.minecraft.entity.IProjectile, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityDart(net.minecraft.world.World world) { super(world); }
    public EntityDart(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter) {
        super(world, shooter);
    }

    @Override
    protected net.minecraft.item.ItemStack getArrowStack() { return net.minecraft.item.ItemStack.EMPTY; }

    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
