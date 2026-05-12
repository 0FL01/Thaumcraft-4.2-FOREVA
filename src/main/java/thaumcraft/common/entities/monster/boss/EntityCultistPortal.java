package thaumcraft.common.entities.monster.boss;

public class EntityCultistPortal extends EntityThaumcraftBoss {
    public EntityCultistPortal(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0);
    }

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return thaumcraft.common.lib.TCSounds.MONOLITH; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return thaumcraft.common.lib.TCSounds.ZAP; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return thaumcraft.common.lib.TCSounds.SHOCK; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: spawn cultists
    }
}
