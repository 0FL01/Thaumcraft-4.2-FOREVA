package thaumcraft.common.entities.monster.boss;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.EntityCultist;
import thaumcraft.common.entities.monster.EntityCultistCleric;
import thaumcraft.common.entities.monster.EntityCultistKnight;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.EntityUtils;

public class EntityCultistLeader extends EntityThaumcraftBoss implements net.minecraft.entity.IRangedAttackMob {
    public static final String[] NAMES = {"Alberic", "Anselm", "Bastian", "Beturian", "Chabier", "Chorache", "Chuse", "Dodorol", "Ebardo", "Ferrando", "Fertus", "Guillen", "Larpe", "Obano", "Zelipe"};
    private static final DataParameter<Integer> TITLE = EntityDataManager.createKey(EntityCultistLeader.class, DataSerializers.VARINT);

    public EntityCultistLeader(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.75F, 2.25F);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 16.0, 1.0, 30, 40, 24.0f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.1, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.experienceValue = 40;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TITLE, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(125.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.equipPraetorGear();
    }

    private void equipPraetorGear() {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ConfigItems.itemHelmetCultistLeader));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ConfigItems.itemChestCultistLeader));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ConfigItems.itemLegsCultistLeader));
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ConfigItems.itemCultistBoots));
        if (this.world.getDifficulty() == EnumDifficulty.EASY) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ConfigItems.itemSwordVoid));
        } else {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ConfigItems.itemCrimsonSword));
        }
    }

    private void ensurePraetorGear() {
        if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ConfigItems.itemHelmetCultistLeader));
        }
        if (this.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()) {
            this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ConfigItems.itemChestCultistLeader));
        }
        if (this.getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty()) {
            this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ConfigItems.itemLegsCultistLeader));
        }
        if (this.getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty()) {
            this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ConfigItems.itemCultistBoots));
        }
        if (this.getHeldItemMainhand().isEmpty()) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(
                    this.world.getDifficulty() == EnumDifficulty.EASY ? ConfigItems.itemSwordVoid : ConfigItems.itemCrimsonSword));
        }
    }

    @Override
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
        ItemStack held = this.getHeldItemMainhand();
        float localDifficulty = difficulty.getClampedAdditionalDifficulty();
        if (!held.isEmpty() && this.rand.nextFloat() < 0.5F * localDifficulty) {
            EnchantmentHelper.addRandomEnchantment(this.rand, held, (int) (7.0F + localDifficulty * (float) this.rand.nextInt(22)), false);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        this.setTitle(this.rand.nextInt(NAMES.length));
        EntityUtils.makeChampion(this, true);
        return data;
    }

    @Override
    public void generateName() {
        int type = EntityUtils.getChampionModifierType(this);
        if (type >= 0 && type < ChampionModifier.mods.length) {
            this.setCustomNameTag(String.format(
                    I18n.translateToLocal("entity.thaumcraft.cultistleader.name"),
                    this.getTitle(),
                    ChampionModifier.mods[type].getModNameLocalized()));
        }
    }

    private void setTitle(int title) {
        this.dataManager.set(TITLE, Math.max(0, Math.min(title, NAMES.length - 1)));
    }

    public String getTitle() {
        return NAMES[this.dataManager.get(TITLE)];
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("title", this.dataManager.get(TITLE).byteValue());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setTitle(compound.getByte("title"));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        if (!this.canEntityBeSeen(target)) {
            return;
        }
        this.swingArm(EnumHand.MAIN_HAND);
        this.getLookHelper().setLookPosition(target.posX, target.getEntityBoundingBox().minY + (double) (target.height / 2.0F), target.posZ, 30.0F, 30.0F);
        EntityGolemOrb blast = new EntityGolemOrb(this.world, this, target, true);
        blast.posX += blast.motionX / 2.0D;
        blast.posZ += blast.motionZ / 2.0D;
        blast.setPosition(blast.posX, blast.posY, blast.posZ);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
        double d2 = target.posZ - this.posZ;
        blast.shoot(d0, d1 + 2.0D, d2, 0.66F, 3.0F);
        this.playSound(TCSounds.EGATTACK, 1.0F, 1.0F + this.rand.nextFloat() * 0.1F);
        if (!this.world.isRemote) {
            this.world.spawnEntity(blast);
        }
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote) {
            if (this.ticksExisted <= 5
                    && (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()
                    || this.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()
                    || this.getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty()
                    || this.getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty()
                    || this.getHeldItemMainhand().isEmpty())) {
                this.ensurePraetorGear();
            }
            for (EntityCultist cultist : this.world.getEntitiesWithinAABB(EntityCultist.class, this.getEntityBoundingBox().grow(8.0D))) {
                if (cultist.isEntityAlive() && !cultist.isPotionActive(MobEffects.STRENGTH)) {
                    cultist.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 60, 1));
                }
            }
        }
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return entityIn instanceof EntityCultist || entityIn instanceof EntityCultistLeader || super.isOnSameTeam(entityIn);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (cls == EntityCultistCleric.class || cls == EntityCultistLeader.class || cls == EntityCultistKnight.class) {
            return false;
        }
        return super.canAttackClass(cls);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        this.entityDropItem(new ItemStack(ConfigItems.itemLootBag, 1, 2), 1.5F);
    }
}
