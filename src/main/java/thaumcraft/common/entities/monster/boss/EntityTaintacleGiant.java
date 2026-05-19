package thaumcraft.common.entities.monster.boss;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.List;

public class EntityTaintacleGiant extends thaumcraft.common.entities.monster.EntityTaintacle implements thaumcraft.api.entities.ITaintedMob {

    private final BossInfoServer bossInfo;
    private static final net.minecraft.network.datasync.DataParameter<Integer> ANGER =
            net.minecraft.network.datasync.EntityDataManager.createKey(EntityTaintacleGiant.class, net.minecraft.network.datasync.DataSerializers.VARINT);

    public EntityTaintacleGiant(World world) {
        super(world);
        this.setSize(1.1f, 6.0f);
        this.experienceValue = 20;
        this.bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ANGER, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(125.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0);
    }

    @Override
    public IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty, IEntityLivingData data) {
        EntityUtils.makeChampion(this, true);
        return data;
    }

    // --- Boss bar ---
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote) {
            this.bossInfo.setPercent(Math.max(0.0f, Math.min(1.0f, this.getHealth() / this.getMaxHealth())));
            if (this.getAnger() > 0) {
                this.setAnger(this.getAnger() - 1);
            }
            if (this.ticksExisted % 30 == 0) {
                this.heal(1.0f);
            }
        } else {
            if (this.rand.nextInt(15) == 0 && this.getAnger() > 0) {
                double d0 = this.rand.nextGaussian() * 0.02;
                double d1 = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                Thaumcraft.proxy.drawGenericParticles(
                        this.world,
                        this.posX + (double) (this.rand.nextFloat() * this.width) - (double) this.width / 2.0,
                        this.getEntityBoundingBox().minY + (double) this.height + (double) this.rand.nextFloat() * 0.5,
                        this.posZ + (double) (this.rand.nextFloat() * this.width) - (double) this.width / 2.0,
                        d0, d1, d2,
                        1.0F, 1.0F, 1.0F, 1.0F,
                        false, 81, 1, 1, 16, 0, 1.5F, 1);
            }
        }
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean isNonBoss() { return false; }

    // --- Anger ---
    public int getAnger() { return this.dataManager.get(ANGER); }
    public void setAnger(int anger) { this.dataManager.set(ANGER, anger); }

    // --- Enrage ---
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.world.isRemote && amount > 35.0f) {
            if (this.getAnger() == 0) {
                this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, (int)(amount / 15.0f)));
                this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, (int)(amount / 40.0f)));
                this.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, (int)(amount / 40.0f)));
                this.setAnger(200);
                if (source.getTrueSource() instanceof EntityPlayer) {
                    ((EntityPlayer)source.getTrueSource()).sendMessage(
                            new TextComponentTranslation("tc.boss.enrage", this.getDisplayName()));
                }
            }
            amount = 35.0f;
        }
        return super.attackEntityFrom(source, amount);
    }

    // --- Spawning ---
    @Override
    public boolean getCanSpawnHere() { return false; }

    @Override
    public boolean canDespawn() { return false; }

    @Override
    public boolean canBreatheUnderwater() { return true; }

    @Override
    protected int decreaseAirSupply(int air) { return air; }

    // --- Drops ---
    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        List<EntityTaintacleGiant> nearby = this.world.getEntitiesWithinAABB(EntityTaintacleGiant.class,
                new AxisAlignedBB(this.posX - 48, this.posY - 24, this.posZ - 48,
                                  this.posX + 48, this.posY + 24, this.posZ + 48));
        if (nearby.size() <= 1) {
            this.entityDropItem(new ItemStack(ConfigItems.itemEldritchObject, 1, 3), this.height / 2.0f);
        }
    }
}
