package thaumcraft.common.entities.monster.boss;

public class EntityEldritchGolem extends thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss implements thaumcraft.api.entities.IEldritchMob, net.minecraft.entity.IRangedAttackMob {
    public EntityEldritchGolem(net.minecraft.world.World world) { super(world); }

    @Override public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {}
    @Override public void setSwingingArms(boolean swinging) {}
}
