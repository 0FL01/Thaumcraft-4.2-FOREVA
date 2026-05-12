package thaumcraft.common.entities.monster;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;

public class EntityEldritchCrab extends net.minecraft.entity.monster.EntityMob {
    private static final net.minecraft.network.datasync.DataParameter<Byte> HELM = 
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityEldritchCrab.class, net.minecraft.network.datasync.DataSerializers.BYTE);

    public EntityEldritchCrab(net.minecraft.world.World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.63f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HELM, (byte) 1);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4);
    }

    public boolean hasHelm() { return this.dataManager.get(HELM) == 1; }
    public void setHelm(boolean b) { this.dataManager.set(HELM, (byte) (b ? 1 : 0)); }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (!this.world.isRemote && hasHelm() && amount >= 5.0f) {
            setHelm(false);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.readEntityFromNBT(nbt); }
    @Override public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.writeEntityToNBT(nbt); }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }
}
