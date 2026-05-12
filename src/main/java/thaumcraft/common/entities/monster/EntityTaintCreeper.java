package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class EntityTaintCreeper extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int fuseTime = 30;
    private int explosionRadius = 3;

    private static final DataParameter<Integer> CREEPER_STATE = EntityDataManager.createKey(EntityTaintCreeper.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityTaintCreeper.class, DataSerializers.BOOLEAN);

    public EntityTaintCreeper(net.minecraft.world.World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CREEPER_STATE, -1);
        this.dataManager.register(POWERED, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }

    public int getCreeperState() {
        return this.dataManager.get(CREEPER_STATE);
    }

    public void setCreeperState(int state) {
        this.dataManager.set(CREEPER_STATE, state);
    }

    @Override
    public void onUpdate() {
        if (this.isEntityAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            if (this.timeSinceIgnited >= 30) {
                this.timeSinceIgnited = 30;
                if (!this.world.isRemote) {
                    this.world.createExplosion(this, this.posX, this.posY + (double)(this.height / 2.0f), this.posZ, 1.5f, false);
                    this.setDead();
                }
            }
        }
        super.onUpdate();
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.readEntityFromNBT(nbt); }
    @Override public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.writeEntityToNBT(nbt); }

    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource source) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }
    @Override protected float getSoundPitch() { return 0.7f; }

    @Override
    protected net.minecraft.item.Item getDropItem() { return thaumcraft.common.config.ConfigItems.itemResource; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
    }
}