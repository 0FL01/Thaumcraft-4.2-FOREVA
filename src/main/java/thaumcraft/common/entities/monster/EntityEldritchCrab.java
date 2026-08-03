package thaumcraft.common.entities.monster;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;

public class EntityEldritchCrab extends net.minecraft.entity.monster.EntityMob {
    static final String ATTACHED_CRAB_TAG = "ThaumcraftAttachedEldritchCrab";
    private static final String SNAPSHOT_ENTITY_TAG = "Entity";
    private static final String SNAPSHOT_ATTACK_TIME_TAG = "AttackTime";
    private static final net.minecraft.network.datasync.DataParameter<Byte> HELM = 
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityEldritchCrab.class, net.minecraft.network.datasync.DataSerializers.BYTE);
    private int ridingAttackTime;

    public EntityEldritchCrab(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.8F, 0.6F);
        this.experienceValue = 6;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.63f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
        if (this.getNavigator() instanceof net.minecraft.pathfinding.PathNavigateGround) {
            net.minecraft.pathfinding.PathNavigateGround navigator =
                    (net.minecraft.pathfinding.PathNavigateGround) this.getNavigator();
            navigator.setCanSwim(true);
            navigator.setEnterDoors(true);
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HELM, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.hasHelm() ? 0.275 : 0.3);
    }

    public boolean hasHelm() { return this.dataManager.get(HELM) == 1; }
    public void setHelm(boolean b) {
        this.dataManager.set(HELM, (byte) (b ? 1 : 0));
        if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null) {
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(b ? 0.275 : 0.3);
        }
    }

    @Override
    public int getTotalArmorValue() {
        return this.hasHelm() ? 5 : 0;
    }

    @Override
    public double getYOffset() {
        return this.isRiding() ? 0.5D : 0.0D;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ridingAttackTime > 0) --this.ridingAttackTime;
        if (this.ticksExisted < 20) this.fallDistance = 0.0F;
        EntityLivingBase target = this.getAttackTarget();
        if (!this.world.isRemote && !this.isRiding() && target != null && !target.isBeingRidden() && !this.onGround
                && !this.hasHelm() && !target.isDead
                && this.posY - target.posY >= target.height / 2.0F
                && this.getDistanceSq(target) < 4.0D) {
            this.attachTo(target);
        }
        if (!this.world.isRemote && this.isRiding() && this.ridingAttackTime <= 0) {
            this.ridingAttackTime = 10 + this.rand.nextInt(10);
            Entity mount = this.getRidingEntity();
            if (mount != null) {
                this.attackEntityAsMob(mount);
                if (this.rand.nextFloat() < 0.2F) this.dismountRidingEntity();
            }
        }
    }

    private boolean attachTo(EntityLivingBase target) {
        if (this.world.isRemote || !this.startRiding(target, true)) {
            return false;
        }
        // The vehicle's tracker does not send its passenger list to the tracked player itself.
        this.syncPassengerState(target);
        return true;
    }

    @Override
    public void dismountRidingEntity() {
        Entity vehicle = this.getRidingEntity();
        if (!this.world.isRemote && vehicle instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) vehicle;
            MinecraftServer server = player.getServer();
            // Integrated-server disconnect removes passengers before saving and stopping.
            if (player.hasDisconnected() && server != null && server.isSinglePlayer()) {
                preserveAttachedCrab(player);
            }
        }
        super.dismountRidingEntity();
        if (vehicle != null && this.getRidingEntity() == null) {
            this.syncPassengerState(vehicle);
        }
    }

    private void syncPassengerState(Entity vehicle) {
        if (!this.world.isRemote && vehicle instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) vehicle;
            if (player.connection != null) {
                player.connection.sendPacket(new SPacketSetPassengers(vehicle));
            }
        }
    }

    // Player NBT writes passengers, but the player load path does not recreate them.
    public static void preserveAttachedCrabs(MinecraftServer server) {
        if (server == null || server.getPlayerList() == null) {
            return;
        }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            preserveAttachedCrab(player);
        }
    }

    static void preserveAttachedCrab(EntityPlayer player) {
        for (Entity passenger : player.getPassengers()) {
            if (passenger instanceof EntityEldritchCrab && !passenger.isDead) {
                getPersistedPlayerData(player).setTag(ATTACHED_CRAB_TAG,
                        createAttachedCrabSnapshot((EntityEldritchCrab) passenger));
                return;
            }
        }
    }

    private static NBTTagCompound getPersistedPlayerData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    static NBTTagCompound createAttachedCrabSnapshot(EntityEldritchCrab crab) {
        NBTTagCompound snapshot = new NBTTagCompound();
        snapshot.setTag(SNAPSHOT_ENTITY_TAG, crab.writeToNBT(new NBTTagCompound()));
        snapshot.setInteger(SNAPSHOT_ATTACK_TIME_TAG, crab.ridingAttackTime);
        return snapshot;
    }

    static EntityEldritchCrab loadAttachedCrabSnapshot(EntityPlayer player) {
        NBTTagCompound snapshot = getPersistedPlayerData(player).getCompoundTag(ATTACHED_CRAB_TAG);
        if (!snapshot.hasKey(SNAPSHOT_ENTITY_TAG, 10)) {
            return null;
        }
        EntityEldritchCrab crab = new EntityEldritchCrab(player.world);
        crab.readFromNBT(snapshot.getCompoundTag(SNAPSHOT_ENTITY_TAG));
        crab.ridingAttackTime = Math.max(0, snapshot.getInteger(SNAPSHOT_ATTACK_TIME_TAG));
        return crab;
    }

    public static void restoreAttachedCrab(EntityPlayerMP player) {
        if (!player.getEntityData().hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) {
            return;
        }
        NBTTagCompound playerData = getPersistedPlayerData(player);
        if (!playerData.hasKey(ATTACHED_CRAB_TAG, 10)) {
            return;
        }

        EntityEldritchCrab restored = loadAttachedCrabSnapshot(player);
        if (restored == null) {
            playerData.removeTag(ATTACHED_CRAB_TAG);
            return;
        }
        if (player.isBeingRidden()) {
            for (Entity passenger : player.getPassengers()) {
                if (passenger instanceof EntityEldritchCrab
                        && passenger.getUniqueID().equals(restored.getUniqueID())) {
                    playerData.removeTag(ATTACHED_CRAB_TAG);
                    return;
                }
            }
            return;
        }

        EntityEldritchCrab crab = restored;
        if (player.world instanceof WorldServer) {
            Entity existing = ((WorldServer) player.world).getEntityFromUuid(restored.getUniqueID());
            if (existing != null) {
                if (!(existing instanceof EntityEldritchCrab) || existing.isRiding()) {
                    playerData.removeTag(ATTACHED_CRAB_TAG);
                    return;
                }
                crab = (EntityEldritchCrab) existing;
                crab.ridingAttackTime = restored.ridingAttackTime;
            }
        }

        if (crab == restored) {
            crab.setPosition(player.posX, player.posY, player.posZ);
            if (!player.world.spawnEntity(crab)) {
                return;
            }
        }

        if (crab.attachTo(player)) {
            playerData.removeTag(ATTACHED_CRAB_TAG);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData) {
        if (this.world.getDifficulty() == EnumDifficulty.HARD) {
            this.setHelm(true);
        } else {
            this.setHelm(this.rand.nextFloat() < 0.33F);
        }
        return super.onInitialSpawn(difficulty, livingData);
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        boolean hit = super.attackEntityFrom(source, amount);
        if (!this.world.isRemote && hit && this.hasHelm() && this.getHealth() / this.getMaxHealth() <= 0.5F) {
            this.setHelm(false);
            this.renderBrokenItemStack(new ItemStack(ConfigItems.itemChestCultistPlate));
        }
        return hit;
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("Flags")) {
            this.dataManager.set(HELM, nbt.getByte("Flags"));
            this.setHelm(this.hasHelm());
        }
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("Flags", this.dataManager.get(HELM));
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return thaumcraft.common.lib.TCSounds.CRABTALK; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return net.minecraft.init.SoundEvents.ENTITY_GUARDIAN_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return thaumcraft.common.lib.TCSounds.CRABDEATH; }

    @Override
    public int getTalkInterval() {
        return 160;
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
        if (wasRecentlyHit && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + looting) > 0)) {
            this.dropItem(Items.SPIDER_EYE, 1);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            this.playSound(thaumcraft.common.lib.TCSounds.CRABCLAW,
                    1.0F, 0.9F + this.world.rand.nextFloat() * 0.2F);
            return true;
        }
        return false;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect.getPotion() != MobEffects.POISON && super.isPotionApplicable(effect);
    }
}
