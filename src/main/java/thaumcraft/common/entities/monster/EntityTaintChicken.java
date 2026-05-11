package thaumcraft.common.entities.monster;

public class EntityTaintChicken extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    public boolean field_753_a = false;
    public float field_752_b = 0.0f;
    public float destPos = 0.0f;
    public float field_757_d;
    public float field_756_e;
    public float field_755_h = 1.0f;

    public EntityTaintChicken(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.5f, 0.8f);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4);
    }

    @Override
    public int getMaxSpawnedInChunk() { return 2; }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.field_756_e = this.field_752_b;
        this.field_757_d = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3);
        if (this.destPos < 0.0f) this.destPos = 0.0f;
        if (this.destPos > 1.0f) this.destPos = 1.0f;
        if (!this.onGround && this.field_755_h < 1.0f) this.field_755_h = 1.0f;
        this.field_755_h = (float)((double)this.field_755_h * 0.9);
        if (!this.onGround && this.motionY < 0.0) this.motionY *= 0.9;
        this.field_752_b += this.field_755_h * 2.0f;
    }

    // TODO: add sound events
    // @Override
    // protected net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override
    protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource source) { return null; }
    @Override
    protected net.minecraft.util.SoundEvent getDeathSound() { return null; }
    @Override
    protected float getSoundPitch() { return 0.7f; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextInt(4) == 0) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
    }
}
