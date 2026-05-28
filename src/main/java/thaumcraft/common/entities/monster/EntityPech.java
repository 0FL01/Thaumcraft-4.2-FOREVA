package thaumcraft.common.entities.monster;

import java.util.List;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.pech.AIPechItemEntityGoto;
import thaumcraft.common.entities.ai.pech.AIPechTradePlayer;
import thaumcraft.common.entities.projectile.EntityPechBlast;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class EntityPech extends net.minecraft.entity.monster.EntityMob implements IRangedAttackMob {

    // Data watcher keys — corrected 1.12.2
    private static final DataParameter<Integer> PECH_TYPE =
        EntityDataManager.createKey(EntityPech.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ANGER =
        EntityDataManager.createKey(EntityPech.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> TAMED =
        EntityDataManager.createKey(EntityPech.class, DataSerializers.BOOLEAN);

    // Loot inventory (9 slots, persisted in NBT)
    public ItemStack[] loot = new ItemStack[9];
    public boolean trading = false;
    public float mumble = 0.0F;

    private final AIAttackOnCollide aiMeleeAttack = new AIAttackOnCollide(this, EntityLivingBase.class, 0.6, false);
    private final EntityAIAttackRanged aiRangedAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0f);
    private final EntityAIAvoidEntity<EntityPlayer> aiAvoidPlayer =
        new EntityAIAvoidEntity<>(this, EntityPlayer.class, 8.0f, 0.5, 0.6);

    public EntityPech(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);

        // PathNavigateGround-specific settings (1.12.2: setBreakDoors moved from base PathNavigate)
        PathNavigate nav = this.getNavigator();
        if (nav instanceof PathNavigateGround) {
            PathNavigateGround ground = (PathNavigateGround) nav;
            ground.setBreakDoors(true);
            ground.setEnterDoors(true);
            ground.setCanSwim(true);
        }
        this.setPathPriority(PathNodeType.WATER, 0.0F);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIPechTradePlayer(this));
        this.tasks.addTask(3, new AIPechItemEntityGoto(this));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.5));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0f, 1.0f));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, net.minecraft.entity.EntityLiving.class, 8.0f));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        if (world != null && !world.isRemote) {
            this.setCombatTask();
        }
    }

    @Override
    public String getName() {
        if (this.hasCustomName()) {
            return this.getCustomNameTag();
        }
        return I18n.translateToLocal(getPechNameKey());
    }

    private String getPechNameKey() {
        switch (this.getPechType()) {
            case 1:
                return "entity.Thaumcraft.Pech.1.name";
            case 2:
                return "entity.Thaumcraft.Pech.2.name";
            case 0:
            default:
                return "entity.Thaumcraft.Pech.name";
        }
    }

    // ------------------------------------------------------------------
    // Data watcher
    // ------------------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(PECH_TYPE, 0);
        this.dataManager.register(ANGER, 0);
        this.dataManager.register(TAMED, false);
    }

    public int getPechType()          { return this.dataManager.get(PECH_TYPE); }
    public void setPechType(int type) { this.dataManager.set(PECH_TYPE, type); }

    public int getAnger()          { return this.dataManager.get(ANGER); }
    public void setAnger(int anger) { this.dataManager.set(ANGER, anger); }
    public boolean isAngry()       { return this.getAnger() > 0; }

    public boolean isTamed()        { return this.dataManager.get(TAMED); }
    public void setTamed(boolean b) { this.dataManager.set(TAMED, b); }

    // ------------------------------------------------------------------
    // NBT persistence
    // ------------------------------------------------------------------

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("PechType")) {
            this.setPechType(compound.getByte("PechType"));
        }
        this.setAnger(compound.getShort("Anger"));
        this.setTamed(compound.getBoolean("Tamed"));
        this.trading = compound.getBoolean("trading");

        // Loot array
        if (compound.hasKey("Loot")) {
            NBTTagList list = compound.getTagList("Loot", 10);
            for (int i = 0; i < this.loot.length && i < list.tagCount(); i++) {
                this.loot[i] = new ItemStack(list.getCompoundTagAt(i));
            }
        }

        if (!this.world.isRemote) {
            this.setCombatTask();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("PechType", (byte) this.getPechType());
        compound.setShort("Anger", (short) this.getAnger());
        compound.setBoolean("Tamed", this.isTamed());
        compound.setBoolean("trading", this.trading);

        NBTTagList list = new NBTTagList();
        for (ItemStack stack : this.loot) {
            NBTTagCompound slot = new NBTTagCompound();
            if (stack != null && !stack.isEmpty()) {
                stack.writeToNBT(slot);
            }
            list.appendTag(slot);
        }
        compound.setTag("Loot", list);
    }

    // ------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    // ------------------------------------------------------------------
    // Spawn control — block natural spawns outside MAGICAL biomes and in
    // Taint biome, matching original TC4 getCanSpawnHere logic.
    // ------------------------------------------------------------------

    @Override
    public boolean getCanSpawnHere() {
        Biome biome = this.world.getBiome(this.getPosition());
        boolean magicBiome = false;
        if (biome != null) {
            // Pech belong in MAGICAL biomes, but not Taint
            magicBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL)
                       && biome != ThaumcraftWorldGenerator.biomeTaint;
        }
        // Population cap: no more than 4 Pech in a 16-block cube
        int count = this.world.getEntitiesWithinAABB(
                EntityPech.class, this.getEntityBoundingBox().grow(16, 16, 16)).size();
        // Outside the Overworld restrict to Magical Forest / Eerie only
        if (this.world.provider.getDimension() != 0
                && biome != ThaumcraftWorldGenerator.biomeMagicalForest
                && biome != ThaumcraftWorldGenerator.biomeEerie) {
            magicBiome = false;
        }
        return count < 4 && magicBiome && super.getCanSpawnHere();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        this.setRandomHeldItem();
        ItemStack held = this.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() == ConfigItems.itemWandCasting) {
            this.setPechType(1);
            this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.1F);
        } else if (!held.isEmpty()) {
            if (held.getItem() == Items.BOW) {
                this.setPechType(2);
            }
            this.enchantHeldItem(difficulty);
        }
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.75F * difficulty.getClampedAdditionalDifficulty());
        this.setCombatTask();
        return data;
    }

    private void setRandomHeldItem() {
        switch (this.rand.nextInt(20)) {
            case 0:
            case 12:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, createPechWand());
                break;
            case 1:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                break;
            case 3:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
                break;
            case 5:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                break;
            case 6:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
                break;
            case 7:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
                break;
            case 8:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_PICKAXE));
                break;
            case 9:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
                break;
            case 2:
            case 4:
            case 10:
            case 11:
            case 13:
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                break;
            default:
                break;
        }
    }

    private ItemStack createPechWand() {
        ItemStack wand = new ItemStack(ConfigItems.itemWandCasting);
        if (ConfigItems.focusPech != null && wand.getItem() instanceof ItemWandCasting) {
            ((ItemWandCasting) wand.getItem()).setFocus(wand, new ItemStack(ConfigItems.focusPech));
        }
        ItemWandCasting.addVis(wand, Aspect.EARTH, 2 + this.rand.nextInt(6), true);
        ItemWandCasting.addVis(wand, Aspect.ENTROPY, 2 + this.rand.nextInt(6), true);
        ItemWandCasting.addVis(wand, Aspect.WATER, 2 + this.rand.nextInt(6), true);
        ItemWandCasting.addVis(wand, Aspect.AIR, this.rand.nextInt(4), true);
        ItemWandCasting.addVis(wand, Aspect.FIRE, this.rand.nextInt(4), true);
        ItemWandCasting.addVis(wand, Aspect.ORDER, this.rand.nextInt(4), true);
        return wand;
    }

    private void enchantHeldItem(DifficultyInstance difficulty) {
        ItemStack held = this.getHeldItemMainhand();
        float localDifficulty = difficulty.getClampedAdditionalDifficulty();
        if (!held.isEmpty() && this.rand.nextFloat() < 0.5F * localDifficulty) {
            EnchantmentHelper.addRandomEnchantment(this.rand, held, (int) (7.0F + localDifficulty * (float) this.rand.nextInt(22)), false);
        }
    }

    // ------------------------------------------------------------------
    // Combat task switching based on held item
    // ------------------------------------------------------------------

    public void setCombatTask() {
        this.tasks.removeTask(this.aiMeleeAttack);
        this.tasks.removeTask(this.aiRangedAttack);
        this.tasks.removeTask(this.aiAvoidPlayer);
        ItemStack held = this.getHeldItemMainhand();
        boolean hasBow = !held.isEmpty() && held.getItem() instanceof net.minecraft.item.ItemBow;
        boolean hasWand = !held.isEmpty() && held.getItem() instanceof ItemWandCasting;
        if (hasBow || hasWand || this.getPechType() == 1 || this.getPechType() == 2) {
            this.tasks.addTask(2, this.aiRangedAttack);
        } else {
            this.tasks.addTask(2, this.aiMeleeAttack);
        }
        if (this.isTamed()) {
            this.tasks.removeTask(this.aiAvoidPlayer);
        } else {
            this.tasks.addTask(4, this.aiAvoidPlayer);
        }
    }

    // ------------------------------------------------------------------
    // Overrides
    // ------------------------------------------------------------------

    @Override
    public void onUpdate() {
        if (!this.world.isRemote && this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
            EntityLivingBase revengeTarget = this.getRevengeTarget();
            if (this.getAnger() > 0 && this.getAttackTarget() == null && revengeTarget != null && revengeTarget.isEntityAlive()) {
                this.setAttackTarget(revengeTarget);
            }
            if (this.getAnger() <= 0 && this.getAttackTarget() instanceof EntityPlayer) {
                this.setAttackTarget(null);
            }
        }
        super.onUpdate();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        // Passive regen
        if (!this.world.isRemote && this.ticksExisted % 40 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity attacker = source.getTrueSource();
        if (!this.world.isRemote && attacker instanceof EntityPlayer) {
            List<EntityPech> nearby = this.world.getEntitiesWithinAABB(
                    EntityPech.class,
                    this.getEntityBoundingBox().grow(32.0D, 16.0D, 32.0D));
            for (EntityPech pech : nearby) {
                if (pech != this) {
                    pech.becomeAngryAt(attacker);
                }
            }
            this.becomeAngryAt(attacker);
        }
        return super.attackEntityFrom(source, amount);
    }

    private void becomeAngryAt(Entity attacker) {
        if (!(attacker instanceof EntityLivingBase)) return;
        if (this.getAnger() <= 0) {
            this.world.setEntityState(this, (byte) 19);
            this.playSound(TCSounds.PECH_CHARGE, this.getSoundVolume(), this.getSoundPitch());
        }
        EntityLivingBase target = (EntityLivingBase) attacker;
        this.setRevengeTarget(target);
        this.setAttackTarget(target);
        this.setAnger(400 + this.rand.nextInt(400));
        this.setTamed(false);
        this.setCombatTask();
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        ItemStack held = this.getHeldItemMainhand();
        int type = this.getPechType();
        if (type == 0 && !held.isEmpty() && held.getItem() instanceof net.minecraft.item.ItemBow) {
            type = 2;
        } else if (type == 0 && !held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
            type = 1;
        }

        if (type == 2) {
            EntityArrow arrow = new EntityTippedArrow(this.world, this);
            double d0 = target.posX - this.posX;
            double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - arrow.posY;
            double d2 = target.posZ - this.posZ;
            double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));

            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, held);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, held);
            arrow.setDamage((double) (distance * 2.0F) + this.rand.nextGaussian() * 0.25D + (double) ((float) this.world.getDifficulty().getId() * 0.11F));
            if (power > 0) {
                arrow.setDamage(arrow.getDamage() + (double) power * 0.5D + 0.5D);
            }
            if (punch > 0) {
                arrow.setKnockbackStrength(punch);
            }
            this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            this.world.spawnEntity(arrow);
        } else if (type == 1) {
            EntityPechBlast blast = new EntityPechBlast(this.world, this, 1, 0, this.rand.nextFloat() < 0.1F);
            double d0 = target.posX + target.motionX - this.posX;
            double d1 = target.posY + (double) target.getEyeHeight() - 1.500000023841858D - this.posY;
            double d2 = target.posZ + target.motionZ - this.posZ;
            float d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            blast.shoot(d0, d1 + (double) (d3 * 0.1F), d2, 1.5F, 4.0F);
            this.playSound(TCSounds.ICE, 0.4F, 1.0F + this.rand.nextFloat() * 0.1F);
            this.world.spawnEntity(blast);
        }
        this.swingArm(EnumHand.MAIN_HAND);
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 16) {
            this.mumble = (float) Math.PI;
        } else if (id == 17) {
            this.mumble = (float) Math.PI * 2.0F;
        } else if (id == 18) {
            spawnReactionParticles(80);
        } else if (id == 19) {
            spawnReactionParticles(81);
            this.mumble = (float) Math.PI * 2.0F;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnReactionParticles(int start) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            Thaumcraft.proxy.drawGenericParticles(
                    this.world,
                    this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height),
                    this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    d0, d1, d2,
                    1.0F, 1.0F, 1.0F, 1.0F,
                    false, start, 1, 1, 16, 0, 1.5F, 1);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, net.minecraft.util.EnumHand hand) {
        if (this.isTamed()) {
            if (!this.world.isRemote) {
                player.openGui(thaumcraft.common.Thaumcraft.instance, thaumcraft.common.CommonProxy.GUI_PECH, this.world, this.getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    // ------------------------------------------------------------------
    // Inventory / pickup
    // ------------------------------------------------------------------

    public boolean canPickup(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!this.isEntityAlive() || this.trading) return false;
        // Untamed pechs only pick up valued items
        if (!this.isTamed()) return this.isValued(stack);
        // Tamed pechs: check if there's room in loot array
        for (int a = 0; a < this.loot.length; a++) {
            if (this.loot[a] == null || this.loot[a].isEmpty()) return true;
            if (net.minecraftforge.items.ItemHandlerHelper.canItemStacksStack(stack, this.loot[a])
                && stack.getCount() + this.loot[a].getCount() <= this.loot[a].getMaxStackSize())
                return true;
        }
        return false;
    }

    public ItemStack pickupItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return stack;
        if (!this.isEntityAlive() || this.trading) return stack;

        // For untamed pechs: only valued items can tame them.
        if (!this.isTamed()) {
            if (!this.isValued(stack)) return stack;
            int value = this.getValue(stack);
            stack.shrink(1);
            if (!this.world.isRemote && this.rand.nextInt(10) < value) {
                this.setTamed(true);
                this.setCombatTask();
                this.world.setEntityState(this, (byte) 18);
            }
            if (stack.isEmpty()) return ItemStack.EMPTY;
            return stack;
        }

        // For tamed pechs: add to loot inventory
        for (int a = 0; a < this.loot.length; a++) {
            if (this.loot[a] != null && !this.loot[a].isEmpty()
                && net.minecraftforge.items.ItemHandlerHelper.canItemStacksStack(stack, this.loot[a])) {
                int space = this.loot[a].getMaxStackSize() - this.loot[a].getCount();
                int transfer = Math.min(stack.getCount(), space);
                this.loot[a].grow(transfer);
                stack.shrink(transfer);
                if (stack.isEmpty()) return ItemStack.EMPTY;
            }
        }
        // Put into empty slot
        for (int a = 0; a < this.loot.length; a++) {
            if (this.loot[a] == null || this.loot[a].isEmpty()) {
                this.loot[a] = stack.splitStack(stack.getCount());
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public boolean isValued(ItemStack item) {
        return this.getValue(item) > 0;
    }

    public int getValue(ItemStack item) {
        if (item == null || item.isEmpty()) return 0;
        int value = this.getConfiguredValue(item);
        if (value > 0) return value;

        int greed = 0;
        AspectList objectTags = ThaumcraftCraftingManager.getObjectTags(item);
        if (objectTags != null) {
            greed = Math.max(greed, objectTags.getAmount(Aspect.GREED));
        }
        AspectList bonusTags = ThaumcraftCraftingManager.getBonusTags(item, objectTags);
        if (bonusTags != null) {
            greed = Math.max(greed, bonusTags.getAmount(Aspect.GREED));
        }
        return Math.min(32, greed);
    }

    private int getConfiguredValue(ItemStack item) {
        if (ConfigItems.itemManaBean != null && item.getItem() == ConfigItems.itemManaBean) return 1;
        if (item.getItem() == Items.GOLD_INGOT) return 2;
        if (item.getItem() == Items.GOLDEN_APPLE) return 2;
        if (item.getItem() == Items.ENDER_PEARL) return 3;
        if (item.getItem() == Items.DIAMOND) return 4;
        if (item.getItem() == Items.EMERALD) return 5;
        return 0;
    }

    // ------------------------------------------------------------------
    // Sounds
    // ------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() { return TCSounds.PECH_IDLE; }
    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return TCSounds.PECH_HIT; }
    @Override
    protected SoundEvent getDeathSound() { return TCSounds.PECH_DEATH; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        for (ItemStack stack : this.loot) {
            if (stack != null && !stack.isEmpty() && this.world.rand.nextFloat() < 0.88F) {
                this.entityDropItem(stack.copy(), 1.5F);
            }
        }
        Aspect[] aspects = Aspect.getPrimalAspects().toArray(new Aspect[0]);
        for (int a = 0; a < 1 + looting; ++a) {
            if (ConfigItems.itemManaBean != null && aspects.length > 0 && this.rand.nextBoolean()) {
                ItemStack bean = new ItemStack(ConfigItems.itemManaBean);
                NBTTagCompound tag = new NBTTagCompound();
                new AspectList().add(aspects[this.rand.nextInt(aspects.length)], 1).writeToNBT(tag);
                bean.setTagCompound(tag);
                this.entityDropItem(bean, 1.5F);
            }
        }
        if (ConfigItems.itemResource != null && this.world.rand.nextInt(10) < 1 + looting) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, thaumcraft.common.items.ItemResource.META_COIN), 1.5F);
        }
        if (wasRecentlyHit && ConfigItems.itemResource != null && this.rand.nextInt(200) - looting < 5) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, thaumcraft.common.items.ItemResource.META_KNOWLEDGE_FRAGMENT), 1.5F);
        }
        super.dropFewItems(wasRecentlyHit, looting);
    }

    @Override
    public int getMaxSpawnedInChunk() { return 3; }
}
