package thaumcraft.common.entities.monster.boss;

public class EntityEldritchGolem extends thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss implements thaumcraft.api.entities.IEldritchMob, net.minecraft.entity.IRangedAttackMob {
    public EntityEldritchGolem(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    @Override
    public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {
        // TODO: beam attack
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        // TODO: go headless at lethal damage
        return super.attackEntityFrom(source, amount);
    }
}
