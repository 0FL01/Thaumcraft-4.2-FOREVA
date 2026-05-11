package thaumcraft.common.entities.monster;

public class EntityWisp extends net.minecraft.entity.EntityFlying implements net.minecraft.entity.monster.IMob {
    private static final net.minecraft.network.datasync.DataParameter<String> WISP_TYPE =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityWisp.class, net.minecraft.network.datasync.DataSerializers.STRING);

    public EntityWisp(net.minecraft.world.World world) { super(world); this.setSize(0.5f, 0.5f); }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(WISP_TYPE, "");
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
    }

    public String getWispType() { return this.dataManager.get(WISP_TYPE); }
    public void setWispType(String s) { this.dataManager.set(WISP_TYPE, s); }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
            // TODO: lightning zap attack on nearest player
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.world.rand.nextInt(4) == 0 && super.getCanSpawnHere();
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        // TODO: drop ItemWispEssence with aspect
    }

    @Override public int getMaxSpawnedInChunk() { return 3; }
}
