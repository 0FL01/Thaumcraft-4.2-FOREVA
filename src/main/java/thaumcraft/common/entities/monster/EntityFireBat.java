package thaumcraft.common.entities.monster;

public class EntityFireBat extends net.minecraft.entity.monster.EntityMob {
    public static final byte FLAG_HANGING = 1;
    public static final byte FLAG_SUMMONED = 2;
    public static final byte FLAG_EXPLOSIVE = 4;
    public static final byte FLAG_DEVIL = 8;
    public static final byte FLAG_VAMPIRE = 16;

    private static final net.minecraft.network.datasync.DataParameter<Byte> FLAGS =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityFireBat.class, net.minecraft.network.datasync.DataSerializers.BYTE);

    public EntityFireBat(net.minecraft.world.World world) { super(world); this.setSize(0.5f, 0.9f); }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FLAGS, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }

    public boolean getFlag(byte flag) { return (this.dataManager.get(FLAGS) & flag) != 0; }
    public void setFlag(byte flag, boolean value) {
        byte b = this.dataManager.get(FLAGS);
        if (value) b |= flag; else b &= ~flag;
        this.dataManager.set(FLAGS, b);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote && this.getFlag(FLAG_HANGING)) {
            // FX
        }
        if (!this.world.isRemote && this.getFlag(FLAG_EXPLOSIVE) && this.getFlag(FLAG_SUMMONED)) {
            // Explode after timeout
        }
    }

    @Override public boolean isEntityInvulnerable(net.minecraft.util.DamageSource source) {
        return this.getFlag(FLAG_VAMPIRE) && source == net.minecraft.util.DamageSource.IN_WALL;
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextInt(3) == 0) this.dropItem(net.minecraft.init.Items.COAL, 1);
    }
}
