package thaumcraft.common.entities.golems;

public class EntityTravelingTrunk extends net.minecraft.entity.EntityLiving implements net.minecraft.entity.IEntityOwnable {

    private net.minecraft.entity.EntityLivingBase owner;
    public final InventoryTrunk inventory = new InventoryTrunk();
    public float lidrot;
    public float field_768_a;
    public float field_767_b;
    private int jumpDelay;
    private int attackCooldown;
    private static final net.minecraft.network.datasync.DataParameter<com.google.common.base.Optional<java.util.UUID>> OWNER_UUID =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final net.minecraft.network.datasync.DataParameter<java.lang.Boolean> OPEN =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.BOOLEAN);
    private static final net.minecraft.network.datasync.DataParameter<java.lang.Boolean> STAY =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.BOOLEAN);
    private static final net.minecraft.network.datasync.DataParameter<java.lang.Integer> UPGRADE =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.VARINT);
    private static final net.minecraft.network.datasync.DataParameter<java.lang.Integer> ROWS =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.VARINT);
    private static final net.minecraft.network.datasync.DataParameter<java.lang.Integer> ANGER =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.VARINT);

    public EntityTravelingTrunk(net.minecraft.world.World world) {
        super(world);
        this.isImmuneToFire = true;
        this.enablePersistence();
        this.jumpDelay = this.rand.nextInt(20) + 10;
        this.lidrot = 0.0F;
        this.setSize(0.8f, 0.8f);
        this.inventory.setEntity(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(OWNER_UUID, com.google.common.base.Optional.absent());
        this.dataManager.register(OPEN, false);
        this.dataManager.register(STAY, false);
        this.dataManager.register(UPGRADE, -1);
        this.dataManager.register(ROWS, 3);
        this.dataManager.register(ANGER, 0);
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
        if (this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
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
            if (this.isInWater()) {
                this.setAir(300);
            }
        }
        boolean teleported = !this.world.isRemote
                && !this.getStay()
                && owner != null
                && this.getDistance(owner) > 20.0F
                && this.tryTeleportToOwner(owner);
        if (!teleported && !this.getStay() && owner != null && this.getAttackTarget() == null && this.getDistance(owner) > 4.0f) {
            this.getNavigator().tryMoveToEntityLiving(owner, this.getUpgrade() == 0 ? 0.65 : 0.5);
        }
    }

    @Override
    public void onUpdate() {
        boolean wasOnGround = this.onGround;
        this.field_767_b += (this.field_768_a - this.field_767_b) * 0.5F;
        super.onUpdate();
        if (this.isInWater()) {
            this.motionY += 0.033D;
        }
        if (this.world.isRemote) {
            if (!this.onGround && this.motionY < 0.0D) {
                this.lidrot += 0.015F;
            }
            if ((this.onGround || this.isInWater()) && !this.isOpen()) {
                this.lidrot -= 0.1F;
                if (this.lidrot < 0.0F) {
                    this.lidrot = 0.0F;
                }
            }
            if (this.isOpen()) {
                this.lidrot += 0.035F;
            }
            float limit = this.isOpen() ? 0.5F : 0.2F;
            if (this.lidrot > limit) {
                this.lidrot = limit;
            }
        }
        if (this.onGround && !wasOnGround) {
            this.field_768_a = -0.5F;
        } else if (!this.onGround && wasOnGround) {
            this.field_768_a = 1.0F;
        } else if (this.onGround && (this.motionX * this.motionX + this.motionZ * this.motionZ) > 0.0025D && this.jumpDelay-- <= 0) {
            this.field_768_a = 0.35F;
            this.jumpDelay = this.rand.nextInt(10) + 5;
        }
        this.field_768_a *= 0.6F;
    }

    private void updateDefensiveTarget(net.minecraft.entity.EntityLivingBase owner) {
        net.minecraft.entity.EntityLivingBase target = this.getAttackTarget();
        if (target != null && (!target.isEntityAlive() || target == owner)) {
            this.setAttackTarget(null);
            this.setAnger(5);
            target = null;
        }
        if (target != null && this.getAnger() <= 0) {
            this.setAttackTarget(null);
            target = null;
        }
        if (!this.getStay() && this.getUpgrade() == 2 && this.getAnger() == 0 && target == null && owner != null) {
            net.minecraft.entity.EntityLivingBase ownerTarget = owner.getRevengeTarget();
            if (ownerTarget == null && owner instanceof net.minecraft.entity.EntityLiving) {
                ownerTarget = ((net.minecraft.entity.EntityLiving) owner).getAttackTarget();
            }
            if (this.isValidDefensiveTarget(owner, ownerTarget)) {
                this.setAnger(600);
                this.setAttackTarget(ownerTarget);
                target = ownerTarget;
            }
        }
        if (this.getAnger() > 0 && target != null && target.isEntityAlive() && target != owner) {
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
        return this.dataManager.get(UPGRADE);
    }

    public void setUpgrade(int upgrade) {
        this.dataManager.set(UPGRADE, upgrade);
    }

    public void setInvSize() {
        this.setRows(this.getUpgrade() == 1 ? 4 : 3);
        this.inventory.setSlotCount(this.getRows() * 9);
    }

    public int getRows() {
        return this.dataManager.get(ROWS);
    }

    public void setRows(int rows) {
        this.dataManager.set(ROWS, Math.max(1, Math.min(4, rows)));
    }

    public void setOpen(boolean open) {
        this.dataManager.set(OPEN, open);
    }

    public boolean getOpen() {
        return this.isOpen();
    }

    public boolean isOpen() {
        return this.dataManager.get(OPEN);
    }

    public void setStay(boolean stay) {
        this.dataManager.set(STAY, stay);
    }

    public boolean getStay() {
        return this.dataManager.get(STAY);
    }

    public int getAnger() {
        return this.dataManager.get(ANGER);
    }

    public void setAnger(int anger) {
        this.dataManager.set(ANGER, anger);
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
        if (id == 17) {
            this.lidrot = 0.15F;
        } else if (id == 18) {
            this.lidrot = 0.15F;
            this.showHeartsOrSmokeFX(true);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    private void showHeartsOrSmokeFX(boolean positive) {
        net.minecraft.util.EnumParticleTypes particle = positive
                ? net.minecraft.util.EnumParticleTypes.HEART
                : net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL;
        for (int i = 0; i < 7; ++i) {
            double mx = this.rand.nextGaussian() * 0.02D;
            double my = this.rand.nextGaussian() * 0.02D;
            double mz = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(
                    particle,
                    this.posX + (this.rand.nextFloat() * this.width * 2.0F) - this.width,
                    this.posY + 0.5D + this.rand.nextFloat() * this.height,
                    this.posZ + (this.rand.nextFloat() * this.width * 2.0F) - this.width,
                    mx, my, mz);
        }
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setStay(compound.getBoolean("Stay"));
        this.setUpgrade(compound.hasKey("upgrade") ? compound.getByte("upgrade") : -1);
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
        compound.setBoolean("Stay", this.getStay());
        compound.setByte("upgrade", (byte) this.getUpgrade());
        java.util.UUID ownerId = this.getOwnerId();
        if (ownerId != null) {
            compound.setUniqueId("OwnerUUID", ownerId);
        }
        compound.setTag("Inventory", this.inventory.writeToNBT(new net.minecraft.nbt.NBTTagList()));
    }

    /**
     * Teleports the trunk to a safe position near its owner when too far away.
     * Mirrors the original 1.7.10 logic from updateEntityActionState():
     *   - distance {@code > 20} blocks triggers teleport
     *   - searches a 5x5 perimeter around the owner (skipping inner 3x3)
     *   - plays enderman teleport sound on arrival
     *   - clears attack target and navigation path after teleport
     * <p>
     * <b>Reference:</b> {@code thaumcraft_src/EntityTravelingTrunk.class} &mdash;
     * {@code func_70626_be} / {@code updateEntityActionState()}, teleport branch at
     * distance {@code > 20}, 5x5 perimeter search, {@code setLocationAndAngles},
     * portal sound, {@code setAttackTarget(null)}.
     * <p>
     * <b>Deviation from original (intentional):</b> The original {@code isAirBlock}
     * checks invert body/head safety&mdash;it teleports the trunk inside blocks and
     * relies on Minecraft\u2019s entity collision resolution to push it to open space.
     * This implementation uses a conservative safe-spot check: solid floor (or water),
     * clear body space, clear headroom. This avoids unpredictable collision pushes
     * while still achieving the same gameplay result (trunk appears near owner).
     * <p>
     * <b>Blocked by:</b> In Forge 1.12.2, {@code EntityLiving.updateEntityActionState()}
     * is {@code final}, so teleport logic runs from {@code onLivingUpdate()} instead.
     *
     * @param owner the owner to teleport near
     * @return true if a teleport occurred
     */
    private boolean tryTeleportToOwner(net.minecraft.entity.EntityLivingBase owner) {
        if (this.world.isRemote || this.getStay() || owner == null || this.getDistance(owner) <= 20.0F) {
            return false;
        }
        net.minecraft.util.math.BlockPos ownerPos = new net.minecraft.util.math.BlockPos(owner);
        if (!this.world.isBlockLoaded(ownerPos)) {
            return false;
        }
        int baseX = ownerPos.getX() - 2;
        int baseZ = ownerPos.getZ() - 2;
        int baseY = net.minecraft.util.math.MathHelper.floor(owner.getEntityBoundingBox().minY);

        for (int dx = 0; dx <= 4; ++dx) {
            for (int dz = 0; dz <= 4; ++dz) {
                // Skip inner 3x3 block — only check the perimeter (same as original)
                if (dx >= 1 && dz >= 1 && dx <= 3 && dz <= 3) {
                    continue;
                }
                net.minecraft.util.math.BlockPos candidate = new net.minecraft.util.math.BlockPos(baseX + dx, baseY, baseZ + dz);
                if (!this.isSafeTeleportSpot(candidate)) {
                    continue;
                }
                // All checks passed — teleport
                double x = (double) (baseX + dx) + 0.5D;
                double y = (double) baseY;
                double z = (double) (baseZ + dz) + 0.5D;
                this.world.playSound(null,
                        (double) (baseX + dx) + 0.5D, (double) baseY, (double) (baseZ + dz) + 0.5D,
                        net.minecraft.init.SoundEvents.ENTITY_ENDERMEN_TELEPORT,
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.5F, 1.0F);
                this.setLocationAndAngles(x, (double) baseY, z, this.rotationYaw, this.rotationPitch);
                this.setAttackTarget(null);
                this.getNavigator().clearPath();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given BlockPos is safe for the trunk to appear.
     * <p>
     * This is an <b>intentional gameplay-safe deviation</b> from the original
     * 1.7.10 safe-spot check. The original logic (from {@code func_70626_be})
     * accepts positions where the body and head levels are <b>not</b> air
     * (i.e. trunk teleports inside blocks) and relies on Minecraft collision
     * resolution to push the entity out. That approach is fragile with the
     * 1.12.2 physics engine, so this check uses the inverse: it requires the
     * body and head positions to be clear (air or replaceable), and the floor
     * to be solid (or water).
     * <p>
     * Conditions:
     *   <ol>
     *     <li>Floor (pos.down()) must be present (not air) OR be water.</li>
     *     <li>Body space (pos) must be air or replaceable.</li>
     *     <li>Head space (pos.up()) must be air or replaceable.</li>
     *     <li>All three positions must be in loaded chunks.</li>
     *   </ol>
     */
    private boolean isSafeTeleportSpot(net.minecraft.util.math.BlockPos pos) {
        if (!this.world.isBlockLoaded(pos)
                || !this.world.isBlockLoaded(pos.down())
                || !this.world.isBlockLoaded(pos.up())) {
            return false;
        }
        // Floor: must be solid (non-air) OR water
        net.minecraft.block.state.IBlockState floor = this.world.getBlockState(pos.down());
        if (this.world.isAirBlock(pos.down()) && floor.getMaterial() != net.minecraft.block.material.Material.WATER) {
            return false;
        }
        // Body: must be air or replaceable (trunk needs to fit)
        if (!this.world.isAirBlock(pos)
                && !this.world.getBlockState(pos).getBlock().isReplaceable(this.world, pos)) {
            return false;
        }
        // Head: must be air or replaceable (headroom)
        if (!this.world.isAirBlock(pos.up())
                && !this.world.getBlockState(pos.up()).getBlock().isReplaceable(this.world, pos.up())) {
            return false;
        }
        return true;
    }
}
