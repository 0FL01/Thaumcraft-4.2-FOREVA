package thaumcraft.common.entities.monster;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.pech.AIPechItemEntityGoto;
import thaumcraft.common.entities.ai.pech.AIPechTradePlayer;
import thaumcraft.common.lib.TCSounds;

public class EntityPech extends net.minecraft.entity.monster.EntityMob implements IRangedAttackMob {

    // Data watcher keys — corrected 1.12.2 types per ROUND_D_ANSWERS.md §D2
    private static final DataParameter<Integer> PECH_TYPE =
        EntityDataManager.createKey(EntityPech.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ANGER =
        EntityDataManager.createKey(EntityPech.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> TAMED =
        EntityDataManager.createKey(EntityPech.class, DataSerializers.BOOLEAN);

    // Loot inventory (9 slots, persisted in NBT)
    public ItemStack[] loot = new ItemStack[9];
    public boolean trading = false;

    private final AIAttackOnCollide aiMeleeAttack = new AIAttackOnCollide(this, EntityLivingBase.class, 0.6, false);
    private final EntityAIAttackRanged aiRangedAttack = new EntityAIAttackRanged(this, 0.6, 20, 50, 15.0f);
    private final EntityAIAvoidEntity<EntityPlayer> aiAvoidPlayer =
        new EntityAIAvoidEntity<>(this, EntityPlayer.class, 8.0f, 0.5, 0.6);

    public EntityPech(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
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
    // Combat task switching based on held item
    // ------------------------------------------------------------------

    public void setCombatTask() {
        this.tasks.removeTask(this.aiMeleeAttack);
        this.tasks.removeTask(this.aiRangedAttack);
        ItemStack held = this.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof net.minecraft.item.ItemBow) {
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
        // Anger countdown (server)
        if (this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
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
        if (!this.world.isRemote && source.getTrueSource() instanceof EntityPlayer) {
            this.setAnger(400 + this.rand.nextInt(400));
            this.setTamed(false);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        // TODO: pech blast / arrow (Phase 5 focus port)
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    // ------------------------------------------------------------------
    // Inventory / pickup
    // ------------------------------------------------------------------

    public boolean canPickup(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!this.isEntityAlive() || this.trading) return false;
        // Untamed pechs only pick up valued items
        if (!this.isTamed()) return true; // will check value later
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

        // For untamed pechs: valued items can tame them (simplified — real logic uses ThaumcraftCraftingManager)
        if (!this.isTamed()) {
            if (!stack.isEmpty()) {
                stack.shrink(1);
                this.setTamed(true);
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
        super.dropFewItems(wasRecentlyHit, looting);
        // Loot drops handled in onDeath by original — stub for now
    }

    @Override
    public int getMaxSpawnedInChunk() { return 3; }
}
