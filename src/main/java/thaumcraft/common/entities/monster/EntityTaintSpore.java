package thaumcraft.common.entities.monster;

public class EntityTaintSpore extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityTaintSpore(net.minecraft.world.World world) { super(world); }

    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
