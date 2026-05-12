package thaumcraft.common.entities.monster;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.pech.AIPechItemEntityGoto;
import thaumcraft.common.entities.ai.pech.AIPechTradePlayer;

public class EntityPech extends net.minecraft.entity.monster.EntityMob implements net.minecraft.entity.IRangedAttackMob {
    private static final net.minecraft.network.datasync.DataParameter<Integer> PECH_TYPE =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityPech.class, net.minecraft.network.datasync.DataSerializers.VARINT);
    private static final net.minecraft.network.datasync.DataParameter<Boolean> ANGRY =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityPech.class, net.minecraft.network.datasync.DataSerializers.BOOLEAN);

    public boolean trading = false;

    public EntityPech(net.minecraft.world.World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIPechTradePlayer(this));
        this.tasks.addTask(3, new AIPechItemEntityGoto(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.5));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0f, 1.0f));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, net.minecraft.entity.EntityLiving.class, 8.0f));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, false));
    }

    public void setCombatTask() {
        this.tasks.removeTask(this.aiMeleeAttack);
        // Replaced EntityAIArrowAttack with EntityAIAttackRanged
        this.tasks.removeTask(this.aiRangedAttack);
        ItemStack itemstack = this.getHeldItemMainhand();
        if (!itemstack.isEmpty() && itemstack.getItem() instanceof net.minecraft.item.ItemBow) {
            this.tasks.addTask(2, this.aiRangedAttack);
        } else {
            this.tasks.addTask(2, this.aiMeleeAttack);
        }
        if (!this.isTamed()) {
            this.tasks.addTask(4, this.aiAvoidPlayer);
        } else {
            this.tasks.removeTask(this.aiAvoidPlayer);
        }
    }

    private final AIAttackOnCollide aiMeleeAttack = new AIAttackOnCollide(this, net.minecraft.entity.EntityLivingBase.class, 0.6, false);
    private final EntityAIAttackRanged aiRangedAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0f);
    private final EntityAIAvoidEntity<EntityPlayer> aiAvoidPlayer =
        new EntityAIAvoidEntity<>(this, EntityPlayer.class, 8.0f, 0.5, 0.6);

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

    public boolean isTamed() {
        return false;
    }

    public boolean canPickup(ItemStack stack) {
        return stack != null && !stack.isEmpty() && this.isEntityAlive() && !this.trading;
    }

    public ItemStack pickupItem(ItemStack stack) {
        if (!canPickup(stack)) return stack;
        return ItemStack.EMPTY;
    }

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

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.trading = nbt.getBoolean("trading");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("trading", this.trading);
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return thaumcraft.common.lib.TCSounds.PECH_IDLE; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return thaumcraft.common.lib.TCSounds.PECH_HIT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return thaumcraft.common.lib.TCSounds.PECH_DEATH; }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }

    @Override public int getMaxSpawnedInChunk() { return 3; }
}
