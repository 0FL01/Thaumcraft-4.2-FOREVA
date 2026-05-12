package thaumcraft.common.entities.golems;

public class EntityGolemBobber extends net.minecraft.entity.Entity implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {

    public EntityGolemBobber(net.minecraft.world.World world) { super(world); }

    public EntityGolemBobber(net.minecraft.world.World world, EntityGolemBase golem, int x, int y, int z) {
        super(world);
        this.setPosition(x + 0.5, y + 1.0, z + 0.5);
    }

    @Override protected void entityInit() {}
    @Override protected void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {}
    @Override protected void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {}
    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
