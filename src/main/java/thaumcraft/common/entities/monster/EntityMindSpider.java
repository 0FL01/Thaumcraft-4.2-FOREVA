package thaumcraft.common.entities.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class EntityMindSpider extends net.minecraft.entity.monster.EntitySpider {
    private static final DataParameter<Byte> HARMLESS =
            EntityDataManager.createKey(EntityMindSpider.class, DataSerializers.BYTE);
    private static final DataParameter<String> VIEWER =
            EntityDataManager.createKey(EntityMindSpider.class, DataSerializers.STRING);

    private int lifeSpan = Integer.MAX_VALUE;

    public EntityMindSpider(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.3F, 0.3F);
        this.experienceValue = 1;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HARMLESS, (byte) 0);
        this.dataManager.register(VIEWER, "");
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        if (this.isHarmless()) {
            return 0;
        }
        return super.getExperiencePoints(player);
    }

    public boolean isHarmless() {
        return this.dataManager.get(HARMLESS) != 0;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted > this.lifeSpan) {
            this.setDead();
        }
    }

    @Override
    protected float getSoundPitch() { return 0.7F; }

    @Override
    public boolean isAIDisabled() { return this.isHarmless(); }

    @Override
    public boolean isEntityInvulnerable(net.minecraft.util.DamageSource src) { return this.isHarmless(); }

    public float spiderScaleAmount() {
        return 0.3F;
    }

    @Override
    public double getYOffset() {
        return this.isHarmless() ? 0.0D : 0.1D;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity entityIn) {
        if (this.isHarmless()) {
            return false;
        }
        return super.attackEntityAsMob(entityIn);
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {}
    @Override public int getMaxSpawnedInChunk() { return 200; }

    // ---- WarpEvents phantom spider support ----

    public void setViewer(String name) {
        this.dataManager.set(VIEWER, String.valueOf(name));
    }

    public String getViewer() {
        return this.dataManager.get(VIEWER);
    }

    public void setHarmless(boolean harmless) {
        if (harmless) {
            this.lifeSpan = 1200;
        }
        this.dataManager.set(HARMLESS, harmless ? (byte) 1 : (byte) 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.dataManager.set(HARMLESS, nbt.getByte("harmless"));
        this.dataManager.set(VIEWER, String.valueOf(nbt.getString("viewer")));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("harmless", this.dataManager.get(HARMLESS));
        nbt.setString("viewer", this.dataManager.get(VIEWER));
    }
}
