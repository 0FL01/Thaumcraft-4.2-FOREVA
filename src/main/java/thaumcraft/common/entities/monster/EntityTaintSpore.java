package thaumcraft.common.entities.monster;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class EntityTaintSpore extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob, net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    protected int growth = 0;
    public float displaySize = 0.0f;
    private static final DataParameter<Integer> SPORE_SIZE = EntityDataManager.createKey(EntityTaintSpore.class, DataSerializers.VARINT);

    public EntityTaintSpore(net.minecraft.world.World world) {
        super(world);
        this.setSporeSize(2);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SPORE_SIZE, 1);
    }

    public void setSporeSize(int size) {
        this.dataManager.set(SPORE_SIZE, size);
        float s = Math.max(0.15f * (float)size, 0.5f);
        this.setSize(s, s);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.experienceValue = size;
    }

    public int getSporeSize() {
        return this.dataManager.get(SPORE_SIZE);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
            // TODO: check biome taint
        }
        if (this.getSporeSize() < 10 && this.growth++ == 1200) {
            this.setSporeSize(this.getSporeSize() + 1);
            this.growth = 0;
        }
    }

    @Override public boolean canBeCollidedWith() { return true; }
    @Override public boolean canBePushed() { return false; }

    @Override
    public void move(net.minecraft.entity.MoverType type, double x, double y, double z) {
        x = 0.0; z = 0.0;
        if (y > 0.0) y = 0.0;
        super.move(type, x, y, z);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSporeSize(nbt.getInteger("Size") + 1);
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Size", this.getSporeSize() - 1);
    }

    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}

    // TODO: add sound events
    // @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource source) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
    }
}