package thaumcraft.common.entities.monster;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.blocks.BlockTaintFibres;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityTaintSpore extends EntityMob implements ITaintedMob, IEntityAdditionalSpawnData {
    public final ArrayList<Object> swarm = new ArrayList<>();
    protected int growth = 0;
    public float displaySize = 0.0f;
    private static final DataParameter<Integer> SPORE_SIZE =
        EntityDataManager.createKey(EntityTaintSpore.class, DataSerializers.VARINT);

    public EntityTaintSpore(World world) {
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
    public double getYOffset() {
        return 0.0D;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0D;
    }

    @Override
    public int getBrightnessForRender() {
        return 15728880;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }

    // --- No AI ---
    @Override protected void updateAITasks() {}

    // --- Rooted movement ---
    @Override
    public void move(MoverType type, double x, double y, double z) {
        x = 0.0; z = 0.0;
        if (y > 0.0) y = 0.0;
        BlockPos below = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
        if (this.world.getBlockState(below).getBlock() == ConfigBlocks.blockTaintFibres
            && this.world.getBlockState(below).getValue(BlockTaintFibres.TYPE) == 4) {
            return;
        }
        super.move(type, x, y, z);
    }

    // --- Core update ---
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted % 20 == 0
                && this.world.getBiome(this.getPosition()) != ThaumcraftWorldGenerator.biomeTaint) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0f);
        }
        this.sporeOnUpdate();
    }

    protected void sporeOnUpdate() {
        // Growth
        if (this.getSporeSize() < 10 && this.growth++ == 1200) {
            this.setSporeSize(this.getSporeSize() + 1);
            this.growth = 0;
        }
        // Client visual
        if (this.world.isRemote) {
            if (this.displaySize < (float)this.getSporeSize()) {
                this.displaySize += 0.02f;
            }
            for (int i = 0; i < this.swarm.size();) {
                if (!thaumcraft.common.Thaumcraft.proxy.isParticleAlive(this.swarm.get(i))) {
                    this.swarm.remove(i);
                } else {
                    i++;
                }
            }
            if (this.swarm.size() < this.getSporeSize() / 3) {
                this.swarm.add(thaumcraft.common.Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.1F, 10.0F, 0.0F));
            }
        }
        // Burst check
        BlockPos below = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
        boolean onMatureFibre = this.world.getBlockState(below).getBlock() == ConfigBlocks.blockTaintFibres
            && this.world.getBlockState(below).getValue(BlockTaintFibres.TYPE) == 4;
        if (!onMatureFibre || this.hurtResistantTime > 0) {
            this.spiderBurst();
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        this.spiderBurst();
    }

    protected void spiderBurst() {
        if (!this.world.isRemote) {
            this.playSound(TCSounds.GORE, 1.0f, 0.9f + this.rand.nextFloat() * 0.1f);
            int q = this.getSporeSize() / 3 + this.rand.nextInt(this.getSporeSize() / 2 + 1);
            for (int a = 0; a < q; ++a) {
                EntityTaintSpider spiderling = new EntityTaintSpider(this.world);
                spiderling.setLocationAndAngles(
                    this.posX + (double)(this.rand.nextFloat() - this.rand.nextFloat()),
                    this.posY + (double)this.rand.nextFloat(),
                    this.posZ + (double)(this.rand.nextFloat() - this.rand.nextFloat()),
                    this.rand.nextFloat() * 360.0f, 0.0f);
                this.world.spawnEntity(spiderling);
            }
            // Deplete fibre
            BlockPos below = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
            if (this.world.getBlockState(below).getBlock() == ConfigBlocks.blockTaintFibres) {
                int meta = this.world.getBlockState(below).getValue(BlockTaintFibres.TYPE);
                if (meta == 4) {
                    this.world.setBlockState(below, ConfigBlocks.blockTaintFibres.getDefaultState()
                        .withProperty(BlockTaintFibres.TYPE, 3), 3);
                }
            }
            this.setDead();
        } else {
            this.sploosh(50);
        }
    }

    protected void sploosh(int amount) {
        for (int i = 0; i < amount; i++) {
            thaumcraft.common.Thaumcraft.proxy.splooshFX(this);
        }
    }

    // --- Misc ---
    @Override public boolean canBeCollidedWith() { return true; }
    @Override public boolean canBePushed() { return false; }

    // --- NBT ---
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSporeSize(nbt.getInteger("Size") + 1);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Size", this.getSporeSize() - 1);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        try { this.displaySize = data.readFloat(); }
        catch (Exception e) {}
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeFloat((float)this.getSporeSize());
    }

    // --- Sounds ---
    @Override protected SoundEvent getAmbientSound() { return TCSounds.SWARM; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return TCSounds.GORE; }
    @Override protected SoundEvent getDeathSound() { return TCSounds.GORE; }
    @Override protected float getSoundVolume() { return 0.1f; }
    @Override protected int getExperiencePoints(EntityPlayer player) { return 200; }

    // --- Drops ---
    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        ItemStack drop = this.world.rand.nextBoolean()
            ? new ItemStack(ConfigItems.itemResource, 1, 11)
            : new ItemStack(ConfigItems.itemResource, 1, 12);
        this.entityDropItem(drop, this.height / 2.0f);
    }
}
