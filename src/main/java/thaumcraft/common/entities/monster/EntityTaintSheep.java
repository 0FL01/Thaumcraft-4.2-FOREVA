package thaumcraft.common.entities.monster;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;

public class EntityTaintSheep extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob, net.minecraftforge.common.IShearable {
    private static final DataParameter<Byte> SHEEP_FLAGS = EntityDataManager.createKey(EntityTaintSheep.class, DataSerializers.BYTE);
    private int sheepTimer;

    public EntityTaintSheep(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.9f, 1.3f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SHEEP_FLAGS, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23);
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

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return net.minecraft.init.SoundEvents.ENTITY_SHEEP_AMBIENT; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return net.minecraft.init.SoundEvents.ENTITY_SHEEP_HURT; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return net.minecraft.init.SoundEvents.ENTITY_SHEEP_DEATH; }
    @Override protected float getSoundPitch() { return 0.7f; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
    }
}
