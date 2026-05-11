package thaumcraft.common.entities.monster;

public class EntityCultistCleric extends thaumcraft.common.entities.monster.EntityCultist implements net.minecraft.entity.IRangedAttackMob, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public EntityCultistCleric(net.minecraft.world.World world) {
        super(world);
    }

    @Override
    public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {
        // TODO: fire projectile
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override
    public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
