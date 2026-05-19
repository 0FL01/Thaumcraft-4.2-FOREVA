package thaumcraft.common.entities.monster.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.utils.EntityUtils;

public class EntityThaumcraftBoss extends EntityMob {
    private static final DataParameter<Integer> ANGER =
            net.minecraft.network.datasync.EntityDataManager.createKey(EntityThaumcraftBoss.class, DataSerializers.VARINT);
    private static final AttributeModifier[] HP_BUFFS = new AttributeModifier[] {
            new AttributeModifier(UUID.fromString("54d621c1-dd4d-4b43-8bd2-5531c8875797"), "HEALTH BUFF 1", 50.0, 0),
            new AttributeModifier(UUID.fromString("f51257dc-b7fa-4f7a-92d7-75d68e8592c4"), "HEALTH BUFF 2", 50.0, 0),
            new AttributeModifier(UUID.fromString("3d6b2e42-4141-4364-b76d-0e8664bbd0bb"), "HEALTH BUFF 3", 50.0, 0),
            new AttributeModifier(UUID.fromString("02c97a08-801c-4131-afa2-1427a6151934"), "HEALTH BUFF 4", 50.0, 0),
            new AttributeModifier(UUID.fromString("0f354f6a-33c5-40be-93be-81b1338567f1"), "HEALTH BUFF 5", 50.0, 0)
    };
    private static final AttributeModifier[] DAMAGE_BUFFS = new AttributeModifier[] {
            new AttributeModifier(UUID.fromString("534f8c57-929a-48cf-bbd6-0fd851030748"), "DAMAGE BUFF 1", 0.5, 0),
            new AttributeModifier(UUID.fromString("d317a76e-0e7c-4c61-acfd-9fa286053b32"), "DAMAGE BUFF 2", 0.5, 0),
            new AttributeModifier(UUID.fromString("ff462d63-26a2-4363-830e-143ed97e2a4f"), "DAMAGE BUFF 3", 0.5, 0),
            new AttributeModifier(UUID.fromString("cf1eb39e-0c67-495f-887c-0d3080828d2f"), "DAMAGE BUFF 4", 0.5, 0),
            new AttributeModifier(UUID.fromString("3cfab9da-2701-43d8-ac07-885f16fa4117"), "DAMAGE BUFF 5", 0.5, 0)
    };

    private final BossInfoServer bossInfo;
    private final Map<Integer, Integer> aggro = new HashMap<>();
    protected int spawnTimer = 0;

