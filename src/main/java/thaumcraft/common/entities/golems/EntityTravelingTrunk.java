package thaumcraft.common.entities.golems;

public class EntityTravelingTrunk extends net.minecraft.entity.EntityLiving implements net.minecraft.entity.IEntityOwnable {

    private net.minecraft.entity.EntityLivingBase owner;
    public final InventoryTrunk inventory = new InventoryTrunk();
    private boolean open;
    private boolean stay;
    private int upgrade = -1;
    private int anger;
    private int attackCooldown;
    private static final net.minecraft.network.datasync.DataParameter<com.google.common.base.Optional<java.util.UUID>> OWNER_UUID =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.OPTIONAL_UNIQUE_ID);

    public EntityTravelingTrunk(net.minecraft.world.World world) {
        super(world);
        this.isImmuneToFire = true;
        this.enablePersistence();
        this.setSize(0.8f, 0.8f);
        this.inventory.setEntity(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(OWNER_UUID, com.google.common.base.Optional.absent());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(75.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        if (this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE) == null) {
            this.getAttributeMap().registerAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE);
        }
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.getUpgrade() == 5) {
            this.pullItems();
        }
        if (!this.world.isRemote && this.getHealth() < this.getMaxHealth()
                && (this.getUpgrade() == 3 || this.ticksExisted % 50 == 0)) {
            this.heal(1.0F);
        }
        if (this.anger > 0) {
            this.anger--;
        }
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        net.minecraft.entity.Entity ownerEntity = this.getOwner();
        net.minecraft.entity.EntityLivingBase owner = (ownerEntity instanceof net.minecraft.entity.EntityLivingBase) ? (net.minecraft.entity.EntityLivingBase)ownerEntity : null;
        if (!this.world.isRemote && owner != null) {
            thaumcraft.common.lib.events.EventHandlerEntity.linkTravelingTrunk(this, owner.getUniqueID());
        }
        if (!this.world.isRemote) {
            this.updateDefensiveTarget(owner);
        }
        if (!this.getStay() && owner != null && this.getAttackTarget() == null && this.getDistance(owner) > 4.0f) {
            this.getNavigator().tryMoveToEntityLiving(owner, this.getUpgrade() == 0 ? 0.65 : 0.5);
        }
    }

    private void updateDefensiveTarget(net.minecraft.entity.EntityLivingBase owner) {
        net.minecraft.entity.EntityLivingBase target = this.getAttackTarget();
        if (target != null && (!target.isEntityAlive() || target == owner)) {
            this.setAttackTarget(null);
            this.anger = 5;
            target = null;
        }
        if (target != null && this.anger <= 0) {
            this.setAttackTarget(null);
            target = null;
        }
        if (!this.getStay() && this.getUpgrade() == 2 && this.anger == 0 && target == null && owner != null) {
            net.minecraft.entity.EntityLivingBase ownerTarget = owner.getRevengeTarget();
            if (ownerTarget == null && owner instanceof net.minecraft.entity.EntityLiving) {
                ownerTarget = ((net.minecraft.entity.EntityLiving) owner).getAttackTarget();
            }
            if (this.isValidDefensiveTarget(owner, ownerTarget)) {
                this.anger = 600;
                this.setAttackTarget(ownerTarget);
                target = ownerTarget;
            }
        }
        if (this.anger > 0 && target != null && target.isEntityAlive() && target != owner) {
            this.faceEntity(target, 10.0F, 20.0F);
            this.getNavigator().tryMoveToEntityLiving(target, 0.6D);
            if (this.attackCooldown <= 0 && this.getDistance(target) < 1.5F
                    && target.getEntityBoundingBox().maxY > this.getEntityBoundingBox().minY
                    && target.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
                this.attackCooldown = 10 + this.rand.nextInt(5);
                float damage = (float)this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                target.attackEntityFrom(net.minecraft.util.DamageSource.causeMobDamage(this), damage);
                this.world.setEntityState(this, (byte) 17);
                this.playSound(net.minecraft.init.SoundEvents.ENTITY_BLAZE_HURT, 0.5F, this.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    private boolean isValidDefensiveTarget(net.minecraft.entity.EntityLivingBase owner, net.minecraft.entity.EntityLivingBase target) {
        return target != null
                && target != this
                && target != owner
                && target.isEntityAlive()
                && this.canEntityBeSeen(target);
    }

    private void pullItems() {
        if (this.isDead || this.getHealth() <= 0.0F) {
            return;
        }
        if (!this.world.isRemote) {
            java.util.List<net.minecraft.entity.item.EntityItem> closeItems = this.world.getEntitiesWithinAABB(
                    net.minecraft.entity.item.EntityItem.class,
                    new net.minecraft.util.math.AxisAlignedBB(this.posX - 0.5D, this.posY - 0.5D, this.posZ - 0.5D,
                            this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D));
            for (net.minecraft.entity.item.EntityItem itemEntity : closeItems) {
                net.minecraft.item.ItemStack stack = itemEntity.getItem().copy();
                net.minecraft.item.ItemStack remaining = thaumcraft.common.lib.utils.InventoryUtils
                        .placeItemStackIntoInventory(stack, this.inventory, 0, true);
                if (remaining.isEmpty()) {
                    itemEntity.setDead();
                } else if (remaining.getCount() != stack.getCount()) {
                    itemEntity.setItem(remaining);
                } else {
                    continue;
                }
                this.world.playSound(null, this.getPosition(), net.minecraft.init.SoundEvents.ENTITY_GENERIC_EAT,
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.5F, this.world.rand.nextFloat() * 0.5F + 0.5F);
                this.world.setEntityState(this, (byte) 17);
            }
        }

        java.util.List<net.minecraft.entity.item.EntityItem> nearbyItems = this.world.getEntitiesWithinAABB(
                net.minecraft.entity.item.EntityItem.class, this.getEntityBoundingBox().grow(3.0D));
        for (net.minecraft.entity.item.EntityItem itemEntity : nearbyItems) {
            double dx = itemEntity.posX - this.posX;
            double dy = itemEntity.posY - this.posY + this.height * 0.8F;
            double dz = itemEntity.posZ - this.posZ;
            double distance = net.minecraft.util.math.MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance <= 0.0D) {
                continue;
            }
            double strength = 0.075D;
            itemEntity.motionX -= dx / distance * strength;
            itemEntity.motionY -= dy / distance * strength;
            itemEntity.motionZ -= dz / distance * strength;
        }
    }

    @Override
    public net.minecraft.entity.Entity getOwner() {
        if (this.owner == null) {
            java.util.UUID id = this.getOwnerId();
            if (id != null && this.world != null) {
                for (net.minecraft.entity.player.EntityPlayer p : this.world.playerEntities) {
                    if (p.getUniqueID().equals(id)) { this.owner = p; break; }
                }
            }
        }
        return this.owner;
    }

    @Override
    public java.util.UUID getOwnerId() {
        com.google.common.base.Optional<java.util.UUID> opt = this.dataManager.get(OWNER_UUID);
        return opt.isPresent() ? opt.get() : null;
    }

    public void setOwnerId(java.util.UUID id) {
        this.owner = null;
        this.dataManager.set(OWNER_UUID, com.google.common.base.Optional.fromNullable(id));
    }

    public int getUpgrade() {
        return this.upgrade;
    }

    public void setUpgrade(int upgrade) {
        this.upgrade = upgrade;
    }

    public void setInvSize() {
        this.inventory.setSlotCount(this.upgrade == 1 ? 36 : 27);
    }

    public int getRows() {
        return Math.max(1, Math.min(4, (this.inventory.getSizeInventory() + 8) / 9));
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean getOpen() {
        return this.open;
    }

    public void setStay(boolean stay) {
        this.stay = stay;
    }

    public boolean getStay() {
        return this.stay;
    }

    public int getAnger() {
        return this.anger;
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty,
                                                                 net.minecraft.entity.IEntityLivingData livingdata) {
        this.setInvSize();
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public boolean processInteract(net.minecraft.entity.player.EntityPlayer player, net.minecraft.util.EnumHand hand) {
        if (player.isSneaking()) {
            return false;
        }
        net.minecraft.item.ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem() == thaumcraft.common.config.ConfigItems.itemGolemBell) {
            return this.getUpgrade() == 3 && !this.isOwner(player);
        }
        if (this.getUpgrade() == -1 && !stack.isEmpty()
                && stack.getItem() == thaumcraft.common.config.ConfigItems.itemGolemUpgrade) {
            if (!this.world.isRemote) {
                this.setUpgrade(stack.getItemDamage());
                this.setInvSize();
                stack.shrink(1);
                this.world.playSound(null, this.getPosition(), thaumcraft.common.lib.TCSounds.UPGRADE,
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.5F, 1.0F);
            }
            player.swingArm(hand);
            return true;
        }
        if (!stack.isEmpty() && stack.getItem() instanceof net.minecraft.item.ItemFood && this.getHealth() < this.getMaxHealth()) {
            if (!this.world.isRemote) {
                net.minecraft.item.ItemFood food = (net.minecraft.item.ItemFood) stack.getItem();
                int healAmount = food.getHealAmount(stack);
                stack.shrink(1);
                this.heal(healAmount);
                this.world.playSound(null, this.getPosition(),
                        this.getHealth() == this.getMaxHealth()
                                ? net.minecraft.init.SoundEvents.ENTITY_PLAYER_BURP
                                : net.minecraft.init.SoundEvents.ENTITY_GENERIC_EAT,
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.5F, this.world.rand.nextFloat() * 0.5F + 0.5F);
                this.world.setEntityState(this, (byte) 18);
            }
            player.swingArm(hand);
            return true;
        }
        if (!this.world.isRemote) {
            if (this.getUpgrade() == 3 && !this.isOwner(player)) {
                return true;
            }
            player.openGui(thaumcraft.common.Thaumcraft.instance, thaumcraft.common.CommonProxy.GUI_TRAVELING_TRUNK, this.world, this.getEntityId(), 0, 0);
        }
        return true;
    }

    private boolean isOwner(net.minecraft.entity.player.EntityPlayer player) {
        java.util.UUID ownerId = this.getOwnerId();
        return ownerId == null || ownerId.equals(player.getUniqueID());
    }

    public boolean transferToOwnerDimension(net.minecraft.entity.player.EntityPlayerMP player) {
        if (player == null || this.world.isRemote || this.getStay() || this.isDead || player.world == this.world) {
            return false;
        }
        java.util.UUID ownerId = this.getOwnerId();
        if (ownerId == null || !ownerId.equals(player.getUniqueID())) {
            return false;
        }
        EntityTravelingTrunk copy = new EntityTravelingTrunk(player.world);
        copy.setOwnerId(ownerId);
        copy.setUpgrade(this.getUpgrade());
        copy.setInvSize();
        net.minecraft.nbt.NBTTagList inventoryTag = this.inventory.writeToNBT(new net.minecraft.nbt.NBTTagList());
        copy.inventory.readFromNBT(inventoryTag);
        copy.inventory.setEntity(copy);
        copy.setStay(this.getStay());
        copy.setHealth(this.getHealth());
        if (this.hasCustomName()) {
            copy.setCustomNameTag(this.getCustomNameTag());
        }
        copy.setLocationAndAngles(player.posX, player.posY + 0.25D, player.posZ, this.rotationYaw, this.rotationPitch);
        copy.rotationYawHead = copy.rotationYaw;
        copy.renderYawOffset = copy.rotationYaw;
        if (player.world.spawnEntity(copy)) {
            this.setDead();
            return true;
        }
        return false;
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (source == net.minecraft.util.DamageSource.FALL || this.getUpgrade() == 3) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(net.minecraft.util.DamageSource cause) {
        if (!this.world.isRemote) {
            this.inventory.dropAllItems();
        }
        super.onDeath(cause);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 17 || id == 18) {
            this.open = true;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.stay = compound.getBoolean("Stay");
        this.upgrade = compound.hasKey("upgrade") ? compound.getByte("upgrade") : -1;
        if (compound.hasUniqueId("OwnerUUID")) {
            this.setOwnerId(compound.getUniqueId("OwnerUUID"));
        }
        this.setInvSize();
        this.inventory.readFromNBT(compound.getTagList("Inventory", 10));
        this.inventory.setEntity(this);
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Stay", this.stay);
        compound.setByte("upgrade", (byte) this.upgrade);
        java.util.UUID ownerId = this.getOwnerId();
        if (ownerId != null) {
            compound.setUniqueId("OwnerUUID", ownerId);
        }
        compound.setTag("Inventory", this.inventory.writeToNBT(new net.minecraft.nbt.NBTTagList()));
    }
}
