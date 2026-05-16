package thaumcraft.common.entities.monster;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AICreeperSwell;

public class EntityTaintCreeper extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int fuseTime = 30;
    private int explosionRadius = 3;

    private static final DataParameter<Integer> CREEPER_STATE = EntityDataManager.createKey(EntityTaintCreeper.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityTaintCreeper.class, DataSerializers.BOOLEAN);

    public EntityTaintCreeper(net.minecraft.world.World world) {
        super(world);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new AICreeperSwell(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0f, 1.0, 1.2));
        this.tasks.addTask(4, new AIAttackOnCollide(this, 1.0, false));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, false));
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

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public int getCreeperState() {
        return this.dataManager.get(CREEPER_STATE);
    }

    public void setCreeperState(int state) {
        this.dataManager.set(CREEPER_STATE, state);
    }

    public boolean getPowered() {
        return this.dataManager.get(POWERED);
    }

    public float getCreeperFlashIntensity(float partialTicks) {
        return ((float) this.lastActiveTime + (float) (this.timeSinceIgnited - this.lastActiveTime) * partialTicks) / 28.0F;
    }

    @Override
    public void onUpdate() {
        if (this.isEntityAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;

            int state = this.getCreeperState();
            if (state > 0 && this.timeSinceIgnited < this.fuseTime) {
                this.timeSinceIgnited++;
            } else if (state <= 0 && this.timeSinceIgnited > 0) {
                this.timeSinceIgnited--;
            }

            if (this.timeSinceIgnited >= this.fuseTime) {
                this.timeSinceIgnited = this.fuseTime;
                if (!this.world.isRemote) {
                    boolean powered = this.dataManager.get(POWERED);
                    float power = powered ? 2.0F : 1.5F;
                    this.world.createExplosion(this, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, power, false);
                    this.setDead();
                }
            }
        }
        super.onUpdate();
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.timeSinceIgnited = nbt.getShort("Fuse");
        this.lastActiveTime = this.timeSinceIgnited;
        this.dataManager.set(CREEPER_STATE, (int)nbt.getByte("CreeperState"));
        this.dataManager.set(POWERED, nbt.getBoolean("Powered"));
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setShort("Fuse", (short)this.timeSinceIgnited);
        nbt.setByte("CreeperState", (byte)this.getCreeperState());
        nbt.setBoolean("Powered", this.dataManager.get(POWERED));
    }

    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource source) { return net.minecraft.init.SoundEvents.ENTITY_CREEPER_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return net.minecraft.init.SoundEvents.ENTITY_CREEPER_DEATH; }
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
