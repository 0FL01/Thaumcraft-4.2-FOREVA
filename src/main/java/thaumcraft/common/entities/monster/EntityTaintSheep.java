package thaumcraft.common.entities.monster;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.misc.AIConvertGrass;

public class EntityTaintSheep extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob, net.minecraftforge.common.IShearable {
    private static final DataParameter<Byte> SHEEP_FLAGS = EntityDataManager.createKey(EntityTaintSheep.class, DataSerializers.BYTE);
    private final AIConvertGrass convertGrassAI = new AIConvertGrass(this);
    private int sheepTimer;

    public EntityTaintSheep(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.9f, 1.3f);
        if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround) this.getNavigator()).setCanSwim(true);
        }
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, this.convertGrassAI);
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityVillager.class, 1.0, true));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SHEEP_FLAGS, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
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
    public int getTotalArmorValue() {
        return 1;
    }

    @Override
    public boolean isShearable(net.minecraft.item.ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos) {
        return !this.getSheared();
    }

    @Override
    public java.util.List<net.minecraft.item.ItemStack> onSheared(net.minecraft.item.ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos, int fortune) {
        java.util.ArrayList<net.minecraft.item.ItemStack> drops = new java.util.ArrayList<>();
        this.setSheared(true);
        int count = 1 + this.rand.nextInt(3);
        for (int i = 0; i < count; i++) {
            drops.add(new ItemStack(Blocks.WOOL, 1, 10));
        }
        return drops;
    }

    public boolean getSheared() {
        return (this.dataManager.get(SHEEP_FLAGS) & 0x10) != 0;
    }

    public void setSheared(boolean sheared) {
        byte flags = this.dataManager.get(SHEEP_FLAGS);
        if (sheared) {
            this.dataManager.set(SHEEP_FLAGS, (byte) (flags | 0x10));
        } else {
            this.dataManager.set(SHEEP_FLAGS, (byte) (flags & ~0x10));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Sheared", this.getSheared());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }

    @Override
    public void onLivingUpdate() {
        if (this.world.isRemote) {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }
        super.onLivingUpdate();
        if (this.world.isRemote && this.ticksExisted < 5) {
            for (int i = 0; i < Thaumcraft.proxy.particleCount(10); i++) {
                Thaumcraft.proxy.splooshFX(this);
            }
        }
    }

    @Override
    protected void updateAITasks() {
        this.sheepTimer = this.convertGrassAI.getConvertTimer();
        super.updateAITasks();
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 10) {
            this.sheepTimer = 40;
            return;
        }
        super.handleStatusUpdate(id);
    }

    public float getHeadRotationPointY(float partialTicks) {
        if (this.sheepTimer <= 0) {
            return 0.0F;
        }
        if (this.sheepTimer >= 4 && this.sheepTimer <= 36) {
            return 1.0F;
        }
        if (this.sheepTimer < 4) {
            return ((float) this.sheepTimer - partialTicks) / 4.0F;
        }
        return -((float) (this.sheepTimer - 40) - partialTicks) / 4.0F;
    }

    public float getHeadRotationAngleX(float partialTicks) {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
            float phase = ((float) (this.sheepTimer - 4) - partialTicks) / 32.0F;
            return 0.62831855F + 0.2199115F * MathHelper.sin(phase * 28.7F);
        }
        return this.sheepTimer > 0 ? 0.62831855F : this.rotationPitch * 0.017453292F;
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return SoundEvents.ENTITY_SHEEP_AMBIENT; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return SoundEvents.ENTITY_SHEEP_AMBIENT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return SoundEvents.ENTITY_SHEEP_AMBIENT; }
    @Override protected float getSoundPitch() { return 0.7f; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextInt(3) == 0) {
            if (this.world.rand.nextBoolean()) {
                this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
            } else {
                this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
            }
        }
    }
}
