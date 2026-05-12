package thaumcraft.common.entities.monster;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.ai.misc.AIAltarFocus;

public class EntityCultistCleric extends EntityCultist implements net.minecraft.entity.IRangedAttackMob, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {

    private static final DataParameter<Boolean> RITUALIST = EntityDataManager.createKey(EntityCultistCleric.class, DataSerializers.BOOLEAN);

    public EntityCultistCleric(net.minecraft.world.World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIAltarFocus(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 2.0, 1.0, 20, 40, 24.0f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
        this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
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
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        // TODO: fire projectile
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override
    public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}
}
