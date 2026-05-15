package thaumcraft.common.entities.monster;

public class EntityTaintSwarm extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    public EntityTaintSwarm(net.minecraft.world.World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return thaumcraft.common.lib.TCSounds.SWARMATTACK; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return thaumcraft.common.lib.TCSounds.SWARMATTACK; }
    @Override protected float getSoundVolume() { return 0.1f; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        }
    }
}
