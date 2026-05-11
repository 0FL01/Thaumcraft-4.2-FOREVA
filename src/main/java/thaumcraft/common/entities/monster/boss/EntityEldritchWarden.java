package thaumcraft.common.entities.monster.boss;

public class EntityEldritchWarden extends thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss implements net.minecraft.entity.IRangedAttackMob, thaumcraft.api.entities.IEldritchMob {
    public EntityEldritchWarden(net.minecraft.world.World world) { super(world); }

    @Override public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {}
    @Override public void setSwingingArms(boolean swinging) {}
}
