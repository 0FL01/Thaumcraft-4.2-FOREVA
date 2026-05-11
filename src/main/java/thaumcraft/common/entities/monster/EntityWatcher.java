package thaumcraft.common.entities.monster;

public class EntityWatcher extends net.minecraft.entity.monster.EntityMob {
    public EntityWatcher(net.minecraft.world.World world) { super(world); this.setSize(0.85f, 0.85f); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: gaze attack
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }
}
