package thaumcraft.common.entities.projectile;

public class EntityPrimalArrow extends net.minecraft.entity.projectile.EntityArrow implements net.minecraft.entity.IProjectile, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityPrimalArrow(net.minecraft.world.World world) { super(world); }

    @Override public net.minecraft.item.ItemStack getArrowStack() { return net.minecraft.item.ItemStack.EMPTY; }
    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
