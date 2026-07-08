package thaumcraft.common.entities.monster;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity entityIn) {
        return true;
    }

    @Override
    public int getMaxFallHeight() {
        if (this.getAttackTarget() == null) {
            return 3;
        }
        return 3 + (int)(this.getHealth() - 1.0F);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier);
        this.timeSinceIgnited = (int)(this.timeSinceIgnited + distance * 1.5F);
        if (this.timeSinceIgnited > this.fuseTime - 5) {
            this.timeSinceIgnited = this.fuseTime - 5;
        }
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
            if (state > 0 && this.timeSinceIgnited == 0) {
                this.playSound(net.minecraft.init.SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
            }
            this.timeSinceIgnited += state;

            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime) {
                this.timeSinceIgnited = this.fuseTime;
                if (!this.world.isRemote) {
                    this.world.createExplosion(this, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 1.5F, false);
                    for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
                            new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ).grow(6.0D, 6.0D, 6.0D))) {
                        if (entity instanceof thaumcraft.api.entities.ITaintedMob) {
                            continue;
                        }
                        if (thaumcraft.common.config.Config.potionFluxTaint == null || entity.isPotionActive(thaumcraft.common.config.Config.potionFluxTaint)) {
                            continue;
                        }
                        entity.addPotionEffect(new PotionEffect(thaumcraft.common.config.Config.potionFluxTaint, 100, 0, false, true));
                    }
                    int y = (int) this.posY;
                    for (int i = 0; i < 10; i++) {
                        int x = (int) (this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 5.0F);
                        int z = (int) (this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 5.0F);
                        if (this.rand.nextBoolean() && this.world.getBiome(new BlockPos(x, 0, z)) != thaumcraft.common.lib.world.ThaumcraftWorldGenerator.biomeTaint) {
                            thaumcraft.common.lib.utils.Utils.setBiomeAt(this.world, x, z, thaumcraft.common.lib.world.ThaumcraftWorldGenerator.biomeTaint);
                        }
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockPos below = pos.down();
                        if (this.world.isSideSolid(below, EnumFacing.UP, false)
                                && this.world.getBlockState(pos).getMaterial().isReplaceable()) {
                            this.world.setBlockState(pos, thaumcraft.common.config.ConfigBlocks.blockTaintFibres.getDefaultState(), 3);
                        }
                    }
                    this.setDead();
                } else {
                    for (int i = 0; i < thaumcraft.common.Thaumcraft.proxy.particleCount(100); i++) {
                        thaumcraft.common.Thaumcraft.proxy.taintsplosionFX(this);
                    }
                }
            }
        }
        super.onUpdate();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.world.isRemote && this.ticksExisted < 5) {
            for (int i = 0; i < thaumcraft.common.Thaumcraft.proxy.particleCount(10); i++) {
                thaumcraft.common.Thaumcraft.proxy.splooshFX(this);
            }
        }
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("Fuse", 99)) {
            this.fuseTime = nbt.getShort("Fuse");
        }
        if (nbt.hasKey("ExplosionRadius", 99)) {
            this.explosionRadius = nbt.getByte("ExplosionRadius");
        }
        boolean powered = nbt.hasKey("powered", 1) ? nbt.getBoolean("powered") : nbt.getBoolean("Powered");
        this.dataManager.set(POWERED, powered);
        this.timeSinceIgnited = 0;
        this.lastActiveTime = this.timeSinceIgnited;
        this.dataManager.set(CREEPER_STATE, (int)nbt.getByte("CreeperState"));
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("powered", this.dataManager.get(POWERED));
        nbt.setShort("Fuse", (short)this.fuseTime);
        nbt.setByte("ExplosionRadius", (byte)this.explosionRadius);
        nbt.setBoolean("Powered", this.dataManager.get(POWERED));
        nbt.setByte("CreeperState", (byte)this.getCreeperState());
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
