package thaumcraft.common.entities.golems;

public class EntityGolemBase extends net.minecraft.entity.monster.EntityGolem implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityGolemBase(net.minecraft.world.World world) { super(world); }

    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
