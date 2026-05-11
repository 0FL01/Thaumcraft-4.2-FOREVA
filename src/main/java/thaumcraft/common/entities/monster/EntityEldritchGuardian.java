package thaumcraft.common.entities.monster;

public class EntityEldritchGuardian extends net.minecraft.entity.monster.EntityMob implements net.minecraft.entity.IRangedAttackMob, thaumcraft.api.entities.IEldritchMob {
    public EntityEldritchGuardian(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }

    @Override
    public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {
        // TODO: fire EldritchOrb
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: sonic screech attack
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }
}
