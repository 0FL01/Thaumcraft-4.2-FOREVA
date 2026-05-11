package thaumcraft.common.entities.monster;

public class EntityEldritchCrab extends net.minecraft.entity.monster.EntityMob {
    private static final net.minecraft.network.datasync.DataParameter<Byte> HELM = 
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityEldritchCrab.class, net.minecraft.network.datasync.DataSerializers.BYTE);

    public EntityEldritchCrab(net.minecraft.world.World world) { super(world); }

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
