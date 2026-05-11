package thaumcraft.common.entities.monster;

public class EntityPech extends net.minecraft.entity.monster.EntityMob implements net.minecraft.entity.IRangedAttackMob {
    private static final net.minecraft.network.datasync.DataParameter<Integer> PECH_TYPE =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityPech.class, net.minecraft.network.datasync.DataSerializers.VARINT);
    private static final net.minecraft.network.datasync.DataParameter<Boolean> ANGRY =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityPech.class, net.minecraft.network.datasync.DataSerializers.BOOLEAN);

    public EntityPech(net.minecraft.world.World world) { super(world); }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(PECH_TYPE, 0);
        this.dataManager.register(ANGRY, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }

    public int getPechType() { return this.dataManager.get(PECH_TYPE); }
    public boolean isAngry() { return this.dataManager.get(ANGRY); }
    public void setAngry(boolean b) { this.dataManager.set(ANGRY, b); }

    @Override
    public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {
        // TODO: pech blast
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.ticksExisted % 40 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0f);
        }
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (!this.world.isRemote && source.getTrueSource() instanceof net.minecraft.entity.player.EntityPlayer) {
            this.setAngry(true);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.readEntityFromNBT(nbt); }
    @Override public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.writeEntityToNBT(nbt); }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }

    @Override public int getMaxSpawnedInChunk() { return 3; }
}
