package thaumcraft.common.entities;

public class EntityAspectOrb extends net.minecraft.entity.Entity implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityAspectOrb(net.minecraft.world.World world) { super(world); }

    @Override public void entityInit() {}
    @Override public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {}
    @Override public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {}
    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
