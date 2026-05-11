package thaumcraft.common.entities.golems;

public class EntityTravelingTrunk extends net.minecraft.entity.EntityLiving implements net.minecraft.entity.IEntityOwnable {

    private net.minecraft.entity.EntityLivingBase owner;
    private static final net.minecraft.network.datasync.DataParameter<com.google.common.base.Optional<java.util.UUID>> OWNER_UUID =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityTravelingTrunk.class, net.minecraft.network.datasync.DataSerializers.OPTIONAL_UNIQUE_ID);

    public EntityTravelingTrunk(net.minecraft.world.World world) { super(world); this.setSize(0.7f, 0.5f); }

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
        this.dataManager.set(OWNER_UUID, com.google.common.base.Optional.fromNullable(id));
    }
}
