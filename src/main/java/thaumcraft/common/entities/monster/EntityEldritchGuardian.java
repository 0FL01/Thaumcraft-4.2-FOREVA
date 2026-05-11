package thaumcraft.common.entities.monster;

public class EntityEldritchGuardian extends net.minecraft.entity.monster.EntityMob implements net.minecraft.entity.IRangedAttackMob, thaumcraft.api.entities.IEldritchMob {
    public EntityEldritchGuardian(net.minecraft.world.World world) { super(world); }

    @Override public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {}
    @Override public void setSwingingArms(boolean swinging) {}
}
