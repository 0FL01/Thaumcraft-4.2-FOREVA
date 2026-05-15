package thaumcraft.common.entities.golems;

public class EntityTravelingTrunk extends net.minecraft.entity.EntityLiving implements net.minecraft.entity.IEntityOwnable {

    private net.minecraft.entity.EntityLivingBase owner;
    public final InventoryTrunk inventory = new InventoryTrunk();
    private boolean open;
    private boolean stay;
    private int upgrade = -1;
    private static final net.minecraft.network.datasync.DataParameter<com.google.common.base.Optional<java.util.UUID>> OWNER_UUID =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.OPTIONAL_UNIQUE_ID);

    public EntityTravelingTrunk(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.7f, 0.5f);
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
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        net.minecraft.entity.Entity ownerEntity = this.getOwner();
        net.minecraft.entity.EntityLivingBase owner = (ownerEntity instanceof net.minecraft.entity.EntityLivingBase) ? (net.minecraft.entity.EntityLivingBase)ownerEntity : null;
        if (owner != null && this.getDistance(owner) > 4.0f) {
            this.getNavigator().tryMoveToEntityLiving(owner, 0.5);
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

    @Override
    public net.minecraft.entity.IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty,
                                                                 net.minecraft.entity.IEntityLivingData livingdata) {
        this.setInvSize();
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public boolean processInteract(net.minecraft.entity.player.EntityPlayer player, net.minecraft.util.EnumHand hand) {
        if (!this.world.isRemote) {
            player.openGui(thaumcraft.common.Thaumcraft.instance, thaumcraft.common.CommonProxy.GUI_TRAVELING_TRUNK, this.world, this.getEntityId(), 0, 0);
        }
        return true;
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