    public EntityThaumcraftBoss(net.minecraft.world.World world) {
        super(world);
        this.experienceValue = 50;
        this.bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ANGER, 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("HomeD")) {
            this.setHomePosAndDistance(
                    new BlockPos(compound.getInteger("HomeX"), compound.getInteger("HomeY"), compound.getInteger("HomeZ")),
                    compound.getInteger("HomeD"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.hasHome() && this.getMaximumHomeDistance() > 0.0F) {
            BlockPos home = this.getHomePosition();
            compound.setInteger("HomeD", (int) this.getMaximumHomeDistance());
            compound.setInteger("HomeX", home.getX());
            compound.setInteger("HomeY", home.getY());
            compound.setInteger("HomeZ", home.getZ());
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.95);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }

    @Override
    protected void updateAITasks() {
        if (this.getSpawnTimer() == 0) {
            super.updateAITasks();
        }
        if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive()) {
            this.setAttackTarget(null);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        this.setHomePosAndDistance(new BlockPos(this), 24);
        return data;
    }

    public int getAnger() {
        return this.dataManager.get(ANGER);
    }

    public void setAnger(int anger) {
        this.dataManager.set(ANGER, anger);
    }

    public int getSpawnTimer() {
        return this.spawnTimer;
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (!this.world.isRemote) {
            Entity attacker = source.getTrueSource();
            if (attacker instanceof EntityLivingBase) {
                int id = attacker.getEntityId();
                int aggroValue = (int) amount;
                if (this.aggro.containsKey(id)) {
                    aggroValue += this.aggro.get(id);
                }
                this.aggro.put(id, aggroValue);
            }
            if (amount > 35.0F) {
                if (this.getAnger() == 0) {
                    this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, (int) (amount / 15.0F)));
                    this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, (int) (amount / 40.0F)));
                    this.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, (int) (amount / 40.0F)));
                    this.setAnger(200);
                    if (attacker instanceof net.minecraft.entity.player.EntityPlayer) {
                        TextComponentString message = new TextComponentString(this.getName() + " ");
                        message.appendSibling(new TextComponentTranslation("tc.boss.enrage"));
                        ((net.minecraft.entity.player.EntityPlayer) attacker).sendMessage(message);
                    }
                }
                amount = 35.0F;
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.getSpawnTimer() > 0) {
            --this.spawnTimer;
        }
        if (this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
        }
        if (this.world.isRemote) {
            if (this.rand.nextInt(15) == 0 && this.getAnger() > 0) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                Thaumcraft.proxy.drawGenericParticles(
                        this.world,
                        this.posX + (double) (this.rand.nextFloat() * this.width) - (double) this.width / 2.0D,
                        this.getEntityBoundingBox().minY + (double) this.height + (double) this.rand.nextFloat() * 0.5D,
                        this.posZ + (double) (this.rand.nextFloat() * this.width) - (double) this.width / 2.0D,
                        d0, d1, d2,
                        1.0F, 1.0F, 1.0F, 1.0F,
                        false, 81, 1, 1, 16, 0, 1.5F, 1);
            }
        } else {
            if (this.ticksExisted % 30 == 0) {
                this.heal(1.0F);
            }
            if (this.getAttackTarget() != null && this.ticksExisted % 20 == 0) {
                this.updateAggroAndPlayerScaling();
            }
        }
        this.bossInfo.setPercent(Math.max(0.0F, Math.min(1.0F, this.getHealth() / this.getMaxHealth())));
        this.bossInfo.setName(this.getDisplayName());
    }

    private void updateAggroAndPlayerScaling() {
        EntityLivingBase currentTarget = this.getAttackTarget();
        int currentTargetId = currentTarget.getEntityId();
        int currentAggro = this.aggro.containsKey(currentTargetId) ? this.aggro.get(currentTargetId) : 0;
        int highestAggro = currentAggro;
        int players = 0;
        EntityLivingBase newTarget = null;
        List<Integer> deadOrDistant = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : this.aggro.entrySet()) {
            Entity candidate = this.world.getEntityByID(entry.getKey());
            if (!(candidate instanceof EntityLivingBase) || candidate.isDead || this.getDistanceSq(candidate) > 16384.0D) {
                deadOrDistant.add(entry.getKey());
                continue;
            }
            if (candidate instanceof net.minecraft.entity.player.EntityPlayer) {
                ++players;
            }
            int candidateAggro = entry.getValue();
            if (candidateAggro > currentAggro + 25
                    && (double) candidateAggro > (double) currentAggro * 1.1D
                    && candidateAggro > highestAggro) {
                highestAggro = candidateAggro;
                newTarget = (EntityLivingBase) candidate;
            }
        }

        for (Integer id : deadOrDistant) {
            this.aggro.remove(id);
        }
        if (newTarget != null && newTarget != currentTarget) {
            this.setAttackTarget(newTarget);
        }

        IAttributeInstance health = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        IAttributeInstance damage = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        if (health == null || damage == null) {
            return;
        }
        float oldMax = this.getMaxHealth();
        for (int i = 0; i < HP_BUFFS.length; ++i) {
            health.removeModifier(HP_BUFFS[i]);
            damage.removeModifier(DAMAGE_BUFFS[i]);
        }
        int extraPlayers = Math.min(5, Math.max(0, players - 1));
        for (int i = 0; i < extraPlayers; ++i) {
            health.applyModifier(HP_BUFFS[i]);
            damage.applyModifier(DAMAGE_BUFFS[i]);
        }
        if (oldMax > 0.0F) {
            float scaledHealth = (float) ((double) this.getHealth() * (this.getMaxHealth() / oldMax));
            this.setHealth(Math.max(1.0F, Math.min(this.getMaxHealth(), scaledHealth)));
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

    @Override
    public boolean getCanSpawnHere() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return super.canBePushed() && this.getSpawnTimer() <= 0;
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return super.isEntityInvulnerable(source) || this.getSpawnTimer() > 0;
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return entityIn instanceof IEldritchMob || super.isOnSameTeam(entityIn);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        EntityUtils.entityDropSpecialItem(this, new ItemStack(ConfigItems.itemEldritchObject, 1, 3), this.height / 2.0F);
        this.entityDropItem(new ItemStack(ConfigItems.itemLootBag, 1, 2), 1.5F);
    }

    public void generateName() {}
}
