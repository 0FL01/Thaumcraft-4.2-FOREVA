package thaumcraft.common.entities.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;

public class EntityGiantBrainyZombie extends thaumcraft.common.entities.monster.EntityBrainyZombie {
    private static final DataParameter<Float> ANGER =
        EntityDataManager.createKey(EntityGiantBrainyZombie.class, DataSerializers.FLOAT);

    public EntityGiantBrainyZombie(net.minecraft.world.World world) {
        super(world);
        this.experienceValue = 15;
        float scale = 1.2F + this.getAnger();
        this.stepHeight *= scale;
        this.setSize(this.width * scale, this.height * scale);
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ANGER, 1.0F);
    }

    public float getAnger() {
        return this.dataManager.get(ANGER);
    }

    public void setAnger(float anger) {
        this.dataManager.set(ANGER, anger);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.getAnger() > 1.0F) {
            this.setAnger(this.getAnger() - 0.002F);
            float scale = 1.2F + this.getAnger();
            this.setSize(0.6F * scale, 1.8F * scale);
        }
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
            .setBaseValue(7.0D + (double) ((this.getAnger() - 1.0F) * 5.0F));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        this.setAnger(Math.min(2.0F, this.getAnger() + 0.1F));
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        for (int i = 0; i < 6; i++) {
            if (this.world.rand.nextBoolean()) {
                this.dropItem(net.minecraft.init.Items.ROTTEN_FLESH, 2);
            }
        }
        for (int i = 0; i < 6; i++) {
            if (this.world.rand.nextBoolean()) {
                this.dropItem(net.minecraft.init.Items.ROTTEN_FLESH, 2);
            }
        }
        if (this.world.rand.nextInt(10) - looting <= 4) {
            this.entityDropItem(new net.minecraft.item.ItemStack(ConfigItems.itemZombieBrain), 1.5F);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("Anger")) {
            this.setAnger(nbt.getFloat("Anger"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("Anger", this.getAnger());
    }
}
