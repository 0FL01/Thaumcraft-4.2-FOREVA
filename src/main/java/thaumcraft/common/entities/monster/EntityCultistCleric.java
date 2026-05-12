package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class EntityCultistCleric extends thaumcraft.common.entities.monster.EntityCultist implements net.minecraft.entity.IRangedAttackMob, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {

    private static final DataParameter<Boolean> RITUALIST = EntityDataManager.createKey(EntityCultistCleric.class, DataSerializers.BOOLEAN);

    public EntityCultistCleric(net.minecraft.world.World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(RITUALIST, false);
    }

    public boolean getIsRitualist() {
        return this.dataManager.get(RITUALIST);
    }

    public void setIsRitualist(boolean value) {
        this.dataManager.set(RITUALIST, value);
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
