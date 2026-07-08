package thaumcraft.common.entities.monster;

import java.util.ArrayList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;

public class EntityTaintSporeSwarmer extends EntityTaintSpore {
    private int spawnCounter;
    private final ArrayList<Object> swarm = new ArrayList<>();

    public EntityTaintSporeSwarmer(World world) {
        super(world);
        this.spawnCounter = 500;
        this.setSporeSize(10);
    }

    @Override
    public void setSporeSize(int size) {
        super.setSporeSize(size);
        this.setSize(1.0F, 1.0F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.experienceValue = size;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(75.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.world.isRemote) {
            this.sploosh(10);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void sporeOnUpdate() {
        this.pushOutOfBlocks(this.posX, this.posY, this.posZ);

        if (this.spawnCounter > 0) {
            this.spawnCounter--;
        }
        if (this.spawnCounter <= 0 && this.world.getClosestPlayerToEntity(this, 16.0D) != null) {
            this.spawnCounter = 500;
            this.swarmBurst(1);
        }

        if (this.world.isRemote) {
            for (int i = 0; i < this.swarm.size();) {
                if (!thaumcraft.common.Thaumcraft.proxy.isParticleAlive(this.swarm.get(i))) {
                    this.swarm.remove(i);
                } else {
                    i++;
                }
            }
            int maxSwarmParticles = (500 - this.spawnCounter) / 25;
            if (this.swarm.size() < maxSwarmParticles) {
                this.swarm.add(thaumcraft.common.Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.1F, 10.0F, 0.0F));
            }
        }

        if (this.hurtResistantTime == 1) {
            this.swarmBurst(1);
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
    }

    protected void swarmBurst(int amount) {
        if (!this.world.isRemote) {
            this.playSound(thaumcraft.common.lib.TCSounds.GORE, 1.0F, 0.9F + this.rand.nextFloat() * 0.1F);
            for (int i = 0; i < amount; i++) {
                EntityTaintSwarm taintSwarm = new EntityTaintSwarm(this.world);
                taintSwarm.setLocationAndAngles(this.posX, this.posY + 0.5D, this.posZ, this.rand.nextFloat() * 360.0F, 0.0F);
                this.world.spawnEntity(taintSwarm);
            }
            this.world.setEntityState(this, (byte) 6);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 6) {
            this.spawnCounter = 500;
            this.sploosh(25);
            this.swarm.clear();
            return;
        }
        super.handleStatusUpdate(id);
    }

    @Override
    protected void sploosh(int amount) {
        for (int i = 0; i < amount; i++) {
            thaumcraft.common.Thaumcraft.proxy.splooshFX(this);
        }
    }

    @Override
    public int getExperiencePoints(EntityPlayer player) {
        return 200;
    }

    @Override
    public int getBrightnessForRender() {
        int x = MathHelper.floor(this.posX);
        int z = MathHelper.floor(this.posZ);
        if (this.world.isBlockLoaded(new BlockPos(x, 0, z))) {
            double yOffset = (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * 0.66D;
            int y = MathHelper.floor(this.posY - this.getYOffset() + yOffset);
            return this.world.getCombinedLight(new BlockPos(x, y, z), 0);
        }
        return 0;
    }

    @Override
    public float getBrightness() {
        int x = MathHelper.floor(this.posX);
        int z = MathHelper.floor(this.posZ);
        if (this.world.isBlockLoaded(new BlockPos(x, 0, z))) {
            double yOffset = (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * 0.66D;
            int y = MathHelper.floor(this.posY - this.getYOffset() + yOffset);
            return this.world.getLightBrightness(new BlockPos(x, y, z));
        }
        return 0.0F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return thaumcraft.common.lib.TCSounds.ROOTS;
    }

    @Override
    protected Item getDropItem() {
        return ConfigItems.itemResource;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        for (int i = 0; i <= 1; i++) {
            if (this.world.rand.nextBoolean()) {
                this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 11), this.height / 2.0F);
            } else {
                this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 12), this.height / 2.0F);
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.spawnCounter = nbt.hasKey("SpawnCounter") ? nbt.getInteger("SpawnCounter") : this.spawnCounter;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("SpawnCounter", this.spawnCounter);
    }
}
